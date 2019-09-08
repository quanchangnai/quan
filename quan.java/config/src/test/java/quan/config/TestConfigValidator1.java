package quan.config;

import quan.config.common.ItemConstant;
import quan.config.item.ItemConfig;

/**
 * Created by quanchangnai on 2019/8/2.
 */
public class TestConfigValidator1 implements ConfigValidator {

    @Override
    public void validateConfig() {
        String error = "TestConfigValidator1校验测试";
        ItemConfig itemConfig = ItemConfig.getById(1);
        if (itemConfig != null) {
            System.err.println("itemConfig.toJson():" + itemConfig.toJson());
            error += ":" + itemConfig.getName();
        }
        System.err.println("ItemConstant.constant2()=" + ItemConstant.constant2());
        throw new ValidatedException(error);
    }


}
