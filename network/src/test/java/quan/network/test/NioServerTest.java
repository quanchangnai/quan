package quan.network.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import quan.network.bootstrap.ServerBootstrap;
import quan.network.handler.Handler;
import quan.network.handler.HandlerChain;
import quan.network.handler.HandlerConfigurer;
import quan.network.handler.HandlerContext;
import quan.network.handler.codec.LengthFieldCodec;
import quan.network.handler.codec.StringCodec;

import java.net.StandardSocketOptions;

public class NioServerTest {

    protected static final Logger logger = LogManager.getLogger(NioServerTest.class);

    public static void main(String[] args) throws Exception {
        ServerBootstrap server = new ServerBootstrap(8007);
        server.setReadBufferSize(20);
        server.setWriteBufferSize(20);
        server.setSocketOption(StandardSocketOptions.SO_RCVBUF, 1000);
        server.setSocketOption(StandardSocketOptions.SO_SNDBUF, 2);
        server.setHandler(new HandlerConfigurer() {
            @Override
            public void configureHandler(HandlerChain handlerChain) throws Exception {
                handlerChain.addLast(new LengthFieldCodec(4, true));
                handlerChain.addLast(new StringCodec());
                handlerChain.addLast(new TestServerHandler());
                handlerChain.addLast(new TestServerHandler2());
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


    private static class TestServerHandler implements Handler<String> {


        @Override
        public void onConnected(HandlerContext handlerContext) throws Exception {
            System.err.println("onConnected");
//            handlerContext.send(ByteBuffer.wrap("hello".getBytes()));
//            handlerContext.send("hello");
            handlerContext.triggerConnected();
        }

        @Override
        public void onDisconnected(HandlerContext handlerContext) throws Exception {
            System.err.println("onDisconnected");

        }

        @Override
        public void onReceived(HandlerContext handlerContext, String msg) throws Exception {
            System.err.println("onReceived:" + msg);
//            handlerContext.send(msg);
            handlerContext.triggerReceived(msg);
//            handlerContext.triggerEvent(msg);
        }

        @Override
        public void onExceptionCaught(HandlerContext handlerContext, Throwable cause) throws Exception {
            System.err.println("onExceptionCaught");
            cause.printStackTrace();
            handlerContext.triggerExceptionCaught(cause);
        }

    }


    private static class TestServerHandler2 implements Handler<String> {

        private static final Logger logger = LogManager.getLogger(TestServerHandler2.class);


        @Override
        public void onConnected(HandlerContext handlerContext) throws Exception {
            System.err.println("onConnected2");

            new Thread() {
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        handlerContext.send("aaa:" + System.currentTimeMillis()+":aaa");
                        handlerContext.send("bbb:" + System.currentTimeMillis()+":bbb");
                        handlerContext.send("ccc:" + System.currentTimeMillis()+":ccc");
                    }

                }

            }.start();
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
        }

        @Override
        public void onEventTriggered(HandlerContext handlerContext, Object event) {
            System.err.println("onEventTriggered:" + event);
            handlerContext.send("aaa:"+System.nanoTime());
        }
    }

}
