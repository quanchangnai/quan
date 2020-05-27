package quan.network.nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 基于单线程的任务执行器
 *
 * @author quanchangnai
 */
public class TaskExecutor implements Executor, Runnable {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private volatile boolean running;

    private Thread thread;

    private BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();

    public boolean isInMyThread() {
        return Thread.currentThread().getId() == thread.getId();
    }

    @Override
    public void execute(Runnable task) {
        try {
            taskQueue.put(task);
        } catch (InterruptedException e) {
            logger.error("", e);
        }
    }

    @Override
    public void run() {
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
            } catch (Exception e) {
                logger.error("", e);
            }
        }
        taskQueue.clear();
        end();
        thread = null;
    }

    public void start() {
        running = true;
        thread = new Thread(this);
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
