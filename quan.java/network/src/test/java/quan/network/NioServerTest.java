package quan.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.network.codec.FrameCodec;
import quan.network.codec.StringCodec;
import quan.network.handler.Handler;
import quan.network.handler.HandlerChain;
import quan.network.handler.HandlerConfigurer;
import quan.network.handler.HandlerContext;

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
                handlerChain.addLast(new FrameCodec(4, true));
                handlerChain.addLast(new StringCodec());
                handlerChain.addLast(new TestServerHandler());
            }
        });


        server.start();

        logger.info("服务器启动成功");
    }


    private static class TestServerHandler implements Handler<String> {

        @Override
        public void onConnected(HandlerContext handlerContext) {
            logger.info("onConnected");

            new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handlerContext.sendMsg("aaa:" + System.nanoTime());
                }
            }).start();
        }

        @Override
        public void onDisconnected(HandlerContext handlerContext) {
            System.err.println("onDisconnected");
        }

        @Override
        public void onMsgReceived(HandlerContext handlerContext, String msg) {
            logger.info("onMsgReceived:" + msg);
            handlerContext.sendMsg(msg);
        }

        @Override
        public void onExceptionCaught(HandlerContext handlerContext, Throwable cause) {
            logger.error("onExceptionCaught", cause);
        }

        @Override
        public void onEventTriggered(HandlerContext handlerContext, Object event) {
            logger.info("onEventTriggered:{}", event);
            handlerContext.sendMsg("aaa:" + System.nanoTime());
        }
    }

}
