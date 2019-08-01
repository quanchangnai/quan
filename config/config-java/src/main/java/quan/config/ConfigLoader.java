package quan.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.generator.ClassDefinition;
import quan.generator.DefinitionParser;
import quan.generator.XmlDefinitionParser;
import quan.generator.config.ConfigDefinition;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 加载配置数据
 * Created by quanchangnai on 2019/7/30.
 */
public class ConfigLoader {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected DefinitionParser definitionParser = new XmlDefinitionParser();

    //配置的定义文件所在目录
    private List<String> definitionPaths;

    //配置表所在目录
    private String tablePath;

    private String packagePrefix;

    private String enumPackagePrefix;

    private Map<String, ConfigReader> readers = new HashMap<>();

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

    /**
     * 全量加载配置
     */
    public void load() {
        if (!ClassDefinition.getAll().isEmpty()) {
            throw new ConfigException("重加载请调用reload方法");
        }
        definitionParser.setDefinitionPaths(definitionPaths);
        definitionParser.setPackagePrefix(packagePrefix);
        definitionParser.setEnumPackagePrefix(enumPackagePrefix);

        try {
            definitionParser.parse();
        } catch (Exception e) {
            String error = String.format("读取[%s]异常:%s", definitionPaths, e.getMessage());
//            logger.error(error, e);
            throw new ConfigException(error);
        }

        List<String> validatedErrors = ClassDefinition.getValidatedErrors();
        if (!validatedErrors.isEmpty()) {
            ConfigException configException = new ConfigException(String.format("解析定义文件%s共发现%d条错误。", definitionPaths, validatedErrors.size()));
            configException.addErrors(validatedErrors);
            throw configException;
        }

        List<String> errors = new ArrayList<>();
        Set<ConfigDefinition> loadConfigs = new HashSet<>(ConfigDefinition.getTableConfigs().values());
        for (ConfigDefinition configDefinition : loadConfigs) {
            errors.addAll(load(configDefinition));
        }

        if (!errors.isEmpty()) {
            throw new ConfigException(errors);
        }
    }

    private List<String> load(ConfigDefinition configDefinition) {
        List<String> errors = new ArrayList<>();
        List<Config> configs = new ArrayList<>();

        for (String table : configDefinition.getTables()) {
            ConfigReader configReader = readers.get(table);
            if (configReader == null) {
                configReader = new CSVConfigReader(tablePath, table, ConfigDefinition.getTableConfigs().get(table));
                readers.put(table, configReader);
            }
            try {
                configs.addAll(configReader.readObjects());
            } catch (ConfigException e) {
                errors.addAll(e.getErrors());
            }
        }

        errors.addAll(index(configDefinition, configs));

        return errors;
    }

    @SuppressWarnings({"unchecked"})
    private List<String> index(ConfigDefinition configDefinition, List<Config> configs) {
        List<String> errors = new ArrayList<>();

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
//            logger.error(error, e);
            return errors;
        }

        try {
            errors.addAll((List<String>) indexMethod.invoke(null, configs));
        } catch (Exception e) {
            String error = String.format("调用配置[%s]的索引方法出错:%s", configDefinition.getName(), e.getMessage());
            errors.add(error);
//            logger.error(error, e);
        }

        return errors;

    }

    /**
     * 重加载配置
     *
     * @param tables 表格名字列表
     */
    public void reload(List<String> tables) {
        List<String> errors = new ArrayList<>();
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
            errors.addAll(load(configDefinition));
        }

        if (!errors.isEmpty()) {
            throw new ConfigException(errors);
        }
    }

}
