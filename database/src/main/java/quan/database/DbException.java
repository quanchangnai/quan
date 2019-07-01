package quan.database;

/**
 * Created by quanchangnai on 2019/6/21.
 */
public class DbException extends RuntimeException {

    public DbException() {
        super();
    }

    public DbException(String message) {
        super(message);
    }

    public DbException(String message, Throwable cause) {
        super(message, cause);
    }

}
