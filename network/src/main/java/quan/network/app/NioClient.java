package quan.network.app;

import quan.network.connection.Connection;
import quan.network.util.SingleThreadExecutor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketOption;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 基于nio的网络客户端
 *
 * @author quanchangnai
 */
public class NioClient extends NetworkApp {

    private IoEventExecutor ioEventExecutor;

    private boolean autoReconnect = true;

    private long reconnectWaitTime = 60 * 1000;

    public NioClient(int port) {
        this.ip = "127.0.0.1";
        this.port = port;
    }

    public NioClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
        try {
            ioEventExecutor = new IoEventExecutor(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isAutoReconnect() {
        return autoReconnect;
    }

    public void setAutoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
    }

    public long getReconnectWaitTime() {
        return reconnectWaitTime;
    }

    public void setReconnectWaitTime(long reconnectWaitTime) {
        this.reconnectWaitTime = reconnectWaitTime;
    }

    @Override
    public void start() {
        connect();
    }

    @Override
    public void stop() {
        disconnect();
    }

    public void connect() {
        if (getHandler() == null) {
            throw new NullPointerException("handler");
        }
        logger.debug("连接服务器");
        try {
            ioEventExecutor = new IoEventExecutor(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ioEventExecutor.submit(this::doConnect);

        setRunning(true);
        ioEventExecutor.start();
    }

    private void doConnect() {
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
            ioEventExecutor.registerChannel(socketChannel);
            socketChannel.connect(new InetSocketAddress(getIp(), getPort()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void disconnect() {
        logger.debug("断开连接");
        setRunning(false);
        ioEventExecutor.stop();
    }

    private void reconnect() {
        if (!isRunning() || !isAutoReconnect()) {
            return;
        }
        try {
            Thread.sleep(getReconnectWaitTime());
        } catch (InterruptedException e1) {
            logger.error(e1);
        }

        doConnect();
    }

    private static class IoEventExecutor extends SingleThreadExecutor {

        private NioClient client;

        private Selector selector;

        public IoEventExecutor(NioClient client) throws IOException {
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
                logger.error(e);
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
                logger.error(e);
            } finally {
                if (client.isRunning()) {
                    client.disconnect();
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
                            logger.error(e);
                            tryReconnect();
                            break;
                        }
                        socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                        Connection connection = new Connection(selectedKey, this);
                        connection.setReadBufferSize(client.getReadBufferSize());
                        connection.setWriteBufferSize(client.getWriteBufferSize());
                        connection.getHandlerChain().addLast(client.getHandler());
                        selectedKey.attach(connection);
                        connection.connected();
                    }
                }

                if (selectedKey.isValid() && selectedKey.isReadable()) {
                    Connection connection = (Connection) selectedKey.attachment();
                    int readedNum = connection.read();
                    if (readedNum < 0) {
                        tryReconnect();
                        break;
                    }
                }

                if (selectedKey.isValid() && selectedKey.isWritable()) {
                    Connection connection = (Connection) selectedKey.attachment();
                    int writedNum = connection.write();
                    if (writedNum < 0) {
                        tryReconnect();
                        break;
                    }
                }
            }
        }

        private void tryReconnect() {
            if (client.isAutoReconnect()) {
                submit(client::reconnect);
            }
        }

    }

}
