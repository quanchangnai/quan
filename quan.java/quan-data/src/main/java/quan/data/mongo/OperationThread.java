package quan.data.mongo;

/**
 * 数据库操作线程，其他线程不允许读写数据库
 */
class OperationThread extends Thread {

    public OperationThread(Runnable target) {
        super(target);
    }

    static boolean isInside() {
        return Thread.currentThread() instanceof OperationThread;
    }

}
