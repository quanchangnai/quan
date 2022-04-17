package quan.rpc;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * 服务方法可以先返回延迟结果，过一段时间后再使用它设置真实返回值
 *
 * @author quanchangnai
 */
public final class DelayedResult<R> {

    private long callId;

    private int originServerId;

    private Worker worker;

    private R result;

    private Consumer<R> resultHandler;

    private boolean finished;

    DelayedResult(Worker worker) {
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
            throw new IllegalStateException("不能重复设置延迟结果");
        }

        Worker current = Worker.current();
        if (current != this.worker) {
            this.worker.execute(() -> setResult(result));
            return;
        }

        this.result = result;
        this.finished = true;

        if (originServerId > 0) {
            this.worker.handleDelayedResult(this);
        }

        if (resultHandler != null) {
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

        resultHandler = handler;

        if (finished) {
            if (worker == Worker.current()) {
                resultHandler.accept(result);
            } else {
                worker.execute(() -> resultHandler.accept(result));
            }
        }

    }

    public boolean isFinished() {
        return finished;
    }

}
