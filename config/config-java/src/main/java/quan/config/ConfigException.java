package quan.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by quanchangnai on 2019/7/31.
 */
public class ConfigException extends RuntimeException {

    private List<String> errors = new ArrayList<>();

    public ConfigException(String error) {
        this.errors.add(error);
    }

    public ConfigException(String error, Throwable cause) {
        this(Collections.singletonList(error), cause);
    }

    public ConfigException(List<String> errors, Throwable cause) {
        super(cause);
        this.errors.addAll(errors);
        if (cause instanceof ConfigException) {
            this.errors.addAll(((ConfigException) cause).getErrors());
        }
    }

    public ConfigException(Throwable cause) {
        if (cause instanceof ConfigException) {
            this.errors.addAll(((ConfigException) cause).getErrors());
        } else {
            this.errors.add(cause.getMessage());
        }
    }

    public List<String> getErrors() {
        return errors;
    }

    @Override
    public String getMessage() {
        return errors.toString();
    }

}
