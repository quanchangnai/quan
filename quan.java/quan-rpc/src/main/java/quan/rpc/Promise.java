package quan.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * @author quanchangnai
 */
public class Promise<R> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private R result;

    private Consumer<R> resultHandler;

    protected Promise() {
    }

    void setResult(Object result) {
        this.result = (R) result;
        if (resultHandler != null) {
            resultHandler.accept(this.result);
        }
    }

    public void then(Consumer<R> handler) {
        this.resultHandler = handler;
    }

}
