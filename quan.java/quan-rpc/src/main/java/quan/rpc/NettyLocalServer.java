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
import quan.message.CodedBuffer;
import quan.message.NettyCodedBuffer;

/**
 * @author quanchangnai
 */
public class NettyLocalServer extends LocalServer {

    private ServerBootstrap serverBootstrap;

    public NettyLocalServer(int id, String ip, int port, int workerNum) {
        super(id, ip, port, workerNum);
    }

    public NettyLocalServer(int id, int port, int workerNum) {
        this(id, null, port, workerNum);
    }

    public NettyLocalServer(int id, int port) {
        this(id, null, port, 0);
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

        if (getIp() != null) {
            serverBootstrap.bind(getIp(), getPort());
        } else {
            serverBootstrap.bind(getPort());
        }
    }

    @Override
    protected void stopNetwork() {
        if (serverBootstrap != null) {
            serverBootstrap.config().group().shutdownGracefully();
            serverBootstrap.config().childGroup().shutdownGracefully();
        }
    }

    private class ChannelHandler extends ChannelDuplexHandler {

        @Override
        public void channelRead(ChannelHandlerContext context, Object msg) {
            CodedBuffer buffer = new NettyCodedBuffer((ByteBuf) msg);
            ObjectReader reader = new ObjectReader(buffer);
            NettyLocalServer.this.handleMsg(reader.read());
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            logger.error("", cause);
        }

    }

}
