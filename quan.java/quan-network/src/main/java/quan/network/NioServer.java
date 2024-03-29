package quan.network;

import quan.network.handler.Handler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketOption;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 基于nio的网络服务器，单线程负责处理新通道的接受，一组线程负责处理已有通道的读写，<br>
 * 其中的一个线程负责处理多个通道的操作，一个通道的所有操作都是在单线程中完成的。
 *
 * @author quanchangnai
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class NioServer {

    private String ip;

    private int port;

    private volatile boolean running;

    private Handler handler;

    private Map<SocketOption<?>, Object> socketOptions = new LinkedHashMap<>();

    private int readBufferSize = 8096;

    private int writeBufferSize = 8096;

    private AcceptWorker acceptWorker;

    private int readWriteThreadNum;

    private ReadWriteExecutor[] readWriteExecutors;

    private int readWriteExecutorIndex;

    private Map<SocketOption<?>, Object> serverSocketOptions = new LinkedHashMap<>();

    public NioServer(int port) {
        this.ip = "0.0.0.0";
        this.port = port;
    }

    public NioServer(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public NioServer(int port, Handler handler) {
        this(port);
        this.handler = handler;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public boolean isRunning() {
        return this.running;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public <T> void setSocketOption(SocketOption<T> option, T value) {
        if (value == null) {
            socketOptions.remove(option);
        } else {
            socketOptions.put(option, value);
        }
    }

    public Map<SocketOption<?>, Object> getSocketOptions() {
        return socketOptions;
    }

    public int getReadBufferSize() {
        return readBufferSize;
    }

    public void setReadBufferSize(int readBufferSize) {
        this.readBufferSize = readBufferSize;
    }

    public int getWriteBufferSize() {
        return writeBufferSize;
    }

    public void setWriteBufferSize(int writeBufferSize) {
        this.writeBufferSize = writeBufferSize;
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

    public void start() {
        Objects.requireNonNull(handler, "参数[handler]不能为空");

        if (isRunning()) {
            stop();
        }

        try {
            doStart();
        } catch (Exception e) {
            stop();
            throw new RuntimeException(e);
        }
    }

    private void doStart() throws Exception {
        acceptWorker = new AcceptWorker(this);
        if (readWriteThreadNum <= 0) {
            readWriteThreadNum = Runtime.getRuntime().availableProcessors() * 2;
        }

        readWriteExecutors = new ReadWriteExecutor[readWriteThreadNum];
        for (int i = 0; i < readWriteThreadNum; i++) {
            readWriteExecutors[i] = new ReadWriteExecutor(this);
        }

        running = true;

        acceptWorker.start();
        for (ReadWriteExecutor readWriteExecutor : readWriteExecutors) {
            readWriteExecutor.start();
        }

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
        acceptWorker.registerChannel(serverSocketChannel);
    }

    public void stop() {
        running = false;

        acceptWorker.stop();
        acceptWorker = null;

        for (ReadWriteExecutor readWriteExecutor : readWriteExecutors) {
            readWriteExecutor.stop();
        }
        readWriteExecutors = null;
    }

    private ReadWriteExecutor nextReadWriteExecutor() {
        if (readWriteExecutorIndex > readWriteExecutors.length - 1) {
            readWriteExecutorIndex = 0;
        }
        return readWriteExecutors[readWriteExecutorIndex++];
    }

    private static class AcceptWorker extends TaskExecutor {

        private NioServer server;

        private Selector selector;

        public AcceptWorker(NioServer server) throws IOException {
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
        protected void runTasks() throws InterruptedException {
            super.runTasks();
            try {
                select();
            } catch (IOException e) {
                logger.error("", e);
            }
        }

        private void select() throws IOException {
            // 等待io事件
            selector.select();
            Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();

            while (isRunning() && selectedKeys.hasNext()) {
                SelectionKey selectedKey = selectedKeys.next();
                selectedKeys.remove();

                if (!selectedKey.isValid() || !selectedKey.isAcceptable()) {
                    continue;
                }

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

                server.nextReadWriteExecutor().registerChannel(socketChannel);
            }
        }

        @Override
        protected void destroy() {
            super.destroy();
            try {
                for (SelectionKey key : selector.keys()) {
                    key.channel().close();
                }
                selector.close();
            } catch (IOException e) {
                logger.error("", e);
            } finally {
                if (server.isRunning()) {
                    server.stop();
                }
            }
        }
    }

    private static class ReadWriteExecutor extends TaskExecutor {

        private NioServer server;

        private BlockingQueue<SocketChannel> acceptedChannels;

        private Selector selector;

        public ReadWriteExecutor(NioServer server) throws IOException {
            this.server = server;
            this.acceptedChannels = new LinkedBlockingQueue<>();
            this.selector = Selector.open();
        }

        public void registerChannel(SocketChannel socketChannel) {
            try {
                acceptedChannels.put(socketChannel);
                selector.wakeup();
            } catch (InterruptedException e) {
                logger.error("", e);
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
        protected void runTasks() throws InterruptedException {
            super.runTasks();
            try {
                select();
                doRegisterChannels();
            } catch (IOException e) {
                logger.error("", e);
            }
        }

        @Override
        protected void destroy() {
            super.destroy();
            try {
                for (SelectionKey key : selector.keys()) {
                    Connection connection = (Connection) key.attachment();
                    connection.close();
                }
                selector.close();
            } catch (IOException e) {
                logger.error("", e);
            }
        }

        private void doRegisterChannels() throws IOException {
            while (isRunning()) {
                SocketChannel socketChannel = acceptedChannels.poll();
                if (socketChannel == null) {
                    break;
                }
                SelectionKey selectionKey = socketChannel.register(selector, SelectionKey.OP_READ);

                Connection connection = new Connection(selectionKey, this, server.getReadBufferSize(), server.getWriteBufferSize());
                connection.getHandlerChain().addLast(server.getHandler());
                selectionKey.attach(connection);

                connection.triggerConnected();
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
