package quan.rpc;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
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
import quan.rpc.serialize.ObjectWriter;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 基于Netty的网络连接器
 *
 * @author quanchangnai
 */
public class NettyConnector extends Connector {

    protected final static Logger logger = LoggerFactory.getLogger(NettyConnector.class);

    /**
     * 重连时间间隔(秒)
     */
    private int reconnectInterval = 5;

    private Receiver receiver;

    private final Map<Integer, Sender> senders = new ConcurrentHashMap<>();

    public NettyConnector(String ip, int port) {
        receiver = new Receiver(ip, port, this);
    }

    public void setReconnectInterval(int reconnectInterval) {
        this.reconnectInterval = reconnectInterval;
    }

    public int getReconnectInterval() {
        return reconnectInterval;
    }

    public LocalServer getLocalServer() {
        return localServer;
    }

    @Override
    protected void start() {
        receiver.start();
        senders.values().forEach(Sender::start);
    }

    @Override
    protected void stop() {
        receiver.stop();
        senders.values().forEach(Sender::stop);
    }

    @Override
    protected void update() {
        senders.values().forEach(Sender::update);
    }

    public boolean addRemote(int remoteId, String remoteIp, int remotePort) {
        if (senders.containsKey(remoteId) || localServer != null && localServer.hasRemote(remoteId)) {
            logger.error("远程服务器[{}]已存在", remoteId);
            return false;
        }

        Sender sender = new Sender(remoteId, remoteIp, remotePort, this);
        senders.put(remoteId, sender);

        if (localServer != null && localServer.isRunning()) {
            sender.start();
        }

        return true;
    }

    public boolean removeRemote(int remoteId) {
        Sender sender = senders.remove(remoteId);
        if (sender != null) {
            sender.stop();
        }
        return sender != null;
    }

    public Set<Integer> getRemoteIds() {
        return senders.keySet();
    }

    @Override
    protected void sendProtocol(int remoteId, Protocol protocol) {
        senders.get(remoteId).sendProtocol(protocol);
    }

    protected void sendProtocol(ChannelHandlerContext context, Protocol protocol) {
        ByteBuf byteBuf = context.alloc().buffer();
        try {
            ObjectWriter objectWriter = localServer.getWriterFactory().apply(new NettyCodedBuffer(byteBuf));
            objectWriter.write(protocol);
            // TODO 刷新会执行系统调用，需要优化
            context.writeAndFlush(byteBuf);
        } catch (Throwable e) {
            byteBuf.release();
            logger.error("", e);
        }
    }

    /**
     * 处理RPC握手逻辑
     */
    protected void handleHandshake(Handshake handshake) {
        int remoteId = handshake.getServerId();
        if (!senders.containsKey(remoteId)) {
            addRemote(remoteId, handshake.getServerIp(), handshake.getServerPort());
        }
    }

    protected void handlePingPong(int originServerId, PingPong pingPong) {
        Sender sender = senders.get(originServerId);
        if (sender != null) {
            sender.handlePingPong(pingPong);
        }
        pingPong.setTime(System.currentTimeMillis());
    }

    /**
     * 用于接收远程服务器的数据
     */
    private static class Receiver {

        private NettyConnector connector;

        private final String ip;

        private final int port;

        private ServerBootstrap serverBootstrap;

        public Receiver(String ip, int port, NettyConnector connector) {
            this.ip = ip;
            this.port = port;
            this.connector = connector;
        }

