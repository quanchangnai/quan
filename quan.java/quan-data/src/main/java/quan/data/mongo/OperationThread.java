package quan.data.mongo;

class OperationThread extends Thread {

    public OperationThread(Runnable target) {
        super(target);
    }

    static boolean isInside() {
        return Thread.currentThread() instanceof OperationThread;
    }

}
