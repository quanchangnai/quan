package quan.config;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.common.util.ClassUtils;
import quan.generator.ClassDefinition;
import quan.generator.DefinitionParser;
import quan.generator.FieldDefinition;
import quan.generator.XmlDefinitionParser;
import quan.generator.config.ConfigDefinition;
import quan.generator.config.IndexDefinition;

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
    private List<String> definitionPaths;

    //配置表所在目录
    private String tablePath;

    //仅检查配置，不加载到类
    private boolean onlyCheck;

    private String packagePrefix;

    private String enumPackagePrefix;

    private Map<String, ConfigReader> readers = new HashMap<>();

    private boolean definitionParsed;

    //自定义的配置检查器
    private Set<ConfigChecker> checkers = new HashSet<>();

    private List<String> errors = new ArrayList<>();

    public ConfigLoader(List<String> definitionPaths, String tablePath) {
        this.definitionPaths = definitionPaths;
        this.tablePath = tablePath;
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

    public ConfigLoader setOnlyCheck(boolean onlyCheck) {
        this.onlyCheck = onlyCheck;
        return this;
    }

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
            configReader = new CSVConfigReader(tablePath, table, ConfigDefinition.getTableConfigs().get(table));
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

        Set<ConfigDefinition> loadConfigs = new HashSet<>(ConfigDefinition.getTableConfigs().values());
        for (ConfigDefinition configDefinition : loadConfigs) {
            //通用检查
            check(configDefinition);
            //加载配置
            if (!onlyCheck) {
                load(configDefinition);
            }
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

    private void check(ConfigDefinition configDefinition) {
        Map<IndexDefinition, Map> configIndexedJsons = new HashMap<>();
        Map<JSONObject, String> jsonTables = new HashMap();

        for (String table : configDefinition.getTables()) {
            ConfigReader configReader = getOrAddReader(table);
            List<JSONObject> tableJsons;
            try {
                tableJsons = configReader.readJsons();
            } catch (ConfigException e) {
                tableJsons = configReader.getJsons();
                errors.addAll(e.getErrors());
            }

            for (JSONObject json : tableJsons) {
                //记录Json配置对应的表格
                jsonTables.put(json, table);
                //检查索引
                for (IndexDefinition indexDefinition : configDefinition.getIndexes()) {
                    Map indexedJsons = configIndexedJsons.computeIfAbsent(indexDefinition, k -> new HashMap());
                    checkTableIndex(indexDefinition, indexedJsons, jsonTables, json);
                }
            }

        }
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
                errors.add(String.format("配置[%s]有重复[%s]:[%s]", repeatedTables, field1.getColumn(), json.get(field1.getName())));
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
                errors.add(String.format("配置[%s]有重复[%s,%s]:[%s,%s]", repeatedTables, field1.getColumn(), field2.getColumn(), json.get(field1.getName()), json.get(field2.getName())));
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
                errors.add(String.format("配置[%s]有重复[%s,%s,%s]:[%s,%s,%s]", repeatedTables, field1.getColumn(), field2.getColumn(), field3.getColumn(), json.get(field1.getName()), json.get(field2.getName()), json.get(field3.getName())));
            }
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
            try {
                configs.addAll(configReader.readObjects());
            } catch (ConfigException e) {
                errors.addAll(e.getErrors());
            }
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
        errors.clear();
        Set<ConfigDefinition> reloadConfigs = new HashSet<>();

        for (String table : tables) {
            ConfigReader configReader = readers.get(table);
            if (configReader == null) {
                errors.add(String.format("重加载[%s]出错，对应配置从未被加载", table));
                continue;
            }
            configReader.clear();
            ConfigDefinition configDefinition = ConfigDefinition.getTableConfigs().get(table);
            while (configDefinition != null) {
                reloadConfigs.add(configDefinition);
                configDefinition = configDefinition.getParentDefinition();
            }
        }

        for (ConfigDefinition configDefinition : reloadConfigs) {
            reload(configDefinition);
        }

        if (!errors.isEmpty()) {
            throw new ConfigException(errors);
        }
    }

}
