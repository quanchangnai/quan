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

    private Thread thread;

    private BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();

    //刷帧开始时间
    private volatile long updateTime;

    private volatile boolean updateFinished = true;

    private long stackTraceTime;

    //管理的所有服务，key:服务ID
    private final Map<Object, Service> allServices = new HashMap<>();

    private Set<UpdatableService> updatableServices = new HashSet<>();

    private int nextCallId = 1;

    private Map<Long, Promise<Object>> mappedPromises = new HashMap<>();

    //按时间排序
    private TreeSet<Promise<Object>> sortedPromises = new TreeSet<>(Comparator.comparingLong(Promise::getTime));

    private TreeSet<DelayedResult<Object>> delayedResults = new TreeSet<>(Comparator.comparingLong(DelayedResult::getTime));

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
        allServices.put(service.getId(), service);
        if (service instanceof UpdatableService) {
            updatableServices.add((UpdatableService) service);
        }
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
        if (!allServices.containsKey(serviceId)) {
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
        allServices.remove(serviceId);
        if (service instanceof UpdatableService) {
            updatableServices.remove(service);
        }

    }

    private void destroyService(Service service) {
        try {
            service.destroy();
        } catch (Exception e) {
            logger.error("服务[{}]销毁异常", service.getId(), e);
        }
    }

    protected void start() {
        thread = new Thread(this::run);
        thread.start();
        execute(() -> allServices.values().forEach(this::initService));
    }

    protected void stop() {
        execute(() -> {
            allServices.values().forEach(this::destroyService);
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
        thread = null;
    }

    public void execute(Runnable task) {
        Objects.requireNonNull(task, "参数[task]不能为空");
        try {
            taskQueue.put(task);
        } catch (InterruptedException e) {
            logger.error("", e);
        }
    }

    /**
     * 驱动刷帧并检测帧率，不在当前线程中执行
     */
    protected void driveUpdate() {
        if (updateFinished) {
            updateFinished = false;
            execute(this::update);
        }

        long currentTime = System.currentTimeMillis();
        long intervalTime = currentTime - updateTime;
        if (intervalTime > getServer().getUpdateInterval() * 2 && currentTime - stackTraceTime > 10000 && updateTime > 0) {
            stackTraceTime = currentTime;
            StringBuilder stackTrace = new StringBuilder();
            for (StackTraceElement traceElement : thread.getStackTrace()) {
                stackTrace.append("\tat ").append(traceElement).append("\n");
            }
            logger.error("工作线程[{}]帧率过低，距离上次刷帧已经过了{}ms，线程可能在执行耗时任务\n{}", id, intervalTime, stackTrace);
        }
    }

    protected void update() {
        updateTime = System.currentTimeMillis();

        for (UpdatableService service : updatableServices) {
            try {
                service.update();
            } catch (Throwable e) {
                logger.error("", e);
            }
        }

        expirePromises();
        expireDelayedResults();

        long costTime = System.currentTimeMillis() - updateTime;
        if (costTime > getServer().getUpdateInterval()) {
            logger.error("工作线程[{}]帧率过低，本次刷帧消耗时间{}ms", id, costTime);
        }
        updateFinished = true;
    }

    private void expirePromises() {
        if (sortedPromises.isEmpty()) {
            return;
        }

        Iterator<Promise<Object>> iterator = sortedPromises.iterator();
        while (iterator.hasNext()) {
            Promise<Object> promise = iterator.next();
            if (!promise.isExpired()) {
                return;
            }
            iterator.remove();
            mappedPromises.remove(promise.getCallId());
            promise.setTimeout();
        }
    }

    private void expireDelayedResults() {
        if (delayedResults.isEmpty()) {
            return;
        }

        Iterator<DelayedResult<Object>> iterator = delayedResults.iterator();
        while (iterator.hasNext()) {
            DelayedResult<Object> delayedResult = iterator.next();
            if (!delayedResult.isExpired()) {
                return;
            }
            iterator.remove();
            delayedResults.remove(delayedResult);
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
        sortedPromises.add(promise);

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

        Service service = allServices.get(serviceId);
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
