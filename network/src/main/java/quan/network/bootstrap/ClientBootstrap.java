package quan.network.bootstrap;

import quan.network.connection.Connection;
import quan.network.handler.NetworkHandler;
import quan.network.util.TaskExecutor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketOption;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

/**
 * 基于nio的网络客户端
 *
 * @author quanchangnai
 */
public class ClientBootstrap extends Bootstrap {

    private ReadWriteExecutor readWriteExecutor;

    private boolean autoReconnect = true;

    private long reconnectTime = 60 * 1000;

    public ClientBootstrap(int port) {
        this.ip = "127.0.0.1";
        this.port = port;
    }

    public ClientBootstrap(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public ClientBootstrap(String ip, int port, NetworkHandler handler) {
        this.ip = ip;
        this.port = port;
        this.handler = handler;
    }


    public boolean isAutoReconnect() {
        return autoReconnect;
    }

    public void setAutoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
    }

    public long getReconnectTime() {
        return reconnectTime;
    }

    public void setReconnectTime(long reconnectTime) {
        this.reconnectTime = reconnectTime;
    }

    @Override
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

        setRunning(true);
        readWriteExecutor.start();

        readWriteExecutor.submit(this::connect);
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

    @Override
    public void stop() {
        setRunning(false);
        readWriteExecutor.stop();
        readWriteExecutor = null;
    }


    protected void reconnect() {
        if (!isRunning() || !isAutoReconnect()) {
            return;
        }
        try {
            Thread.sleep(getReconnectTime());
        } catch (InterruptedException e) {
            logger.error(e);
        }

        connect();
    }

    private static class ReadWriteExecutor extends TaskExecutor {

        private ClientBootstrap client;

        private Selector selector;

        public ReadWriteExecutor(ClientBootstrap client) throws IOException {
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
                            logger.error(e);
                            tryReconnect();
                            break;
                        }
                        socketChannel.register(selector, SelectionKey.OP_READ);
                        Connection connection = new Connection(selectedKey, this);
                        connection.setReadBufferSize(client.getReadBufferSize());
                        connection.setWriteBufferSize(client.getWriteBufferSize());
                        connection.getHandlerChain().addLast(client.getHandler());
                        selectedKey.attach(connection);
                        connection.triggerConnected();
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
