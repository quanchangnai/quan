package quan.config;

import quan.config.common.ItemConstant;
import quan.config.item.ItemConfig;
import quan.config.item.ItemIds;

import java.util.List;

/**
 * Created by quanchangnai on 2019/8/2.
 */
public class ConfigValidator1 implements ConfigValidator {

    @Override
    public void validateConfig(List<String> errors) {
        String error = "ConfigValidator1校验测试";
        ItemConfig itemConfig = ItemConfig.getById(1);
        if (itemConfig != null) {
            System.err.println("itemConfig.toJson():" + itemConfig.toJson());
            error += ":" + itemConfig.getName();
        }
        System.err.println("ItemIds.item2=" + ItemIds.item2.value());
        System.err.println("ItemConstant.constant2()=" + ItemConstant.constant2());
        errors.add(error);
        throw new ValidatedException(error);
    }


}
