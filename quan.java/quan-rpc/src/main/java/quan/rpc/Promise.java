package quan.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * @author quanchangnai
 */
public class Promise<R> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private long callId;

    private R result;

    private Consumer<R> resultHandler;

    private Runnable errorHandler;

    protected Promise(long callId) {
        this.callId = callId;
    }

    @SuppressWarnings("unchecked")
    void setResult(Object result, boolean error) {
        if (error) {
            logger.error("调用[{}]在远程服务器上执行异常", callId);
            if (errorHandler != null) {
                errorHandler.run();
            }
        } else {
            this.result = (R) result;
            if (resultHandler != null) {
                resultHandler.accept(this.result);
            }
        }
    }

    /**
     * 设置异步调用成功返回时的处理器
     */
    public void then(Consumer<R> handler) {
        this.resultHandler = handler;
    }

    /**
     * 设置异步调用异常返回时的处理器
     */
    public void error(Runnable handler) {
        this.errorHandler = handler;
    }

}
