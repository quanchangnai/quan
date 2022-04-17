package quan.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 用于监听远程方法的调用结果、异常、超时
 *
 * @author quanchangnai
 */
@SuppressWarnings("unchecked")
public class Promise<R> implements Comparable<Promise<?>> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private long callId;

    private long time = System.currentTimeMillis();

    private Object resultHandler;

    private Object errorHandler;

    private Object timeoutHandler;

    private Promise helpPromise;

    protected Promise() {
    }

    protected Promise(long callId) {
        this.callId = callId;
    }

    protected long getCallId() {
        return callId;
    }


    protected Promise getHelpPromise() {
        if (helpPromise == null) {
            helpPromise = new Promise();
        }
        return helpPromise;
    }

    void handleResult(Object result) {
        if (resultHandler == null) {
            return;
        }

        if (resultHandler instanceof Consumer) {
            ((Consumer) resultHandler).accept(result);
        } else {
            Promise<?> handlerPromise = (Promise<?>) ((Function) resultHandler).apply(result);
            if (handlerPromise != null) {
                handlerPromise.delegate(helpPromise);
            }
        }
    }

    void handleError(String error) {
        if (errorHandler == null) {
            // TODO 调用的方法信息
            logger.error("调用[{}]在远程服务器上执行异常，{}", callId, error);
            return;
        }

        if (errorHandler instanceof Consumer) {
            ((Consumer) errorHandler).accept(error);
        } else {
            Promise<?> handlerPromise = (Promise<?>) ((Supplier) errorHandler).get();
            if (handlerPromise != null) {
                handlerPromise.delegate(helpPromise);
            }
        }
    }

    public boolean isTimeout() {
        //暂定10秒超时
        return System.currentTimeMillis() - time > 10000;
    }

    void handleTimeout() {
        if (timeoutHandler == null) {
            // TODO 调用的方法信息
            logger.error("调用[{}]等待响应超时", callId);
            return;
        }

        if (timeoutHandler instanceof Runnable) {
            try {
                ((Runnable) timeoutHandler).run();
            } catch (Throwable e) {
                logger.error("", e);
            }
        } else {
            Promise<?> handlerPromise = (Promise<?>) ((Supplier) timeoutHandler).get();
            if (handlerPromise != null) {
                handlerPromise.delegate(helpPromise);
            }
        }
    }

    private void delegate(Promise<?> promise) {
        this.then(promise::handleResult);
        this.error(promise::handleError);
        this.timeout(promise::handleTimeout);
    }

    private void checkHandler(Object oldHandler, Object newHandler) {
        Objects.requireNonNull(newHandler, "参数[handler]不能为空");
        if (oldHandler != null) {
            throw new IllegalStateException("参数[handler]不能重复设置");
        }
    }

    /**
     * 设置异步调用成功返回时的处理器
     */
    public void then(Consumer<R> handler) {
        checkHandler(this.resultHandler, handler);
        this.resultHandler = handler;
    }

    /**
     * 设置异步调用成功返回时的处理器
     *
     * @param handler handler执行逻辑可能还会调用远程[方法2]，此时handler可以返回[方法2]的结果Promise&lt;R2&gt;
     * @param <R2>    [方法2]的真实返回值类型
     * @return Promise&lt;R2&gt;,可以监听[方法2]的调用结果
     */
    public <R2> Promise<R2> then(Function<R, Promise<R2>> handler) {
        checkHandler(this.resultHandler, handler);
        this.resultHandler = handler;
        return getHelpPromise();
    }

    /**
     * 设置异步调用异常返回时的处理器
     */
    public void error(Consumer<String> handler) {
        checkHandler(this.errorHandler, handler);
        this.errorHandler = handler;
    }

    /**
     * 设置异步调用异常返回时的处理器
     *
     * @see #then(Function)
     */
    public <R2> Promise<R2> error(Function<String, Promise<R2>> handler) {
        checkHandler(this.errorHandler, handler);
        this.errorHandler = handler;
        return getHelpPromise();
    }

    /**
     * 设置异步调用超时返回的处理器
     */
    public void timeout(Runnable handler) {
        checkHandler(this.timeoutHandler, handler);
        this.timeoutHandler = handler;
    }

    /**
     * 设置异步调用超时返回的处理器
     *
     * @see #then(Function)
     */
    public <R2> Promise<R2> timeout(Supplier<Promise<R2>> handler) {
        checkHandler(this.timeoutHandler, handler);
        this.timeoutHandler = handler;
        return getHelpPromise();
    }

    @Override
    public int compareTo(Promise<?> other) {
        return Long.compare(this.time, other.time);
    }

}
