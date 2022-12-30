package quan.config.loader;

import org.apache.commons.lang3.StringUtils;
import quan.config.Config;
import quan.config.TableType;
import quan.config.ValidatedException;
import quan.config.reader.ConfigReader;
import quan.config.reader.JsonConfigReader;
import quan.util.CommonUtils;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 直接加载JSON格式配置的加载器<br/>
 * 配置文件名必须是[不含前缀的配置包名.类名.json]
 */
public class JsonConfigLoader extends ConfigLoader {

    //包名前缀，在没有配置定义加载时需要
    private String packagePrefix = "";

    //配置全类名:所有子孙配置[包名.类名]，包含自己
    private final Map<String, Set<String>> configDescendants = new HashMap<>();

    {
        tableType = TableType.json;
    }

    public JsonConfigLoader(String tablePath) {
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
        Set<File> tableFiles = CommonUtils.listFiles(new File(tablePath), tableType.name());
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
    public Set<Class<? extends Config>> reloadByConfigName(Collection<String> configNames) {
        checkReload();
        validatedErrors.clear();

        LinkedHashMap<String, ConfigReader> reloadReaders = new LinkedHashMap<>();

        for (String configName : configNames) {
            File jsonFile = new File(tablePath, configName + ".json");
            if (!jsonFile.exists()) {
                validatedErrors.add(String.format("重加载[%s]失败，不存在该配置文件", configName));
                continue;
            }
            ConfigReader configReader = readers.get(configName);
            if (configReader == null) {
                validatedErrors.add(String.format("重加载[%s]失败，对应配置从未被加载", configName));
                continue;
            }

            configReader.clear();
            reloadReaders.put(configName, configReader);

            String configFullName = StringUtils.isBlank(packagePrefix) ? configName : packagePrefix + "." + configName;
            load(configFullName, configDescendants.get(configFullName), true);
        }

        for (ConfigReader reloadReader : reloadReaders.values()) {
            List<String> errors = reloadReader.getValidatedErrors();
            if (supportValidate()) {
                this.validatedErrors.addAll(errors);
            }
        }


        Set<Class<? extends Config>> reloadedConfigs = reloadReaders.values().stream().map(r -> r.getPrototype().getClass()).collect(Collectors.toSet());

        callListeners(reloadedConfigs, true);

        if (!validatedErrors.isEmpty()) {
            throw new ValidatedException(validatedErrors);
        }

        return reloadedConfigs;
    }

    @Override
    protected ConfigReader createReader(String table) {
        File tableFile = new File(tablePath, table + "." + tableType);
        String configFullName = StringUtils.isBlank(packagePrefix) ? table : packagePrefix + "." + table;
        return new JsonConfigReader(tableFile, configFullName);
    }

}
