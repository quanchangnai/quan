package quan.config.load;

/**
 * 配置加载监听器
 */
@FunctionalInterface
public interface ConfigLoadListener {

    /**
     * 配置加载时调用
     *
     * @param reload 是不是重加载
     */
    void onLoad(boolean reload);

}
