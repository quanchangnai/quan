package quan.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * @author quanchangnai
 */
public class Promise<R> {

    private static Logger logger = LoggerFactory.getLogger(Promise.class);

    /**
     * 调用ID
     */
    private int callId;

    private R result;

    private Consumer<R> resultHandler;

    protected Promise(int callId) {
        this.callId = callId;
    }

    protected int getCallId() {
        return callId;
    }

    void setResult(Object result) {
        this.result = (R) result;
        if (resultHandler != null) {
            resultHandler.accept(this.result);
        }
    }

    public R await() {
        return result;
    }

    public void then(Consumer<R> handler) {
        this.resultHandler = handler;
    }

}