        public void start() {
            EventLoopGroup bossGroup = new NioEventLoopGroup(1);
            EventLoopGroup workerGroup = new NioEventLoopGroup();

            serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        public void initChannel(SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new LengthFieldPrepender(4));
                            p.addLast(new LengthFieldBasedFrameDecoder(100000, 0, 4, 0, 4));
                            p.addLast(new ChannelInboundHandlerAdapter() {

                                private int remoteServerId;

                                @Override
                                public void channelRead(ChannelHandlerContext context, Object msg) {
                                    CodedBuffer buffer = new NettyCodedBuffer((ByteBuf) msg);
                                    Protocol protocol = connector.localServer.getReaderFactory().apply(buffer).read();

                                    if (protocol instanceof Handshake) {
                                        Handshake handshake = (Handshake) protocol;
                                        remoteServerId = handshake.getServerId();
                                        connector.handleHandshake(handshake);
                                    } else if (protocol instanceof PingPong) {
                                        PingPong pingPong = (PingPong) protocol;
                                        connector.handlePingPong(remoteServerId, pingPong);
                                        connector.sendProtocol(context, protocol);
                                    } else if (protocol instanceof Request) {
                                        connector.localServer.handleRequest(remoteServerId, (Request) protocol);
                                    } else if (protocol instanceof Response) {
                                        connector.localServer.handleResponse((Response) protocol);
                                    } else {
                                        logger.error("收到非法RPC协议:{}", protocol);
                                    }
                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                                    logger.error("", cause);
                                }

                            });
                        }
                    });

            serverBootstrap.bind(ip, port);
        }

        public void stop() {
            if (serverBootstrap != null) {
                serverBootstrap.config().group().shutdownGracefully();
                serverBootstrap.config().childGroup().shutdownGracefully();
                serverBootstrap = null;
            }
        }
    }

    /**
     * 用于向远程服务器发送数据
     */
    private static class Sender {

        protected static final Logger logger = LoggerFactory.getLogger(Sender.class);

        //远程服务器ID
        private int id;

        private String ip;

        private int port;

        protected NettyConnector connector;

        private long lastSendPingPongTime;

        private long lastHandlePingPongTime = System.currentTimeMillis();

        private long lastReportSuspendedTime;

        private Bootstrap bootstrap;

        private ChannelHandlerContext context;

        protected Sender(int id, String ip, int port, NettyConnector connector) {
            Validate.isTrue(id > 0, "服务器ID必须是正整数");
            this.id = id;
            this.ip = ip;
            this.port = port;
            this.connector = connector;
        }

        protected void start() {
            EventLoopGroup group = new NioEventLoopGroup(1);
            bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new LengthFieldPrepender(4));
                            p.addLast(new LengthFieldBasedFrameDecoder(100000, 0, 4, 0, 4));
                            p.addLast(new ChannelInboundHandlerAdapter() {

                                @Override
                                public void channelActive(ChannelHandlerContext context) {
                                    Sender.this.context = context;
                                    sendHandshake();
                                }

                                @Override
                                public void channelInactive(ChannelHandlerContext context) {
                                    Sender.this.context = null;
                                    if (bootstrap != null) {
                                        logger.error("连接断开，将在{}秒后尝试重连: {}", connector.reconnectInterval, context.channel().remoteAddress());
                                        reconnect();
                                    }
                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                                    logger.error("", cause);
                                }

                            });
                        }
                    });

            connect();
        }

        protected void stop() {
            if (bootstrap != null) {
                bootstrap.config().group().shutdownGracefully();
                bootstrap = null;
            }
        }

        protected void connect() {
            ChannelFuture channelFuture = bootstrap.connect(ip, port);
            channelFuture.addListener(future -> {
                if (!future.isSuccess()) {
                    logger.error("连接失败，将在{}秒后尝试重连，失败原因：{}", connector.reconnectInterval, future.cause().getMessage());
                    reconnect();
                }
            });
        }

        protected void reconnect() {
            if (bootstrap != null) {
                bootstrap.config().group().schedule(this::connect, connector.reconnectInterval, TimeUnit.SECONDS);
            }
        }

        protected void sendProtocol(Protocol protocol) {
            if (context == null) {
                logger.error("连接还未建立，不能发送RPC协议");
            } else {
                connector.sendProtocol(context, protocol);
            }
        }

        protected void sendHandshake() {
            Handshake handshake = new Handshake(connector.getLocalServer().getId(), connector.receiver.ip, connector.receiver.port);
            sendProtocol(handshake);
        }

        protected void update() {
            checkSuspended();
            sendPingPong();
        }

        protected void checkSuspended() {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastHandlePingPongTime > 30000 && currentTime - lastReportSuspendedTime > 60000) {
                logger.error("远程服务器[{}]的连接可能已经进入假死状态了", connector.getLocalServer().getId());
                lastReportSuspendedTime = currentTime;
            }
        }

        protected void sendPingPong() {
            if (context == null) {
                return;
            }

            long currentTime = System.currentTimeMillis();
            if (lastSendPingPongTime + 5000 < currentTime) {
                sendProtocol(new PingPong(currentTime));
                lastSendPingPongTime = currentTime;
            }
        }

        protected void handlePingPong(PingPong pingPong) {
            lastHandlePingPongTime = System.currentTimeMillis();
            if (logger.isDebugEnabled()) {
                logger.debug("远程服务器[{}]的延迟时间为：{}ms", id, lastHandlePingPongTime - pingPong.getTime());
            }
        }

    }

}
