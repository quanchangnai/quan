package quan.config;

import java.util.List;

/**
 * Created by quanchangnai on 2019/8/2.
 */
public enum ConfigValidator2 implements ConfigValidator {

    instance;

    @Override
    public void validateConfig(List<String> errors) {
        throw new RuntimeException("ConfigValidator2校验测试");
    }

}
