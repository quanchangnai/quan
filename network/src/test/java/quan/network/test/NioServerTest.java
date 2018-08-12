package quan.network.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import quan.network.bootstrap.ServerBootstrap;
import quan.network.handler.Handler;
import quan.network.handler.HandlerChain;
import quan.network.handler.HandlerContext;
import quan.network.handler.HandlerConfigurer;
import quan.network.handler.codec.LengthFieldCodec;
import quan.network.handler.codec.StringCodec;

import java.net.StandardSocketOptions;

public class NioServerTest {

    public static void main(String[] args) throws Exception {
        ServerBootstrap server = new ServerBootstrap(8007);
//        server.setReadBufferSize(1);
        server.setWriteBufferSize(30);
        server.setSocketOption(StandardSocketOptions.SO_KEEPALIVE, true);
        server.setSocketOption(StandardSocketOptions.SO_REUSEADDR, true);
        server.setSocketOption(StandardSocketOptions.TCP_NODELAY, true);
        server.setSocketOption(StandardSocketOptions.SO_RCVBUF, 1024);
        server.setSocketOption(StandardSocketOptions.SO_SNDBUF, 128);
        server.setHandler(new HandlerConfigurer() {
            @Override
            public void configureHandler(HandlerChain handlerChain) throws Exception {
                handlerChain.addLast(new LengthFieldCodec(1, true));
                handlerChain.addLast(new StringCodec());
                handlerChain.addLast(new TestServerInboundHandler());
                handlerChain.addLast(new TestServerInboundHandler2());
            }
        });


        server.start();

//		 Thread.sleep(5 * 1000);
//		
//		 server.stop();
//		
//		 Thread.sleep(5 * 1000);
//		
//		 server.start();
    }


    private static class TestServerInboundHandler implements Handler<String> {


        @Override
        public void onConnected(HandlerContext handlerContext) throws Exception {
            System.err.println("onConnected");
//		handlerContext.onSend(ByteBuffer.wrap("hello".getBytes()));
//		handlerContext.onSend("hello");
            handlerContext.triggerConnected();
        }

        @Override
        public void onDisconnected(HandlerContext handlerContext) throws Exception {
            System.err.println("onDisconnected");

        }

        @Override
        public void onReceived(HandlerContext handlerContext, String msg) throws Exception {
            System.err.println("onReceived:" + msg);
//		handlerContext.onSend(msg);
            handlerContext.triggerReceived(msg);
        }

        @Override
        public void onExceptionCaught(HandlerContext handlerContext, Throwable cause) throws Exception {
            System.err.println("onExceptionCaught");
            cause.printStackTrace();
            handlerContext.triggerExceptionCaught(cause);
        }

    }


    private static class TestServerInboundHandler2 implements Handler<String> {

        private static final Logger logger = LogManager.getLogger(TestServerInboundHandler2.class);


        @Override
        public void onConnected(HandlerContext handlerContext) throws Exception {
            System.err.println("onConnected2");
            handlerContext.send("aaa:" +System.currentTimeMillis());
            handlerContext.send("bbb:" +System.currentTimeMillis());
            handlerContext.send("ccc");
        }

        @Override
        public void onDisconnected(HandlerContext handlerContext) throws Exception {
            System.err.println("onDisconnected2");
        }

        @Override
        public void onReceived(HandlerContext handlerContext, String msg) throws Exception {
            System.err.println("onReceived2:" + msg);
            handlerContext.send(msg);
        }

        @Override
        public void onExceptionCaught(HandlerContext handlerContext, Throwable cause) throws Exception {
            System.err.println("onExceptionCaught2");
            cause.printStackTrace();
            // handlerContext.postExceptionCaught(cause);
            handlerContext.close();
        }

    }

}
