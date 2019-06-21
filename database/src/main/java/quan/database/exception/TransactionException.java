package quan.database.exception;

/**
 * Created by quanchangnai on 2019/6/21.
 */
public class TransactionException extends RuntimeException {

    public TransactionException() {
        super();
    }

    public TransactionException(String message) {
        super(message);
    }

    public TransactionException(String message, Throwable cause) {
        super(message, cause);
    }

}
