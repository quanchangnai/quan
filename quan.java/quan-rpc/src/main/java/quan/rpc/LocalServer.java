package quan.rpc;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.message.CodedBuffer;
import quan.rpc.protocol.Handshake;
import quan.rpc.protocol.PingPong;
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
 * @author quanchangnai
 */
public abstract class LocalServer {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final int id;

    private final String ip;

    private final int port;

    private int reconnectTime;

    private Function<CodedBuffer, ObjectReader> readerFactory = ObjectReader::new;

    private Function<CodedBuffer, ObjectWriter> writerFactory = ObjectWriter::new;

    /**
     * 使用服务名作为参数，调用后返回服务的目标服务器ID，多次调用返回要一致
     */
    private Function<String, Integer> targetServerIdResolver;

    //管理的所有工作线程，key:工作线程ID
    private final Map<Integer, Worker> workers = new HashMap<>();

    private final List<Integer> workerIds = new ArrayList<>();

    private int workerIndex;

    //管理的所有服务，key:服务ID
    private final Map<Object, Service> services = new HashMap<>();

    //管理的所有远程服务器，key:服务器ID
    private final Map<Integer, RemoteServer> remotes = new ConcurrentHashMap<>();

    private ScheduledExecutorService scheduler;

    protected LocalServer(int id, String ip, int port, int workerNum) {
        Validate.isTrue(id > 0, "服务器ID必须是正整数");
        this.id = id;
        this.ip = ip;
        this.port = port;
        this.initWorkers(workerNum);
    }

    public final int getId() {
        return id;
    }

    public final String getIp() {
        return ip;
    }

    public final int getPort() {
        return port;
    }

    public final int getWorkerNum() {
        return workers.size();
    }

    /**
     * 远程服务器断线重连的等待时间
     */
    public void setReconnectTime(int reconnectTime) {
        this.reconnectTime = reconnectTime;
    }

    /**
     * 设置{@link ObjectReader}工厂，用于扩展对象序列化
     */
    public void setReaderFactory(Function<CodedBuffer, ObjectReader> readerFactory) {
        this.readerFactory = Objects.requireNonNull(readerFactory);
    }

    /**
     * 设置{@link ObjectWriter}工厂，用于扩展对象序列化
     */
    public void setWriterFactory(Function<CodedBuffer, ObjectWriter> writerFactory) {
        this.writerFactory = Objects.requireNonNull(writerFactory);
    }

    /**
     * 如果目标服务器是单进程的，该Resolver用来查找目标服务器ID，可以省去每次构造服务代理都必需要传参的麻烦
     *
     * @see #targetServerIdResolver
     */
    public void setTargetServerIdResolver(Function<String, Integer> targetServerIdResolver) {
        this.targetServerIdResolver = targetServerIdResolver;
    }

    public int getReconnectTime() {
        return reconnectTime;
    }

    public Function<CodedBuffer, ObjectReader> getReaderFactory() {
        return readerFactory;
    }

    public Function<CodedBuffer, ObjectWriter> getWriterFactory() {
        return writerFactory;
    }

    public Function<String, Integer> getTargetServerIdResolver() {
        return targetServerIdResolver;
    }

