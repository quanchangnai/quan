package quan.config;

import quan.config.item.ItemConfig;

/**
 * Created by quanchangnai on 2019/8/2.
 */
public class TestConfigChecker1 implements ConfigChecker {

    @Override
    public void checkConfig() {
        String error = "TestConfigChecker1检查错误测试";
        ItemConfig itemConfig = ItemConfig.getById(1);
        if (itemConfig != null) {
            error += ":" + itemConfig.getName();
        }
        throw new ConfigException(error);
    }


}
