package quan.network;

import quan.network.handler.HandlerChain;
import quan.network.handler.HandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * nio网络连接的封装，提供读写数据等功能
 *
 * @author quanchangnai
 */
public class Connection {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private boolean connected;

    private SelectionKey selectionKey;

    private SocketChannel socketChannel;

    private SingleThreadExecutor executor;

    protected HandlerChain handlerChain;

    /**
     * 读缓冲区里
     */
    private ByteBuffer readBuffer;

    /**
     * 写缓冲区里
     */
    private ByteBuffer writeBuffer;

    /**
     * 写缓冲区里的数据是否已经全部写进了Socket写缓冲区里
     */
    private boolean writeFinished = true;

    private Queue<ByteBuffer> msgQueue = new LinkedList<>();

    private Map<Object, Object> attachments = new ConcurrentHashMap<>();

    public Connection(SelectionKey selectionKey, SingleThreadExecutor executor, int readBufferSize, int writeBufferSize) {
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

    public SingleThreadExecutor getExecutor() {
        return executor;
    }

    public boolean isConnected() {
        return connected;
    }

    public InetSocketAddress getRemoteAddress() {
        try {
            return (InetSocketAddress) socketChannel.getRemoteAddress();
        } catch (IOException e) {
            logger.error("获取地址失败", e);
        }
        return null;
    }

    public Map<Object, Object> getAttachments() {
        return attachments;
    }

    /**
     * 读数据
     *
     * @return 读取的字节数，负数表示连接已断开
     */
    public int read() {
        int readCount;
        try {
            readCount = socketChannel.read(readBuffer);
            if (readCount > 0) {
                readBuffer.flip();
                triggerReceived(readBuffer);
            }
            readBuffer.clear();
        } catch (IOException e) {
            triggerExceptionCaught(e);
            close();
            readCount = -1;
        }
        return readCount;
    }


    /**
     * 写数据
     *
     * @return 写入的字节数，负数表示连接已断开
     */
    public int write() {
        if (writeFinished && msgQueue.isEmpty()) {
            int interestOps = selectionKey.interestOps();
            if ((interestOps & SelectionKey.OP_WRITE) != 0) {
                selectionKey.interestOps(interestOps & ~SelectionKey.OP_WRITE);
            }
            return 0;
        }

        int writeCount = 0;

        if (!writeFinished) {
            //缓冲区还有数据没有写入通道
            int remaining = writeBuffer.remaining();
            int doWriteCount = doWrite(writeBuffer);
            if (doWriteCount < 0) {
                return -1;
            } else if (doWriteCount < remaining) {
                //Socket发送缓冲区满了，下次再写
                writeCount += doWriteCount;
                return writeCount;
            } else {
                writeCount += doWriteCount;
            }
        }

        writeBuffer.clear();

        ByteBuffer msgBuffer = msgQueue.peek();
        while (msgBuffer != null) {
            if (writeBuffer.remaining() >= msgBuffer.remaining()) {
                // 缓冲区剩余空间充足,完整写入消息
                writeBuffer.put(msgBuffer);
            } else {
                // 缓冲区剩余空间不足,拆分消息
                while (msgBuffer.hasRemaining()) {
                    if (writeBuffer.hasRemaining()) {
                        if (msgBuffer.remaining() <= writeBuffer.remaining()) {
                            writeBuffer.put(msgBuffer);
                        } else {
                            byte[] writeBytes = new byte[writeBuffer.remaining()];
                            msgBuffer.get(writeBytes);
                            writeBuffer.put(writeBytes);
                        }
                    }

                    if (!writeBuffer.hasRemaining()) {
                        //缓冲区满了，写入通道
                        writeBuffer.flip();
                        int remaining = writeBuffer.remaining();
                        int doWriteCount = doWrite(writeBuffer);
                        if (doWriteCount < 0) {
                            return -1;
                        } else if (doWriteCount < remaining) {
                            //Socket发送缓冲区满了，下次再写
                            writeFinished = false;
                            writeCount += doWriteCount;
                            return writeCount;
                        } else {
                            writeBuffer.clear();
                            writeCount += doWriteCount;
                        }
                    }
                }

            }

            msgQueue.poll();
            msgBuffer = msgQueue.peek();

        }

        writeBuffer.flip();
        if (writeBuffer.hasRemaining()) {
            //缓冲区还有数据没有写入通道
            int remaining = writeBuffer.remaining();
            int doWriteCount = doWrite(writeBuffer);
            if (doWriteCount < 0) {
                return -1;
            } else if (doWriteCount < remaining) {
                //Socket发送缓冲区满了，下次再写
                writeFinished = false;
                writeCount += doWriteCount;
                return writeCount;
            } else {
                writeCount += doWriteCount;
            }
        }

        writeFinished = true;
        return writeCount;
    }

    protected int doWrite(ByteBuffer writeBuffer) {
        int writeCount;
        try {
            writeCount = socketChannel.write(writeBuffer);
        } catch (IOException e) {
            triggerExceptionCaught(e);
            close();
            writeCount = -1;
        }
        return writeCount;
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
     */
    protected void triggerReceived(ByteBuffer msg) {
        handlerChain.triggerReceived(msg);
    }

    /**
     * 触发捕获了异常
     */
    protected void triggerExceptionCaught(Throwable cause) {
        handlerChain.triggerExceptionCaught(cause);
    }

    /**
     * 发送消息，不能在具体逻辑中直接调用，具体逻辑中应该调用{@link HandlerContext#sendMsg(Object)}
     */
    public void send(ByteBuffer msg) {
        if (executor.isMyThread()) {
            doSend(msg);
        } else {
            executor.execute(() -> doSend(msg));
        }
    }

    protected void doSend(ByteBuffer msg) {
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
        if (executor.isMyThread()) {
            doClose();
        } else {
            executor.execute(this::doClose);
        }
    }

    protected void doClose() {
        try {
            if (!this.isConnected()) {
                return;
            }
            selectionKey.cancel();
            socketChannel.close();
            triggerDisconnected();
        } catch (IOException e) {
            triggerExceptionCaught(e);
        }
    }

}
