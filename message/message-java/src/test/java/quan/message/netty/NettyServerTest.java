package quan.message.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import quan.message.Message;
import quan.message.MessageTest;
import quan.message.NettyMessageCodec;
import quan.message.role.SRoleLogin;

/**
 * Created by quanchangnai on 2019/7/11.
 */
public class NettyServerTest {

    public static void main(String[] args) throws Exception {

        NioEventLoopGroup bossEventLoopGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerEventLoopGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(bossEventLoopGroup, workerEventLoopGroup);
            serverBootstrap.channel(NioServerSocketChannel.class);

            serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LengthFieldPrepender(4));
                    ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(100000, 0, 4,0,4));
                    ch.pipeline().addLast(new NettyMessageCodec(MessageTest.messageFactory));
                    ch.pipeline().addLast(new NettyServerHandler());
                }
            });

            ChannelFuture channelFuture = serverBootstrap.bind(9898).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossEventLoopGroup.shutdownGracefully();
            workerEventLoopGroup.shutdownGracefully();
        }

    }


    private static class NettyServerHandler extends SimpleChannelInboundHandler<Message> {

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            new Thread(() -> {
                while (true) {

                    SRoleLogin sRoleLogin = new SRoleLogin();
                    sRoleLogin.setRoleId(System.currentTimeMillis());
                    sRoleLogin.setRoleName("name:" + System.currentTimeMillis());

                    ctx.writeAndFlush(sRoleLogin);

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
            System.err.println(msg);
        }

    }

}
