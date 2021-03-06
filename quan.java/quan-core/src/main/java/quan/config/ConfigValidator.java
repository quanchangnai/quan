package quan.config;

import java.util.List;

/**
 * 自定义配置校验器
 * Created by quanchangnai on 2019/8/2.
 */
public interface ConfigValidator {

    /**
     * 校验配置
     *
     * @param errors 校验出来的错误信息加到这里
     */
    void validateConfig(List<String> errors);

}
