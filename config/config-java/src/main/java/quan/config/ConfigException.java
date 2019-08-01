package quan.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quanchangnai on 2019/7/31.
 */
public class ConfigException extends RuntimeException {

    private List<String> errors = new ArrayList<>();

    public ConfigException() {
    }

    public ConfigException(String error) {
        errors.add(error);
    }

    public ConfigException(List<String> errors) {
        this.errors.addAll(errors);
    }

    public List<String> getErrors() {
        return errors;
    }

    public ConfigException addError(String error) {
        errors.add(error);
        return this;
    }

    public ConfigException addErrors(List<String> errors) {
        this.errors.addAll(errors);
        return this;
    }

    @Override
    public String getMessage() {
        return errors.toString();
    }

}
