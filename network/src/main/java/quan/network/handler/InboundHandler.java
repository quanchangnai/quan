package quan.network.handler;

/**
 * @author quanchangnai
 */
public interface InboundHandler<M> extends Handler {

    /**
     * 连接建立了
     *
     * @param handlerContext
     * @throws Exception
     */
    default void onConnected(HandlerContext handlerContext) throws Exception {
        handlerContext.triggerConnected();
    }

    /**
     * 连接断开了
     *
     * @param handlerContext
     * @throws Exception
     */
    default void onDisconnected(HandlerContext handlerContext) throws Exception {
        handlerContext.triggerDisconnected();
    }

    /**
     * 收到消息了
     *
     * @param handlerContext
     * @param msg
     * @throws Exception
     */
    default void onReceived(HandlerContext handlerContext, M msg) throws Exception {
        handlerContext.triggerReceived(msg);
    }

    /**
     * 捕获异常了
     *
     * @param handlerContext
     * @param cause
     * @throws Exception
     */
    default void onExceptionCaught(HandlerContext handlerContext, Throwable cause) throws Exception {
        handlerContext.triggerExceptionCaught(cause);
    }
}
