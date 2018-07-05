package quan.network.handler;

import quan.network.connection.Connection;
import quan.network.util.TaskExecutor;

/**
 * 处理器上下文
 *
 * @author quanchangnai
 */
public class HandlerContext {

    private NetworkHandler handler;

    private HandlerChain chain;

    HandlerContext prev;

    HandlerContext next;

    public HandlerContext(NetworkHandler handler, HandlerChain chain) {
        this.handler = handler;
        this.chain = chain;
    }

    public NetworkHandler getHandler() {
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

    private static boolean isInbound(NetworkHandler handler) {
        return handler instanceof InboundHandler;
    }

    private static boolean isOutbound(NetworkHandler handler) {
        return handler instanceof OutboundHandler;
    }

    private HandlerContext findNextInboundHandlerContext() {
        HandlerContext context = this.next;
        while (context != null && context.getHandler() != null) {
            if (isInbound(context.getHandler())) {
                break;
            }
            context = context.next;
        }
        return context;
    }

    private HandlerContext findPrevOutboundHandlerContext() {
        HandlerContext context = this.prev;
        while (context != null && context.getHandler() != null) {
            if (context.getHandler() != null && isOutbound(context.getHandler())) {
                break;
            }
            context = context.prev;
        }
        return context;
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
            HandlerContext nextInboundHandleContext = findNextInboundHandlerContext();
            if (nextInboundHandleContext != null && nextInboundHandleContext.getHandler() != null) {
                ((InboundHandler) nextInboundHandleContext.getHandler()).onConnected(nextInboundHandleContext);
            }
        } catch (Exception e) {
            triggerNextHandlerEexceptionCaught(e);
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
            HandlerContext nextInboundHandleContext = findNextInboundHandlerContext();
            if (nextInboundHandleContext != null && nextInboundHandleContext.getHandler() != null) {
                ((InboundHandler) nextInboundHandleContext.getHandler()).onDisconnected(nextInboundHandleContext);
            }
        } catch (Exception e) {
            triggerNextHandlerEexceptionCaught(e);
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
            HandlerContext nextInboundHandleContext = findNextInboundHandlerContext();
            if (nextInboundHandleContext != null && nextInboundHandleContext.getHandler() != null) {
                ((InboundHandler) nextInboundHandleContext.getHandler()).onReceived(nextInboundHandleContext, msg);
            }
        } catch (Exception e) {
            triggerNextHandlerEexceptionCaught(e);
        }
    }

    public void triggerExceptionCaught(Throwable cause) {
        if (getExecutor().isInMyThread()) {
            triggerNextHandlerEexceptionCaught(cause);
        } else {
            getExecutor().submit(() -> triggerNextHandlerEexceptionCaught(cause));
        }
    }

    private void triggerNextHandlerEexceptionCaught(Throwable cause) {
        HandlerContext nextInboundHandleContext = findNextInboundHandlerContext();
        try {
            if (nextInboundHandleContext != null && nextInboundHandleContext.getHandler() != null) {
                ((InboundHandler) nextInboundHandleContext.getHandler()).onExceptionCaught(nextInboundHandleContext, cause);
            }
        } catch (Exception e) {
            nextInboundHandleContext.triggerExceptionCaught(e);
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
        HandlerContext prevOutboundHandlerContext = findPrevOutboundHandlerContext();
        try {
            if (prevOutboundHandlerContext != null && prevOutboundHandlerContext.getHandler() != null) {
                ((OutboundHandler) prevOutboundHandlerContext.getHandler()).onSend(prevOutboundHandlerContext, msg);
            }
        } catch (Exception e) {
            triggerNextHandlerEexceptionCaught(e);
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
        HandlerContext prevOutboundHandlerContext = findPrevOutboundHandlerContext();
        try {
            if (prevOutboundHandlerContext != null && prevOutboundHandlerContext.getHandler() != null) {
                ((OutboundHandler) prevOutboundHandlerContext.getHandler()).onClose(prevOutboundHandlerContext);
            }
        } catch (Exception e) {
            triggerNextHandlerEexceptionCaught(e);
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
