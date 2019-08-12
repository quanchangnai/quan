package quan.database;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * Created by quanchangnai on 2019/7/1.
 */
public class Executor implements ExecutorService {

    protected ThreadPoolExecutor threadPool;

    public Executor() {
        threadPool = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), r -> new ExecutorThread(r, Executor.this));
    }


    private static class ExecutorThread extends Thread {
        //所属线程池
        private Executor executor;

        public ExecutorThread(Runnable target, Executor executor) {
            super(target);
            this.executor = executor;
        }
    }

    /**
     * 在事务中执行任务
     *
     * @param task
     */
    public final void execute(Task task) {
        Objects.requireNonNull(task);

        boolean isInside = false;
        Thread currentThread = Thread.currentThread();
        if (currentThread instanceof ExecutorThread && ((ExecutorThread) currentThread).executor == this) {
            isInside = true;
        }

        if (isInside) {
            if (Transaction.isInside()) {
                Transaction.insideExecute(task);
            } else {
                Transaction.outsideExecute(task);
            }
        } else {
            threadPool.execute(() -> Transaction.outsideExecute(task));
        }
    }


    @Override
    public void shutdown() {
        threadPool.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return threadPool.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return threadPool.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return threadPool.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return threadPool.awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return threadPool.submit(task);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return threadPool.submit(task, result);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return threadPool.submit(task);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return threadPool.invokeAll(tasks);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return threadPool.invokeAll(tasks, timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return threadPool.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return threadPool.invokeAny(tasks, timeout, unit);
    }

    @Override
    public void execute(Runnable task) {
        threadPool.execute(task);
    }


}
