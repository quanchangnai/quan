package quan.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 用于监听远程方法的调用结果等
 *
 * @author quanchangnai
 */
@SuppressWarnings("unchecked")
public class Promise<R> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private long callId;

    private String callee;

    private long time = System.currentTimeMillis();

    private R result;

    private Exception exception;

    private Object resultHandler;

    private Object exceptionHandler;

    private Object timeoutHandler;

    private Promise helpPromise;

    private boolean finished;

    protected Promise() {
    }

    protected Promise(long callId, String callee) {
        this.callId = callId;
        this.callee = callee;
    }

    protected long getCallId() {
        return callId;
    }

    protected void setCallId(long callId) {
        this.callId = callId;
    }

    protected long getTime() {
        return time;
    }

    protected boolean isFinished() {
        return finished;
    }

    protected Promise getHelpPromise() {
        if (helpPromise == null) {
            helpPromise = new Promise();
        }
        return helpPromise;
    }

    protected void setResult(R result) {
        this.finished = true;
        this.result = result;

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

    protected R getResult() {
        return result;
    }

    protected void setException(Exception exception) {
        this.finished = true;
        this.exception = exception;
        if (exception instanceof CallException) {
            CallException callException = (CallException) exception;
            callException.setCallId(callId);
            callException.setCallee(callee);
        }

        if (exceptionHandler == null) {
            logger.error("", exception);
            return;
        }

        if (exceptionHandler instanceof Consumer) {
            ((Consumer) exceptionHandler).accept(exception);
        } else {
            Promise<?> handlerPromise = (Promise<?>) ((Supplier) exceptionHandler).get();
            if (handlerPromise != null) {
                handlerPromise.delegate(helpPromise);
            }
        }
    }

    protected String getExceptionStr() {
        if (exception == null) {
            return null;
        } else {
            return exception.toString();
        }
    }

    protected boolean isExpired() {
        //暂定10秒过期
        return System.currentTimeMillis() - time > 10000;
    }

    protected void setTimeout() {
        this.finished = true;
        if (timeoutHandler == null) {
            if (callId > 0 && callee != null) {
                logger.error("调用[{}]方法[{}]等待超时", callId, callee);
            } else {
                logger.error("{}等待超时", getClass().getSimpleName());
            }
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

    private void delegate(Promise promise) {
        promise.callId = this.callId;
        promise.callee = this.callee;
        promise.time = this.time;
        this.then(promise::setResult);
        this.except(promise::setException);
        this.timeout(promise::setTimeout);
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
    public void except(Consumer<Exception> handler) {
        checkHandler(this.exceptionHandler, handler);
        this.exceptionHandler = handler;
    }

    /**
     * 设置异步调用异常返回时的处理器
     *
     * @see #then(Function)
     */
    public <R2> Promise<R2> except(Function<String, Promise<R2>> handler) {
        checkHandler(this.exceptionHandler, handler);
        this.exceptionHandler = handler;
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

}
