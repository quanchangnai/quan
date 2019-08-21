package quan.network.handler;

/**
 * 处理器
 *
 * @author quanchangnai
 */
public interface Handler<M> {

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

    /**
     * 触发了自定义事件
     *
     * @param handlerContext
     * @param event
     */
    default void onEventTriggered(HandlerContext handlerContext, Object event) {
        handlerContext.triggerEvent(event);
    }

    /**
     * 发送消息
     *
     * @param handlerContext
     * @param msg
     * @throws Exception
     */
    default void onSend(HandlerContext handlerContext, Object msg) throws Exception {
        handlerContext.send(msg);
    }

    /**
     * 关闭连接
     *
     * @param handlerContext
     * @throws Exception
     */
    default void onClose(HandlerContext handlerContext) throws Exception {
        handlerContext.close();
    }
}
