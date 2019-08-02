package quan.config;

/**
 * Created by quanchangnai on 2019/8/2.
 */
public class TestConfigChecker1 implements ConfigChecker {

    @Override
    public void checkConfig() {
        throw new ConfigException("TestConfigChecker1检查错误测试");
    }


}
