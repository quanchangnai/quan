package quan.network;

import quan.network.bootstrap.ClientBootstrap;
import quan.network.handler.Handler;
import quan.network.handler.HandlerChain;
import quan.network.handler.HandlerConfigurer;
import quan.network.handler.HandlerContext;
import quan.network.handler.codec.LengthFieldCodec;
import quan.network.handler.codec.StringCodec;

import java.net.StandardSocketOptions;

public class NioClientTest {

    public static void main(String[] args) throws Exception {
        ClientBootstrap client = new ClientBootstrap("127.0.0.1", 8007);
        client.setSocketOption(StandardSocketOptions.SO_KEEPALIVE, true);
        client.setSocketOption(StandardSocketOptions.SO_RCVBUF, 2);
        client.setSocketOption(StandardSocketOptions.SO_SNDBUF, 2);
        client.setHandler(new HandlerConfigurer() {
            @Override
            public void configureHandler(HandlerChain handlerChain) throws Exception {
                handlerChain.addLast(new LengthFieldCodec(4, true));
                handlerChain.addLast(new StringCodec());
                handlerChain.addLast(new TestClientHandler());

            }
        });
        client.setReconnectTime(15 * 1000);
        client.start();


    }

    private static class TestClientHandler implements Handler<String> {

        @Override
        public void onConnected(HandlerContext handlerContext) throws Exception {
            System.err.println("onConnected");
        }

        @Override
        public void onDisconnected(HandlerContext handlerContext) throws Exception {
            System.err.println("onDisconnected");
        }

        @Override
        public void onReceived(final HandlerContext handlerContext, final String msg) throws Exception {
            System.err.println("onReceived:" + msg);
        }

        @Override
        public void onExceptionCaught(HandlerContext handlerContext, Throwable cause) throws Exception {
            System.err.println("onExceptionCaught");
            cause.printStackTrace();

        }

    }

}
