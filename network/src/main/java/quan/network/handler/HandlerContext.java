package quan.network.handler;

import quan.network.connection.Connection;
import quan.network.util.TaskExecutor;

/**
 * 处理器上下文
 *
 * @author quanchangnai
 */
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
            triggerNextHandlerConnected();
        } else {
            getExecutor().submit(this::triggerNextHandlerConnected);
        }
    }

    private void triggerNextHandlerConnected() {
        try {
            if (next != null && next.getHandler() != null) {
                next.getHandler().onConnected(next);
            }
        } catch (Exception e) {
            triggerNextHandlerExceptionCaught(e);
        }
    }

    public void triggerDisconnected() {
        if (getExecutor().isInMyThread()) {
            triggerNextHandlerDisconnected();
        } else {
            getExecutor().submit(this::triggerNextHandlerDisconnected);
        }
    }

    private void triggerNextHandlerDisconnected() {
        try {
            if (next != null && next.getHandler() != null) {
                next.getHandler().onDisconnected(next);
            }
        } catch (Exception e) {
            triggerNextHandlerExceptionCaught(e);
        }
    }

    public void triggerReceived(Object msg) {
        if (getExecutor().isInMyThread()) {
            triggerNextHandlerReceived(msg);
        } else {
            getExecutor().submit(() -> triggerNextHandlerReceived(msg));
        }
    }

    private void triggerNextHandlerReceived(Object msg) {
        try {
            if (next != null && next.getHandler() != null) {
                next.getHandler().onReceived(next, msg);
            }
        } catch (Exception e) {
            triggerNextHandlerExceptionCaught(e);
        }
    }

    public void triggerExceptionCaught(Throwable cause) {
        if (getExecutor().isInMyThread()) {
            triggerNextHandlerExceptionCaught(cause);
        } else {
            getExecutor().submit(() -> triggerNextHandlerExceptionCaught(cause));
        }
    }

    private void triggerNextHandlerExceptionCaught(Throwable cause) {
        try {
            if (next != null && next.getHandler() != null) {
                next.getHandler().onExceptionCaught(next, cause);
            }
        } catch (Exception e) {
            next.triggerExceptionCaught(e);
        }
    }

    public void send(Object msg) {
        if (getExecutor().isInMyThread()) {
            triggerPrevHandlerSend(msg);
        } else {
            getExecutor().submit(() -> triggerPrevHandlerSend(msg));
        }
    }

    private void triggerPrevHandlerSend(Object msg) {
        try {
            if (prev != null && prev.getHandler() != null) {
                prev.getHandler().onSend(prev, msg);
            }
        } catch (Exception e) {
            triggerNextHandlerExceptionCaught(e);
        }
    }

    public void close() {
        if (getExecutor().isInMyThread()) {
            triggerPrevHandlerClose();
        } else {
            getExecutor().submit(this::triggerPrevHandlerClose);
        }
    }

    private void triggerPrevHandlerClose() {
        try {
            if (prev != null && prev.getHandler() != null) {
                (prev.getHandler()).onClose(prev);
            }
        } catch (Exception e) {
            triggerNextHandlerExceptionCaught(e);
        }
    }

    public Object putAttr(Object key, Object value) {
        return chain.getConnection().putAttr(key, value);
    }

    public Object getAttr(Object key) {
        return chain.getConnection().getAttr(key);
    }

    public Object removeAttr(Object key) {
        return chain.getConnection().removeAttr(key);
    }

}
