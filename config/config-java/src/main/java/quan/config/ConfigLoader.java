package quan.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.common.util.ClassUtils;
import quan.common.util.PathUtils;
import quan.generator.*;
import quan.generator.config.ConfigDefinition;
import quan.generator.config.IndexDefinition;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 加载配置数据
 * Created by quanchangnai on 2019/7/30.
 */
@SuppressWarnings({"unchecked"})
public class ConfigLoader {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected DefinitionParser definitionParser;

    private boolean definitionParsed;

    //配置表所在目录
    private String tablePath;

    //配置表类型,csv xls xlsx等
    private String tableType = "csv";

    private Type loadType = Type.validateAndLoad;

    private int bodyRowNum;

    private Map<String, ConfigReader> readers = new HashMap<>();

    //自定义的配置校验器
    private Set<ConfigValidator> validators = new HashSet<>();

    private LinkedHashSet<String> validatedErrors = new LinkedHashSet<>();

    private boolean loaded;

    public ConfigLoader(String tablePath) {
        this.tablePath = PathUtils.crossPlatPath(tablePath);
    }

    public ConfigLoader setTableType(String tableType) {
        Objects.requireNonNull(tableType, "表格类型不能为空");
        this.tableType = tableType;
        readers.clear();
        return this;
    }

    public String getTableType() {
        return tableType;
    }

    /**
     * 使用XML配置解析器，只有加载以配置全类名作为JSON文件名时可以为空
     */
    public void useXmlDefinitionParser(List<String> definitionPaths, String packagePrefix, String enumPackagePrefix) {
        definitionParser = new XmlDefinitionParser();
        definitionParser.setDefinitionPaths(definitionPaths);
        definitionParser.setPackagePrefix(packagePrefix);
        definitionParser.setEnumPackagePrefix(enumPackagePrefix);
    }

    /**
     * 使用XML配置解析器，只有加载以配置全类名作为JSON文件名时可以为空
     */
    public void useXmlDefinitionParser(String definitionPath, String packagePrefix, String enumPackagePrefix) {
        definitionParser = new XmlDefinitionParser();
        definitionParser.setDefinitionPath(definitionPath);
        definitionParser.setPackagePrefix(packagePrefix);
        definitionParser.setEnumPackagePrefix(enumPackagePrefix);
    }

    /**
     * 设置配置解析器，只有加载以配置全类名作为JSON文件名时可以为空
     */
    public ConfigLoader setDefinitionParser(DefinitionParser definitionParser) {
        this.definitionParser = definitionParser;
        return this;
    }

    public ConfigLoader setLoadType(Type loadType) {
        Objects.requireNonNull(loadType, "加载类型不能为空");
        this.loadType = loadType;
        return this;
    }

    /**
     * 设置表格正文起始行号，默认是第3行,第1行固定是表头，中间是注释等，行号从1开始
     */
    public ConfigLoader setBodyRowNum(int bodyRowNum) {
        if (bodyRowNum > 1 && this.bodyRowNum == 0) {
            this.bodyRowNum = bodyRowNum;
        }
        return this;
    }

    public boolean needValidate() {
        return loadType == Type.onlyValidate || loadType == Type.validateAndLoad;
    }

    public boolean needLoad() {
        return loadType == Type.onlyLoad || loadType == Type.validateAndLoad;
    }

    /**
     * 设置自定义配置校验器所在的包并实例化校验器对象
     */
    public void setValidatorsPackage(String packageName) {
        if (StringUtils.isBlank(packageName)) {
            return;
        }

        validators.clear();
        Set<Class<?>> validatorClasses = ClassUtils.loadClasses(packageName, ConfigValidator.class);

        for (Class<?> validatorClass : validatorClasses) {
            if (validatorClass.isEnum() && validatorClass.getEnumConstants().length > 0) {
                validators.add((ConfigValidator) validatorClass.getEnumConstants()[0]);
            }
            if (!validatorClass.isEnum()) {
                try {
                    validators.add((ConfigValidator) validatorClass.getConstructor().newInstance());
                } catch (Exception e) {
                    logger.error("实例化配置校验器[{}]失败", validatorClass, e);
                }
            }
        }
    }

