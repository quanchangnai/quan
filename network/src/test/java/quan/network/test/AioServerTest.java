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
        AsynchronousChannelGroup channelGroup = AsynchronousChannelGroup.withFixedThreadPool(5, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "Thread" + n++);
            }
        });
        AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open(channelGroup);
        serverSocketChannel.bind(new InetSocketAddress(8007));
        serverSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
            @Override
            public void completed(AsynchronousSocketChannel socketChannel, Object attachment) {
                System.err.println("accept completed");
                write(socketChannel);
                read(socketChannel);
                serverSocketChannel.accept(null, this);
            }

            @Override
            public void failed(Throwable exc, Object attachment) {

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
            }
        });
    }

    public static void write(AsynchronousSocketChannel socketChannel) {
        System.err.println("write");
        socketChannel.write(ByteBuffer.wrap("aaa".getBytes()));
    }
}
