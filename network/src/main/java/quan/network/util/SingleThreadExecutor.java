package quan.network.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.*;

/**
 * 单线程执行器
 *
 * @author quanchangnai
 */
public class SingleThreadExecutor implements Executor, Runnable {

    protected final Logger logger = LogManager.getLogger(getClass());

    private volatile boolean running = false;

    private Thread thread;

    private BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();

    public boolean isInMyThread() {
        return Thread.currentThread().getId() == thread.getId();
    }

    public Future<Void> submit(Runnable task) {
        FutureTask<Void> futureTask = new FutureTask<>(task, null);
        execute(futureTask);
        return futureTask;
    }

    @Override
    public void execute(Runnable task) {
        try {
            taskQueue.put(task);
        } catch (InterruptedException e) {
            logger.error(e);
        }
    }

    @Override
    public void run() {
        while (isRunning()) {
            for (Runnable task = taskQueue.poll(); task != null; task = taskQueue.poll()) {
                try {
                    task.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            after();
        }
        end();
    }

    public void start() {
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    public void stop() {
        running = false;
        thread = null;
    }

    public boolean isRunning() {
        return running;
    }

    protected void after() {
    }

    protected void end() {
    }

}
