package quan.config.loader;

import org.apache.commons.lang3.StringUtils;
import quan.common.PathUtils;
import quan.config.Config;
import quan.config.TableType;
import quan.config.reader.ConfigReader;
import quan.config.reader.JsonConfigReader;

import java.io.File;
import java.util.*;

/**
 * 没有配置定义时加载配置<br/>
 * 配置文件名必须是[不含前缀的配置包名.类名.格式]，目前仅支持JSON格式
 * Created by quanchangnai on 2019/8/23.
 */
public class WithoutDefinitionConfigLoader extends ConfigLoader {

    //包名前缀，在没有配置定义加载时需要
    private String packagePrefix = "";

    //配置全类名:所有子孙配置[包名.类名]，包含自己
    private final Map<String, Set<String>> configDescendants = new HashMap<>();

    {
        tableType = TableType.json;
    }

    public WithoutDefinitionConfigLoader(String tablePath) {
        super(tablePath);
    }

    public void setPackagePrefix(String packagePrefix) {
        this.packagePrefix = packagePrefix;
    }

    @Override
    public void setTableType(TableType tableType) {
    }

    @Override
    protected void doLoadAll() {
        initConfigDescendants();
        for (String configFullName : configDescendants.keySet()) {
            load(configFullName, configDescendants.get(configFullName), true);
        }

        //格式错误
        for (ConfigReader reader : readers.values()) {
            validatedErrors.addAll(reader.getValidatedErrors());
        }
    }

    /**
     * 初始化配置类对应的后代子孙类，包含自己
     */
    private void initConfigDescendants() {
        Set<File> tableFiles = PathUtils.listFiles(new File(tablePath), tableType.name());
        List<Config> configs = new ArrayList<>();

        for (File tableFile : tableFiles) {
            //文件名:[不含前缀的配置包名.类名.json]
            String configNameWithPackage = tableFile.getName().substring(0, tableFile.getName().lastIndexOf("."));
            String configFullName = StringUtils.isBlank(packagePrefix) ? configNameWithPackage : packagePrefix + "." + configNameWithPackage;

            configDescendants.computeIfAbsent(configFullName, k -> new HashSet<>());

            ConfigReader reader = getReader(configNameWithPackage);
            Config prototype = reader.getPrototype();
            if (prototype != null) {
                configs.add(prototype);
            }
        }

        for (Config config1 : configs) {
            for (Config config2 : configs) {
                if (!config1.getClass().isAssignableFrom(config2.getClass())) {
                    continue;
                }
                String config2NameWithPackage = config2.getClass().getName();
                if (!StringUtils.isBlank(packagePrefix)) {
                    config2NameWithPackage = config2.getClass().getName().substring(packagePrefix.length() + 1);
                }
                configDescendants.get(config1.getClass().getName()).add(config2NameWithPackage);
            }
        }
    }

    @Override
    public void reloadByConfigName(Collection<String> configNames) {
        checkReload();
        validatedErrors.clear();

        Set<File> tableFiles = PathUtils.listFiles(new File(tablePath), tableType.name());
        LinkedHashMap<String, ConfigReader> reloadReaders = new LinkedHashMap<>();

        for (File tableFile : tableFiles) {
            String configNameWithPackage = tableFile.getName().substring(0, tableFile.getName().lastIndexOf("."));
            String configFullName = StringUtils.isBlank(packagePrefix) ? configNameWithPackage : packagePrefix + "." + configNameWithPackage;
            String configName = configNameWithPackage.substring(configNameWithPackage.lastIndexOf(".") + 1);

            if (!configNames.contains(configName)) {
                continue;
            }

            ConfigReader configReader = readers.get(configNameWithPackage);
            if (configReader == null) {
                logger.error("重加载[{}]失败，对应配置从未被加载", configName);
                continue;
            }
            configReader.clear();
            reloadReaders.put(configName, configReader);

            load(configFullName, configDescendants.get(configFullName), true);
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

    @Override
    protected ConfigReader createReader(String table) {
        File tableFile = new File(tablePath, table + "." + tableType);
        String configFullName = StringUtils.isBlank(packagePrefix) ? table : packagePrefix + "." + table;
        return new JsonConfigReader(tableFile, configFullName);
    }

}
