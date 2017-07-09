package quan.network.handler;

import quan.network.connection.Connection;
import quan.network.util.SingleThreadExecutor;

/**
 * 处理器上下文
 *
 * @author quanchangnai
 */
public class HandlerContext {

    private NetworkHandler handler;

    private HandlerChain chain;

    private SingleThreadExecutor executor;

    HandlerContext prev;

    HandlerContext next;

    public HandlerContext(NetworkHandler handler, HandlerChain chain) {
        this.handler = handler;
        this.chain = chain;
        this.executor = chain.getExecutor();
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

    public SingleThreadExecutor getExecutor() {
        return executor;
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
        if (executor.isInMyThread()) {
            invokeNextHandlerConnected();
        } else {
            executor.submit(this::invokeNextHandlerConnected);
        }
    }

    private void invokeNextHandlerConnected() {
        try {
            HandlerContext nextInboundHandleContext = findNextInboundHandlerContext();
            if (nextInboundHandleContext != null && nextInboundHandleContext.getHandler() != null) {
                ((InboundHandler) nextInboundHandleContext.getHandler()).onConnected(nextInboundHandleContext);
            }
        } catch (Exception e) {
            invokeNextHandlerEexceptionCaught(e);
        }
    }

    public void triggerDisconnected() {
        if (executor.isInMyThread()) {
            invokeNextHandlerDisconnected();
        } else {
            executor.submit(this::invokeNextHandlerDisconnected);
        }
    }

    private void invokeNextHandlerDisconnected() {
        try {
            HandlerContext nextInboundHandleContext = findNextInboundHandlerContext();
            if (nextInboundHandleContext != null && nextInboundHandleContext.getHandler() != null) {
                ((InboundHandler) nextInboundHandleContext.getHandler()).onDisconnected(nextInboundHandleContext);
            }
        } catch (Exception e) {
            invokeNextHandlerEexceptionCaught(e);
        }
    }

    public void triggerReceived(Object msg) {
        if (executor.isInMyThread()) {
            invokeNextHandlerReceived(msg);
        } else {
            executor.submit(() -> invokeNextHandlerReceived(msg));
        }
    }

    private void invokeNextHandlerReceived(Object msg) {
        try {
            HandlerContext nextInboundHandleContext = findNextInboundHandlerContext();
            if (nextInboundHandleContext != null && nextInboundHandleContext.getHandler() != null) {
                ((InboundHandler) nextInboundHandleContext.getHandler()).onReceived(nextInboundHandleContext, msg);
            }
        } catch (Exception e) {
            invokeNextHandlerEexceptionCaught(e);
        }
    }

    public void triggerExceptionCaught(Throwable cause) {
        if (executor.isInMyThread()) {
            invokeNextHandlerEexceptionCaught(cause);
        } else {
            executor.submit(() -> invokeNextHandlerEexceptionCaught(cause));
        }
    }

    private void invokeNextHandlerEexceptionCaught(Throwable cause) {
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
        if (executor.isInMyThread()) {
            invokePrevHandlerSend(msg);
        } else {
            executor.submit(() -> invokePrevHandlerSend(msg));
        }
    }

    private void invokePrevHandlerSend(Object msg) {
        HandlerContext prevOutboundHandlerContext = findPrevOutboundHandlerContext();
        try {
            if (prevOutboundHandlerContext != null && prevOutboundHandlerContext.getHandler() != null) {
                ((OutboundHandler) prevOutboundHandlerContext.getHandler()).onSend(prevOutboundHandlerContext, msg);
            }
        } catch (Exception e) {
            invokeNextHandlerEexceptionCaught(e);
        }
    }

    public void close() {
        if (executor.isInMyThread()) {
            invokePrevHandlerClose();
        } else {
            executor.submit(this::invokePrevHandlerClose);
        }
    }

    private void invokePrevHandlerClose() {
        HandlerContext prevOutboundHandlerContext = findPrevOutboundHandlerContext();
        try {
            if (prevOutboundHandlerContext != null && prevOutboundHandlerContext.getHandler() != null) {
                ((OutboundHandler) prevOutboundHandlerContext.getHandler()).onClose(prevOutboundHandlerContext);
            }
        } catch (Exception e) {
            invokeNextHandlerEexceptionCaught(e);
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
