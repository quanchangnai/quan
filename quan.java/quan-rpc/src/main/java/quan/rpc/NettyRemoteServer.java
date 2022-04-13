package quan.rpc;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import quan.message.NettyCodedBuffer;
import quan.rpc.serialize.ObjectWriter;

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
        EventLoopGroup group = new NioEventLoopGroup();
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
    protected void sendMsg(Object msg) {
        if (context == null) {
            logger.error("连接还未建立，不能发送RPC消息");
            return;
        }
        ByteBuf byteBuf = context.alloc().buffer();
        ObjectWriter objectWriter = new ObjectWriter(new NettyCodedBuffer(byteBuf));
        objectWriter.write(msg);
        // TODO 刷新会执行系统调用，需要优化
        context.writeAndFlush(byteBuf);
    }

    private class ChannelHandler extends ChannelDuplexHandler {

        @Override
        public void channelActive(ChannelHandlerContext context) {
            NettyRemoteServer.this.context = context;
            handshake();
        }

        @Override
        public void channelInactive(ChannelHandlerContext context) {
            NettyRemoteServer.this.context = null;
            logger.error("连接断开，将在{}秒后尝试重连: {}", getReconnectTime(), context.channel().remoteAddress());
            reconnect();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            logger.error("", cause);
        }

    }

}
