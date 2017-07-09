package quan.network.app;

import quan.network.connection.Connection;
import quan.network.handler.NetworkHandler;
import quan.network.util.SingleThreadExecutor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketOption;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 基于nio的网络服务器，单线程负责处理新通道的接受，一组线程负责处理已有通道的读写，<br>
 * 其中的一个线程负责处理多个通道的操作， 一个通道的所有操作都是在单线程中完成的。
 *
 * @author quanchangnai
 */
public class NioServer extends NetworkApp {

    private AcceptEventExecutor acceptEventExecutor;

    private int readWriteThreadNum;

    private ReadWriteEventExecutor[] readWriteEventExecutors;

    private int readWriteEventExecutorIndex;

    private Map<SocketOption<?>, Object> serverSocketOptions = new LinkedHashMap<>();

    public NioServer(int port) {
        this.ip = "0.0.0.0";
        this.port = port;
    }

    public NioServer(int port, NetworkHandler handler) {
        this.ip = "0.0.0.0";
        this.port = port;
        this.setHandler(handler);
    }

    public <T> void setServerSocketOption(SocketOption<T> option, T value) {
        if (value == null) {
            serverSocketOptions.remove(option);
        } else {
            serverSocketOptions.put(option, value);
        }
    }

    public Map<SocketOption<?>, Object> getServerSocketOptions() {
        return serverSocketOptions;
    }

    public int getReadWriteThreadNum() {
        return readWriteThreadNum;
    }

    public void setReadWriteThreadNum(int readWriteThreadNum) {
        this.readWriteThreadNum = readWriteThreadNum;
    }

    @Override
    public void start() {
        if (getHandler() == null) {
            throw new NullPointerException("handler");
        }
        logger.debug("开启服务器");
        try {
            acceptEventExecutor = new AcceptEventExecutor(this);
            if (readWriteThreadNum <= 0) {
                readWriteThreadNum = Runtime.getRuntime().availableProcessors() * 2;
            }

            readWriteEventExecutors = new ReadWriteEventExecutor[readWriteThreadNum];
            for (int i = 0; i < readWriteThreadNum; i++) {
                readWriteEventExecutors[i] = new ReadWriteEventExecutor(this);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setRunning(true);
        acceptEventExecutor.start();
        for (int i = 0; i < readWriteEventExecutors.length; i++) {
            readWriteEventExecutors[i].start();
        }

        acceptEventExecutor.submit(() -> {
            try {
                ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
                serverSocketChannel.configureBlocking(false);
                for (SocketOption<?> socketOption : getServerSocketOptions().keySet()) {
                    Object optionValue = getServerSocketOptions().get(socketOption);
                    if (optionValue instanceof Integer) {
                        serverSocketChannel.setOption((SocketOption<Integer>) socketOption, (Integer) optionValue);
                    } else if (optionValue instanceof Boolean) {
                        serverSocketChannel.setOption((SocketOption<Boolean>) socketOption, (Boolean) optionValue);
                    }
                }
                serverSocketChannel.socket().bind(new InetSocketAddress(getIp(), getPort()));
                acceptEventExecutor.registerChannel(serverSocketChannel);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void stop() {
        logger.debug("停止服务器");
        setRunning(false);
        acceptEventExecutor.stop();
        for (int i = 0; i < readWriteEventExecutors.length; i++) {
            readWriteEventExecutors[i].stop();
        }
    }

    private ReadWriteEventExecutor nextReadWriteThreadExecutor() {
        if (readWriteEventExecutorIndex > readWriteEventExecutors.length - 1) {
            readWriteEventExecutorIndex = 0;
        }
        return readWriteEventExecutors[readWriteEventExecutorIndex++];
    }

    private static class AcceptEventExecutor extends SingleThreadExecutor {

        private NioServer server;

        private Selector selector;

        public AcceptEventExecutor(NioServer server) throws IOException {
            this.server = server;
            this.selector = Selector.open();
        }

        public void registerChannel(ServerSocketChannel serverSocketChannel) throws IOException {
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            selector.wakeup();
        }

        @Override
        public void execute(Runnable task) {
            super.execute(task);
            selector.wakeup();
        }

        @Override
        public void stop() {
            super.stop();
            selector.wakeup();
        }

        @Override
        protected void after() {
            try {
                select();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void select() throws IOException {
            // 等待io事件
            selector.select();

            Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
            while (isRunning() && selectedKeys.hasNext()) {
                SelectionKey selectedKey = selectedKeys.next();
                selectedKeys.remove();

                if (selectedKey.isValid() && selectedKey.isAcceptable()) {
                    ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectedKey.channel();
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    for (SocketOption<?> socketOption : server.getSocketOptions().keySet()) {
                        Object optionValue = server.getSocketOptions().get(socketOption);
                        if (optionValue instanceof Integer) {
                            socketChannel.setOption((SocketOption<Integer>) socketOption, (Integer) optionValue);
                        } else if (optionValue instanceof Boolean) {
                            socketChannel.setOption((SocketOption<Boolean>) socketOption, (Boolean) optionValue);
                        }
                    }

                    ReadWriteEventExecutor readWriteEventExecutor = server.nextReadWriteThreadExecutor();
                    readWriteEventExecutor.registerChannel(socketChannel);

                }
            }
        }

        @Override
        protected void end() {
            try {
                Set<SelectionKey> keys = selector.keys();
                for (SelectionKey key : keys) {
                    key.channel().close();
                }
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (server.isRunning()) {
                    server.stop();
                }
            }

        }
    }

    private static class ReadWriteEventExecutor extends SingleThreadExecutor {

        private NioServer server;

        private BlockingQueue<SocketChannel> acceptedChannels;

        private Selector selector;

        public ReadWriteEventExecutor(NioServer server) throws IOException {
            this.server = server;
            this.acceptedChannels = new LinkedBlockingQueue<>();
            this.selector = Selector.open();
        }

        public void registerChannel(SocketChannel socketChannel) {
            try {
                acceptedChannels.put(socketChannel);
                selector.wakeup();
            } catch (InterruptedException e) {
                logger.error(e);
            }
        }

        @Override
        public void execute(Runnable task) {
            super.execute(task);
            selector.wakeup();
        }

        @Override
        public void stop() {
            super.stop();
            selector.wakeup();
        }

        @Override
        protected void after() {
            try {
                select();
                doRegisterChannels();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void end() {
            try {
                Set<SelectionKey> keys = selector.keys();
                for (SelectionKey key : keys) {
                    Connection connection = (Connection) key.attachment();
                    connection.close();
                }
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void doRegisterChannels() throws IOException {
            while (isRunning()) {
                SocketChannel socketChannel = acceptedChannels.poll();
                if (socketChannel == null) {
                    break;
                }
                SelectionKey selectionKey = socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                Connection connection = new Connection(selectionKey, this);
                connection.setReadBufferSize(server.getReadBufferSize());
                connection.setWriteBufferSize(server.getWriteBufferSize());
                connection.getHandlerChain().addLast(server.getHandler());
                selectionKey.attach(connection);
                connection.connected();
            }
        }

        private void select() throws IOException {
            // 等待io事件
            selector.select();

            Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();

            while (isRunning() && selectedKeys.hasNext()) {
                SelectionKey selectedKey = selectedKeys.next();
                selectedKeys.remove();

                if (selectedKey.isValid() && selectedKey.isReadable()) {
                    Connection connection = (Connection) selectedKey.attachment();
                    connection.read();
                }

                if (selectedKey.isValid() && selectedKey.isWritable()) {
                    Connection connection = (Connection) selectedKey.attachment();
                    connection.write();
                }
            }
        }
    }
}
