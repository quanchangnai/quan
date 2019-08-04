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

    protected DefinitionParser definitionParser = new XmlDefinitionParser();

    //配置的定义文件所在目录
    private List<String> definitionPaths = new ArrayList<>();

    private boolean definitionParsed;

    //配置表所在目录
    private String tablePath;

    //配置表类型,csv xls xlsx等
    private String tableType = "csv";

    private Type loadType = Type.validateAndLoad;

    private String packagePrefix;

    private String enumPackagePrefix;

    private Map<String, ConfigReader> readers = new HashMap<>();

    //自定义的配置校验器
    private Set<ConfigValidator> validators = new HashSet<>();

    private LinkedHashSet<String> validatedErrors = new LinkedHashSet<>();

    private boolean loaded;

    public ConfigLoader(List<String> definitionPaths, String tablePath) {
        for (String definitionPath : definitionPaths) {
            this.definitionPaths.add(PathUtils.crossPlatPath(definitionPath));
        }
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

    public ConfigLoader setDefinitionParser(DefinitionParser definitionParser) {
        this.definitionParser = definitionParser;
        return this;
    }

    public ConfigLoader setPackagePrefix(String packagePrefix) {
        this.packagePrefix = packagePrefix;
        return this;
    }

    public ConfigLoader setEnumPackagePrefix(String enumPackagePrefix) {
        this.enumPackagePrefix = enumPackagePrefix;
        return this;
    }

    public ConfigLoader setLoadType(Type loadType) {
        Objects.requireNonNull(loadType, "加载类型不能为空");
        this.loadType = loadType;
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
        if (definitionParsed) {
            return;
        }
        definitionParser.setDefinitionPaths(definitionPaths);
        definitionParser.setPackagePrefix(packagePrefix);
        definitionParser.setEnumPackagePrefix(enumPackagePrefix);

        try {
            definitionParser.parse();
        } catch (Exception e) {
            logger.error("解析配置定义文件[{}]出错", definitionPaths, e);
            return;
        }

        List<String> validatedErrors = ClassDefinition.getValidatedErrors();
        if (!validatedErrors.isEmpty()) {
            ConfigException configException = new ConfigException(String.format("解析配置定义文件%s共发现%d条错误。", definitionPaths, validatedErrors.size()));
            configException.addErrors(validatedErrors);
            throw configException;
        }
        definitionParsed = true;
    }

    /**
     * 加载全部配置
     */
    public void load() {
        if (loaded) {
            logger.error("配置已经全部加载了");
            return;
        }
        validatedErrors.clear();
        //解析定义文件
        parseDefinition();

        Set<ConfigDefinition> configDefinitions = new HashSet<>(ConfigDefinition.getTableConfigs().values());

        //配置对应的已索引Json数据
        Map<ConfigDefinition, Map<IndexDefinition, Map>> configIndexedJsonsAll = new HashMap<>();

        for (ConfigDefinition configDefinition : configDefinitions) {
            //索引校验
            if (needValidate()) {
                configIndexedJsonsAll.put(configDefinition, validateIndex(configDefinition));
            }
            //needLoad():加载配置
            load(configDefinition);
        }

        if (needValidate()) {
            for (ConfigReader reader : readers.values()) {
                validatedErrors.addAll(reader.getValidatedErrors());
            }
            //引用校验，依赖索引结果
            for (ConfigDefinition configDefinition : configIndexedJsonsAll.keySet()) {
                validateRef(configDefinition, configIndexedJsonsAll);
            }

            //自定义校验
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

        loaded = true;

        if (!validatedErrors.isEmpty()) {
            throw new ConfigException(validatedErrors);
        }
    }

    public void writeJson(String path) {
        Objects.requireNonNull(path, "输出目录不能为空");

        File pathFile = new File(PathUtils.crossPlatPath(path));
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

    private Collection<String> getConfigTables(ConfigDefinition configDefinition) {
        if (tableType.equals("json")) {
            return configDefinition.getChildrenAndMe();
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
    @SuppressWarnings({"unchecked"})
    private void load(ConfigDefinition configDefinition) {
        if (!needLoad()) {
            return;
        }
        List<Config> configs = new ArrayList<>();
        for (String table : getConfigTables(configDefinition)) {
            ConfigReader configReader = getOrCreateReader(table);
            configs.addAll(configReader.readObjects());
        }

        String indexClass = configDefinition.getFullName();
        if (configDefinition.getParentConfig() != null) {
            indexClass += "$self";
        }

        Method indexMethod;
        try {
            indexMethod = Class.forName(indexClass).getMethod("index", List.class);
        } catch (Exception e) {
            logger.error("加载配置[{}]类出错", configDefinition.getName(), e);
            return;
        }

        try {
            List<String> indexErrors = (List<String>) indexMethod.invoke(null, configs);
            if (needValidate()) {
                validatedErrors.addAll(indexErrors);
            }
        } catch (Exception e) {
            logger.error("调用配置[{}]的索引方法出错", configDefinition.getName(), e);
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
     * 重加载给定的配置，部分加载不校验依赖
     *
     * @param configs 配置名字
     */
    public void reload(Collection<String> configs) {
        if (!needLoad()) {
            return;
        }
        validatedErrors.clear();

        Set<String> needReloadTables = new LinkedHashSet<>();

        for (String configName : configs) {
            ConfigDefinition configDefinition = ConfigDefinition.getConfig(configName);
            if (configDefinition == null) {
                logger.error("重加载[{}]失败，不存在该配置", configName);
                continue;
            }
            needReloadTables.addAll(getConfigTables(configDefinition));
        }

        reloadTables(needReloadTables);
    }

    /**
     * 重加载给定的表格，部分加载不校验依赖，注意：表格转成Json格式后使用的表名实际上是配置类名
     *
     * @param tables       表名
     * @param originalName 表名是不是原名，表格转成Json格式后使用的表名实际上是配置类名
     */
    public void reloadTables(Collection<String> tables, boolean originalName) {
        if (!needLoad()) {
            return;
        }

        if (!tableType.equals("json") || !originalName) {
            reloadTables(tables);
            return;
        }

        List<String> realTables = new ArrayList<>();
        for (String table : tables) {
            ConfigDefinition configDefinition = ConfigDefinition.getTableConfigs().get(table);
            if (configDefinition == null) {
                logger.error("重加载[{}]失败，不存在该配置", table);
                continue;
            }
            realTables.add(configDefinition.getName());
        }

        reloadTables(realTables);
    }

    /**
     * 重加载表格，部分加载不校验依赖，注意：表格转成Json格式后使用的表名实际上是配置类名
     *
     * @param tables 表名
     */
    public void reloadTables(Collection<String> tables) {
        if (!needLoad()) {
            return;
        }
        validatedErrors.clear();

        Set<ConfigDefinition> needReloadConfigs = new LinkedHashSet<>();
        Set<ConfigReader> reloadReaders = new LinkedHashSet<>();

        for (String table : tables) {
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
            load(configDefinition);
        }

        for (ConfigReader reloadReader : reloadReaders) {
            List<String> errors = reloadReader.getValidatedErrors();
            if (needValidate()) {
                this.validatedErrors.addAll(errors);
            }
        }

        if (!validatedErrors.isEmpty()) {
            throw new ConfigException(validatedErrors);
        }
    }

    private ConfigReader createReader(String table) {
        ConfigDefinition configDefinition = getConfigByTable(table);
        switch (tableType) {
            case "csv":
                return new CSVConfigReader(tablePath, table + "." + tableType, configDefinition);
            case "xls":
            case "xlsx":
                return new ExcelConfigReader(tablePath, table + "." + tableType, configDefinition);
            case "json":
                return new JsonConfigReader(tablePath, table + "." + tableType, configDefinition);
            default:
                return null;
        }

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
