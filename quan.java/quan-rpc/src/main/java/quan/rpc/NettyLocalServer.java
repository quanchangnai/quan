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
import io.netty.util.AttributeKey;
import quan.message.CodedBuffer;
import quan.message.NettyCodedBuffer;
import quan.rpc.msg.Handshake;
import quan.rpc.msg.Request;
import quan.rpc.msg.Response;

/**
 * @author quanchangnai
 */
public class NettyLocalServer extends LocalServer {

    private ServerBootstrap serverBootstrap;

    public NettyLocalServer(int id, String ip, int port, int workerNum) {
        super(id, ip, port, workerNum);
    }

    public NettyLocalServer(int id, String ip, int port) {
        this(id, ip, port, 0);
    }

    @Override
    protected RemoteServer newRemote(int remoteId, String remoteIp, int remotePort) {
        return new NettyRemoteServer(remoteId, remoteIp, remotePort);
    }

    @Override
    protected void startNetwork() {
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
                        p.addLast(new ChannelHandler());
                    }
                });

        serverBootstrap.bind(getIp(), getPort());
    }

    @Override
    protected void stopNetwork() {
        if (serverBootstrap != null) {
            serverBootstrap.config().group().shutdownGracefully();
            serverBootstrap.config().childGroup().shutdownGracefully();
        }
    }

    private class ChannelHandler extends ChannelDuplexHandler {

        private final AttributeKey<Integer> key = AttributeKey.valueOf("originServerId");

        @Override
        public void channelRead(ChannelHandlerContext context, Object msg) {
            CodedBuffer buffer = new NettyCodedBuffer((ByteBuf) msg);
            msg = newReader(buffer).read();

            if (msg instanceof Handshake) {
                Handshake handshake = (Handshake) msg;
                context.channel().attr(key).setIfAbsent(handshake.getServerId());
                handshake(handshake);
            } else if (msg instanceof Request) {
                handleRequest(context.channel().attr(key).get(), (Request) msg);
            } else if (msg instanceof Response) {
                handleResponse((Response) msg);
            } else {
                logger.error("收到非法RPC消息:{}", msg);
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            logger.error("", cause);
        }

    }

}
