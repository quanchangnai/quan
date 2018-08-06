package quan.network.connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import quan.network.handler.HandlerChain;
import quan.network.handler.HandlerContext;
import quan.network.util.TaskExecutor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * 网络连接的封装，提供读写数据等功能
 *
 * @author quanchangnai
 */
public class Connection {

    protected final Logger logger = LogManager.getLogger(getClass());

    private boolean connected;

    private SelectionKey selectionKey;

    private SocketChannel socketChannel;

    private TaskExecutor executor;

    protected HandlerChain handlerChain;

    private ByteBuffer readBuffer;

    private ByteBuffer writeBuffer;

    private int readBufferSize = 1024;

    private int writeBufferSize = 1024;

    private Queue<ByteBuffer> msgQueue = new LinkedList<>();

    private Map<Object, Object> attrs = new HashMap<>();

    public Connection(SelectionKey selectionKey, TaskExecutor executor) {
        this.executor = executor;
        this.selectionKey = selectionKey;
        this.socketChannel = (SocketChannel) selectionKey.channel();
        this.handlerChain = new HandlerChain(this);
        this.readBuffer = ByteBuffer.allocate(readBufferSize);
        this.writeBuffer = ByteBuffer.allocate(writeBufferSize);
    }

    public HandlerChain getHandlerChain() {
        return handlerChain;
    }

    public int getReadBufferSize() {
        return readBufferSize;
    }

    public void setReadBufferSize(int readBufferSize) {
        if (readBufferSize == this.readBufferSize) {
            return;
        }
        this.readBufferSize = readBufferSize;
        this.readBuffer = ByteBuffer.allocate(readBufferSize);
    }

    public int getWriteBufferSize() {
        return writeBufferSize;
    }

    public void setWriteBufferSize(int writeBufferSize) {
        if (writeBufferSize == this.writeBufferSize) {
            return;
        }
        this.writeBufferSize = writeBufferSize;
        this.writeBuffer = ByteBuffer.allocate(writeBufferSize);
    }

    public TaskExecutor getExecutor() {
        return executor;
    }

    public boolean isConnected() {
        return connected;
    }

    public Object putAttr(Object key, Object value) {
        return attrs.put(key, value);
    }

    public Object getAttr(Object key) {
        return attrs.get(key);
    }

    public Object removeAttr(Object key) {
        return attrs.remove(key);
    }

    public InetSocketAddress getRemoteAddress() {
        try {
            return (InetSocketAddress) socketChannel.getRemoteAddress();
        } catch (IOException e) {
            logger.error(e);
        }
        return null;
    }

    /**
     * 读数据
     *
     * @return 读取的字节数，负数表示连接已断开
     */
    public int read() {
        int readedCount;
        try {
            readedCount = doRead(readBuffer);
            if (readedCount < 0) {
                close();
                return -1;
            } else {
                readBuffer.flip();
                triggerReceived(readBuffer);
            }
        } catch (IOException e) {
            triggerExceptionCaught(e);
            close();
            readedCount = -1;
        } finally {
            readBuffer.clear();
        }
        return readedCount;
    }

    protected int doRead(ByteBuffer readBuffer) throws IOException {
        return socketChannel.read(readBuffer);
    }

    /**
     * 写数据
     *
     * @return 写入的字节数，负数表示连接已断开
     */
    public int write() {
        if (msgQueue.isEmpty()) {
            int interestOps = selectionKey.interestOps();
            if ((interestOps & SelectionKey.OP_WRITE) != 0) {
                selectionKey.interestOps(interestOps & ~SelectionKey.OP_WRITE);
            }
            return 0;
        }

        writeBuffer.clear();
        int writedCount = 0;

        for (ByteBuffer msgBuffer = msgQueue.poll(); msgBuffer != null; msgBuffer = msgQueue.poll()) {
            if (writeBuffer.remaining() > msgBuffer.remaining()) {
                // 缓冲区剩余空间充足,完整写入消息
                writeBuffer.put(msgBuffer);
            } else {// 缓冲区剩余空间不足,拆分消息
                while (msgBuffer.hasRemaining()) {
                    int writeBytesLength = writeBuffer.remaining();
                    if (msgBuffer.remaining() < writeBytesLength) {
                        writeBytesLength = msgBuffer.remaining();
                    }
                    byte[] writeBytes = new byte[writeBytesLength];
                    msgBuffer.get(writeBytes);
                    writeBuffer.put(writeBytes);
                    if (!writeBuffer.hasRemaining()) {
                        //缓冲区填满了
                        writeBuffer.flip();
                        int doWritedCount = doWrite(writeBuffer);
                        writeBuffer.clear();
                        if (doWritedCount < 0) {
                            return doWritedCount;
                        } else {
                            writedCount += doWritedCount;
                        }
                    }
                }
            }
        }

        writeBuffer.flip();
        if (writeBuffer.hasRemaining()) {
            //缓冲区还有数据没有写入通道
            int doWritedCount = doWrite(writeBuffer);
            if (doWritedCount < 0) {
                return -1;
            } else {
                writedCount += doWritedCount;
            }
        }
        return writedCount;
    }

    protected int doWrite(ByteBuffer writeBuffer) {
        int writedCount;
        try {
            writedCount = socketChannel.write(writeBuffer);
        } catch (IOException e) {
            triggerExceptionCaught(e);
            close();
            writedCount = -1;
        }
        return writedCount;
    }

    /**
     * 触发建立建立了
     */
    public void triggerConnected() {
        this.connected = true;
        handlerChain.triggerConnected();
    }

    /**
     * 触发连接断开了
     */
    protected void triggerDisconnected() {
        handlerChain.triggerDisconnected();
        handlerChain.removeAll();
        this.connected = false;
    }

    /**
     * 触发收到了消息
     *
     * @param msg
     */
    protected void triggerReceived(ByteBuffer msg) {
        handlerChain.triggerReceived(msg);
    }

    /**
     * 触发捕获了异常
     *
     * @param cause
     */
    protected void triggerExceptionCaught(Throwable cause) {
        handlerChain.triggerExceptionCaught(cause);
    }

    /**
     * 发送消息，不能在具体逻辑中直接调用，具体逻辑中应该调用{@link HandlerContext#send(Object)}
     *
     * @param msg
     */
    public void send(ByteBuffer msg) {
        if (!selectionKey.isValid()) {
            return;
        }
        msgQueue.offer(msg);
        int interestOps = selectionKey.interestOps();
        if ((interestOps & SelectionKey.OP_WRITE) == 0) {
            selectionKey.interestOps(interestOps | SelectionKey.OP_WRITE);
        }
    }

    /**
     * 关闭连接，不能在具体逻辑中直接调用，具体逻辑中应该调用{@link HandlerContext#close()}
     */
    public void close() {
        try {
            if (!this.isConnected()) {
                return;
            }
            selectionKey.cancel();
            socketChannel.close();
            triggerDisconnected();
        } catch (IOException e) {
            logger.error("关闭连接异常", e);
        }
    }

}
