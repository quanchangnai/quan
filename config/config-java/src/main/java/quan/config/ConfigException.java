package quan.config;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * Created by quanchangnai on 2019/7/31.
 */
public class ConfigException extends RuntimeException {

    private LinkedHashSet<String> errors = new LinkedHashSet<>();

    public ConfigException() {
    }

    public ConfigException(String error) {
        errors.add(error);
    }

    public ConfigException(Collection<String> errors) {
        this.errors.addAll(errors);
    }

    public LinkedHashSet<String> getErrors() {
        return errors;
    }

    public ConfigException addError(String error) {
        errors.add(error);
        return this;
    }

    public ConfigException addErrors(Collection<String> errors) {
        this.errors.addAll(errors);
        return this;
    }

    @Override
    public String getMessage() {
        return errors.toString();
    }

}
