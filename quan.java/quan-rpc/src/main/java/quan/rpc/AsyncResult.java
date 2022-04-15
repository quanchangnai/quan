package quan.rpc;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * 异步(延迟)结果
 *
 * @author quanchangnai
 */
public final class AsyncResult<R> {

    private long callId;

    private int originServerId;

    private Worker worker;

    private R result;

    private Consumer<R> resultHandler;

    private boolean finished;

    AsyncResult(Worker worker) {
        this.worker = worker;
    }

    int getOriginServerId() {
        return originServerId;
    }

    void setOriginServerId(int originServerId) {
        this.originServerId = originServerId;
    }

    void setCallId(long callId) {
        this.callId = callId;
    }

    long getCallId() {
        return callId;
    }

    public void setResult(R result) {
        if (this.finished) {
            throw new IllegalStateException("不能重复设置异步结果");
        }

        Worker current = Worker.current();
        if (current != this.worker) {
            this.worker.execute(() -> setResult(result));
            return;
        }

        this.result = result;
        this.finished = true;
        if (originServerId > 0) {
            this.worker.handleAsyncResult(this);
        } else if (resultHandler != null) {
            resultHandler.accept(result);
        }
    }

    public R getResult() {
        return result;
    }

    /**
     * 设置不通过RPC而是通过正常途径调用时的结果处理器
     */
    public void then(Consumer<R> handler) {
        Objects.requireNonNull(handler, "参数[handler]不能为空");
        if (resultHandler != null) {
            throw new IllegalStateException("参数[handler]不能重复设置");
        }
        this.resultHandler = handler;
    }

    public boolean isFinished() {
        return finished;
    }

}
