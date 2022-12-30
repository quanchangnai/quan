package quan.network.handler;

import quan.network.TaskExecutor;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Objects;

/**
 * 处理器上下文,用来表示处理器链上的一个节点
 *
 * @author quanchangnai
 */
@SuppressWarnings({"unchecked", "rawtypes"})
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

    public TaskExecutor getExecutor() {
        return chain.getExecutor();
    }

    public void triggerConnected() {
        if (getExecutor().isMyThread()) {
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
        if (getExecutor().isMyThread()) {
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

    public void triggerMsgReceived(Object msg) {
        if (getExecutor().isMyThread()) {
            onMsgReceived(msg);
        } else {
            getExecutor().execute(() -> onMsgReceived(msg));
        }
    }

    private void onMsgReceived(Object msg) {
        try {
            if (next != null && next.getHandler() != null) {
                next.getHandler().onMsgReceived(next, msg);
            }
        } catch (Exception e) {
            onExceptionCaught(e);
        }
    }

    public void triggerExceptionCaught(Throwable cause) {
        if (getExecutor().isMyThread()) {
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
            next.triggerExceptionCaught(e);
        }
    }

    /**
     * 触发自定义事件
     */
    public void triggerEvent(Object event) {
        if (getExecutor().isMyThread()) {
            onEventTriggered(event);
        } else {
            getExecutor().execute(() -> onEventTriggered(event));
        }
    }

    private void onEventTriggered(Object event) {
        try {
            if (next != null && next.getHandler() != null) {
                next.getHandler().onEventTriggered(next, event);
            }
        } catch (Exception e) {
            onExceptionCaught(e);
        }
    }

    public void sendMsg(Object msg) {
        Objects.requireNonNull(msg, "参数[msg]不能为空");
        if (getExecutor().isMyThread()) {
            onSendMsg(msg);
        } else {
            getExecutor().execute(() -> onSendMsg(msg));
        }
    }

    private void onSendMsg(Object msg) {
        try {
            if (prev != null && prev.getHandler() != null) {
                prev.getHandler().onSendMsg(prev, msg);
            }
        } catch (Exception e) {
            onExceptionCaught(e);
        }
    }

    public void close() {
        if (getExecutor().isMyThread()) {
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

    public InetSocketAddress getRemoteAddress() {
        return chain.connection.getRemoteAddress();
    }

    public Map<Object, Object> getAttachments() {
        return chain.connection.getAttachments();
    }

}
