package quan.network.connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import quan.network.handler.HandlerChain;
import quan.network.handler.HandlerContext;
import quan.network.util.SingleThreadExecutor;

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

    private static final Logger logger = LogManager.getLogger(Connection.class);

    public static final int STATE_INIT = 0;// 初始状态

    public static final int STATE_CONNECTED = 1;// 连接已建立

    public static final int STATE_DISCONNECTED = 2;// 连接已断开

    private int state;

    private SelectionKey selectionKey;

    private SocketChannel socketChannel;

    private SingleThreadExecutor executor;

    protected HandlerChain handlerChain;

    private ByteBuffer readBuffer;

    private ByteBuffer writeBuffer;

    private int readBufferSize = 1024;

    private int writeBufferSize = 1024;

    private Queue<ByteBuffer> msgQueue = new LinkedList<>();

    private Map<Object, Object> attrs = new HashMap<>();

    public Connection(SelectionKey selectionKey, SingleThreadExecutor executor) {
        this.executor = executor;
        this.selectionKey = selectionKey;
        this.socketChannel = (SocketChannel) selectionKey.channel();
        this.handlerChain = new HandlerChain(this);
        this.state = STATE_INIT;
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

    public SingleThreadExecutor getExecutor() {
        return executor;
    }

    public boolean isConnected() {
        return state == STATE_CONNECTED;
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

    /**
     * 获取连接地址
     *
     * @return
     */
    public InetSocketAddress getRemoteAddress() {
        try {
            return (InetSocketAddress) socketChannel.getRemoteAddress();
        } catch (IOException e) {
            logger.error("获取连接地址异常", e);

        }
        return null;
    }

    /**
     * 读数据
     *
     * @return 读取的字节数，负数表示连接已断开
     */
    public int read() {
        int readedBytesNum;
        try {
            readedBytesNum = doRead(readBuffer);
            if (readedBytesNum < 0) {
                close();
                return readedBytesNum;
            } else {
                readBuffer.flip();
                received(readBuffer);
            }
        } catch (IOException e) {
            exceptionCaught(e);
            close();
            readedBytesNum = -1;
        } finally {
            readBuffer.clear();
        }
        return readedBytesNum;
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
        int writedBytesNum = 0;

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
                        int doWritedBytesNum = doWrite(writeBuffer);
                        writeBuffer.clear();
                        if (doWritedBytesNum < 0) {
                            return doWritedBytesNum;
                        } else {
                            writedBytesNum += doWritedBytesNum;
                        }
                    }
                }
            }
        }

        writeBuffer.flip();
        if (writeBuffer.hasRemaining()) {
            //缓冲区还有数据没有写入通道
            int doWritedBytesNum = doWrite(writeBuffer);
            if (doWritedBytesNum < 0) {
                return doWritedBytesNum;
            } else {
                writedBytesNum += doWritedBytesNum;
            }
        }
        return writedBytesNum;
    }

    protected int doWrite(ByteBuffer writeBuffer) {
        int writedBytesNum;
        try {
            writedBytesNum = socketChannel.write(writeBuffer);
        } catch (IOException e) {
            exceptionCaught(e);
            close();
            writedBytesNum = -1;
        }
        return writedBytesNum;
    }

    /**
     * 连接建立了的
     */
    public void connected() {
        this.state = STATE_CONNECTED;
        handlerChain.triggerConnected();
    }

    /**
     * 连接断开了
     */
    protected void disconnected() {
        handlerChain.triggerDisconnected();
        handlerChain.removeAll();
        this.state = STATE_DISCONNECTED;
    }

    /**
     * 收到了消息
     *
     * @param msg
     */
    protected void received(ByteBuffer msg) {
        handlerChain.triggerReceived(msg);
    }

    /**
     * 捕获了异常
     *
     * @param cause
     */
    protected void exceptionCaught(Throwable cause) {
        handlerChain.triggerExceptionCaught(cause);
    }

    /**
     * 发送消息，具体的逻辑中调用{@link HandlerContext#send(Object)}
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
     * 关闭连接，具体的逻辑中调用{@link HandlerContext#close()}
     */
    public void close() {
        try {
            if (!this.isConnected()) {
                return;
            }

            selectionKey.cancel();
            socketChannel.close();
            disconnected();
        } catch (IOException e) {
            logger.error("关闭连接异常", e);
        }
    }

}
