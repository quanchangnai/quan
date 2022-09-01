package quan.rpc;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.message.CodedBuffer;
import quan.message.NettyCodedBuffer;
import quan.rpc.protocol.*;
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

    private final String ip;

    private final int port;

    private int updateInterval = 50;

    private int reconnectInterval;

    private Function<CodedBuffer, ObjectReader> readerFactory = ObjectReader::new;

    private Function<CodedBuffer, ObjectWriter> writerFactory = ObjectWriter::new;

    /**
     * 使用服务名作为参数，调用后返回目标服务器ID
     */
    private Function<String, Integer> targetServerIdResolver;

    //管理的所有工作线程，key:工作线程ID
    private final Map<Integer, Worker> workers = new HashMap<>();

    private final List<Integer> workerIds = new ArrayList<>();

    private int workerIndex;

    //管理的所有服务，key:服务ID
    private final Map<Object, Service> services = new ConcurrentHashMap<>();

    //管理的所有远程服务器，key:服务器ID
    private final Map<Integer, RemoteServer> remotes = new HashMap<>();

    private ScheduledExecutorService executor;

    private ServerBootstrap serverBootstrap;

    public LocalServer(int id, String ip, int port, int workerNum) {
        Validate.isTrue(id > 0, "服务器ID必须是正整数");
        this.id = id;
        this.ip = ip;
        this.port = port;
        this.initWorkers(workerNum);
    }

    public LocalServer(int id, String ip, int port) {
        this(id, ip, port, 0);
    }

    public LocalServer(int id, int port) {
        this(id, "0.0.0.0", port, 0);
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

    /**
     * 设置远程服务器断线重连的间隔时间(s)
     */
    public void setReconnectInterval(int reconnectInterval) {
        this.reconnectInterval = reconnectInterval;
    }

    public int getReconnectInterval() {
        return reconnectInterval;
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

    /**
     * 设置用来查找目标服务器ID的Resolver，如果服务的目标服务器是单进程的，可以省去每次构造服务代理都必需要传参的麻烦
     *
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
        workerIds.addAll(workers.keySet());
    }

    private Worker nextWorker() {
        int workerId = workerIds.get(workerIndex++);
        if (workerIndex >= workerIds.size()) {
            workerIndex = 0;
        }
        return workers.get(workerId);
    }

    public synchronized void start() {
        workers.values().forEach(Worker::start);
        startNetwork();
        remotes.values().forEach(RemoteServer::start);
        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(this::update, updateInterval, updateInterval, TimeUnit.MILLISECONDS);
    }

    public synchronized void stop() {
        executor.shutdown();
        executor = null;
        remotes.values().forEach(RemoteServer::stop);
        stopNetwork();
        workers.values().forEach(Worker::stop);
    }

    protected void startNetwork() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup);
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        serverBootstrap.handler(new LoggingHandler(LogLevel.INFO));
        serverBootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

            @Override
            @SuppressWarnings("NullableProblems")
            public void initChannel(SocketChannel ch) {
                ChannelPipeline p = ch.pipeline();
                p.addLast(new LengthFieldPrepender(4));
                p.addLast(new LengthFieldBasedFrameDecoder(100000, 0, 4, 0, 4));
                p.addLast(new ChannelHandler());
            }
        });

        serverBootstrap.bind(ip, port);
    }

    protected void stopNetwork() {
        if (serverBootstrap != null) {
            serverBootstrap.config().group().shutdownGracefully();
            serverBootstrap.config().childGroup().shutdownGracefully();
        }
    }

    protected void update() {
        remotes.values().forEach(RemoteServer::update);
        for (Worker worker : workers.values()) {
            worker.tryUpdate();
        }
    }

    public void addService(Service service) {
        addService(nextWorker(), service);
    }

    public void addService(Worker worker, Service service) {
        Object serviceId = service.getId();
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

    public synchronized void addRemote(int remoteId, String remoteIp, int remotePort) {
        if (remotes.containsKey(remoteId)) {
            logger.error("远程服务器[{}]已存在", remoteId);
            return;
        }

        RemoteServer remoteServer = new RemoteServer(remoteId, remoteIp, remotePort);
        remoteServer.setLocalServer(this);
        remotes.put(remoteServer.getId(), remoteServer);

        if (executor != null) {
            remoteServer.start();
        }
    }

    /**
     * 处理RPC握手逻辑
     */
    protected void handleHandshake(Handshake handshake) {
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
    protected void sendRequest(int targetServerId, Request request, int securityModifier) {
        if (targetServerId == this.id || targetServerId == 0) {
            //本地服务器直接处理
            handleRequest(this.id, request, securityModifier);
        } else {
            RemoteServer remoteServer = remotes.get(targetServerId);
            if (remoteServer != null) {
                remoteServer.sendProtocol(request);
            } else {
                logger.error("发送RPC请求，远程服务器[{}]不存在", targetServerId);
            }
        }
    }

    /**
     * 处理RPC请求
     */
    protected void handleRequest(int originServerId, Request request, int securityModifier) {
        Service service = services.get(request.getServiceId());
        if (service == null) {
            logger.error("处理RPC请求，服务[{}]不存在", request.getServiceId());
        } else {
            Worker worker = service.getWorker();
            worker.execute(() -> worker.handleRequest(originServerId, request, securityModifier));
        }
    }


    protected void handleRequest(int originServerId, Request request) {
        handleRequest(originServerId, request, 0b11);
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
                remoteServer.sendProtocol(response);
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

    protected void sendProtocol(ChannelHandlerContext context, Protocol protocol) {
        ByteBuf byteBuf = context.alloc().buffer();
        try {
            ObjectWriter objectWriter = writerFactory.apply(new NettyCodedBuffer(byteBuf));
            objectWriter.write(protocol);
            // TODO 刷新会执行系统调用，需要优化
            context.writeAndFlush(byteBuf);
        } catch (Throwable e) {
            byteBuf.release();
            logger.error("", e);
        }
    }

    private class ChannelHandler extends ChannelInboundHandlerAdapter {

        private int remoteServerId;

        @Override
        @SuppressWarnings("NullableProblems")
        public void channelRead(ChannelHandlerContext context, Object msg) {
            CodedBuffer buffer = new NettyCodedBuffer((ByteBuf) msg);
            Protocol protocol = readerFactory.apply(buffer).read();

            if (protocol instanceof Handshake) {
                Handshake handshake = (Handshake) protocol;
                remoteServerId = handshake.getServerId();
                handleHandshake(handshake);
            } else if (protocol instanceof PingPong) {
                PingPong pingPong = (PingPong) protocol;
                handlePingPong(remoteServerId, pingPong);
                pingPong.setTime(System.currentTimeMillis());
                sendProtocol(context, protocol);
            } else if (protocol instanceof Request) {
                handleRequest(remoteServerId, (Request) protocol);
            } else if (protocol instanceof Response) {
                handleResponse((Response) protocol);
            } else {
                logger.error("收到非法RPC协议:{}", protocol);
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            logger.error("", cause);
        }

    }

}
