package quan.rpc;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.message.CodedBuffer;
import quan.rpc.protocol.Protocol;
import quan.rpc.protocol.Request;
import quan.rpc.protocol.Response;
import quan.rpc.serialize.ObjectReader;
import quan.rpc.serialize.ObjectWriter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * 本地服务器
 *
 * @author quanchangnai
 */
public class LocalServer {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final int id;

    private int updateInterval = 50;

    private int callTtl = 10;

    private Function<CodedBuffer, ObjectReader> readerFactory = ObjectReader::new;

    private Function<CodedBuffer, ObjectWriter> writerFactory = ObjectWriter::new;

    private Set<Connector> connectors = new HashSet<>();

    /**
     * 如果服务的目标服务器只有一个，使用服务名作为参数调用后返回目标服务器ID，可以省去每次构造服务代理都必需要传参的麻烦
     */
    private Function<String, Integer> targetServerIdResolver;

    //管理的所有工作线程，key:工作线程ID
    private Map<Integer, Worker> workers = new HashMap<>();

    private final List<Integer> workerIds = new ArrayList<>();

    private int workerIndex;

    //管理的所有服务，key:服务ID
    private final Map<Object, Service> services = new ConcurrentHashMap<>();

    private ScheduledExecutorService executor;

    private boolean running;

    public LocalServer(int id, int workerNum, Connector... connectors) {
        Validate.isTrue(id > 0, "服务器ID必须是正整数");
        this.id = id;
        this.initWorkers(workerNum);
        this.initConnectors(connectors);
    }

    public LocalServer(int id, Connector... connectors) {
        this(id, 0, connectors);
    }

    public final int getId() {
        return id;
    }

    public final Map<Integer, Worker> getWorkers() {
        return workers;
    }

    public final int getWorkerNum() {
        return workers.size();
    }

    /**
     * 设置刷帧的间隔时间(ms)
     */
    public void setUpdateInterval(int updateInterval) {
        if (updateInterval > 0) {
            this.updateInterval = updateInterval;
        }
    }

    public int getUpdateInterval() {
        return updateInterval;
    }

    public int getCallTtl() {
        return callTtl;
    }

    /**
     * 设置调用超时时间(秒)
     */
    public void setCallTtl(int callTtl) {
        if (callTtl > 0) {
            this.callTtl = callTtl;
        }
    }

    /**
     * 设置{@link ObjectReader}工厂，用于扩展对象序列化
     */
    public void setReaderFactory(Function<CodedBuffer, ObjectReader> readerFactory) {
        this.readerFactory = Objects.requireNonNull(readerFactory);
    }

    public Function<CodedBuffer, ObjectReader> getReaderFactory() {
        return readerFactory;
    }

    /**
     * 设置{@link ObjectWriter}工厂，用于扩展对象序列化
     */
    public void setWriterFactory(Function<CodedBuffer, ObjectWriter> writerFactory) {
        this.writerFactory = Objects.requireNonNull(writerFactory);
    }

    public Function<CodedBuffer, ObjectWriter> getWriterFactory() {
        return writerFactory;
    }

    public Set<Connector> getConnectors() {
        return connectors;
    }

    public Connector getConnector(int remoteId) {
        for (Connector connector : connectors) {
            if (connector.getRemoteIds().contains(remoteId)) {
                return connector;
            }
        }
        return null;
    }

    /**
     * @see #targetServerIdResolver
     */
    public void setTargetServerIdResolver(Function<String, Integer> targetServerIdResolver) {
        this.targetServerIdResolver = targetServerIdResolver;
    }

    public Function<String, Integer> getTargetServerIdResolver() {
        return targetServerIdResolver;
    }

    private void initWorkers(int workerNum) {
        if (workerNum <= 0) {
            workerNum = Runtime.getRuntime().availableProcessors();
        }

        for (int i = 0; i < workerNum; i++) {
            Worker worker = new Worker(this);
            workers.put(worker.getId(), worker);
        }

        workers = Collections.unmodifiableMap(workers);
        workerIds.addAll(workers.keySet());
    }

    private Worker nextWorker() {
        int workerId = workerIds.get(workerIndex++);
        if (workerIndex >= workerIds.size()) {
            workerIndex = 0;
        }
        return workers.get(workerId);
    }

