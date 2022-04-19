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
import quan.rpc.protocol.*;
import quan.rpc.serialize.ObjectWriter;

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

    @Override
    protected RemoteServer newRemote(int remoteId, String remoteIp, int remotePort) {
        return new NettyRemoteServer(remoteId, remoteIp, remotePort);
    }

    protected void send(ChannelHandlerContext context, Protocol protocol) {
        ByteBuf byteBuf = context.alloc().buffer();
        try {
            ObjectWriter objectWriter = getWriterFactory().apply(new NettyCodedBuffer(byteBuf));
            objectWriter.write(protocol);
            // TODO 刷新会执行系统调用，需要优化
            context.writeAndFlush(byteBuf);
        } catch (Throwable e) {
            byteBuf.release();
            logger.error("", e);
        }
    }

    private class ChannelHandler extends ChannelDuplexHandler {

        private int remoteServerId;

        @Override
        public void channelRead(ChannelHandlerContext context, Object msg) {
            CodedBuffer buffer = new NettyCodedBuffer((ByteBuf) msg);
            Protocol protocol = getReaderFactory().apply(buffer).read();

            if (protocol instanceof Handshake) {
                Handshake handshake = (Handshake) protocol;
                remoteServerId = handshake.getServerId();
                handshake(handshake);
            } else if (protocol instanceof PingPong) {
                PingPong pingPong = (PingPong) protocol;
                handlePingPong(remoteServerId, pingPong);
                pingPong.setTime(System.currentTimeMillis());
                send(context, protocol);
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