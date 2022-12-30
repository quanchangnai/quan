package quan.config;

import java.util.*;

/**
 * 配置校验出来的异常
 */
public class ValidatedException extends RuntimeException {

    private final LinkedHashSet<String> errors = new LinkedHashSet<>();

    private String message;

    public ValidatedException() {
    }

    public ValidatedException(String... error) {
        this.errors.addAll(Arrays.asList(error));
    }

    public ValidatedException(Collection<String> errors) {
        this.errors.addAll(errors);
    }

    public Set<String> getErrors() {
        return Collections.unmodifiableSet(errors);
    }

    public ValidatedException addError(String... error) {
        this.errors.addAll(Arrays.asList(error));
        message = null;
        return this;
    }

    public ValidatedException addErrors(Collection<String> errors) {
        this.errors.addAll(errors);
        message = null;
        return this;
    }

    @Override
    public String getMessage() {
        if (message != null) {
            return message;
        }

        StringBuilder sb = new StringBuilder();

        if (errors.size() > 1) {
            sb.append("总共有").append(errors.size()).append("条错误");
            for (String error : errors) {
                sb.append("\n\t").append(error);
            }
        } else if (errors.size() == 1) {
            sb.append(errors.stream().findFirst().get());
        } else {
            sb.append("没有错误");
        }

        message = sb.toString();

        return message;
    }

}
