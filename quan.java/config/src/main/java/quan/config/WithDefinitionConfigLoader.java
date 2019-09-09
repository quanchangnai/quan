package quan.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.lang3.tuple.Triple;
import quan.common.util.PathUtils;
import quan.definition.*;
import quan.definition.config.ConfigDefinition;
import quan.definition.config.IndexDefinition;
import quan.definition.parser.DefinitionParser;
import quan.definition.parser.XmlDefinitionParser;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

/**
 * 使用配置定义加载配置，根据配置定义实现了索引、引用校验等
 * Created by quanchangnai on 2019/8/23.
 */
@SuppressWarnings({"unchecked"})
public class WithDefinitionConfigLoader extends ConfigLoader {

    protected DefinitionParser definitionParser;

    //表格正文起始行号
    private int bodyRowNum;

    {
        tableType = TableType.csv;
    }

    public WithDefinitionConfigLoader(String tablePath) {
        super(tablePath);
    }

    /**
     * 设置表格正文起始行号，默认是第3行,第1行固定是表头，中间是注释等，行号从1开始
     */
    public void setBodyRowNum(int bodyRowNum) {
        if (bodyRowNum > 1 && this.bodyRowNum == 0) {
            this.bodyRowNum = bodyRowNum;
        }
    }

    public void setLoadType(LoadType loadType) {
        Objects.requireNonNull(loadType, "加载类型不能为空");
        this.loadType = loadType;
    }

    /**
     * 使用XML配置解析器
     */
    public DefinitionParser useXmlDefinition(List<String> definitionPaths, String packagePrefix) {
        definitionParser = new XmlDefinitionParser();
        definitionParser.setCategory(DefinitionCategory.config);
        definitionParser.setDefinitionPaths(definitionPaths);
        definitionParser.setPackagePrefix(packagePrefix);
        return definitionParser;
    }

    /**
     * 使用XML配置解析器
     */
    public DefinitionParser useXmlDefinition(String definitionPath, String packagePrefix) {
        return useXmlDefinition(Collections.singletonList(definitionPath), packagePrefix);
    }

    /**
     * 设置配置解析器
     */
    public void setDefinitionParser(DefinitionParser definitionParser) {
        Objects.requireNonNull(definitionParser, "配置解析器不能为空");
        definitionParser.setCategory(DefinitionCategory.config);
        this.definitionParser = definitionParser;
    }

    /**
     * 解析配置定义
     */
    private void parseDefinition() {
        Objects.requireNonNull(definitionParser, "配置定义解析器不能为空");
        if (!definitionParser.getClasses().isEmpty()) {
            return;
        }

        try {
            definitionParser.parse();
        } catch (Exception e) {
            logger.error("解析配置定义文件[{}]出错", definitionParser.getDefinitionPaths(), e);
            return;
        }

        List<String> validatedErrors = definitionParser.getValidatedErrors();
        if (!validatedErrors.isEmpty()) {
            ValidatedException validatedException = new ValidatedException(String.format("解析配置定义文件%s共发现%d条错误。", definitionParser.getDefinitionPaths(), validatedErrors.size()));
            validatedException.addErrors(validatedErrors);
            throw validatedException;
        }
    }

    protected void loadAll0() {
        parseDefinition();

        Set<ConfigDefinition> configDefinitions = new HashSet<>(definitionParser.getTableConfigs().values());
        //配置对应的其已索引JSON数据
        Map<ConfigDefinition, Map<IndexDefinition, Map>> allConfigIndexedJsons = new HashMap<>();

        for (ConfigDefinition configDefinition : configDefinitions) {
            //索引校验
            if (needValidate()) {
                allConfigIndexedJsons.put(configDefinition, validateIndex(configDefinition));
            }
            //needLoad():加载配置
            load(configDefinition.getFullName(), getConfigTables(configDefinition), false);
        }

        if (needValidate()) {
            //格式错误
            for (ConfigReader reader : readers.values()) {
                validatedErrors.addAll(reader.getValidatedErrors());
            }
            //引用校验，依赖索引结果
            for (ConfigDefinition configDefinition : allConfigIndexedJsons.keySet()) {
                validateRef(configDefinition, allConfigIndexedJsons);
            }
        }
    }

    public List<JSONObject> loadJsons(ConfigDefinition configDefinition) {
        List<JSONObject> jsons = new ArrayList<>();
        for (String configTable : getConfigTables(configDefinition)) {
            ConfigReader configReader = getReader(configTable);
            jsons.addAll(configReader.readJsons());
        }
        return jsons;
    }

    public void writeJson(String path) {
        writeJson(path, true);
    }

