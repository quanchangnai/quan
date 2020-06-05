package quan.network.handler;

import quan.network.TaskExecutor;
import quan.network.Connection;

import java.util.Map;

/**
 * 处理器上下文,用来表示处理器链上的一个节点
 *
 * @author quanchangnai
 */
@SuppressWarnings("unchecked")
public class HandlerContext {

    private Handler handler;

    private HandlerChain chain;

    HandlerContext prev;

    HandlerContext next;

    public HandlerContext(Handler handler, HandlerChain chain) {
        this.handler = handler;
        this.chain = chain;
    }

    public Handler getHandler() {
        return handler;
    }

    public HandlerChain getHandlerChain() {
        return chain;
    }

    public Connection getConnection() {
        return chain.getConnection();
    }

    public TaskExecutor getExecutor() {
        return chain.getExecutor();
    }

    public void triggerConnected() {
        if (getExecutor().isInMyThread()) {
            onConnected();
        } else {
            getExecutor().execute(this::onConnected);
        }
    }

    private void onConnected() {
        try {
            if (next != null && next.getHandler() != null) {
                next.getHandler().onConnected(next);
            }
        } catch (Exception e) {
            onExceptionCaught(e);
        }
    }

    public void triggerDisconnected() {
        if (getExecutor().isInMyThread()) {
            onDisconnected();
        } else {
            getExecutor().execute(this::onDisconnected);
        }
    }

    private void onDisconnected() {
        try {
            if (next != null && next.getHandler() != null) {
                next.getHandler().onDisconnected(next);
            }
        } catch (Exception e) {
            onExceptionCaught(e);
        }
    }

    public void triggerReceived(Object msg) {
        if (getExecutor().isInMyThread()) {
            onReceived(msg);
        } else {
            getExecutor().execute(() -> onReceived(msg));
        }
    }

    private void onReceived(Object msg) {
        try {
            if (next != null && next.getHandler() != null) {
                next.getHandler().onReceived(next, msg);
            }
        } catch (Exception e) {
            onExceptionCaught(e);
        }
    }

    public void triggerException(Throwable cause) {
        if (getExecutor().isInMyThread()) {
            onExceptionCaught(cause);
        } else {
            getExecutor().execute(() -> onExceptionCaught(cause));
        }
    }

    private void onExceptionCaught(Throwable cause) {
        try {
            if (next != null && next.getHandler() != null) {
                next.getHandler().onExceptionCaught(next, cause);
            }
        } catch (Exception e) {
            next.triggerException(e);
        }
    }

    /**
     * 触发自定义事件
     */
    public void triggerEvent(Object event) {
        if (getExecutor().isInMyThread()) {
            onEventTriggered(event);
        } else {
            getExecutor().execute(() -> onEventTriggered(event));
        }
    }

    public void onEventTriggered(Object event) {
        try {
            if (next != null && next.getHandler() != null) {
                next.getHandler().onEventTriggered(next, event);
            }
        } catch (Exception e) {
            onExceptionCaught(e);
        }
    }

    public void send(Object msg) {
        if (getExecutor().isInMyThread()) {
            onSend(msg);
        } else {
            getExecutor().execute(() -> onSend(msg));
        }
    }

    private void onSend(Object msg) {
        try {
            if (prev != null && prev.getHandler() != null) {
                prev.getHandler().onSend(prev, msg);
            }
        } catch (Exception e) {
            onExceptionCaught(e);
        }
    }

    public void close() {
        if (getExecutor().isInMyThread()) {
            onClose();
        } else {
            getExecutor().execute(this::onClose);
        }
    }

    private void onClose() {
        try {
            if (prev != null && prev.getHandler() != null) {
                (prev.getHandler()).onClose(prev);
            }
        } catch (Exception e) {
            onExceptionCaught(e);
        }
    }

    public Map<Object, Object> getAttachments() {
        return chain.getConnection().getAttachments();
    }

}
