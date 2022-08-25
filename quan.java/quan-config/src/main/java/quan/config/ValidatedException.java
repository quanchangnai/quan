package quan.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * 配置校验出来的异常
 */
public class ValidatedException extends RuntimeException {

    private LinkedHashSet<String> errors = new LinkedHashSet<>();

    public ValidatedException() {
    }

    public ValidatedException(String... error) {
        this.errors.addAll(Arrays.asList(error));
    }

    public ValidatedException(Collection<String> errors) {
        this.errors.addAll(errors);
    }

    public LinkedHashSet<String> getErrors() {
        return errors;
    }

    public ValidatedException addError(String... error) {
        this.errors.addAll(Arrays.asList(error));
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
