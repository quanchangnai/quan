package quan.network.handler;

/**
 * @author quanchangnai
 */
public interface OutboundHandler extends Handler {

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
