package quan.network.test;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ThreadFactory;

/**
 * Created by quanchangnai on 2018/7/17.
 */
public class AioServerTest {

    static int n = 1;

    public static void main(String[] args) throws Exception {
        AsynchronousChannelGroup channelGroup = AsynchronousChannelGroup.withFixedThreadPool(5, r -> new Thread(r, "Thread" + n++));
        AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open(channelGroup);

        serverSocketChannel.bind(new InetSocketAddress(8007));
        serverSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
            @Override
            public void completed(AsynchronousSocketChannel socketChannel, Object attachment) {
                System.err.println("accept completed" + ":" + Thread.currentThread().getName());
                serverSocketChannel.accept(null, this);
                write(socketChannel);
                read(socketChannel);
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                exc.printStackTrace();
            }
        });
    }

    public static void read(AsynchronousSocketChannel socketChannel) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        socketChannel.read(byteBuffer, null, new CompletionHandler<Integer, Object>() {
            @Override
            public void completed(Integer result, Object attachment) {
                System.err.println("read completed " + new String(byteBuffer.array()) + ":" + Thread.currentThread().getName());
                read(socketChannel);
                write(socketChannel);
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                exc.printStackTrace();
            }
        });
    }

    public static void write(AsynchronousSocketChannel socketChannel) {
        socketChannel.write(ByteBuffer.wrap("aaa".getBytes()), null, new CompletionHandler<Integer, Object>() {
            @Override
            public void completed(Integer result, Object attachment) {
                System.err.println("write completed" + ":" + Thread.currentThread().getName());
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                exc.printStackTrace();
            }
        });

    }
}
