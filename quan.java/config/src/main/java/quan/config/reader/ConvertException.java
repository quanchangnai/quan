package quan.config.reader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by quanchangnai on 2019/8/7.
 */
class ConvertException extends RuntimeException {

    private ErrorType errorType;

    private List<String> params = new ArrayList<>();

    public ConvertException(ErrorType errorType, String... params) {
        this.errorType = errorType;
        this.params.addAll(Arrays.asList(params));
    }

    public ConvertException(ErrorType errorType, Throwable cause, String... params) {
        super(cause);
        this.errorType = errorType;
        this.params.addAll(Arrays.asList(params));
    }


    public ConvertException(ErrorType errorType, List<String> params) {
        this.errorType = errorType;
        this.params.addAll(params);
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public List<String> getParams() {
        return params;
    }

    public String getParam(int i) {
        return params.get(i);
    }

    @Override
    public String getMessage() {
        return "配置转换错误类型:" + errorType.name();
    }

    public enum ErrorType {
        common,
        typeError,
        enumValue,
        enumName,
        setDuplicateValue,
        mapInvalidKey,
        mapInvalidValue,
        mapDuplicateKey,
    }
}