package quan.network.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import quan.network.bootstrap.ServerBootstrap;
import quan.network.handler.HandlerChain;
import quan.network.handler.HandlerContext;
import quan.network.handler.HandlerInitializer;
import quan.network.handler.InboundHandler;
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
        server.setHandler(new HandlerInitializer() {
            @Override
            public void initHandler(HandlerChain handlerChain) throws Exception {
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


    private static class TestServerInboundHandler implements InboundHandler<String> {

        @Override
        public void onHandlerAdded(HandlerContext handlerContext) throws Exception {
            System.err.println("onHandlerAdded");
        }

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

        @Override
        public void onHandlerRemoved(HandlerContext handlerContext) throws Exception {
            System.err.println("onHandlerRemoved");
        }
    }


    private static class TestServerInboundHandler2 implements InboundHandler<String> {

        private static final Logger logger = LogManager.getLogger(TestServerInboundHandler2.class);

        @Override
        public void onHandlerAdded(HandlerContext handlerContext) throws Exception {
            System.err.println("onHandlerAdded2");
        }

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

        @Override
        public void onHandlerRemoved(HandlerContext handlerContext) throws Exception {
            System.err.println("onHandlerRemoved2");

        }
    }

}
