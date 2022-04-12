package quan.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.rpc.msg.Handshake;
import quan.rpc.msg.Request;
import quan.rpc.msg.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author quanchangnai
 */
public abstract class LocalServer {

    protected final static Logger logger = LoggerFactory.getLogger(LocalServer.class);

    private final int id;

    private final String ip;

    private final int port;

    //管理的所有工作线程，key:工作线程ID
    private final Map<Integer, Worker> workers = new HashMap<>();

    private int workerIndex;

    //管理的所有服务，key:服务ID
    private final Map<Object, Service> services = new HashMap<>();

    //管理的所有远程服务器，key:服务器ID
    private final Map<Integer, RemoteServer> remotes = new HashMap<>();

    private ScheduledExecutorService scheduler;

    public LocalServer(int id, String ip, int port, int workerNum) {
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

    public int getId() {
        return id;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public int getWorkerNum() {
        return workers.size();
    }

    public void start() {
        logger.info("RpcServer.start()");

        workers.values().forEach(Worker::start);
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::update, 50, 50, TimeUnit.MILLISECONDS);
        startNetwork();
    }

    protected abstract void startNetwork();

    public void stop() {
        logger.info("RpcServer.stop()");

        remotes.values().forEach(RemoteServer::stop);
        stopNetwork();
        scheduler.shutdown();
        workers.values().forEach(Worker::stop);

    }

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

    protected void handleMsg(Object msg) {
        if (msg instanceof Handshake) {
            handshake((Handshake) msg);
        } else if (msg instanceof Request) {
            handleRequest(1, (Request) msg);
        } else if (msg instanceof Response) {
            handleResponse((Response) msg);
        } else {
            logger.error("非法数据:{}", msg);
        }
    }

    protected void handshake(Handshake handshake) {
        int remoteId = handshake.getServerId();
        RemoteServer remoteServer = remotes.get(remoteId);
        if (remoteServer == null) {
            remoteServer = new RemoteServer(remoteId, handshake.getServerIp(), handshake.getServerPort());
            addRemote(remoteServer);
        }
    }

    public void addRemote(RemoteServer remoteServer) {
        remotes.put(remoteServer.getId(), remoteServer);
        remoteServer.start();
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
                remoteServer.sendRequest(request);
            } else {
                logger.error("发送RPC请求,目标服务器[{}]不存在", targetServerId);
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
                remoteServer.sendResponse(response);
            } else {
                logger.error("发送RPC响应，来源服务器[{}]不存在", originServerId);
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
