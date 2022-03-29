package quan.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

/**
 * @author quanchangnai
 */
public class RpcServer {

    private Logger logger = LoggerFactory.getLogger(RpcServer.class);

    private int id;

    //所有的服务，key:服务ID
    private Map<Object, Service> services = new HashMap<>();

    //所有的线程
    private Map<Integer, RpcThread> threads = new HashMap<>();

    private ScheduledExecutorService scheduler;

    public RpcServer(int id, int threadNum) {
        this.id = id;
        if (threadNum <= 0) {
            threadNum = Runtime.getRuntime().availableProcessors();
        }
        for (int i = 1; i <= threadNum; i++) {
            threads.put(i, new RpcThread(i, this));
        }
    }

    public int getId() {
        return id;
    }

    public int getThreadNum() {
        return threads.size();
    }

    public void start() {
        logger.info("RpcServer.start()");
        for (RpcThread thread : threads.values()) {
            thread.start();
        }
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::update, 50, 50, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        logger.info("RpcServer.stop()");
        scheduler.shutdown();
        for (RpcThread thread : threads.values()) {
            thread.stop();
        }
    }

    protected void update() {
        for (RpcThread thread : threads.values()) {
            thread.execute(thread::update);
        }
    }

    public synchronized void addService(Service service) {
        Object id = service.getId();
        if (services.putIfAbsent(id, service) != null) {
            logger.error("RPC服务[{}]已存在", id);
            return;
        }
        randomThread().addService(service);
    }

    public synchronized void removeService(Service service) {
        Object id = service.getId();
        if (!services.remove(id, service)) {
            logger.error("RPC服务[{}]不存在", id);
        }
        service.getThread().removeService(service);
    }

    protected RpcThread randomThread() {
        int threadId = new Random().nextInt(threads.size()) + 1;
        return threads.get(threadId);
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

        RpcThread thread = service.getThread();
        thread.execute(() -> thread.handleRequest(originServerId, request));
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
        int threadId = (int) (response.getCallId() >> 32);
        RpcThread thread = threads.get(threadId);
        if (thread == null) {
            logger.error("处理RPC响应，线程[{}]不存在", threadId);
            return;
        }

        thread.execute(() -> thread.handleResponse(response));
    }

}
