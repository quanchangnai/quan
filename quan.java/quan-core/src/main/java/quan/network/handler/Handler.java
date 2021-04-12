package quan.network.handler;

/**
 * 网络IO处理器
 *
 * @author quanchangnai
 */
@SuppressWarnings("RedundantThrows")
public interface Handler<M> {

    /**
     * 连接建立了
     */
    default void onConnected(HandlerContext handlerContext) throws Exception {
        handlerContext.triggerConnected();
    }

    /**
     * 连接断开了
     */
    default void onDisconnected(HandlerContext handlerContext) throws Exception {
        handlerContext.triggerDisconnected();
    }

    /**
     * 收到消息了
     */
    default void onMsgReceived(HandlerContext handlerContext, M msg) throws Exception {
        handlerContext.triggerMsgReceived(msg);
    }

    /**
     * 捕获异常了
     */
    default void onExceptionCaught(HandlerContext handlerContext, Throwable cause) {
        handlerContext.triggerExceptionCaught(cause);
    }

    /**
     * 触发了自定义事件
     */
    default void onEventTriggered(HandlerContext handlerContext, Object event) throws Exception {
        handlerContext.triggerEvent(event);
    }

    /**
     * 发送消息
     */
    default void onSendMsg(HandlerContext handlerContext, Object msg) throws Exception {
        handlerContext.sendMsg(msg);
    }

    /**
     * 关闭连接
     */
    default void onClose(HandlerContext handlerContext) throws Exception {
        handlerContext.close();
    }

}
