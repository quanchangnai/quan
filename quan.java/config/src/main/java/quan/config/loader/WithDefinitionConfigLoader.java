package quan.config.loader;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.lang3.tuple.Triple;
import quan.common.utils.PathUtils;
import quan.config.TableType;
import quan.config.ValidatedException;
import quan.config.reader.CSVConfigReader;
import quan.config.reader.ConfigReader;
import quan.config.reader.ExcelConfigReader;
import quan.config.reader.JsonConfigReader;
import quan.definition.*;
import quan.definition.config.ConfigDefinition;
import quan.definition.parser.DefinitionParser;
import quan.definition.parser.XmlDefinitionParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * 使用配置定义加载配置，根据配置定义实现了索引、引用校验等
 * Created by quanchangnai on 2019/8/23.
 */
@SuppressWarnings({"unchecked"})
public class WithDefinitionConfigLoader extends ConfigLoader {

    protected DefinitionParser parser;

    //表格正文起始行号
    private int tableBodyStartRow;

    {
        tableType = TableType.xlsx;
    }

    public WithDefinitionConfigLoader(String tablePath) {
        super(tablePath);
    }

    /**
     * 设置表格正文起始行号，默认是第3行,第1行固定是表头，中间是注释等，行号从1开始
     */
    public void setTableBodyStartRow(int tableBodyStartRow) {
        if (tableBodyStartRow > 1 && this.tableBodyStartRow == 0) {
            this.tableBodyStartRow = tableBodyStartRow;
        }
    }

    public void setLoadMode(LoadMode loadMode) {
        Objects.requireNonNull(loadMode, "加载模式不能为空");
        this.loadMode = loadMode;
    }

