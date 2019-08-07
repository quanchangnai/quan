package quan.config;

/**
 * Created by quanchangnai on 2019/8/7.
 */
public enum LoadType {
    /**
     * 仅校验配置
     */
    onlyValidate,
    /**
     * 仅加载,会创建配置对象并加载到类的缓存里
     */
    onlyLoad,
    /**
     * 校验并加载,会创建配置对象并加载到类的缓存里
     */
    validateAndLoad
}
