package quan.config.loader;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.config.Config;
import quan.config.ConfigValidator;
import quan.config.TableType;
import quan.config.ValidatedException;
import quan.config.reader.ConfigReader;
import quan.util.ClassUtils;
import quan.util.CommonUtils;

import java.util.*;
import java.util.function.Function;

/**
 * 配置加载器
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class ConfigLoader {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected static Map<Class<? extends Config>, Function<List, List>> loadFunctions = new HashMap<>();

    //配置表所在目录
    protected String tablePath;

    //配置表类型
    protected TableType tableType;

    //加载模式，加载或者校验
    protected LoadMode loadMode = LoadMode.VALIDATE_AND_LOAD;

    protected final Map<String, ConfigReader> readers = new HashMap<>();

    //自定义的配置校验器
    protected final Set<ConfigValidator> validators = new HashSet<>();

    protected final LinkedHashSet<String> validatedErrors = new LinkedHashSet<>();

    protected boolean loaded;

    //配置加载监听器
    protected Map<Class<? extends Config>, Set<ConfigLoadListener>> listeners = new HashMap<>();

    public ConfigLoader(String tablePath) {
        Objects.requireNonNull(tablePath, "配置表路径不能为空");
        this.tablePath = CommonUtils.toPlatPath(tablePath);
    }

    public static void registerLoadFunction(Class<? extends Config> clazz, Function<List, List> function) {
        loadFunctions.put(clazz, function);
    }

    public LoadMode getLoadMode() {
        return loadMode;
    }

    public void setTableType(TableType tableType) {
        Objects.requireNonNull(tableType, "配置表类型不能为空");
        this.tableType = tableType;
    }

    public TableType getTableType() {
        return tableType;
    }

    public boolean supportValidate() {
        return loadMode == LoadMode.ONLY_VALIDATE || loadMode == LoadMode.VALIDATE_AND_LOAD;
    }

    public boolean supportLoad() {
        return loadMode == LoadMode.ONLY_LOAD || loadMode == LoadMode.VALIDATE_AND_LOAD;
    }

    /**
     * 初始化配置校验器
     *
     * @param packageName 校验器所在的包名
     */
    public void initValidators(String packageName) {
        if (StringUtils.isBlank(packageName)) {
            return;
        }

        validators.clear();
        Set<Class<?>> validatorClasses = ClassUtils.loadClasses(packageName, ConfigValidator.class);

        for (Class<?> validatorClass : validatorClasses) {
            if (!validatorClass.isEnum()) {
                try {
                    validators.add((ConfigValidator) validatorClass.getConstructor().newInstance());
                } catch (Exception e) {
                    logger.error("实例化配置校验器[{}]失败", validatorClass, e);
                }
            } else if (validatorClass.getEnumConstants().length > 0) {
                validators.add((ConfigValidator) validatorClass.getEnumConstants()[0]);
            }
        }
    }


    protected void loadAll(boolean reload) {
        if (reload) {
            checkReload();
            loaded = false;

            for (ConfigReader reader : readers.values()) {
                reader.clear();
            }
        }

        if (loaded) {
            throw new IllegalStateException("配置已经加载了，重加载调用reloadXxx方法");
        }

        loaded = true;
        validatedErrors.clear();

        doLoadAll();

        if (supportValidate()) {
            List<String> errors = new ArrayList<>();
            for (ConfigValidator validator : validators) {
                try {
                    validator.validateConfig(errors);
                    validatedErrors.addAll(errors);
                    errors.clear();
                } catch (ValidatedException e) {
                    validatedErrors.addAll(e.getErrors());
                } catch (Exception e) {
                    String error = String.format("配置校验报错:%s", e.getMessage());
                    validatedErrors.add(error);
                    logger.error(error, e);
                }
            }
        }

        callListeners(null, reload);

        if (!validatedErrors.isEmpty()) {
            throw new ValidatedException(validatedErrors);
        }
    }


    /**
     * 加载所有配置
     */
    public void loadAll() {
        loadAll(false);
    }

    protected abstract void doLoadAll();

    /**
     * 加载配置到类索引
     */
    protected void load(String configFullName, Collection<String> configTables, boolean validate) {
        if (!supportLoad()) {
            return;
        }

        String configName = configFullName.substring(configFullName.lastIndexOf(".") + 1);
        List<Config> configs = new ArrayList<>();
        for (String table : configTables) {
            ConfigReader configReader = getReader(table);
            configs.addAll(configReader.getConfigs());
        }

        Function<List, List> loadFunction;
        try {
            loadFunction = loadFunctions.get(Class.forName(configFullName));
        } catch (Exception e) {
            logger.error("加载配置[{}]类出错，配置类[{}]不存在", configName, configFullName);
            return;
        }

        if (loadFunction == null) {
            logger.error("加载配置[{}]类出错，配置类[{}]的load方法不存在", configName, configFullName);
            return;
        }

        try {
            List<String> loadErrors = (List<String>) loadFunction.apply(configs);
            if (supportValidate() && validate) {
                validatedErrors.addAll(loadErrors);
            }
        } catch (Exception e) {
            logger.error("加载配置[{}]类出错，调用配置类[{}]的load方法出错", configName, configFullName, e);
        }
    }

    /**
     * 重加载全部配置，并且会校验依赖
     */
    public void reloadAll() {
        loadAll(true);
    }

    protected void checkReload() {
        if (!supportLoad()) {
            throw new UnsupportedOperationException("配置加载器仅支持校验配置，不支持重加载");
        }
        if (!loaded) {
            throw new IllegalStateException("配置加载器还没有加载过配置，不能重加载");
        }
    }

    /**
     * 通过配置的[不含前缀的包名.类名]重加载
     *
     * @return 重加载配置成功的配置类
     */
    public abstract Set<Class<? extends Config>> reloadByConfigName(Collection<String> configNames);

    /**
     * @see #reloadByConfigName(Collection)
     */
    public void reloadByConfigName(String... configNames) {
        reloadByConfigName(Arrays.asList(configNames));
    }

    public ConfigReader getReader(String table) {
        ConfigReader configReader = readers.get(table);
        if (configReader == null) {
            configReader = createReader(table);
            readers.put(table, configReader);
        }
        return configReader;
    }

    protected abstract ConfigReader createReader(String table);


    /**
     * 注册加载指定配置的监听器
     *
     * @param configs  监听的待加载的配置类
     * @param listener 配置加载监听器
     */
    public void registerListener(Collection<Class<? extends Config>> configs, ConfigLoadListener listener) {
        Objects.requireNonNull(listener, "配置加载监听器不能为空");

        if (configs == null) {
            //监听任意配置类
            listeners.computeIfAbsent(null, k -> new HashSet<>()).add(listener);
            return;
        }

        for (Class<? extends Config> config : configs) {
            if (config != null) {
                listeners.computeIfAbsent(config, k -> new HashSet<>()).add(listener);
            }
        }
    }

    /**
     * 注册加载指定配置的监听器
     *
     * @see #registerListener(Collection, ConfigLoadListener)
     */
    public void registerListener(Class<? extends Config> config, ConfigLoadListener listener) {
        registerListener(Collections.singleton(config), listener);
    }

    /**
     * 注册加载任意配置的监听器
     *
     * @see #registerListener(Collection, ConfigLoadListener)
     */
    public void registerListener(ConfigLoadListener listener) {
        registerListener((Collection<Class<? extends Config>>) null, listener);
    }

    /**
     * 调用配置加载监听器
     */
    protected void callListeners(Set<Class<? extends Config>> configs, boolean reload) {
        Set<ConfigLoadListener> targetListeners = new HashSet<>();

        if (configs == null) {
            for (Set<ConfigLoadListener> set : listeners.values()) {
                targetListeners.addAll(set);
            }
        } else {
            for (Class<? extends Config> config : configs) {
                if (listeners.containsKey(config)) {
                    targetListeners.addAll(listeners.get(config));
                }
            }
        }

        for (ConfigLoadListener listener : targetListeners) {
            try {
                listener.onLoad(reload);
            } catch (Exception e) {
                logger.error("配置加载监听器执行出错", e);
            }
        }
    }

}
