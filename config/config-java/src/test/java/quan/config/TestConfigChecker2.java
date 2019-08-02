package quan.config;

/**
 * Created by quanchangnai on 2019/8/2.
 */
public enum TestConfigChecker2 implements ConfigChecker {

    instance;

    @Override
    public void checkConfig() {
        throw new RuntimeException("TestConfigChecker2检查错误测试");
    }


}