    private void initConnectors(Connector... connectors) {
        if (connectors == null) {
            this.connectors = Collections.emptySet();
            return;
        }

        Set<Integer> allRemoteIds = new HashSet<>();
        Set<Integer> duplicateRemoteIds = new HashSet<>();

        for (Connector connector : connectors) {
            this.connectors.add(connector);
            connector.localServer = this;
            for (Integer remoteId : connector.getRemoteIds()) {
                if (!allRemoteIds.add(remoteId)) {
                    duplicateRemoteIds.add(remoteId);
                }
            }
        }

        if (!duplicateRemoteIds.isEmpty()) {
            throw new IllegalStateException(String.format("远程服务器%s重复了", duplicateRemoteIds));
        }

        this.connectors = Collections.unmodifiableSet(this.connectors);
    }

    public synchronized void start() {
        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(this::update, updateInterval, updateInterval, TimeUnit.MILLISECONDS);
        workers.values().forEach(Worker::start);
        connectors.forEach(Connector::start);
        running = true;
    }

    public synchronized void stop() {
        running = false;
        executor.shutdown();
        executor = null;
        connectors.forEach(Connector::stop);
        workers.values().forEach(Worker::stop);
        workers = new HashMap<>();
        workerIds.clear();
    }

    public boolean isRunning() {
        return running;
    }

    protected void update() {
        if (running) {
            try {
                connectors.forEach(Connector::update);
                workers.values().forEach(Worker::tryUpdate);
            } catch (Exception e) {
                logger.error("", e);
            }
        }
    }

    public final boolean hasRemote(int remoteId) {
        return getConnector(remoteId) != null;
    }

    public final boolean isRemoteActivated(int remoteId) {
        Connector connector = getConnector(remoteId);
        return connector != null && connector.isRemoteActivated(remoteId);
    }

    public void addService(Service service) {
        addService(nextWorker(), service);
    }

    public void addService(Worker worker, Service service) {
        if (workers.get(worker.getId()) != worker) {
            throw new IllegalArgumentException(String.format("参数[worker]不是服务器[%s]管理的工作线程", this.id));
        }

        Object serviceId = Objects.requireNonNull(service.getId(), "服务ID不能为空");
        if (services.putIfAbsent(serviceId, service) == null) {
            worker.execute(() -> worker.doAddService(service));
        } else {
            logger.error("服务[{}]已存在", serviceId);
        }
    }

    public void removeService(Object serviceId) {
        Service service = services.remove(serviceId);
        if (service == null) {
            logger.error("服务[{}]不存在", serviceId);
            return;
        }

        Worker worker = service.getWorker();
        worker.execute(() -> worker.doRemoveService(service));
    }

    protected void sendProtocol(int targetServerId, Protocol protocol) {
        Connector connector = getConnector(targetServerId);
        if (connector != null) {
            connector.sendProtocol(targetServerId, protocol);
        } else {
            throw new IllegalArgumentException(String.format("远程服务器[%s]不存在", targetServerId));
        }
    }

    /**
     * 发送RPC请求
     */
    protected void sendRequest(int targetServerId, Request request, int securityModifier) {
        if (targetServerId == this.id || targetServerId == 0) {
            //本地服务器直接处理
            handleRequest(request, securityModifier);
        } else {
            sendProtocol(targetServerId, request);
        }
    }

    /**
     * 处理RPC请求
     */
    protected void handleRequest(Request request, int securityModifier) {
        Service service = services.get(request.getServiceId());
        if (service == null) {
            logger.error("处理RPC请求，服务[{}]不存在", request.getServiceId());
        } else {
            Worker worker = service.getWorker();
            worker.execute(() -> worker.handleRequest(request, securityModifier));
        }
    }

    protected void handleRequest(Request request) {
        handleRequest(request, 0b11);
    }

    /**
     * 发送RPC响应
     */
    protected void sendResponse(int targetServerId, Response response) {
        if (targetServerId == this.id) {
            //本地服务器直接处理
            handleResponse(response);
        } else {
            sendProtocol(targetServerId, response);
        }
    }

    /**
     * 处理RPC响应
     */
    protected void handleResponse(Response response) {
        long callId = response.getCallId();
        int workerId = (int) (callId >> 32);
        Worker worker = workers.get(workerId);
        if (worker == null) {
            logger.error("处理RPC响应，工作线程[{}]不存在，originServerId:{}，callId:{}", workerId, response.getServerId(), callId);
        } else {
            worker.execute(() -> worker.handleResponse(response));
        }
    }

}
