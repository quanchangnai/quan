package quan.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.rpc.msg.Request;
import quan.rpc.msg.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
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

    //按调用时间排序的Promise
    private TreeSet<Promise<?>> sortedPromises = new TreeSet<>();

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
        new Thread(this::run).start();
    }

    protected void stop() {
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
            try {
                service.update();
            } catch (Throwable e) {
                logger.error("", e);
            }
        }

        while (!sortedPromises.isEmpty()) {
            Promise<?> promise = sortedPromises.first();
            if (!promise.isTimeout()) {
                break;
            }
            sortedPromises.pollFirst();
            this.promises.remove(promise.getCallId());
            promise.handleTimeout();
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

        Promise<R> promise = new Promise<>(callId);
        promises.put(callId, promise);
        sortedPromises.add(promise);

        return promise;
    }

    protected void handleRequest(int originServerId, Request request) {
        long callId = request.getCallId();
        Object result = null;
        String error = null;
        Service service = services.get(request.getServiceId());

        try {
            result = service.call(request.getMethodId(), request.getParams());
        } catch (Throwable e) {
            error = e.toString();
            logger.error("处理RPC请求，调用[{}][{}]执行异常", originServerId, callId, e);
        }

        Response response = new Response(callId, result, error);
        server.sendResponse(originServerId, response);
    }

    protected void handleResponse(Response response) {
        long callId = response.getCallId();
        Promise<?> promise = promises.remove(callId);
        if (promise == null) {
            logger.error("处理RPC响应，调用[{}]不存在或者已超时", callId);
            return;
        }
        sortedPromises.remove(promise);

        String error = response.getError();
        if (error != null) {
            promise.handleError(error);
        } else {
            promise.handleResult(response.getResult());
        }
    }

}
