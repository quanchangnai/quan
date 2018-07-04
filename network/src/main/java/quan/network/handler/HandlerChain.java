package quan.network.handler;

import quan.network.connection.Connection;
import quan.network.util.TaskExecutor;

import java.nio.ByteBuffer;

/**
 * 处理器链，管理连接上的所有的上行处理器和下行处理器，采用双端链表实现
 *
 * @author quanchangnai
 */
public class HandlerChain {

    private Connection connection;

    private TaskExecutor executor;

    private HandlerContext head;

    private HandlerContext tail;

    public HandlerChain(Connection connection) {
        this.connection = connection;
        this.executor = connection.getExecutor();
        this.head = new HeadHandlerContext(null, this);
        this.tail = new TailHandlerContext(null, this);
        this.head.next = tail;
        this.tail.prev = head;
    }

    public Connection getConnection() {
        return connection;
    }

    public TaskExecutor getExecutor() {
        return executor;
    }

    public void addLast(NetworkHandler handler) {
        if (handler == null) {
            throw new NullPointerException("handler");
        }
        HandlerContext handlerContext = new HandlerContext(handler, this);
        handlerContext.prev = tail.prev;
        handlerContext.next = tail;
        handlerContext.prev.next = handlerContext;
        tail.prev = handlerContext;
        try {
            handler.onHandlerAdded(handlerContext);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addFirst(NetworkHandler handler) {
        if (handler == null) {
            throw new NullPointerException("handler");
        }
        HandlerContext handlerContext = new HandlerContext(handler, this);
        handlerContext.prev = head;
        handlerContext.next = head.next;
        head.next = handlerContext;
        handlerContext.next.prev = handlerContext;
        try {
            handler.onHandlerAdded(handlerContext);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void remove(NetworkHandler handler) {
        if (handler == null) {
            throw new NullPointerException("handler");
        }
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

        try {
            handlerContext.prev.next = handlerContext.next;
            handlerContext.next.prev = handlerContext.prev;
            handlerContext.prev = null;
            handlerContext.next = null;
            handlerContext.getHandler().onHandlerRemoved(handlerContext);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void removeAll() {
        HandlerContext handlerContext = head.next;
        while (handlerContext != tail) {
            remove(handlerContext.getHandler());
            handlerContext = head.next;
        }
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

    private class HeadHandlerContext extends HandlerContext implements OutboundHandler, InboundHandler {

        public HeadHandlerContext(NetworkHandler handler, HandlerChain chain) {
            super(handler, chain);
        }

        @Override
        public NetworkHandler getHandler() {
            return this;
        }

        @Override
        public void onSend(HandlerContext handlerContext, Object msg) {
            if (msg instanceof ByteBuffer) {
                connection.send((ByteBuffer) msg);
            } else {
                try {
                    throw new IllegalArgumentException("发送的消息经过OutboundHandler链处理后的最终结果必须是ByteBuffer类型");
                } catch (Exception e) {
                    handlerContext.triggerExceptionCaught(e);
                }

            }
        }

        @Override
        public void close() {
            getConnection().close();
        }
    }

    private class TailHandlerContext extends HandlerContext implements InboundHandler {

        public TailHandlerContext(NetworkHandler handler, HandlerChain chain) {
            super(handler, chain);
        }

        @Override
        public NetworkHandler getHandler() {
            return this;
        }

        @Override
        public void onConnected(HandlerContext handlerContext) throws Exception {
        }

        @Override
        public void onDisconnected(HandlerContext handlerContext) throws Exception {
        }

        @Override
        public void onReceived(HandlerContext handlerContext, Object msg) throws Exception {
        }

        @Override
        public void onExceptionCaught(HandlerContext handlerContext, Throwable cause) throws Exception {
            handlerContext.close();
            cause.printStackTrace();
        }

    }
}
