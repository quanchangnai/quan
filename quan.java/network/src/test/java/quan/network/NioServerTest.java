package quan.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.network.nio.NioServer;
import quan.network.nio.handler.Handler;
import quan.network.nio.handler.HandlerChain;
import quan.network.nio.handler.HandlerConfigurer;
import quan.network.nio.handler.HandlerContext;
import quan.network.nio.codec.LengthFieldCodec;
import quan.network.nio.codec.StringCodec;

import java.net.StandardSocketOptions;

public class NioServerTest {

    protected static final Logger logger = LoggerFactory.getLogger(NioServerTest.class);

    public static void main(String[] args) {
        NioServer server = new NioServer(8007);
        server.setReadBufferSize(1);
        server.setWriteBufferSize(1);
        server.setSocketOption(StandardSocketOptions.SO_RCVBUF, 1000);
        server.setSocketOption(StandardSocketOptions.SO_SNDBUF, 2);
        server.setHandler(new HandlerConfigurer() {
            @Override
            public void configureHandler(HandlerChain handlerChain) {
                handlerChain.addLast(new LengthFieldCodec(4, true));
                handlerChain.addLast(new StringCodec());
                handlerChain.addLast(new TestServerHandler());
            }
        });


        server.start();

        logger.error("服务器启动成功");
    }


    private static class TestServerHandler implements Handler<String> {

        @Override
        public void onConnected(HandlerContext handlerContext) {
            System.err.println("onConnected");

            new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handlerContext.send("aaa:" + System.nanoTime());
                }
            }).start();
        }

        @Override
        public void onDisconnected(HandlerContext handlerContext) {
            System.err.println("onDisconnected");
        }

        @Override
        public void onReceived(HandlerContext handlerContext, String msg) {
            System.err.println("onReceived:" + msg);
            handlerContext.send(msg);
        }

        @Override
        public void onExceptionCaught(HandlerContext handlerContext, Throwable cause) {
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
