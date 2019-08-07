package quan.config;

/**
 * Created by quanchangnai on 2019/8/7.
 */
public class ConvertException extends RuntimeException {

    private ErrorType errorType;

    public ConvertException(ErrorType errorType) {
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    @Override
    public String getMessage() {
        return "配置转换错误类型:" + errorType.name();
    }

    public enum ErrorType {
        enumValue,
        enumName
    }

}
