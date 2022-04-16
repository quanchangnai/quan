package quan.rpc;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import quan.rpc.protocol.Protocol;

import java.util.concurrent.TimeUnit;

/**
 * @author quanchangnai
 */
public class NettyRemoteServer extends RemoteServer {

    private Bootstrap bootstrap;

    private ChannelHandlerContext context;

    public NettyRemoteServer(int id, String ip, int port) {
        super(id, ip, port);
    }

    @Override
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
                        p.addLast(new ChannelHandler());
                    }
                });

        connect();
    }

    @Override
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
                logger.error("连接失败，将在{}秒后尝试重连，失败原因：{}", getReconnectTime(), future.cause().getMessage());
                reconnect();
            }
        });
    }

    private void reconnect() {
        if (bootstrap != null) {
            bootstrap.config().group().schedule(this::connect, getReconnectTime(), TimeUnit.SECONDS);
        }
    }

    @Override
    protected void send(Protocol protocol) {
        if (context == null) {
            logger.error("连接还未建立，不能发送RPC协议");
        } else {
            ((NettyLocalServer) localServer).send(context, protocol);
        }
    }

    private class ChannelHandler extends ChannelDuplexHandler {

        @Override
        public void channelActive(ChannelHandlerContext context) {
            NettyRemoteServer.this.context = context;
            setActivated(true);
        }

        @Override
        public void channelInactive(ChannelHandlerContext context) {
            NettyRemoteServer.this.context = null;
            setActivated(false);
            logger.error("连接断开，将在{}秒后尝试重连: {}", getReconnectTime(), context.channel().remoteAddress());
            reconnect();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            logger.error("", cause);
        }

    }

}