    public void writeJson(String path, boolean useNameWithPackage) {
        if (definitionParser == null) {
            return;
        }

        Objects.requireNonNull(path, "输出目录不能为空");
        File pathFile = new File(PathUtils.currentPlatPath(path));
        if (!pathFile.exists() && !pathFile.mkdirs()) {
            logger.error("输出目录[{}]创建失败", path);
            return;
        }

        Set<ConfigDefinition> configDefinitions = new HashSet<>(definitionParser.getTableConfigs().values());

        for (ConfigDefinition configDefinition : configDefinitions) {
            JSONArray rows = new JSONArray();
            for (String table : configDefinition.getTables()) {
                ConfigReader reader = readers.get(table);
                if (reader == null) {
                    logger.error("配置[{}]从未被加载", table);
                    continue;
                }
                List<JSONObject> jsons = reader.readJsons();
                rows.addAll(jsons);
            }

            String configName = configDefinition.getName();
            if (useNameWithPackage) {
                configName = configDefinition.getPackageName() + "." + configDefinition.getName();
            }
            try (FileOutputStream fileOutputStream = new FileOutputStream(new File(pathFile, configName + ".json"))) {
                JSON.writeJSONString(fileOutputStream, rows, SerializerFeature.PrettyFormat);
            } catch (Exception e) {
                logger.error("配置[{}]写到JSON文件出错", configName, e);
            }
        }
    }

    /**
     * 通过表名查找配置定义
     *
     * @param table Json的表名实际上就是配置类名
     */
    private ConfigDefinition getConfigByTable(String table) {
        if (tableType == TableType.json) {
            return definitionParser.getConfig(table);
        } else {
            return definitionParser.getTableConfigs().get(table);
        }
    }

    /**
     * 配置的所有分表和子表
     */
    private Collection<String> getConfigTables(ConfigDefinition configDefinition) {
        if (tableType == TableType.json) {
            //Json的表名实际上就是配置类名
            return configDefinition.getDescendantsAndMe();
        } else {
            return configDefinition.getAllTables();
        }
    }

    private Map<IndexDefinition, Map> validateIndex(ConfigDefinition configDefinition) {
        //索引对应的配置JSON
        Map<IndexDefinition, Map> configIndexedJsons = new HashMap<>();
        //配置JSON对应的表格
        Map<JSONObject, String> jsonTables = new HashMap();

        for (String table : getConfigTables(configDefinition)) {
            ConfigReader configReader = getReader(table);
            List<JSONObject> tableJsons = configReader.readJsons();

            for (JSONObject json : tableJsons) {
                jsonTables.put(json, table);
                //校验索引
                for (IndexDefinition indexDefinition : configDefinition.getIndexes()) {
                    Map indexedJsons = configIndexedJsons.computeIfAbsent(indexDefinition, k -> new HashMap());
                    validateTableIndex(indexDefinition, indexedJsons, jsonTables, json);
                }
            }
        }

        return configIndexedJsons;
    }

    private void validateTableIndex(IndexDefinition indexDefinition, Map indexedJsons, Map<JSONObject, String> jsonTables, JSONObject json) {
        String table = jsonTables.get(json);

        if (indexDefinition.isUnique() && indexDefinition.getFields().size() == 1) {
            FieldDefinition field1 = indexDefinition.getFields().get(0);
            if (!json.containsKey(field1.getName())) {
                return;
            }

            JSONObject oldJson = (JSONObject) indexedJsons.put(json.get(field1.getName()), json);
            if (oldJson != null) {
                String repeatedTables = table;
                if (!jsonTables.get(oldJson).equals(table)) {
                    repeatedTables += "," + jsonTables.get(oldJson);
                }
                validatedErrors.add(String.format("配置[%s]有重复数据[%s = %s]", repeatedTables, field1.getColumn(), json.get(field1.getName())));
            }
        }

        if (indexDefinition.isUnique() && indexDefinition.getFields().size() == 2) {
            FieldDefinition field1 = indexDefinition.getFields().get(0);
            FieldDefinition field2 = indexDefinition.getFields().get(1);
            if (!json.containsKey(field1.getName()) || !json.containsKey(field2.getName())) {
                return;
            }

            JSONObject oldJson = (JSONObject) ((Map) indexedJsons.computeIfAbsent(json.get(field1.getName()), k -> new HashMap<>())).put(json.get(field2.getName()), json);
            if (oldJson != null) {
                String repeatedTables = table;
                if (!jsonTables.get(oldJson).equals(table)) {
                    repeatedTables += "," + jsonTables.get(oldJson);
                }
                validatedErrors.add(String.format("配置[%s]有重复数据[%s,%s = %s,%s]", repeatedTables, field1.getColumn(), field2.getColumn(), json.get(field1.getName()), json.get(field2.getName())));
            }
        }

        if (indexDefinition.isUnique() && indexDefinition.getFields().size() == 3) {
            FieldDefinition field1 = indexDefinition.getFields().get(0);
            FieldDefinition field2 = indexDefinition.getFields().get(1);
            FieldDefinition field3 = indexDefinition.getFields().get(2);
            if (!json.containsKey(field1.getName()) || !json.containsKey(field2.getName()) || !json.containsKey(field3.getName())) {
                return;
            }

            JSONObject oldJson = (JSONObject) ((Map) ((Map) indexedJsons.computeIfAbsent(json.get(field1.getName()), k -> new HashMap<>())).computeIfAbsent(json.get(field2.getName()), k -> new HashMap<>())).put(json.get(field3.getName()), json);
            if (oldJson != null) {
                String repeatedTables = table;
                if (!jsonTables.get(oldJson).equals(table)) {
                    repeatedTables += "," + jsonTables.get(oldJson);
                }
                validatedErrors.add(String.format("配置[%s]有重复数据[%s,%s,%s = %s,%s,%s]", repeatedTables, field1.getColumn(), field2.getColumn(), field3.getColumn(), json.get(field1.getName()), json.get(field2.getName()), json.get(field3.getName())));
            }
        }
    }

