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

    private RpcServer server;

    //所有的服务，key:服务ID
    private Map<Object, Service> services = new ConcurrentHashMap<>();

    private int nextCallId = 1;

    private Map<Long, Promise<?>> promises = new HashMap<>();

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

    public int getId() {
        return id;
    }

    protected void addService(Service service) {
        service.thread = this;
        services.put(service.getId(), service);
    }

    protected void removeService(Service service) {
        service.thread = null;
        services.remove(service.getId());
    }

    protected void start() {
        logger.info("RpcThread.start():" + id);
        new Thread(this::run).start();
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

    public <R> Promise<R> sendRequest(int targetServerId, Object serviceId, String methodId, Object... methodParams) {
        checkThread(this.id);

        long callId = (long) this.id << 32 | nextCallId++;
        if (nextCallId < 0) {
            nextCallId = 1;
        }

        Request request = new Request(serviceId, methodId, methodParams);
        request.setCallId(callId);
        server.sendRequest(targetServerId, request);

        Promise<R> promise = new Promise<>();
        promises.put(callId, promise);

        return promise;
    }

    protected void handleRequest(int originServerId, Request request) {
        Service service = services.get(request.getServiceId());
        Object result = service.call(request.getMethodId(), request.getMethodParams());

        Response response = new Response(request.getCallId(), result);
        server.sendResponse(originServerId, response);
    }

    protected void handleResponse(Response response) {
        long callId = response.getCallId();
        Promise<?> promise = promises.remove(callId);
        if (promise == null) {
            logger.error("处理RPC响应，调用[{}]不存在", callId);
            return;
        }

        promise.setResult(response.getResult());
    }

}