    private void parseDefinition() {
        if (definitionParser == null || definitionParsed) {
            return;
        }

        try {
            definitionParser.parse();
        } catch (Exception e) {
            logger.error("解析配置定义文件[{}]出错", definitionParser.getDefinitionPaths(), e);
            return;
        }

        List<String> validatedErrors = ClassDefinition.getValidatedErrors();
        if (!validatedErrors.isEmpty()) {
            ConfigException configException = new ConfigException(String.format("解析配置定义文件%s共发现%d条错误。", definitionParser.getDefinitionPaths(), validatedErrors.size()));
            configException.addErrors(validatedErrors);
            throw configException;
        }
        definitionParsed = true;
    }

    /**
     * 加载全部配置<br/>
     * 有配置定义时加载JSON格式配置，JSON文件名是配置类名<br/>
     * 没有配置定义时加载JSON格式配置，JSON文件名必须是配置全类名并且不支持子表<br/>
     * 其他情况的配置表不变
     */
    public void load() {
        if (loaded) {
            logger.error("配置已经全部加载了");
            return;
        }
        validatedErrors.clear();

        //解析定义文件
        parseDefinition();

        if (isJsonAndNoDefinition()) {
            //没有配置定义直接加载JSON
            loadJsonOnNoDefinition();
        } else {
            //通过配置定义加载或者校验
            loadByDefinitions();
        }

        if (needValidate()) {
            for (ConfigReader reader : readers.values()) {
                validatedErrors.addAll(reader.getValidatedErrors());
            }
        }

        executeValidators();

        loaded = true;

        if (!validatedErrors.isEmpty()) {
            throw new ConfigException(validatedErrors);
        }
    }

    private boolean isJsonAndNoDefinition() {
        return tableType.equals("json") && ConfigDefinition.getTableConfigs().isEmpty();
    }

    /**
     * 没有配置定义直接加载JSON格式配置，JSON文件名必须是配置全类名并且不支持子表
     */
    private void loadJsonOnNoDefinition() {
        Set<File> jsonFiles = PathUtils.listFiles(new File(tablePath), "json");
        for (File jsonFile : jsonFiles) {
            String configFullName = jsonFile.getName().substring(0, jsonFile.getName().lastIndexOf("."));
            load(configFullName, Collections.singleton(configFullName), true);
        }
    }

    /**
     * 通过配置定义加载
     */
    private void loadByDefinitions() {
        Set<ConfigDefinition> configDefinitions = new HashSet<>(ConfigDefinition.getTableConfigs().values());

        //配置对应的已索引JSON数据
        Map<ConfigDefinition, Map<IndexDefinition, Map>> configIndexedJsonsAll = new HashMap<>();

        for (ConfigDefinition configDefinition : configDefinitions) {
            //索引校验
            if (needValidate()) {
                configIndexedJsonsAll.put(configDefinition, validateIndex(configDefinition));
            }
            //needLoad():加载配置
            load(configDefinition.getFullName(), getConfigTables(configDefinition), false);
        }

        if (needValidate()) {
            //引用校验，依赖索引结果
            for (ConfigDefinition configDefinition : configIndexedJsonsAll.keySet()) {
                validateRef(configDefinition, configIndexedJsonsAll);
            }
        }
    }

    /**
     * 执行自定义校验器
     */
    private void executeValidators() {
        if (!needValidate()) {
            return;
        }

        for (ConfigValidator validator : validators) {
            try {
                validator.validateConfig();
            } catch (ConfigException e) {
                validatedErrors.addAll(e.getErrors());
            } catch (Exception e) {
                String error = String.format("配置错误:%s", e.getMessage());
                validatedErrors.add(error);
                logger.debug("", e);
            }
        }
    }

