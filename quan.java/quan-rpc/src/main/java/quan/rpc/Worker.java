package quan.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.message.CodedBuffer;
import quan.message.DefaultCodedBuffer;
import quan.rpc.protocol.Request;
import quan.rpc.protocol.Response;
import quan.rpc.serialize.ObjectReader;
import quan.rpc.serialize.ObjectWriter;
import quan.util.CommonUtils;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;

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

    private volatile boolean running;

    private LocalServer server;

    private BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();

    //管理的所有服务，key:服务ID
    private final Map<Object, Service> services = new HashMap<>();

    private int nextCallId = 1;

    private Map<Long, Promise<Object>> mappedPromises = new HashMap<>();

    private Queue<Promise<Object>> queuedPromises = new LinkedList<>();

    private Queue<DelayedResult<Object>> delayedResults = new LinkedList<>();

    private ObjectWriter writer;

    private ObjectReader reader;

    protected Worker(LocalServer server) {
        this.server = server;
    }

    public static Worker current() {
        return threadLocal.get();
    }

    public int getId() {
        return id;
    }

    public LocalServer getServer() {
        return server;
    }

    public void addService(Service service) {
        server.addService(this, service);
    }

    protected void _addService(Service service) {
        service.worker = this;
        services.put(service.getId(), service);
        if (running) {
            initService(service);
        }
    }

    private void initService(Service service) {
        try {
            service.init();
        } catch (Exception e) {
            logger.error("服务[{}]初始化异常", service.getId(), e);
        }
    }

    public void removeService(Object serviceId) {
        if (!services.containsKey(serviceId)) {
            logger.error("服务[{}]不存在", serviceId);
        } else {
            server.removeService(serviceId);
        }
    }

    protected void _removeService(Service service) {
        Object serviceId = service.getId();
        if (running) {
            destroyService(service);
        }
        service.worker = null;
        services.remove(serviceId);
    }

    private void destroyService(Service service) {
        try {
            service.destroy();
        } catch (Exception e) {
            logger.error("服务[{}]销毁异常", service.getId(), e);
        }
    }

    protected void start() {
        new Thread(this::run).start();
        execute(() -> services.values().forEach(this::initService));
    }

    protected void stop() {
        execute(() -> {
            services.values().forEach(this::destroyService);
            running = false;
        });
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
        threadLocal.set(null);
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

        while (!queuedPromises.isEmpty()) {
            Promise<Object> promise = queuedPromises.peek();
            if (!promise.isExpired()) {
                break;
            }
            queuedPromises.remove();
            mappedPromises.remove(promise.getCallId());
            promise.setTimeout();
        }

        while (!delayedResults.isEmpty()) {
            DelayedResult<Object> delayedResult = delayedResults.peek();
            if (!delayedResult.isExpired()) {
                break;
            }
            delayedResults.remove();
            delayedResult.setTimeout();
        }

    }

    protected int resolveTargetServerId(String serviceName) {
        Function<String, Integer> targetServerIdResolver = server.getTargetServerIdResolver();
        if (targetServerIdResolver != null) {
            return targetServerIdResolver.apply(serviceName);
        } else {
            return 0;
        }
    }

    protected <R> Promise<R> sendRequest(int targetServerId, Object serviceId, String signature, int securityModifier, int methodId, Object... params) {
        long callId = (long) this.id << 32 | nextCallId++;
        if (nextCallId < 0) {
            nextCallId = 1;
        }

        makeParamSafe(targetServerId, securityModifier, params);

        Request request = new Request(serviceId, methodId, params);
        request.setCallId(callId);
        server.sendRequest(targetServerId, request, securityModifier);

        Promise<Object> promise = new Promise<>(callId, signature);
        mappedPromises.put(callId, promise);
        queuedPromises.add(promise);

        //noinspection unchecked
        return (Promise<R>) promise;
    }

    protected Object clone(Object object) {
        if (writer == null) {
            CodedBuffer buffer = new DefaultCodedBuffer();
            writer = server.getWriterFactory().apply(buffer);
            reader = server.getReaderFactory().apply(buffer);
        } else {
            writer.getBuffer().clear();
        }
        writer.write(object);
        return reader.read();
    }

    /**
     * 如果有参数是不安全的,则需要复制它以保证安全
     *
     * @param securityModifier 1:所有参数都是安全的，参考 {@link Endpoint#paramSafe()}
     */
    protected void makeParamSafe(int targetServerId, int securityModifier, Object[] params) {
        if (targetServerId != 0 && targetServerId != this.server.getId()) {
            return;
        }
        if (params == null || (securityModifier & 0b01) == 0b01) {
            return;
        }

        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            if (!CommonUtils.isConstant(param)) {
                params[i] = clone(param);
            }
        }
    }

    /**
     * 如果返回结果是不安全的，则需要复它以保证安全
     *
     * @param securityModifier 2:返回结果是安全的，参考 {@link Endpoint#resultSafe()}
     */
    protected Object makeResultSafe(int originServerId, int securityModifier, Object result) {
        if (originServerId != this.server.getId()) {
            return result;
        }
        if (CommonUtils.isConstant(result) || (securityModifier & 0b10) == 0b10) {
            return result;
        } else {
            return clone(result);
        }
    }

    protected void handleRequest(int originServerId, Request request, int securityModifier) {
        long callId = request.getCallId();
        Object serviceId = request.getServiceId();
        Object result = null;
        String exception = null;

        Service service = services.get(serviceId);
        if (service == null) {
            logger.error("处理RPC请求，服务[{}]不存在，originServerId:{}，callId:{}", serviceId, originServerId, callId);
            return;
        }

        try {
            result = service.call(request.getMethodId(), request.getParams());
        } catch (Throwable e) {
            exception = e.toString();
            logger.error("处理RPC请求，方法执行异常，originServerId:{}，callId:{}", originServerId, callId, e);
        }

        if (result instanceof DelayedResult) {
            DelayedResult<?> delayedResult = (DelayedResult<?>) result;
            if (!delayedResult.isFinished()) {
                delayedResult.setCallId(callId);
                delayedResult.setOriginServerId(originServerId);
                delayedResult.setSecurityModifier(securityModifier);
                return;
            } else {
                exception = delayedResult.getExceptionStr();
                if (exception == null) {
                    result = makeResultSafe(originServerId, securityModifier, delayedResult.getResult());
                }
            }
        }

        Response response = new Response(callId, result, exception);
        server.sendResponse(originServerId, response);
    }

    protected void handleDelayedResult(DelayedResult delayedResult) {
        int originServerId = delayedResult.getOriginServerId();
        Object result = makeResultSafe(originServerId, delayedResult.getSecurityModifier(), delayedResult.getResult());
        Response response = new Response(delayedResult.getCallId(), result, delayedResult.getExceptionStr());
        server.sendResponse(originServerId, response);
    }

    protected void handleResponse(Response response) {
        long callId = response.getCallId();
        Promise<Object> promise = mappedPromises.remove(callId);
        if (promise == null) {
            logger.error("处理RPC响应，调用[{}]不存在或者已超时", callId);
            return;
        }
        queuedPromises.remove(promise);

        String exception = response.getException();
        if (exception != null) {
            promise.setException(new CallException(exception));
        } else {
            promise.setResult(response.getResult());
        }
    }

    public <R> DelayedResult<R> newDelayedResult() {
        return new DelayedResult<>(this);
    }

}
