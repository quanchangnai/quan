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
import java.util.concurrent.ConcurrentHashMap;
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

    private boolean running;

    private LocalServer server;

    private BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();

    //管理的所有服务，key:服务ID
    private Map<Object, Service> services = new ConcurrentHashMap<>();

    private int nextCallId = 1;

    private Map<Long, Promise<Object>> mappedPromises = new HashMap<>();

    //按时间排序
    private TreeSet<Promise<Object>> sortedPromises = new TreeSet<>(Comparator.comparingLong(Promise::getTime));

    private TreeSet<DelayedResult<Object>> delayedResults = new TreeSet<>(Comparator.comparingLong(DelayedResult::getTime));

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
            Promise<Object> promise = sortedPromises.first();
            if (!promise.isExpired()) {
                break;
            }
            sortedPromises.remove(promise);
            this.mappedPromises.remove(promise.getCallId());
            promise.setTimeout();
        }

        while (!delayedResults.isEmpty()) {
            DelayedResult<Object> delayedResult = delayedResults.first();
            if (!delayedResult.isExpired()) {
                break;
            }
            delayedResults.remove(delayedResult);
            delayedResult.setTimeout();
        }

    }

    public int resolveTargetServerId(String serviceName) {
        Function<String, Integer> targetServerIdResolver = server.getTargetServerIdResolver();
        if (targetServerIdResolver != null) {
            return targetServerIdResolver.apply(serviceName);
        } else {
            return 0;
        }
    }

    public <R> Promise<R> sendRequest(int targetServerId, Object serviceId, String callee, int mutable, int methodId, Object... params) {
        check(this.id);

        long callId = (long) this.id << 32 | nextCallId++;
        if (nextCallId < 0) {
            nextCallId = 1;
        }

        checkParamsMutable(targetServerId, mutable, params);

        Request request = new Request(serviceId, methodId, params);
        request.setCallId(callId);
        server.sendRequest(targetServerId, request, mutable);

        Promise<Object> promise = new Promise<>(callId, callee);
        mappedPromises.put(callId, promise);
        sortedPromises.add(promise);

        //noinspection unchecked
        return (Promise<R>) promise;
    }

    /**
     * 如果参数有可能被修改,则需要复制对应参数以保证安全
     *
     * @param mutable 1:参数是有可能被修改，参考 {@link Endpoint#paramMutable()}
     */
    protected void checkParamsMutable(int targetServerId, int mutable, Object[] params) {
        if (targetServerId != 0 && targetServerId != this.server.getId()) {
            return;
        }
        if (params == null || (mutable & 1) == 0) {
            return;
        }

        CodedBuffer buffer = null;
        ObjectWriter writer = null;
        ObjectReader reader = null;

        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            if (CommonUtils.isConstant(param)) {
                continue;
            }
            if (buffer == null) {
                buffer = new DefaultCodedBuffer();
                writer = server.getWriterFactory().apply(buffer);
                reader = server.getReaderFactory().apply(buffer);
            }
            writer.write(param);
            params[i] = reader.read();
            buffer.clear();
        }
    }

    /**
     * 如果返回结果有可能被修改，则需要复制返回结果以保证安全
     *
     * @param mutable 2:返回结果有可能被修改，参考 {@link Endpoint#resultMutable()}
     */
    protected Object checkResultMutable(int originServerId, int mutable, Object result) {
        if (originServerId != this.server.getId()) {
            return result;
        }
        if (CommonUtils.isConstant(result) || (mutable & 2) == 0) {
            return result;
        }

        CodedBuffer buffer = new DefaultCodedBuffer();
        ObjectWriter writer = server.getWriterFactory().apply(buffer);
        writer.write(result);
        return server.getReaderFactory().apply(buffer).read();
    }

    protected void handleRequest(int originServerId, Request request, int mutable) {
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
                delayedResult.setMutable(mutable);
                return;
            } else {
                exception = delayedResult.getExceptionStr();
                if (exception == null) {
                    result = checkResultMutable(originServerId, mutable, delayedResult.getResult());
                }
            }
        }

        Response response = new Response(callId, result, exception);
        server.sendResponse(originServerId, response);
    }

    protected void handleDelayedResult(DelayedResult delayedResult) {
        int originServerId = delayedResult.getOriginServerId();
        Object result = checkResultMutable(originServerId, delayedResult.getMutable(), delayedResult.getResult());

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
        sortedPromises.remove(promise);

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