    private void validateRef(ConfigDefinition configDefinition, Map<ConfigDefinition, Map<IndexDefinition, Map>> allConfigIndexedJsons) {
        for (String table : getConfigTables(configDefinition)) {
            List<JSONObject> tableJsons = getReader(table).readJsons();
            for (int i = 0; i < tableJsons.size(); i++) {
                JSONObject json = tableJsons.get(i);
                for (String fieldName : json.keySet()) {
                    FieldDefinition field = configDefinition.getField(fieldName);
                    if (field == null) {
                        continue;
                    }
                    Object fieldValue = json.get(fieldName);
                    Triple position = Triple.of(table, String.valueOf(i + 1), field.getColumn());
                    validateFieldRef(position, configDefinition, field, fieldValue, allConfigIndexedJsons);
                }
            }
        }
    }

    private void validateFieldRef(Triple position, BeanDefinition bean, FieldDefinition field, Object value, Map<ConfigDefinition, Map<IndexDefinition, Map>> allConfigIndexedJsons) {
        if (field.isPrimitiveType()) {
            validatePrimitiveTypeRef(position, bean, field, value, false, allConfigIndexedJsons);
        } else if (field.isBeanType()) {
            validateBeanTypeRef(position, field.getBean(), (JSONObject) value, allConfigIndexedJsons);
        } else if (field.getType().equals("map")) {
            JSONObject map = (JSONObject) value;
            for (String mapKey : map.keySet()) {
                //校验map的key引用
                validatePrimitiveTypeRef(position, bean, field, mapKey, true, allConfigIndexedJsons);
                //校验map的value引用
                Object mapValue = map.get(mapKey);
                if (field.isPrimitiveValueType()) {
                    validatePrimitiveTypeRef(position, bean, field, mapValue, false, allConfigIndexedJsons);
                } else {
                    validateBeanTypeRef(position, field.getValueBean(), (JSONObject) mapValue, allConfigIndexedJsons);
                }
            }

        } else if (field.getType().equals("set") || field.getType().equals("list")) {
            JSONArray array = (JSONArray) value;
            for (Object arrayValue : array) {
                if (field.isPrimitiveValueType()) {
                    validatePrimitiveTypeRef(position, bean, field, arrayValue, false, allConfigIndexedJsons);
                } else {
                    validateBeanTypeRef(position, field.getValueBean(), (JSONObject) arrayValue, allConfigIndexedJsons);
                }
            }
        }
    }

