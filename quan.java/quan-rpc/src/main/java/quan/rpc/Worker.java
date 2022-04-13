package quan.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.rpc.msg.Request;
import quan.rpc.msg.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * RPC工作线程
 *
 * @author quanchangnai
 */
public class Worker {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static ThreadLocal<Worker> threadLocal = new ThreadLocal<>();

    private static int nextId = 1;

    private int id = nextId++;

    private boolean running;

    private LocalServer server;

    private BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();

    //管理的所有服务，key:服务ID
    private Map<Object, Service> services = new ConcurrentHashMap<>();

    private int nextCallId = 1;

    private Map<Long, Promise<?>> promises = new HashMap<>();

    protected Worker(LocalServer server) {
        this.server = server;
    }

    public static Worker current() {
        return threadLocal.get();
    }

    protected static void check(int id) {
        Worker current = current();
        if (current == null || current.id != id) {
            throw new IllegalStateException("当前所处线程不合法");
        }
    }

    public int getId() {
        return id;
    }

    protected void addService(Service service) {
        service.worker = this;
        services.put(service.getId(), service);
    }

    protected void removeService(Service service) {
        service.worker = null;
        services.remove(service.getId());
    }

    protected void start() {
        logger.info("Worker.start():" + id);
        new Thread(this::run).start();
    }

    protected void stop() {
        logger.info("Worker.stop():" + id);
        running = false;
    }

    protected void run() {
        threadLocal.set(this);
        running = true;

        while (running) {
            try {
                taskQueue.take().run();
            } catch (Throwable e) {
                logger.error("", e);
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

    public <R> Promise<R> sendRequest(int targetServerId, Object serviceId, int methodId, Object... params) {
        check(this.id);

        long callId = (long) this.id << 32 | nextCallId++;
        if (nextCallId < 0) {
            nextCallId = 1;
        }

        Request request = new Request(serviceId, methodId, params);
        request.setCallId(callId);
        server.sendRequest(targetServerId, request);

        Promise<R> promise = new Promise<>();
        promises.put(callId, promise);

        return promise;
    }

    protected void handleRequest(int originServerId, Request request) {
        Service service = services.get(request.getServiceId());
        Object result = null;
        try {
            result = service.call(request.getMethodId(), request.getParams());
        } catch (Throwable e) {
            e.printStackTrace();
        }

        Response response = new Response(request.getCallId(), result);
        server.sendResponse(originServerId, response);
    }

    protected void handleResponse(Response response) {
        long callId = response.getCallId();
        Promise<?> promise = promises.remove(callId);
        if (promise == null) {
            logger.error("处理RPC响应，不存在该调用:{}", callId);
            return;
        }

        promise.setResult(response.getResult());
    }

}
