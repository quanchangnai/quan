package quan.config;

/**
 * 加载模式
 * Created by quanchangnai on 2019/8/7.
 */
public enum LoadMode {

    /**
     * 仅校验配置，不实际加载
     */
    onlyValidate,

    /**
     * 仅加载不校验,会创建配置对象并加载到类的缓存里
     */
    onlyLoad,

    /**
     * 校验并加载,会创建配置对象并加载到类的缓存里
     */
    validateAndLoad
}
