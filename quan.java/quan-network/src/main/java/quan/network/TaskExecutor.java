package quan.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 基于单线程的任务执行器
 *
 * @author quanchangnai
 */
public class TaskExecutor implements Executor {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private volatile boolean running;

    private Thread thread;

    private BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();

    /**
     * 判断当前线程是不是执行器关联的线程
     */
    public boolean isMyThread() {
        return Thread.currentThread() == thread;
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

    protected void runTasks() throws InterruptedException {
        while (true) {
            Runnable task = taskQueue.poll(20, TimeUnit.MILLISECONDS);
            if (task == null) {
                break;
            }
            task.run();
        }
    }

    protected void run() {
        running = true;
        while (running) {
            try {
                runTasks();
            } catch (Throwable e) {
                logger.error("", e);
            }
        }

        destroy();
    }

    public void start() {
        thread = new Thread(this::run);
        thread.start();
    }

    public void stop() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    protected void destroy() {
        taskQueue.clear();
        thread = null;
    }

}
