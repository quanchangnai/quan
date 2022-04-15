package quan.rpc;

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
        this.worker.handleAsyncResult(this);
    }

    public R getResult() {
        return result;
    }

    public boolean isFinished() {
        return finished;
    }

}
