package quan.config;

import java.util.List;

/**
 * 配置校验器，可以自定义校验逻辑
 */
public interface ConfigValidator {

    /**
     * 校验配置
     *
     * @param errors 校验出来的错误信息可以加到这里
     * @throws ValidatedException 校验出来的错误信息也可以通过异常抛出
     */
    void validateConfig(List<String> errors);

}