    private void validatePrimitiveTypeRef(Triple position, BeanDefinition bean, FieldDefinition field, Object value, boolean mapKey, Map<ConfigDefinition, Map<IndexDefinition, Map>> allConfigIndexedJsons) {
        ConfigDefinition fieldRefConfig = field.getRefConfig(mapKey);
        FieldDefinition fieldRefField = field.getRefField(mapKey);
        if (fieldRefConfig == null || fieldRefField == null) {
            return;
        }

        if (value == null || value.equals("")) {
            return;
        }

        String fieldRefs = fieldRefConfig.getName() + "." + fieldRefField.getName();

        IndexDefinition fieldRefIndex = fieldRefConfig.getIndexByField1(fieldRefField);
        Map refIndexedJsons = allConfigIndexedJsons.get(fieldRefConfig).get(fieldRefIndex);

        if (refIndexedJsons == null || !refIndexedJsons.containsKey(value)) {
            String error;
            String keyOrValue = "值";
            if (field.isCollectionType() && mapKey) {
                keyOrValue = "键";
            }
            if (bean instanceof ConfigDefinition) {
                error = String.format("配置[%s]的第%s行[%s]的%s引用[%s]数据[%s]不存在", position.getLeft(), position.getMiddle(), position.getRight(), keyOrValue, fieldRefs, value);
            } else {
                error = String.format("配置[%s]第%s行[%s]的对象[%s]字段[%s]的%s引用[%s]数据[%s]不存在", position.getLeft(), position.getMiddle(), position.getRight(), bean.getName(), field.getName(), keyOrValue, fieldRefs, value);
            }
            validatedErrors.add(error);
        }
    }

    private void validateBeanTypeRef(Triple position, BeanDefinition bean, JSONObject json, Map<ConfigDefinition, Map<IndexDefinition, Map>> allConfigIndexedJsons) {
        if (json == null) {
            return;
        }
        for (FieldDefinition field : bean.getFields()) {
            Object fieldValue = json.get(field.getName());
            validateFieldRef(position, bean, field, fieldValue, allConfigIndexedJsons);
        }
    }

    @Override
    protected void checkReload() {
        super.checkReload();
        Objects.requireNonNull(definitionParser, "配置定义解析器不能为空");
    }

    /**
     * 通过配置类名重加载，部分加载不校验依赖
     *
     * @param configNames 配置类名
     */
    @Override
    public void reloadByConfigName(Collection<String> configNames) {
        checkReload();
        validatedErrors.clear();

        Set<String> tableNames = new LinkedHashSet<>();
        for (String configName : configNames) {
            ConfigDefinition configDefinition = definitionParser.getConfig(configName);
            if (configDefinition == null) {
                logger.error("重加载[{}]失败，不存在该配置", configName);
                continue;
            }
            tableNames.addAll(getConfigTables(configDefinition));
        }
        reloadByTableName(tableNames);
    }

    /**
     * 通过表名重加载,表格转成JSON格式后使用的表名实际上是配置类名
     */
    public void reloadByTableName(Collection<String> tableNames) {
        checkReload();
        validatedErrors.clear();

        Set<ConfigDefinition> needReloadConfigs = new LinkedHashSet<>();
        Set<ConfigReader> reloadReaders = new LinkedHashSet<>();

        for (String table : tableNames) {
            ConfigReader configReader = readers.get(table);
            if (configReader == null) {
                logger.error("重加载[{}]失败，对应配置从未被加载", table);
                continue;
            }
            configReader.clear();
            reloadReaders.add(configReader);

            ConfigDefinition configDefinition = getConfigByTable(table);
            while (configDefinition != null) {
                needReloadConfigs.add(configDefinition);
                configDefinition = configDefinition.getParentConfig();
            }
        }

        for (ConfigDefinition configDefinition : needReloadConfigs) {
            load(configDefinition.getFullName(), getConfigTables(configDefinition), true);
        }

        for (ConfigReader reloadReader : reloadReaders) {
            List<String> errors = reloadReader.getValidatedErrors();
            if (needValidate()) {
                this.validatedErrors.addAll(errors);
            }
        }

        if (!validatedErrors.isEmpty()) {
            throw new ValidatedException(validatedErrors);
        }
    }

    /**
     * 通过表格原名重加载
     */
    public void reloadByOriginalName(Collection<String> originalNames) {
        checkReload();

        List<String> configNames = new ArrayList<>();
        for (String originalName : originalNames) {
            ConfigDefinition configDefinition = definitionParser.getTableConfigs().get(originalName);
            if (configDefinition == null) {
                logger.error("重加载[{}]失败，不存在该配置定义", originalName);
                continue;
            }
            configNames.add(configDefinition.getName());
        }

        reloadByConfigName(configNames);
    }


    protected ConfigReader createReader(String table) {
        ConfigReader configReader = null;
        File tableFile = new File(tablePath, table + "." + tableType);
        ConfigDefinition configDefinition = getConfigByTable(table);
        switch (tableType) {
            case csv:
                configReader = new CSVConfigReader(tableFile, configDefinition);
                break;
            case xls:
            case xlsx:
                configReader = new ExcelConfigReader(tableFile, configDefinition);
                break;
            case json: {
                configReader = new JsonConfigReader(tableFile, configDefinition.getFullName());
                break;
            }
        }

        configReader.setBodyRowNum(bodyRowNum);

        return configReader;
    }

}