    /**
     * 使用XML配置解析器
     */
    public DefinitionParser useXmlDefinition(List<String> definitionPaths, String packagePrefix) {
        parser = new XmlDefinitionParser();
        parser.setCategory(Category.config);
        parser.setDefinitionPaths(definitionPaths);
        parser.setPackagePrefix(packagePrefix);
        return parser;
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
    public void setParser(DefinitionParser parser) {
        Objects.requireNonNull(parser, "配置解析器不能为空");
        parser.setCategory(Category.config);
        this.parser = parser;
    }

    /**
     * 解析配置定义
     */
    private void parseDefinitions() {
        Objects.requireNonNull(parser, "配置定义解析器不能为空");
        if (!parser.getClasses().isEmpty()) {
            return;
        }

        try {
            parser.parse();
        } catch (Exception e) {
            logger.error("解析配置定义文件[{}]出错", parser.getDefinitionPaths(), e);
            return;
        }

        LinkedHashSet<String> validatedErrors = parser.getValidatedErrors();
        if (!validatedErrors.isEmpty()) {
            ValidatedException validatedException = new ValidatedException(String.format("解析配置定义文件%s共发现%d条错误。", parser.getDefinitionPaths(), validatedErrors.size()));
            validatedException.addErrors(validatedErrors);
            throw validatedException;
        }
    }

    protected void doLoadAll() {
        parseDefinitions();

        Set<ConfigDefinition> configDefinitions = new HashSet<>(parser.getTableConfigs().values());
        //配置对应的其已索引JSON数据
        Map<ConfigDefinition, Map<IndexDefinition, Map>> allConfigIndexedJsons = new HashMap<>();

        for (ConfigDefinition configDefinition : configDefinitions) {
            //索引校验
            if (needValidate()) {
                allConfigIndexedJsons.put(configDefinition, validateIndex(configDefinition));
            }
            //needLoad():加载配置
            load(configDefinition.getFullName(Language.java), getConfigTables(configDefinition), false);
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

    /**
     * 加载JSON格式配置数据
     *
     * @param configDefinition 配置定义
     * @param self             true:只包含自己的数据，false:包含自己和子孙配置的数据
     * @return SON格式配置数据
     */
    public List<JSONObject> loadJsons(ConfigDefinition configDefinition, boolean self) {
        TreeSet<String> configTables = new TreeSet<>();
        if (self) {
            if (tableType == TableType.json) {
                configTables.add(configDefinition.getName());
            } else {
                configTables.addAll(configDefinition.getTables());
            }
        } else {
            configTables.addAll(getConfigTables(configDefinition));
        }

        List<JSONObject> jsons = new ArrayList<>();

        for (String configTable : configTables) {
            ConfigReader configReader = getReader(configTable);
            jsons.addAll(configReader.readJsons());
        }

        return jsons;
    }

    public void writeJson(String path, boolean useNameWithPackage, Language language) {
        if (parser == null) {
            return;
        }

        Objects.requireNonNull(path, "输出目录不能为空");
        File pathFile = new File(PathUtils.toPlatPath(path));
        if (!pathFile.exists() && !pathFile.mkdirs()) {
            logger.error("输出目录[{}]创建失败", path);
            return;
        }

        Set<ConfigDefinition> configDefinitions = new HashSet<>(parser.getTableConfigs().values());

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
                configName = configDefinition.getPackageName(language) + "." + configDefinition.getName();
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
            return parser.getConfig(table);
        } else {
            return parser.getTableConfigs().get(table);
        }
    }

    /**
     * 配置的所有分表和子表
     */
    private Collection<String> getConfigTables(ConfigDefinition configDefinition) {
        if (tableType == TableType.json) {
            //Json的表名实际上就是配置类名
            return configDefinition.getMeAndDescendants();
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
                jsonTables.put(json, table + "." + tableType);
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
                validatedErrors.add(String.format("配置[%s]有重复数据[(%s,%s) = (%s,%s)]", repeatedTables, field1.getColumn(), field2.getColumn(), json.get(field1.getName()), json.get(field2.getName())));
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
                validatedErrors.add(String.format("配置[%s]有重复数据[(%s,%s,%s) = (%s,%s,%s)]", repeatedTables, field1.getColumn(), field2.getColumn(), field3.getColumn(), json.get(field1.getName()), json.get(field2.getName()), json.get(field3.getName())));
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
                    Triple position = Triple.of(table + "." + tableType, String.valueOf(i + 1), field.getColumn());
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
            String keyOrValue = "";
            if (field.isCollectionType()) {
                keyOrValue = mapKey ? "键" : "值";
            }
            if (bean instanceof ConfigDefinition) {
                error = String.format("配置[%s]的第%s行[%s]的%s引用[%s]数据[%s]不存在", position.getLeft(), position.getMiddle(), position.getRight(), keyOrValue, fieldRefs, value);
            } else {
                error = String.format("配置[%s]第%s行[%s]的对象[%s]字段[%s]%s引用[%s]数据[%s]不存在", position.getLeft(), position.getMiddle(), position.getRight(), bean.getName(), field.getName(), keyOrValue, fieldRefs, value);
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
        Objects.requireNonNull(parser, "配置定义解析器不能为空");
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
            ConfigDefinition configDefinition = parser.getConfig(configName);
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
                configDefinition = configDefinition.getParent();
            }
        }

        for (ConfigDefinition configDefinition : needReloadConfigs) {
            load(configDefinition.getFullName(Language.java), getConfigTables(configDefinition), true);
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
     * @see #reloadByTableName(Collection)
     */
    public void reloadByTableName(String... tableNames) {
        reloadByTableName(Arrays.asList(tableNames));
    }

    /**
     * 通过表格原名重加载
     */
    public void reloadByOriginalName(Collection<String> originalNames) {
        checkReload();

        List<String> configNames = new ArrayList<>();
        for (String originalName : originalNames) {
            ConfigDefinition configDefinition = parser.getTableConfigs().get(originalName);
            if (configDefinition == null) {
                logger.error("重加载[{}]失败，不存在该配置", originalName);
                continue;
            }
            configNames.add(configDefinition.getName());
        }

        reloadByConfigName(configNames);
    }

    /**
     * @see #reloadByOriginalName(Collection)
     */
    public void reloadByOriginalName(String... originalNames) {
        reloadByOriginalName(Arrays.asList(originalNames));
    }


    @Override
    protected ConfigReader createReader(String table) {
        File tableFile = new File(tablePath, table + "." + tableType);
        try {
            String canonicalName = tableFile.getCanonicalFile().getName();
            if (!canonicalName.equals(tableFile.getName())) {
                validatedErrors.add(String.format("配置[%s]和实际表格文件[%s]的名字大小写必须保持一致", tableFile.getName(), canonicalName));
            }
        } catch (IOException ignored) {
        }

        ConfigReader configReader = null;
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
                configReader = new JsonConfigReader(tableFile, configDefinition.getFullName(Language.java));
                break;
            }
        }

        configReader.setTable(table + "." + tableType);
        configReader.setTableBodyStartRow(tableBodyStartRow);

        return configReader;
    }

}
