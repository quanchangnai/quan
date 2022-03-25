package quan.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author quanchangnai
 */
public class RpcThread {

    private static Logger logger = LoggerFactory.getLogger(RpcThread.class);

    private static ThreadLocal<RpcThread> threadLocal = new ThreadLocal<>();

    private int id;

    private boolean running;

    private BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();

    private Thread thread;

    private RpcServer server;

    //所有的服务，key:服务ID
    private Map<Object, Service> services = new ConcurrentHashMap<>();

    private int nextCallId = 1;

    private Map<Integer, Promise<?>> promises = new HashMap<>();

    protected RpcThread(int id, RpcServer server) {
        this.id = id;
        this.server = server;
    }

    public static RpcThread current() {
        return threadLocal.get();
    }

    protected static void checkThread(int id) {
        RpcThread current = current();
        if (current == null) {
            throw new RuntimeException("当前不在RPC线程中");
        }
        if (id > 0 && current.id != id) {
            throw new RuntimeException("当前RPC线程" + current.id + "不是期望线程" + id);
        }
    }

    protected void addService(Service service) {
        service.thread = this;
        services.put(service.getId(), service);
    }

    protected void start() {
        logger.info("RpcThread.start():" + id);
        thread = new Thread(this::run);
        thread.start();
    }

    protected void stop() {
        logger.info("RpcThread.stop():" + id);
        running = false;
    }

    protected void run() {
        threadLocal.set(this);
        running = true;

        while (running) {
            for (Runnable task = taskQueue.poll(); task != null; task = taskQueue.poll()) {
                try {
                    task.run();
                } catch (Throwable e) {
                    logger.error("", e);
                }
            }
        }

        taskQueue.clear();
    }

    public void execute(Runnable task) {
        Objects.requireNonNull(task, "参数[task]不能为空");
        try {
            taskQueue.put(task);
        } catch (InterruptedException e) {
            logger.error("", e);
        }
    }

    protected void update() {
        for (Service service : services.values()) {
            service.update();
        }
    }

    public Promise sendRequest(int targetServerId, Object serviceId, String methodName, Object... params) {
        checkThread(this.id);

        Request request = new Request(targetServerId, serviceId, methodName, params);
        request.setCallId(nextCallId++);
        request.setOriginServer(this.server.getId());
        request.setOriginThread(this.id);
        server.sendRequest(request);

        Promise<?> promise = new Promise<>(request.getCallId());
        promises.put(promise.getCallId(), promise);

        return promise;
    }

    protected void handleRequest(Request request) {
        Service service = services.get(request.getService());
        Object result = service.call(request.getMethod(), request.getParams());

        Response response = new Response(request.getCallId(), request.getOriginServer(), request.getOriginThread(), result);
        server.sendResponse(response);
    }

    protected void handleResponse(Response response) {
        int callId = response.getCallId();
        Promise<?> promise = promises.remove(callId);
        if (promise == null) {
            logger.error("处理RPC响应，调用[{}]不存在", callId);
            return;
        }

        promise.setResult(response.getResult());
    }

}
