package quan.config;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * 配置校验异常
 * Created by quanchangnai on 2019/7/31.
 */
public class ValidatedException extends RuntimeException {

    private LinkedHashSet<String> errors = new LinkedHashSet<>();

    public ValidatedException() {
    }

    public ValidatedException(String error) {
        errors.add(error);
    }

    public ValidatedException(Collection<String> errors) {
        this.errors.addAll(errors);
    }

    public LinkedHashSet<String> getErrors() {
        return errors;
    }

    public ValidatedException addError(String error) {
        errors.add(error);
        return this;
    }

    public ValidatedException addErrors(Collection<String> errors) {
        this.errors.addAll(errors);
        return this;
    }

    @Override
    public String getMessage() {
        return errors.toString();
    }

}
