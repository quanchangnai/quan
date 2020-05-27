package quan.network;

import quan.network.nio.NioClient;
import quan.network.nio.handler.Handler;
import quan.network.nio.handler.HandlerChain;
import quan.network.nio.handler.HandlerConfigurer;
import quan.network.nio.handler.HandlerContext;
import quan.network.nio.codec.LengthFieldCodec;
import quan.network.nio.codec.StringCodec;

import java.net.StandardSocketOptions;

public class NioClientTest {

    public static void main(String[] args) {
        NioClient client = new NioClient("127.0.0.1", 8007);
        client.setSocketOption(StandardSocketOptions.SO_KEEPALIVE, true);
        client.setSocketOption(StandardSocketOptions.SO_RCVBUF, 2);
        client.setSocketOption(StandardSocketOptions.SO_SNDBUF, 2);
        client.setHandler(new HandlerConfigurer() {
            @Override
            public void configureHandler(HandlerChain handlerChain) {
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
        public void onConnected(HandlerContext handlerContext) {
            System.err.println("onConnected");
        }

        @Override
        public void onDisconnected(HandlerContext handlerContext) {
            System.err.println("onDisconnected");
        }

        @Override
        public void onReceived(final HandlerContext handlerContext, final String msg) {
            System.err.println("onReceived:" + msg);
        }

        @Override
        public void onExceptionCaught(HandlerContext handlerContext, Throwable cause) {
            System.err.println("onExceptionCaught");
            cause.printStackTrace();

        }

    }

}
