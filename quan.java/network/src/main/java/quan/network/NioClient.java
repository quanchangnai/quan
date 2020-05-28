package quan.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.network.handler.Handler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketOption;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;

/**
 * 基于nio的网络客户端
 *
 * @author quanchangnai
 */
@SuppressWarnings("unchecked")
public class NioClient {

    private static final Logger logger = LoggerFactory.getLogger(NioClient.class);

    private String ip;

    private int port;

    private volatile boolean running;

    private Handler handler;

    private int readBufferSize = 8096;

    private int writeBufferSize = 8096;

    private Map<SocketOption<?>, Object> socketOptions = new LinkedHashMap<>();

    private ReadWriteExecutor readWriteExecutor;

    private boolean autoReconnect = true;

    private long reconnectInterval = 60 * 1000;


    public NioClient(int port) {
        this.ip = "127.0.0.1";
        this.port = port;
    }

    public NioClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public NioClient(String ip, int port, Handler handler) {
        this.ip = ip;
        this.port = port;
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

    public boolean isAutoReconnect() {
        return autoReconnect;
    }

    public void setAutoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
    }

    public long getReconnectInterval() {
        return reconnectInterval;
    }

    public void setReconnectInterval(long reconnectInterval) {
        this.reconnectInterval = reconnectInterval;
    }

    public void start() {
        Objects.requireNonNull(getHandler(), "handler不能为空");
        if (isRunning()) {
            stop();
        }

        try {
            readWriteExecutor = new ReadWriteExecutor(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        running = true;
        readWriteExecutor.start();
        readWriteExecutor.execute(this::connect);
    }

    protected void connect() {
        try {
            if (!isRunning()) {
                return;
            }

            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);

            for (SocketOption<?> socketOption : socketOptions.keySet()) {
                Object optionValue = socketOptions.get(socketOption);
                if (optionValue instanceof Integer) {
                    socketChannel.setOption((SocketOption<Integer>) socketOption, (Integer) optionValue);
                } else if (optionValue instanceof Boolean) {
                    socketChannel.setOption((SocketOption<Boolean>) socketOption, (Boolean) optionValue);
                }
            }

            readWriteExecutor.registerChannel(socketChannel);
            socketChannel.connect(new InetSocketAddress(getIp(), getPort()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        running = false;
        readWriteExecutor.stop();
        readWriteExecutor = null;
    }


    protected void reconnect() {
        if (!isRunning() || !isAutoReconnect()) {
            return;
        }
        try {
            Thread.sleep(getReconnectInterval());
        } catch (InterruptedException e) {
            logger.error("", e);
        }

        connect();
    }

    private static class ReadWriteExecutor extends TaskExecutor {

        private NioClient client;

        private Selector selector;

        public ReadWriteExecutor(NioClient client) throws IOException {
            this.client = client;
            this.selector = Selector.open();
        }

        public void registerChannel(SocketChannel socketChannel) throws ClosedChannelException {
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
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
                logger.error("", e);
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
                logger.error("", e);
            } finally {
                if (client.isRunning()) {
                    client.stop();
                }
            }
        }

        private void select() throws IOException {
            // 等待io事件
            selector.select();
            Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();

            while (isRunning() && selectedKeys.hasNext()) {
                SelectionKey selectedKey = selectedKeys.next();
                selectedKeys.remove();

                if (selectedKey.isValid() && selectedKey.isConnectable()) {
                    SocketChannel socketChannel = (SocketChannel) selectedKey.channel();
                    if (socketChannel.isConnectionPending()) {
                        try {
                            socketChannel.finishConnect();
                        } catch (Exception e) {
                            logger.error("", e);
                            tryReconnect();
                            break;
                        }

                        socketChannel.register(selector, SelectionKey.OP_READ);
                        Connection connection = new Connection(selectedKey, this, client.getReadBufferSize(), client.getWriteBufferSize());
                        connection.getHandlerChain().addLast(client.getHandler());
                        selectedKey.attach(connection);
                        connection.triggerConnected();
                    }
                }

                if (selectedKey.isValid() && selectedKey.isReadable()) {
                    Connection connection = (Connection) selectedKey.attachment();
                    int readCount = connection.read();
                    if (readCount < 0) {
                        tryReconnect();
                        break;
                    }
                }

                if (selectedKey.isValid() && selectedKey.isWritable()) {
                    Connection connection = (Connection) selectedKey.attachment();
                    int writeCount = connection.write();
                    if (writeCount < 0) {
                        tryReconnect();
                        break;
                    }
                }
            }
        }

        private void tryReconnect() {
            if (client.isAutoReconnect()) {
                execute(client::reconnect);
            }
        }
    }
}
