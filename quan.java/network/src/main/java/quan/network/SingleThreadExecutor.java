package quan.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 基于单线程的任务执行器
 *
 * @author quanchangnai
 */
public class SingleThreadExecutor implements Executor {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private volatile boolean running;

    private Thread thread;

    private BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();

    /**
     * 判断当前线程是不是执行器关联的线程
     */
    public boolean isInMyTerritory() {
        return Thread.currentThread().getId() == thread.getId();
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public void execute(Runnable task) {
        Objects.requireNonNull(task, "参数[task]不能为空");
        try {
            taskQueue.put(task);
        } catch (InterruptedException e) {
            logger.error("", e);
        }
    }

    protected void run() {
        while (isRunning()) {
            for (Runnable task = taskQueue.poll(); task != null; task = taskQueue.poll()) {
                try {
                    task.run();
                } catch (Throwable e) {
                    logger.error("", e);
                }
            }
            try {
                after();
            } catch (Throwable e) {
                logger.error("", e);
            }
        }

        try {
            end();
        } catch (Throwable e) {
            logger.error("", e);
        }

        taskQueue.clear();
        thread = null;
    }

    public void start() {
        running = true;
        thread = new Thread(this::run);
        thread.start();
    }

    public void stop() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    protected void after() {
    }

    protected void end() {
    }

}
