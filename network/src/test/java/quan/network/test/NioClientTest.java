package quan.network.test;

import quan.network.bootstrap.ClientBootstrap;
import quan.network.handler.HandlerChain;
import quan.network.handler.HandlerContext;
import quan.network.handler.HandlerInitializer;
import quan.network.handler.InboundHandler;
import quan.network.handler.codec.LengthFieldCodec;
import quan.network.handler.codec.StringCodec;

import java.net.StandardSocketOptions;

public class NioClientTest {

    public static void main(String[] args) {
        ClientBootstrap client = new ClientBootstrap("127.0.0.1", 8007);
        client.setSocketOption(StandardSocketOptions.SO_KEEPALIVE, true);
        client.setHandler(new HandlerInitializer() {
            @Override
            public void initHandler(HandlerChain handlerChain) throws Exception {
//                handlerChain.addLast(new LengthFieldCodec(1, true));
                handlerChain.addLast(new StringCodec());
                handlerChain.addLast(new TestClientInboundHandler());

            }
        });
        client.setReconnectTime(15 * 1000);
        client.connect();
    }

    private static class TestClientInboundHandler implements InboundHandler {

        @Override
        public void onHandlerAdded(HandlerContext handlerContext) throws Exception {
            System.err.println("onHandlerAdded");
        }

        @Override
        public void onConnected(HandlerContext handlerContext) throws Exception {
            System.err.println("onConnected");
        }

        @Override
        public void onDisconnected(HandlerContext handlerContext) throws Exception {
            System.err.println("onDisconnected");
        }

        @Override
        public void onReceived(final HandlerContext handlerContext, final Object msg) throws Exception {
            System.err.println("onReceived:" + msg);

            new Thread() {
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handlerContext.send(msg);
                }

            }.start();
        }

        @Override
        public void onExceptionCaught(HandlerContext handlerContext, Throwable cause) throws Exception {
            System.err.println("onExceptionCaught");
            cause.printStackTrace();

        }

        @Override
        public void onHandlerRemoved(HandlerContext handlerContext) throws Exception {
            System.err.println("onHandlerRemoved");

        }
    }

}
