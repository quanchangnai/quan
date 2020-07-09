package quan.config.loader;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.common.utils.ClassUtils;
import quan.common.utils.PathUtils;
import quan.config.Config;
import quan.config.ConfigValidator;
import quan.config.TableType;
import quan.config.ValidatedException;
import quan.config.reader.ConfigReader;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 抽象配置加载器
 * Created by quanchangnai on 2019/7/30.
 */
@SuppressWarnings({"unchecked"})
public abstract class ConfigLoader {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    //配置表所在目录
    protected String tablePath;

    //配置表类型
    protected TableType tableType;

    //加载模式，加载或者校验
    protected LoadMode loadMode = LoadMode.validateAndLoad;

    protected final Map<String, ConfigReader> readers = new HashMap<>();

    //自定义的配置校验器
    protected final Set<ConfigValidator> validators = new HashSet<>();

    protected final LinkedHashSet<String> validatedErrors = new LinkedHashSet<>();

    protected boolean loaded;

    public ConfigLoader(String tablePath) {
        Objects.requireNonNull(tablePath, "配置表路径不能为空");
        this.tablePath = PathUtils.toPlatPath(tablePath);
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

    public boolean needValidate() {
        return loadMode == LoadMode.onlyValidate || loadMode == LoadMode.validateAndLoad;
    }

    public boolean needLoad() {
        return loadMode == LoadMode.onlyLoad || loadMode == LoadMode.validateAndLoad;
    }

    /**
     * 设置自定义配置校验器所在的包并实例化校验器对象
     */
    public void setValidatorPackage(String packageName) {
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

    /**
     * 加载所有配置
     */
    public void loadAll() {
        if (loaded) {
            throw new IllegalStateException("配置已经加载了，重加载调用reloadXxx方法");
        }
        loaded = true;
        validatedErrors.clear();

        doLoadAll();

        if (needValidate()) {
            List<String> errors = new ArrayList<>();
            for (ConfigValidator validator : validators) {
                try {
                    validator.validateConfig(errors);
                    validatedErrors.addAll(errors);
                    errors.clear();
                } catch (ValidatedException e) {
                    validatedErrors.addAll(e.getErrors());
                } catch (Exception e) {
                    String error = String.format("配置错误:%s", e.getMessage());
                    validatedErrors.add(error);
                    logger.error("", e);
                }
            }
        }

        if (!validatedErrors.isEmpty()) {
            throw new ValidatedException(validatedErrors);
        }
    }

    protected abstract void doLoadAll();

    /**
     * 加载配置到类索引
     */
    protected void load(String configFullName, Collection<String> configTables, boolean validate) {
        if (!needLoad()) {
            return;
        }
        String configName = configFullName.substring(configFullName.lastIndexOf(".") + 1);
        List<Config> configs = new ArrayList<>();
        for (String table : configTables) {
            ConfigReader configReader = getReader(table);
            configs.addAll(configReader.readObjects());
        }

        Method loadMethod;
        try {
            loadMethod = Class.forName(configFullName + "$self").getMethod("load", List.class);
        } catch (Exception e1) {
            try {
                loadMethod = Class.forName(configFullName).getMethod("load", List.class);
            } catch (Exception e2) {
                logger.error("加载配置[{}]类出错，配置类[{}]不存在或者没有加载方法", configName, configFullName);
                return;
            }
        }

        try {
            List<String> indexErrors = (List<String>) loadMethod.invoke(null, configs);
            if (needValidate() && validate) {
                validatedErrors.addAll(indexErrors);
            }
        } catch (Exception e) {
            logger.error("加载配置[{}]类出错，调用配置类[{}]的索引方法出错", configName, configFullName, e);
        }
    }

    /**
     * 重加载全部配置，校验依赖
     */
    public void reloadAll() {
        checkReload();
        loaded = false;
        for (ConfigReader reader : readers.values()) {
            reader.clear();
        }

        loadAll();
    }

    protected void checkReload() {
        if (!needLoad()) {
            throw new IllegalStateException("配置加载器仅支持校验，不能重加载");
        }
        if (!loaded) {
            throw new IllegalStateException("配置没有加载过，不能重加载");
        }
    }

    /**
     * 通过配置类名重加载
     */
    public abstract void reloadByConfigName(Collection<String> configNames);

    /**
     * @see #reloadByConfigName(Collection)
     */
    public void reloadByConfigName(String... configNames) {
        reloadByConfigName(Arrays.asList(configNames));
    }

    protected ConfigReader getReader(String table) {
        ConfigReader configReader = readers.get(table);
        if (configReader == null) {
            configReader = createReader(table);
            readers.put(table, configReader);
        }
        return configReader;
    }

    protected abstract ConfigReader createReader(String table);

}
