package quan.network.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.network.connection.Connection;
import quan.network.util.TaskExecutor;

import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * 处理器链，管理连接上的所有的上行处理器和下行处理器，采用双端链表实现
 *
 * @author quanchangnai
 */
public class HandlerChain {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private Connection connection;

    private HandlerContext head;

    private HandlerContext tail;

    public HandlerChain(Connection connection) {
        this.connection = connection;
        this.head = new HeadHandlerContext(null, this);
        this.tail = new TailHandlerContext(null, this);
        this.head.next = tail;
        this.tail.prev = head;
    }

    public Connection getConnection() {
        return connection;
    }

    public TaskExecutor getExecutor() {
        return connection.getExecutor();
    }

    public void addLast(Handler handler) {
        Objects.requireNonNull(handler, "参数handler不能为空");
        HandlerContext handlerContext = new HandlerContext(handler, this);
        handlerContext.prev = tail.prev;
        handlerContext.next = tail;
        handlerContext.prev.next = handlerContext;
        tail.prev = handlerContext;
    }

    public void addFirst(Handler handler) {
        Objects.requireNonNull(handler, "参数handler不能为空");
        HandlerContext handlerContext = new HandlerContext(handler, this);
        handlerContext.prev = head;
        handlerContext.next = head.next;
        head.next = handlerContext;
        handlerContext.next.prev = handlerContext;
    }

    public void remove(Handler handler) {
        Objects.requireNonNull(handler, "参数handler不能为空");
        HandlerContext handlerContext = head;
        while (handlerContext != tail) {
            if (handlerContext.getHandler() == handler) {
                break;
            }
            handlerContext = handlerContext.next;
        }
        if (handlerContext == head || handlerContext == tail) {
            return;
        }

        handlerContext.prev.next = handlerContext.next;
        handlerContext.next.prev = handlerContext.prev;
        handlerContext.prev = null;
        handlerContext.next = null;
    }

    public void removeAll() {
        head.next = tail;
        tail.prev = head;
    }

    public void triggerConnected() {
        head.triggerConnected();
    }

    public void triggerDisconnected() {
        head.triggerDisconnected();
    }

    public void triggerReceived(Object msg) {
        head.triggerReceived(msg);
    }

    public void triggerExceptionCaught(Throwable cause) {
        head.triggerExceptionCaught(cause);
    }

    private class HeadHandlerContext extends HandlerContext implements Handler {

        public HeadHandlerContext(Handler handler, HandlerChain chain) {
            super(handler, chain);
        }

        @Override
        public Handler getHandler() {
            return this;
        }

        @Override
        public void onSend(HandlerContext handlerContext, Object msg) {
            if (msg instanceof ByteBuffer) {
                connection.send((ByteBuffer) msg);
            } else if (msg instanceof byte[]) {
                connection.send(ByteBuffer.wrap((byte[]) msg));
            } else {
                Exception exception = new IllegalArgumentException("发送的消息经过处理器链后的最终结果必须是ByteBuffer或者byte[]类型");
                handlerContext.triggerExceptionCaught(exception);
            }
        }

        @Override
        public void close() {
            getConnection().close();
        }
    }

    private class TailHandlerContext extends HandlerContext implements Handler {

        public TailHandlerContext(Handler handler, HandlerChain chain) {
            super(handler, chain);
        }

        @Override
        public Handler getHandler() {
            return this;
        }

        @Override
        public void onExceptionCaught(HandlerContext handlerContext, Throwable cause) {
            handlerContext.close();
            logger.error("", cause);
        }

    }
}
