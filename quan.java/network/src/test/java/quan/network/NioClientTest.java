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

public class NioClientTest {

    protected static final Logger logger = LoggerFactory.getLogger(NioClientTest.class);

    public static void main(String[] args) {
        NioClient client = new NioClient("127.0.0.1", 8007);
        client.setSocketOption(StandardSocketOptions.SO_KEEPALIVE, true);
        client.setSocketOption(StandardSocketOptions.SO_RCVBUF, 2);
        client.setSocketOption(StandardSocketOptions.SO_SNDBUF, 2);
        client.setHandler(new HandlerConfigurer() {
            @Override
            public void configureHandler(HandlerChain handlerChain) {
                handlerChain.addLast(new FrameCodec(4, true));
                handlerChain.addLast(new StringCodec());
                handlerChain.addLast(new TestClientHandler());

            }
        });
        client.setReconnectInterval(15 * 1000);
        client.start();


    }

    private static class TestClientHandler implements Handler<String> {

        @Override
        public void onConnected(HandlerContext handlerContext) {
            logger.info("onConnected");
        }

        @Override
        public void onDisconnected(HandlerContext handlerContext) {
            logger.info("onDisconnected");
        }

        @Override
        public void onMsgReceived(final HandlerContext handlerContext, final String msg) {
            logger.info("onMsgReceived:" + msg);
        }

        @Override
        public void onExceptionCaught(HandlerContext handlerContext, Throwable cause) {
            logger.info("onExceptionCaught", cause);
        }

    }

}
