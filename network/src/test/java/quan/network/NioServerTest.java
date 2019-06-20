package quan.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.network.bootstrap.ServerBootstrap;
import quan.network.handler.Handler;
import quan.network.handler.HandlerChain;
import quan.network.handler.HandlerConfigurer;
import quan.network.handler.HandlerContext;
import quan.network.handler.codec.LengthFieldCodec;
import quan.network.handler.codec.MessageCodec;

import java.net.StandardSocketOptions;

public class NioServerTest {

    protected static final Logger logger = LoggerFactory.getLogger(NioServerTest.class);

    public static void main(String[] args) throws Exception {
        ServerBootstrap server = new ServerBootstrap(8007);
        server.setReadBufferSize(1);
        server.setWriteBufferSize(1);
        server.setSocketOption(StandardSocketOptions.SO_RCVBUF, 1000);
        server.setSocketOption(StandardSocketOptions.SO_SNDBUF, 2);
        server.setHandler(new HandlerConfigurer() {
            @Override
            public void configureHandler(HandlerChain handlerChain) throws Exception {
                handlerChain.addLast(new LengthFieldCodec(4, true));
                handlerChain.addLast(new MessageCodec(NetworkTest.messageRegistry));
                handlerChain.addLast(new TestServerHandler());
            }
        });


        server.start();

    }


    private static class TestServerHandler implements Handler<String> {

        private static final Logger logger = LoggerFactory.getLogger(TestServerHandler.class);


        @Override
        public void onConnected(HandlerContext handlerContext) throws Exception {
            System.err.println("onConnected");

            new Thread() {
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        SRoleLogin sRoleLogin = new SRoleLogin();
                        sRoleLogin.setRoleId(123);
                        sRoleLogin.setRoleName("张三");
                        handlerContext.send(sRoleLogin);
                    }
                }
            }.start();
        }

        @Override
        public void onDisconnected(HandlerContext handlerContext) throws Exception {
            System.err.println("onDisconnected");
        }

        @Override
        public void onReceived(HandlerContext handlerContext, String msg) throws Exception {
            System.err.println("onReceived:" + msg);
            handlerContext.send(msg);
        }

        @Override
        public void onExceptionCaught(HandlerContext handlerContext, Throwable cause) throws Exception {
            System.err.println("onExceptionCaught");
            cause.printStackTrace();
        }

        @Override
        public void onEventTriggered(HandlerContext handlerContext, Object event) {
            System.err.println("onEventTriggered:" + event);
            handlerContext.send("aaa:" + System.nanoTime());
        }
    }

}
