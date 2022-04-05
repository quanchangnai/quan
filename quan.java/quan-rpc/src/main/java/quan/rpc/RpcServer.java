package quan.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author quanchangnai
 */
public class RpcServer {

    private Logger logger = LoggerFactory.getLogger(RpcServer.class);

    private int id;

    //管理的所有工作线程，key:工作线程ID
    private Map<Integer, Worker> workers = new HashMap<>();

    //管理的所有服务，key:服务ID
    private Map<Object, Service> services = new HashMap<>();

    private ScheduledExecutorService scheduler;

    public RpcServer(int id, int workerNum) {
        this.id = id;
        if (workerNum <= 0) {
            workerNum = Runtime.getRuntime().availableProcessors();
        }
        for (int i = 1; i <= workerNum; i++) {
            workers.put(i, new Worker(i, this));
        }
    }

    public int getId() {
        return id;
    }

    public int getWorkerNum() {
        return workers.size();
    }

    public void start() {
        logger.info("RpcServer.start()");
        for (Worker worker : workers.values()) {
            worker.start();
        }
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::update, 50, 50, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        logger.info("RpcServer.stop()");
        scheduler.shutdown();
        for (Worker worker : workers.values()) {
            worker.stop();
        }
    }

    protected void update() {
        for (Worker worker : workers.values()) {
            worker.execute(worker::update);
        }
    }

    public synchronized void addService(Service service) {
        Object serviceId = service.getId();
        if (services.putIfAbsent(serviceId, service) != null) {
            logger.error("RPC服务[{}]已存在", serviceId);
            return;
        }
        randomThread().addService(service);
    }

    public synchronized void removeService(Service service) {
        Object serviceId = service.getId();
        if (!services.remove(serviceId, service)) {
            logger.error("RPC服务[{}]不存在", serviceId);
        }
        service.getWorker().removeService(service);
    }

    protected Worker randomThread() {
        int workerId = new Random().nextInt(workers.size()) + 1;
        return workers.get(workerId);
    }

    /**
     * 发送RPC请求
     */
    protected void sendRequest(int targetServerId, Request request) {
        if (targetServerId == this.id) {
            //本地服务器直接处理
            handleRequest(this.id, request);
        }

        //TODO 发送到其他服务器
    }

    /**
     * 处理RPC请求
     */
    protected void handleRequest(int originServerId, Request request) {
        Service service = services.get(request.getServiceId());
        if (service == null) {
            logger.error("处理RPC请求,服务[{}]不存在", request.getServiceId());
            return;
        }

        Worker worker = service.getWorker();
        worker.execute(() -> worker.handleRequest(originServerId, request));
    }

    /**
     * 发送RPC响应
     */
    protected void sendResponse(int originServerId, Response response) {
        if (originServerId == this.id) {
            //本地服务器直接处理
            handleResponse(response);
        }

        //TODO 发送到其他服务器
    }

    /**
     * 处理RPC响应
     */
    protected void handleResponse(Response response) {
        int workerId = (int) (response.getCallId() >> 32);
        Worker worker = workers.get(workerId);
        if (worker == null) {
            logger.error("处理RPC响应，工作线程[{}]不存在", workerId);
            return;
        }

        worker.execute(() -> worker.handleResponse(response));
    }

}
