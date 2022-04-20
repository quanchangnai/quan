package quan.rpc;

import java.util.Objects;

/**
 * 服务方法可以先返回延迟结果，过一段时间后再使用它设置真实返回值
 *
 * @author quanchangnai
 */
public final class DelayedResult<R> extends Promise<R> {

    private int originServerId;

    private int mutable;

    private Worker worker;

    DelayedResult(Worker worker) {
        this.worker = worker;
    }

    int getOriginServerId() {
        return originServerId;
    }

    void setOriginServerId(int originServerId) {
        this.originServerId = originServerId;
    }

    int getMutable() {
        return mutable;
    }

    void setMutable(int mutable) {
        this.mutable = mutable;
    }

    @Override
    public void setResult(R result) {
        if (this.isFinished()) {
            throw new IllegalStateException("不能重复设置延迟结果");
        }

        Worker current = Worker.current();
        if (current != this.worker) {
            this.worker.execute(() -> setResult(result));
            return;
        }

        try {
            super.setResult(result);
        } catch (Exception e) {
            logger.error("", e);
        }

        if (originServerId > 0) {
            this.worker.handleDelayedResult(this);
        }
    }

    @Override
    public void setException(Exception exception) {
        Objects.requireNonNull(exception, "参数[exception]不能为空");
        if (this.isFinished()) {
            throw new IllegalStateException("不能重复设置延迟结果");
        }

        Worker current = Worker.current();
        if (current != this.worker) {
            this.worker.execute(() -> setException(exception));
            return;
        }

        try {
            super.setException(exception);
        } catch (Exception e) {
            logger.error("", e);
        }

        if (originServerId > 0) {
            this.worker.handleDelayedResult(this);
        }
    }

}
