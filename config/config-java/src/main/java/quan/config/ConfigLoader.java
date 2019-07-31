package quan.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        if (!definitionParser.getClassDefinitions().isEmpty()) {
            logger.error("重加载请调用reload方法");
            return;
        }
        definitionParser.setDefinitionPaths(definitionPaths);
        definitionParser.setPackagePrefix(packagePrefix);
        definitionParser.setEnumPackagePrefix(enumPackagePrefix);

        try {
            definitionParser.parse();
        } catch (Exception e) {
            throw new ConfigException("解析配置定义文件[" + definitionPaths + "]出错", e);
        }

        Set<ConfigDefinition> loadConfigs = new HashSet<>(ConfigDefinition.getTableConfigs().values());
        for (ConfigDefinition configDefinition : loadConfigs) {
            load(configDefinition);
        }
    }

    private void load(ConfigDefinition configDefinition) {
        List<Config> configs = new ArrayList<>();
        for (String table : configDefinition.getTables()) {
            ConfigReader configReader = readers.get(table);
            if (configReader == null) {
                configReader = new CSVConfigReader(tablePath, table, ConfigDefinition.getTableConfigs().get(table));
                readers.put(table, configReader);
            }
            configs.addAll(configReader.readObjects());
        }

        String indexer = configDefinition.getFullName();
        if (configDefinition.getParentDefinition() != null) {
            indexer += "$self";
        }
        Method indexMethod;
        try {
            indexMethod = Class.forName(indexer).getMethod("index", List.class);
        } catch (Exception e) {
            throw new ConfigException("配置类[" + configDefinition.getName() + "]加载出错", e);
        }
        try {
            indexMethod.invoke(null, configs);
        } catch (Exception e) {
            throw new ConfigException("配置[" + configDefinition.getName() + "]索引数据出错", e);
        }
    }

    /**
     * 重加载配置
     *
     * @param tables 表格名字列表
     */
    public void reload(List<String> tables) {
        Set<ConfigDefinition> reloadConfigs = new HashSet<>();
        for (String table : tables) {
            ConfigReader configReader = readers.get(table);
            if (configReader == null) {
                logger.error("重加载[{}]出错，对应配置不存在", table);
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
            load(configDefinition);
        }
    }

}
