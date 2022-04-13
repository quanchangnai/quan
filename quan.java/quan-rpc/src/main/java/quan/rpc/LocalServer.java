package quan.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.message.CodedBuffer;
import quan.message.Message;
import quan.rpc.msg.Handshake;
import quan.rpc.msg.Request;
import quan.rpc.msg.Response;
import quan.rpc.serialize.ObjectReader;
import quan.rpc.serialize.Transferable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    protected Function<Integer, Transferable> transferableFactory;

    protected Function<Integer, Message> messageFactory;

    //管理的所有工作线程，key:工作线程ID
    private final Map<Integer, Worker> workers = new HashMap<>();

    private int workerIndex;

    //管理的所有服务，key:服务ID
    private final Map<Object, Service> services = new HashMap<>();

    //管理的所有远程服务器，key:服务器ID
    private final Map<Integer, RemoteServer> remotes = new ConcurrentHashMap<>();

    /**
     * 远程服务器断线重连的等待实现
     */
    private int reconnectTime;

    private ScheduledExecutorService scheduler;

    protected LocalServer(int id, String ip, int port, int workerNum) {
        this.id = id;
        this.ip = ip;
        this.port = port;

        if (workerNum <= 0) {
            workerNum = Runtime.getRuntime().availableProcessors();
        }
        for (int i = 0; i < workerNum; i++) {
            Worker worker = new Worker(this);
            workers.put(worker.getId(), worker);
        }
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
     * 设置{@link Transferable}工厂，{@link ObjectReader}反序列化时需要用到
     */
    public final void setTransferableFactory(Function<Integer, Transferable> transferableFactory) {
        this.transferableFactory = transferableFactory;
    }

    /**
     * 设置{@link Message}工厂，{@link ObjectReader}反序列化时需要用到
     */
    public final void setMessageFactory(Function<Integer, Message> messageFactory) {
        this.messageFactory = messageFactory;
    }

    /**
     * @see #reconnectTime
     */
    public void setReconnectTime(int reconnectTime) {
        this.reconnectTime = reconnectTime;
    }

    public Function<Integer, Transferable> getTransferableFactory() {
        return transferableFactory;
    }

    public Function<Integer, Message> getMessageFactory() {
        return messageFactory;
    }

    public int getReconnectTime() {
        return reconnectTime;
    }

    public synchronized void start() {
        logger.info("LocalServer.start()");
        workers.values().forEach(Worker::start);
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::update, 50, 50, TimeUnit.MILLISECONDS);
        startNetwork();
        remotes.values().forEach(RemoteServer::start);
    }

    public synchronized void stop() {
        logger.info("LocalServer.stop()");
        remotes.values().forEach(RemoteServer::stop);
        stopNetwork();
        scheduler.shutdown();
        scheduler = null;
        workers.values().forEach(Worker::stop);
    }

    protected abstract void startNetwork();

    protected abstract void stopNetwork();

    protected void update() {
        for (Worker worker : workers.values()) {
            worker.execute(worker::update);
        }
    }

    private Worker nextWorker() {
        List<Integer> workerIds = new ArrayList<>(workers.keySet());
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

    protected ObjectReader newReader(CodedBuffer buffer) {
        ObjectReader reader = new ObjectReader(buffer);
        reader.setTransferableFactory(transferableFactory);
        reader.setMessageFactory(messageFactory);
        return reader;
    }

    /**
     * 握手
     */
    protected void handshake(Handshake handshake) {
        int remoteId = handshake.getServerId();
        if (!remotes.containsKey(remoteId)) {
            addRemote(remoteId, handshake.getServerIp(), handshake.getServerPort());
        }
    }

    /**
     * 发送RPC请求
     */
    protected void sendRequest(int targetServerId, Request request) {
        if (targetServerId == this.id) {
            //本地服务器直接处理
            handleRequest(this.id, request);
        } else {
            RemoteServer remoteServer = remotes.get(targetServerId);
            if (remoteServer != null) {
                remoteServer.sendMsg(request);
            } else {
                logger.error("发送RPC请求,远程服务器[{}]不存在", targetServerId);
            }
        }
    }

    /**
     * 处理RPC请求
     */
    protected void handleRequest(int originServerId, Request request) {
        Service service = services.get(request.getServiceId());
        if (service == null) {
            logger.error("处理RPC请求,服务[{}]不存在", request.getServiceId());
        } else {
            Worker worker = service.getWorker();
            worker.execute(() -> worker.handleRequest(originServerId, request));
        }
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
                remoteServer.sendMsg(response);
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
