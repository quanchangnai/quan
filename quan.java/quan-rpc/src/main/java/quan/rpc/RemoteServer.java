package quan.rpc;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.rpc.protocol.Handshake;
import quan.rpc.protocol.PingPong;
import quan.rpc.protocol.Protocol;

import java.util.concurrent.TimeUnit;

/**
 * @author quanchangnai
 */
public class RemoteServer {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private int id;

    private String ip;

    private int port;

    private int reconnectInterval = 5;

    protected LocalServer localServer;

    private boolean connected;

    private long lastSendPingPongTime;

    private long lastHandlePingPongTime = System.currentTimeMillis();

    private long lastReportSuspendedTime;

    private Bootstrap bootstrap;

    private ChannelHandlerContext context;

    protected RemoteServer(int id, String ip, int port) {
        Validate.isTrue(id > 0, "服务器ID必须是正整数");
        this.id = id;
        this.ip = ip;
        this.port = port;
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

    public final int getReconnectInterval() {
        return reconnectInterval;
    }

    final void setLocalServer(LocalServer localServer) {
        this.localServer = localServer;
        if (localServer.getReconnectInterval() > 0) {
            this.reconnectInterval = localServer.getReconnectInterval();
        }
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
                    @SuppressWarnings("NullableProblems")
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new LengthFieldPrepender(4));
                        p.addLast(new LengthFieldBasedFrameDecoder(100000, 0, 4, 0, 4));
                        p.addLast(new ChannelHandler());
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
        ChannelFuture channelFuture = bootstrap.connect(getIp(), getPort());
        channelFuture.addListener(future -> {
            if (!future.isSuccess()) {
                logger.error("连接失败，将在{}秒后尝试重连，失败原因：{}", reconnectInterval, future.cause().getMessage());
                reconnect();
            }
        });
    }

    protected void reconnect() {
        if (bootstrap != null) {
            bootstrap.config().group().schedule(this::connect, reconnectInterval, TimeUnit.SECONDS);
        }
    }

    protected void sendProtocol(Protocol protocol) {
        if (context == null) {
            logger.error("连接还未建立，不能发送RPC协议");
        } else {
            localServer.sendProtocol(context, protocol);
        }
    }

    protected void setConnected(boolean connected) {
        this.connected = connected;
        if (connected) {
            Handshake handshake = new Handshake(localServer.getId(), localServer.getIp(), localServer.getPort());
            sendProtocol(handshake);
        }
    }

    public boolean isConnected() {
        return connected;
    }

    protected void update() {
        checkSuspended();
        sendPingPong();
    }

    protected void checkSuspended() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastHandlePingPongTime > 30000 && currentTime - lastReportSuspendedTime > 60000) {
            logger.error("远程服务器[{}]的连接可能已经进入假死状态了", localServer.getId());
            lastReportSuspendedTime = currentTime;
        }
    }

    protected void sendPingPong() {
        if (!connected) {
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
            logger.debug("远程服务器[{}]的延迟时间为：{}ms", this.id, lastHandlePingPongTime - pingPong.getTime());
        }
    }

    private class ChannelHandler extends ChannelInboundHandlerAdapter {

        @Override
        @SuppressWarnings("NullableProblems")
        public void channelActive(ChannelHandlerContext context) {
            RemoteServer.this.context = context;
            setConnected(true);
        }

        @Override
        public void channelInactive(ChannelHandlerContext context) {
            RemoteServer.this.context = null;
            setConnected(false);
            logger.error("连接断开，将在{}秒后尝试重连: {}", reconnectInterval, context.channel().remoteAddress());
            reconnect();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            logger.error("", cause);
        }

    }

}