    public synchronized void start() {
        workers.values().forEach(Worker::start);
        startNetwork();
        remotes.values().forEach(RemoteServer::start);
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::update, 50, 50, TimeUnit.MILLISECONDS);
    }

    public synchronized void stop() {
        scheduler.shutdown();
        scheduler = null;
        remotes.values().forEach(RemoteServer::stop);
        stopNetwork();
        workers.values().forEach(Worker::stop);
    }

    protected abstract void startNetwork();

    protected abstract void stopNetwork();

    protected void update() {
        remotes.values().forEach(RemoteServer::update);
        for (Worker worker : workers.values()) {
            worker.execute(worker::update);
        }
    }

    private void initWorkers(int workerNum) {
        if (workerNum <= 0) {
            workerNum = Runtime.getRuntime().availableProcessors();
        }
        for (int i = 0; i < workerNum; i++) {
            Worker worker = new Worker(this);
            workers.put(worker.getId(), worker);
        }
        workerIds.addAll(workers.keySet());
    }

    private Worker nextWorker() {
        int workerId = workerIds.get(workerIndex++);
        if (workerIndex >= workerIds.size()) {
            workerIndex = 0;
        }
        return workers.get(workerId);
    }

    public synchronized void addService(Service service) {
        Object serviceId = service.getId();
        if (services.putIfAbsent(serviceId, service) != null) {
            logger.error("RPC服务[{}]已存在", serviceId);
            return;
        }
        nextWorker().addService(service);
    }

    public synchronized void removeService(Service service) {
        Object serviceId = service.getId();
        if (!services.remove(serviceId, service)) {
            logger.error("RPC服务[{}]不存在", serviceId);
            return;
        }
        service.getWorker().removeService(service);
    }

    public synchronized void addRemote(int remoteId, String remoteIp, int remotePort) {
        if (remotes.containsKey(remoteId)) {
            logger.error("添加的远程服务器[{}]已存在", remoteId);
            return;
        }
        RemoteServer remoteServer = newRemote(remoteId, remoteIp, remotePort);
        remoteServer.setLocalServer(this);
        remotes.put(remoteServer.getId(), remoteServer);
        if (scheduler != null) {
            remoteServer.start();
        }
    }

    protected abstract RemoteServer newRemote(int remoteId, String remoteIp, int remotePort);

    /**
     * 处理RPC握手逻辑
     */
    protected void handshake(Handshake handshake) {
        int remoteId = handshake.getServerId();
        if (!remotes.containsKey(remoteId)) {
            addRemote(remoteId, handshake.getServerIp(), handshake.getServerPort());
        }
    }

    protected void handlePingPong(int originServerId, PingPong pingPong) {
        RemoteServer remoteServer = remotes.get(originServerId);
        if (remoteServer != null) {
            remoteServer.handlePingPong(pingPong);
        }
    }

    /**
     * 发送RPC请求
     */
    protected void sendRequest(int targetServerId, Request request, int mutable) {
        if (targetServerId == this.id || targetServerId == 0) {
            //本地服务器直接处理
            handleRequest(this.id, request, mutable);
        } else {
            RemoteServer remoteServer = remotes.get(targetServerId);
            if (remoteServer != null) {
                remoteServer.send(request);
            } else {
                logger.error("发送RPC请求，远程服务器[{}]不存在", targetServerId);
            }
        }
    }

    /**
     * 处理RPC请求
     */
    protected void handleRequest(int originServerId, Request request, int mutable) {
        Service service = services.get(request.getServiceId());
        if (service == null) {
            logger.error("处理RPC请求，服务[{}]不存在", request.getServiceId());
        } else {
            Worker worker = service.getWorker();
            worker.execute(() -> worker.handleRequest(originServerId, request, mutable));
        }
    }


    protected void handleRequest(int originServerId, Request request) {
        handleRequest(originServerId, request, 0);
    }

    /**
     * 发送RPC响应
     */
    protected void sendResponse(int originServerId, Response response) {
        if (originServerId == this.id) {
            //本地服务器直接处理
            handleResponse(response);
        } else {
            RemoteServer remoteServer = remotes.get(originServerId);
            if (remoteServer != null) {
                remoteServer.send(response);
            } else {
                logger.error("发送RPC响应，远程服务器[{}]不存在", originServerId);
            }
        }
    }

    /**
     * 处理RPC响应
     */
    protected void handleResponse(Response response) {
        int workerId = (int) (response.getCallId() >> 32);
        Worker worker = workers.get(workerId);
        if (worker == null) {
            logger.error("处理RPC响应，工作线程[{}]不存在", workerId);
        } else {
            worker.execute(() -> worker.handleResponse(response));
        }
    }


}
