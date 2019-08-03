package quan.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.common.util.ClassUtils;
import quan.generator.*;
import quan.generator.config.ConfigDefinition;
import quan.generator.config.IndexDefinition;

import java.io.File;
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

    //配置表所在目录
    private String tablePath;

    private boolean onlyCheck;

    private String packagePrefix;

    private String enumPackagePrefix;

    private Class<? extends ConfigReader> readerClass = CSVConfigReader.class;

    private Map<String, ConfigReader> readers = new HashMap<>();

    private boolean definitionParsed;

    //自定义的配置检查器
    private Set<ConfigChecker> checkers = new HashSet<>();

    private LinkedHashSet<String> errors = new LinkedHashSet<>();

    public ConfigLoader(List<String> definitionPaths, String tablePath) {
        for (String definitionPath : definitionPaths) {
            this.definitionPaths.add(definitionPath.replace("/", File.separator).replace("\\", File.separator));
        }
        this.tablePath = tablePath.replace("/", File.separator).replace("\\", File.separator);
    }

    public ConfigLoader setReaderClass(Class<? extends ConfigReader> readerClass) {
        Objects.requireNonNull(readerClass, "配置读取器类型不能为空");
        this.readerClass = readerClass;
        return this;
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

    /**
     * 是否仅检查配置
     *
     * @param onlyCheck true:不会创建配置对象。false:会创建配置对象并加载到类的缓存里。
     */
    public void onlyCheck(boolean onlyCheck) {
        this.onlyCheck = onlyCheck;
    }

    /**
     * 实例化给定包下面的自定义检查器对象
     *
     * @param checkerPackage 自定义配置检查器所在的包
     */
    public void setCheckerPackage(String checkerPackage) {
        if (StringUtils.isBlank(checkerPackage)) {
            return;
        }

        checkers.clear();
        Set<Class<?>> checkerClasses = ClassUtils.loadClasses(checkerPackage, ConfigChecker.class);

        for (Class<?> checkerClass : checkerClasses) {
            if (checkerClass.isEnum() && checkerClass.getEnumConstants().length > 0) {
                checkers.add((ConfigChecker) checkerClass.getEnumConstants()[0]);
            }
            if (!checkerClass.isEnum()) {
                try {
                    checkers.add((ConfigChecker) checkerClass.getConstructor().newInstance());
                } catch (Exception e) {
                    logger.error("实例化配置检查器[{}]失败", checkerClass, e);
                }
            }
        }

    }

    private ConfigReader getOrAddReader(String table) {
        ConfigReader configReader = readers.get(table);
        if (configReader == null) {
            if (readerClass == ExcelConfigReader.class) {
                configReader = new ExcelConfigReader(tablePath, table, ConfigDefinition.getTableConfigs().get(table));
            } else {
                configReader = new CSVConfigReader(tablePath, table, ConfigDefinition.getTableConfigs().get(table));
            }
            readers.put(table, configReader);
            if (!onlyCheck) {
                configReader.initPrototype();
            }
        }
        return configReader;
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
            String error = String.format("读取[%s]异常:%s", definitionPaths, e.getMessage());
            logger.debug(error, e);
            throw new ConfigException(error);
        }

        List<String> validatedErrors = ClassDefinition.getValidatedErrors();
        if (!validatedErrors.isEmpty()) {
            ConfigException configException = new ConfigException(String.format("解析定义文件%s共发现%d条错误。", definitionPaths, validatedErrors.size()));
            configException.addErrors(validatedErrors);
            throw configException;
        }
        definitionParsed = true;
    }

    /**
     * 全量加载配置
     */
    public void load() {
        errors.clear();
        //解析定义文件
        parseDefinition();

        //配置对应的已索引Json数据
        Map<ConfigDefinition, Map<IndexDefinition, Map>> configIndexedJsonsAll = new HashMap<>();

        Set<ConfigDefinition> needLoadConfigs = new HashSet<>(ConfigDefinition.getTableConfigs().values());

        for (ConfigDefinition configDefinition : needLoadConfigs) {
            //索引检查
            configIndexedJsonsAll.put(configDefinition, checkIndex(configDefinition));
            //加载配置
            if (!onlyCheck) {
                load(configDefinition);
            }
        }

        for (ConfigReader reader : readers.values()) {
            errors.addAll(reader.getErrors());
        }

        //引用检查，依赖索引结果
        for (ConfigDefinition configDefinition : configIndexedJsonsAll.keySet()) {
            checkRef(configDefinition, configIndexedJsonsAll);
        }

        //自定义检查
        for (ConfigChecker checker : checkers) {
            try {
                checker.checkConfig();
            } catch (ConfigException e) {
                errors.addAll(e.getErrors());
            } catch (Exception e) {
                String error = String.format("配置错误:%s", e.getMessage());
                errors.add(error);
                logger.debug("", e);
            }
        }

        if (!errors.isEmpty()) {
            throw new ConfigException(errors);
        }
    }

    private Map<IndexDefinition, Map> checkIndex(ConfigDefinition configDefinition) {
        //索引对应的配置JSON
        Map<IndexDefinition, Map> configIndexedJsons = new HashMap<>();
        //配置JSON对应的表格
        Map<JSONObject, String> jsonTables = new HashMap();

        for (String table : configDefinition.getTables()) {
            ConfigReader configReader = getOrAddReader(table);
            List<JSONObject> tableJsons = configReader.readJsons();

            for (JSONObject json : tableJsons) {
                jsonTables.put(json, table);
                //检查索引
                for (IndexDefinition indexDefinition : configDefinition.getIndexes()) {
                    Map indexedJsons = configIndexedJsons.computeIfAbsent(indexDefinition, k -> new HashMap());
                    checkTableIndex(indexDefinition, indexedJsons, jsonTables, json);
                }
            }
        }

        return configIndexedJsons;
    }

    private void checkTableIndex(IndexDefinition indexDefinition, Map indexedJsons, Map<JSONObject, String> jsonTables, JSONObject json) {
        String table = jsonTables.get(json);

        if (indexDefinition.isUnique() && indexDefinition.getFields().size() == 1) {
            FieldDefinition field1 = indexDefinition.getFields().get(0);
            JSONObject oldJson = (JSONObject) indexedJsons.put(json.get(field1.getName()), json);
            if (oldJson != null) {
                String repeatedTables = table;
                if (!jsonTables.get(oldJson).equals(table)) {
                    repeatedTables += "," + jsonTables.get(oldJson);
                }
                errors.add(String.format("配置[%s]有重复数据[%s = %s]", repeatedTables, field1.getColumn(), json.get(field1.getName())));
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
                errors.add(String.format("配置[%s]有重复数据[%s,%s = %s,%s]", repeatedTables, field1.getColumn(), field2.getColumn(), json.get(field1.getName()), json.get(field2.getName())));
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
                errors.add(String.format("配置[%s]有重复数据[%s,%s,%s = %s,%s,%s]", repeatedTables, field1.getColumn(), field2.getColumn(), field3.getColumn(), json.get(field1.getName()), json.get(field2.getName()), json.get(field3.getName())));
            }
        }
    }

    private void checkRef(ConfigDefinition configDefinition, Map<ConfigDefinition, Map<IndexDefinition, Map>> configIndexedJsonsAll) {
        for (String table : configDefinition.getTables()) {
            List<JSONObject> tableJsons = getOrAddReader(table).readJsons();
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
                    checkFieldRef(position, configDefinition, field, fieldValue, configIndexedJsonsAll);
                }
            }
        }
    }

    private void checkFieldRef(Triple position, BeanDefinition bean, FieldDefinition field, Object value, Map<ConfigDefinition, Map<IndexDefinition, Map>> configIndexedJsonsAll) {
        if (field.isPrimitiveType()) {
            checkPrimitiveTypeRef(position, bean, field, value, false, configIndexedJsonsAll);
        } else if (field.isBeanType()) {
            checkBeanTypeRef(position, field.getBean(), (JSONObject) value, configIndexedJsonsAll);
        } else if (field.getType().equals("map")) {
            JSONObject map = (JSONObject) value;
            for (String mapKey : map.keySet()) {
                //检查map的key引用
                checkPrimitiveTypeRef(position, bean, field, mapKey, true, configIndexedJsonsAll);
                //检查map的value引用
                Object mapValue = map.get(mapKey);
                if (field.isPrimitiveValueType()) {
                    checkPrimitiveTypeRef(position, bean, field, mapValue, false, configIndexedJsonsAll);
                } else {
                    checkBeanTypeRef(position, field.getValueBean(), (JSONObject) mapValue, configIndexedJsonsAll);
                }
            }

        } else if (field.getType().equals("set") || field.getType().equals("list")) {
            JSONArray array = (JSONArray) value;
            for (Object arrayValue : array) {
                if (field.isPrimitiveValueType()) {
                    checkPrimitiveTypeRef(position, bean, field, arrayValue, false, configIndexedJsonsAll);
                } else {
                    checkBeanTypeRef(position, field.getValueBean(), (JSONObject) arrayValue, configIndexedJsonsAll);
                }
            }
        }
    }

    private void checkPrimitiveTypeRef(Triple position, BeanDefinition bean, FieldDefinition field, Object value, boolean mapKey, Map<ConfigDefinition, Map<IndexDefinition, Map>> configIndexedJsonsAll) {
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

        IndexDefinition fieldRefIndex = fieldRefConfig.getIndexByStartField(fieldRefField);
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
            errors.add(error);
        }
    }

    private void checkBeanTypeRef(Triple position, BeanDefinition bean, JSONObject json, Map<ConfigDefinition, Map<IndexDefinition, Map>> configIndexedJsonsAll) {
        for (FieldDefinition field : bean.getFields()) {
            Object fieldValue = json.get(field.getName());
            checkFieldRef(position, bean, field, fieldValue, configIndexedJsonsAll);
        }
    }

    private void load(ConfigDefinition configDefinition) {
        load(configDefinition, false);
    }

    private void reload(ConfigDefinition configDefinition) {
        load(configDefinition, true);
    }

    private void load(ConfigDefinition configDefinition, boolean reload) {
        List<Config> configs = new ArrayList<>();

        for (String table : configDefinition.getTables()) {
            ConfigReader configReader = getOrAddReader(table);
            configs.addAll(configReader.readObjects());
        }

        indexConfigs(configDefinition, configs, reload);
    }

    @SuppressWarnings({"unchecked"})
    private void indexConfigs(ConfigDefinition configDefinition, List<Config> configs, boolean reload) {
        String indexClass = configDefinition.getFullName();
        if (configDefinition.getParentDefinition() != null) {
            indexClass += "$self";
        }

        Method indexMethod;
        try {
            indexMethod = Class.forName(indexClass).getMethod("index", List.class);
        } catch (Exception e) {
            String error = String.format("加载配置[%s]类出错:%s", configDefinition.getName(), e.getMessage());
            errors.add(error);
            logger.debug(error, e);
            return;
        }

        try {
            List<String> indexErrors = (List<String>) indexMethod.invoke(null, configs);
            if (reload) {
                errors.addAll(indexErrors);
            }
        } catch (Exception e) {
            String error = String.format("调用配置[%s]的索引方法出错:%s", configDefinition.getName(), e.getMessage());
            errors.add(error);
            logger.debug(error, e);
        }


    }

    /**
     * 重加载配置
     *
     * @param tables 表格名字列表
     */
    public void reload(List<String> tables) {
        if (onlyCheck) {
            return;
        }

        errors.clear();

        Set<ConfigDefinition> needReloadConfigs = new LinkedHashSet<>();
        Set<ConfigReader> reloadReaders = new LinkedHashSet<>();

        for (String table : tables) {
            ConfigReader configReader = readers.get(table);
            if (configReader == null) {
                errors.add(String.format("重加载[%s]出错，对应配置从未被加载", table));
                continue;
            }
            reloadReaders.add(configReader);
            configReader.clear();

            ConfigDefinition configDefinition = ConfigDefinition.getTableConfigs().get(table);
            while (configDefinition != null) {
                needReloadConfigs.add(configDefinition);
                configDefinition = configDefinition.getParentDefinition();
            }
        }

        for (ConfigDefinition configDefinition : needReloadConfigs) {
            reload(configDefinition);
        }

        for (ConfigReader reloadReader : reloadReaders) {
            errors.addAll(reloadReader.getErrors());
        }

        if (!errors.isEmpty()) {
            throw new ConfigException(errors);
        }
    }

}
