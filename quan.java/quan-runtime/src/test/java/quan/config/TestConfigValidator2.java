package quan.config;

import java.util.List;

/**
 * Created by quanchangnai on 2019/8/2.
 */
public enum TestConfigValidator2 implements ConfigValidator {

    instance;

    @Override
    public void validateConfig(List<String> errors) {
        throw new RuntimeException("TestConfigValidator2校验测试");
    }

}
