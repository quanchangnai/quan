package quan.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * @author quanchangnai
 */
public class Promise<R> implements Comparable<Promise<R>> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private long callId;

    private long callTime = System.currentTimeMillis();

    private Consumer<R> resultHandler;

    private Runnable errorHandler;

    private Runnable timeoutHandler;

    protected Promise(long callId) {
        this.callId = callId;
    }

    public long getCallId() {
        return callId;
    }

    @SuppressWarnings("unchecked")
    void handleResult(Object result) {
        if (resultHandler != null) {
            try {
                resultHandler.accept((R) result);
            } catch (Throwable e) {
                logger.error("", e);
            }
        }
    }

    void handleError(String error) {
        logger.error("调用[{}]在远程服务器上执行异常，{}", callId, error);
        if (errorHandler != null) {
            try {
                errorHandler.run();
            } catch (Throwable e) {
                logger.error("", e);
            }
        }
    }

    boolean isTimeout() {
        //暂定30秒超时
        return System.currentTimeMillis() - callTime > 30000;
    }

    void handleTimeout() {
        if (timeoutHandler == null) {
            logger.error("调用[{}]等待响应超时", callId);
            return;
        }

        try {
            timeoutHandler.run();
        } catch (Throwable e) {
            logger.error("", e);
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

    /**
     * 设置异步调用超时返回的处理器
     */
    public void timeout(Runnable handler) {
        this.timeoutHandler = handler;
    }

    @Override
    public int compareTo(Promise<R> other) {
        return Long.compare(this.callTime, other.callTime);
    }

}