    public void writeJson(String path) {
        writeJson(path, false);
    }

    public void writeJson(String path, boolean useFullName) {
        Objects.requireNonNull(path, "输出目录不能为空");

        File pathFile = new File(PathUtils.crossPlatPath(path));
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }

        Set<ConfigDefinition> configDefinitions = new HashSet<>(ConfigDefinition.getTableConfigs().values());

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
            if (useFullName) {
                configName = configDefinition.getFullName();
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
     * @param table Json的表名实际上就是配置名
     */
    private ConfigDefinition getConfigByTable(String table) {
        if (tableType.equals("json")) {
            return ConfigDefinition.getConfig(table);
        } else {
            return ConfigDefinition.getTableConfigs().get(table);
        }
    }

    /**
     * 配置的所有分表和子表
     */
    private Set<String> getConfigTables(ConfigDefinition configDefinition) {
        if (tableType.equals("json")) {
            return configDefinition.getChildrenAndMe();
        } else {
            return new HashSet<>(configDefinition.getAllTables());
        }
    }

    private Map<IndexDefinition, Map> validateIndex(ConfigDefinition configDefinition) {
        //索引对应的配置JSON
        Map<IndexDefinition, Map> configIndexedJsons = new HashMap<>();
        //配置JSON对应的表格
        Map<JSONObject, String> jsonTables = new HashMap();

        for (String table : getConfigTables(configDefinition)) {
            ConfigReader configReader = getOrCreateReader(table);
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

    private void validateRef(ConfigDefinition configDefinition, Map<ConfigDefinition, Map<IndexDefinition, Map>> configIndexedJsonsAll) {
        for (String table : getConfigTables(configDefinition)) {
            List<JSONObject> tableJsons = getOrCreateReader(table).readJsons();
            for (int i = 0; i < tableJsons.size(); i++) {
                JSONObject json = tableJsons.get(i);
                configDefinition.getFields();
                for (String fieldName : json.keySet()) {
                    FieldDefinition field = configDefinition.getField(fieldName);
                    if (field == null) {
                        continue;
                    }
                    Object fieldValue = json.get(fieldName);
                    Triple position = Triple.of(table, String.valueOf(i + 1), field.getColumn());
                    validateFieldRef(position, configDefinition, field, fieldValue, configIndexedJsonsAll);
                }
            }
        }
    }

    private void validateFieldRef(Triple position, BeanDefinition bean, FieldDefinition field, Object value, Map<ConfigDefinition, Map<IndexDefinition, Map>> configIndexedJsonsAll) {
        if (field.isPrimitiveType()) {
            validatePrimitiveTypeRef(position, bean, field, value, false, configIndexedJsonsAll);
        } else if (field.isBeanType()) {
            validateBeanTypeRef(position, field.getBean(), (JSONObject) value, configIndexedJsonsAll);
        } else if (field.getType().equals("map")) {
            JSONObject map = (JSONObject) value;
            for (String mapKey : map.keySet()) {
                //校验map的key引用
                validatePrimitiveTypeRef(position, bean, field, mapKey, true, configIndexedJsonsAll);
                //校验map的value引用
                Object mapValue = map.get(mapKey);
                if (field.isPrimitiveValueType()) {
                    validatePrimitiveTypeRef(position, bean, field, mapValue, false, configIndexedJsonsAll);
                } else {
                    validateBeanTypeRef(position, field.getValueBean(), (JSONObject) mapValue, configIndexedJsonsAll);
                }
            }

        } else if (field.getType().equals("set") || field.getType().equals("list")) {
            JSONArray array = (JSONArray) value;
            for (Object arrayValue : array) {
                if (field.isPrimitiveValueType()) {
                    validatePrimitiveTypeRef(position, bean, field, arrayValue, false, configIndexedJsonsAll);
                } else {
                    validateBeanTypeRef(position, field.getValueBean(), (JSONObject) arrayValue, configIndexedJsonsAll);
                }
            }
        }
    }

    private void validatePrimitiveTypeRef(Triple position, BeanDefinition bean, FieldDefinition field, Object value, boolean mapKey, Map<ConfigDefinition, Map<IndexDefinition, Map>> configIndexedJsonsAll) {
        ConfigDefinition fieldRefConfig = field.getRefConfig(mapKey);
        FieldDefinition fieldRefField = field.getRefField(mapKey);
        if (fieldRefConfig == null || fieldRefField == null) {
            return;
        }

        if (value instanceof Number && ((Number) value).doubleValue() <= 0) {
            return;
        }
        if (value instanceof String && value.equals("")) {
            return;
        }

        String fieldRefs = fieldRefConfig.getName() + "." + fieldRefField.getName();

        IndexDefinition fieldRefIndex = fieldRefConfig.getIndexByField1(fieldRefField);
        Map refIndexedJsons = configIndexedJsonsAll.get(fieldRefConfig).get(fieldRefIndex);

        if (!refIndexedJsons.containsKey(value)) {
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

    private void validateBeanTypeRef(Triple position, BeanDefinition bean, JSONObject json, Map<ConfigDefinition, Map<IndexDefinition, Map>> configIndexedJsonsAll) {
        for (FieldDefinition field : bean.getFields()) {
            Object fieldValue = json.get(field.getName());
            validateFieldRef(position, bean, field, fieldValue, configIndexedJsonsAll);
        }
    }

    /**
     * 加载配置到类索引
     */
    private void load(String configFullName, Set<String> configTables, boolean validate) {
        if (!needLoad()) {
            return;
        }
        String configName = configFullName.substring(configFullName.lastIndexOf(".") + 1);
        List<Config> configs = new ArrayList<>();
        for (String table : configTables) {
            ConfigReader configReader = getOrCreateReader(table);
            configs.addAll(configReader.readObjects());
        }

        Method indexMethod;
        try {
            indexMethod = Class.forName(configFullName + "$self").getMethod("index", List.class);
        } catch (Exception e1) {
            try {
                indexMethod = Class.forName(configFullName).getMethod("index", List.class);
            } catch (Exception e2) {
                logger.error("加载配置[{}]类出错，类不存在或者没有索引方法", configName);
                return;
            }
        }

        try {
            List<String> indexErrors = (List<String>) indexMethod.invoke(null, configs);
            if (needValidate() && validate) {
                validatedErrors.addAll(indexErrors);
            }
        } catch (Exception e) {
            logger.error("调用配置[{}]的索引方法出错", configName, e);
        }
    }

    /**
     * 重加载全部配置，校验依赖
     */
    public void reloadAll() {
        if (!needLoad()) {
            return;
        }
        loaded = false;
        for (ConfigReader reader : readers.values()) {
            reader.clear();
        }
        load();
    }

    /**
     * 通过配置类名重加载，部分加载不校验依赖
     *
     * @param configNames 配置类名
     */
    public void reloadByConfigNames(Collection<String> configNames) {
        if (!needLoad()) {
            return;
        }
        validatedErrors.clear();

        if (isJsonAndNoDefinition()) {
            reloadByTableNames(new LinkedHashSet<>(configNames));
            return;
        }

        Set<String> needReloadTables = new LinkedHashSet<>();
        for (String configName : configNames) {
            ConfigDefinition configDefinition = ConfigDefinition.getConfig(configName);
            if (configDefinition == null) {
                logger.error("重加载[{}]失败，不存在该配置", configName);
                continue;
            }
            needReloadTables.addAll(getConfigTables(configDefinition));
        }
        reloadByTableNames(needReloadTables);
    }

    /**
     * 通过表格原名重加载，在有配置定义的时候才能支持
     *
     * @param originalNames 表格原名，表格转成JSON格式后使用的表名实际上是配置类名
     */
    public void reloadByOriginalNames(Collection<String> originalNames) {
        if (!needLoad()) {
            return;
        }

        if (isJsonAndNoDefinition()) {
            logger.error("JSON格式配置在没有配置定义时不支持原名重加载");
            return;
        }

        List<String> configNames = new ArrayList<>();
        for (String originalName : originalNames) {
            ConfigDefinition configDefinition = ConfigDefinition.getTableConfigs().get(originalName);
            if (configDefinition == null) {
                logger.error("重加载[{}]失败，不存在该配置定义", originalName);
                continue;
            }
            configNames.add(configDefinition.getName());
        }

        reloadByConfigNames(configNames);
    }

    /**
     * 通过表名重加载
     *
     * @param tableNames 表名，表格转成JSON格式后使用的表名实际上是配置类名
     */
    public void reloadByTableNames(Collection<String> tableNames) {
        if (!needLoad()) {
            return;
        }
        validatedErrors.clear();

        if (isJsonAndNoDefinition()) {
            //没有配置定义直接加载JSON
            reloadTablesOnNoDefinition(tableNames);
        } else {
            //通过配置定义加载
            reloadTablesByDefinitions(tableNames);
        }

        if (!validatedErrors.isEmpty()) {
            throw new ConfigException(validatedErrors);
        }
    }

    private void reloadTablesOnNoDefinition(Collection<String> configNames) {
        Set<File> jsonFiles = PathUtils.listFiles(new File(tablePath), "json");
        LinkedHashMap<String, ConfigReader> reloadReaders = new LinkedHashMap<>();

        Set<String> notExistentConfigs = new HashSet<>(configNames);
        for (File jsonFile : jsonFiles) {
            String configFullName = jsonFile.getName().substring(0, jsonFile.getName().lastIndexOf("."));
            String configName = configFullName.substring(configFullName.lastIndexOf(".") + 1);
            if (!configNames.contains(configName)) {
                continue;
            } else {
                notExistentConfigs.remove(configName);
            }
            ConfigReader configReader = readers.get(configFullName);
            if (configReader == null) {
                logger.error("重加载[{}]失败，对应配置从未被加载", configName);
                continue;
            }
            configReader.clear();
            reloadReaders.put(configName, configReader);

            load(configFullName, Collections.singleton(configFullName), true);
        }

        for (String configName : configNames) {
            if (!reloadReaders.containsKey(configName)) {
                logger.error("重加载[{}]失败，不存在该配置文件", configName);
            }
        }

        for (ConfigReader reloadReader : reloadReaders.values()) {
            List<String> errors = reloadReader.getValidatedErrors();
            if (needValidate()) {
                this.validatedErrors.addAll(errors);
            }
        }
    }

    private void reloadTablesByDefinitions(Collection<String> tableNames) {
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
    }

    private ConfigReader createReader(String table) {
        ConfigReader configReader = null;
        ConfigDefinition configDefinition = getConfigByTable(table);
        switch (tableType) {
            case "csv":
                configReader = new CSVConfigReader(tablePath, table + "." + tableType, configDefinition);
                break;
            case "xls":
            case "xlsx":
                configReader = new ExcelConfigReader(tablePath, table + "." + tableType, configDefinition);
                break;
            case "json":
                String configFullName = configDefinition == null ? table : configDefinition.getFullName();
                configReader = new JsonConfigReader(tablePath, table + "." + tableType, configFullName);
                break;

        }

        if (configReader != null && bodyRowNum > 0) {
            configReader.setBodyRowNum(bodyRowNum);
        }
        return configReader;
    }

    private ConfigReader getOrCreateReader(String table) {
        ConfigReader configReader = readers.get(table);
        if (configReader == null) {
            configReader = createReader(table);
            readers.put(table, configReader);
        }
        return configReader;
    }


    public enum Type {
        /**
         * 仅校验配置
         */
        onlyValidate,
        /**
         * 仅加载,会创建配置对象并加载到类的缓存里
         */
        onlyLoad,
        /**
         * 校验并加载,会创建配置对象并加载到类的缓存里
         */
        validateAndLoad
    }
}
