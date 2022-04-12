package quan.rpc;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import quan.message.CodedBuffer;
import quan.message.NettyCodedBuffer;
import quan.rpc.msg.Request;
import quan.rpc.msg.Response;

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
    public void start() {
        super.start();
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
    public void stop() {
        super.stop();
        if (bootstrap != null) {
            bootstrap.config().group().shutdownGracefully();
        }
    }

    protected void connect() {
        ChannelFuture channelFuture = bootstrap.connect(getIp(), getPort());
        channelFuture.addListener(future -> {
            if (!future.isSuccess()) {
                reconnect();
            }
        });
    }

    private void reconnect() {
        bootstrap.config().group().schedule(this::connect, 5, TimeUnit.SECONDS);
    }


    @Override
    protected void sendRequest(Request request) {
        context.write(request);
    }

    @Override
    protected void sendResponse(Response response) {
        context.write(response);
    }

    private class ChannelHandler extends ChannelDuplexHandler {

        @Override
        public void channelActive(ChannelHandlerContext context) {
            NettyRemoteServer.this.context = context;
        }

        @Override
        public void channelInactive(ChannelHandlerContext context) {
            NettyRemoteServer.this.context = null;
            NettyRemoteServer.this.reconnect();
        }

        @Override
        public void write(ChannelHandlerContext context, Object msg, ChannelPromise promise) {
            ByteBuf byteBuf = context.alloc().buffer();
            CodedBuffer buffer = new NettyCodedBuffer(byteBuf);
            ObjectWriter writer = new ObjectWriter(buffer);
            writer.write(msg);
            context.write(byteBuf, promise);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            logger.error("", cause);
        }

    }

}
