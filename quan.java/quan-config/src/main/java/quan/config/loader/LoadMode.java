package quan.config.loader;

/**
 * 配置的加载模式
 */
public enum LoadMode {

    /**
     * 仅校验配置，不实际加载
     */
    ONLY_VALIDATE,

    /**
     * 仅加载不校验,会创建配置对象并加载到类的缓存里
     */
    ONLY_LOAD,

    /**
     * 校验并加载,会创建配置对象并加载到类的缓存里
     */
    VALIDATE_AND_LOAD
}
