package quan.network.handler;

/**
 * 处理器
 *
 * @author quanchangnai
 */
public interface Handler {

    /**
     * 当前处理器被加进到处理器链中了
     *
     * @param handlerContext 当前处理器上下文
     * @throws Exception
     */
    default void onHandlerAdded(HandlerContext handlerContext) throws Exception {
    }

    /**
     * 当前处理器被从处理器链中移除了
     *
     * @param handlerContext 当前处理器上下文
     * @throws Exception
     */
    default void onHandlerRemoved(HandlerContext handlerContext) throws Exception {
    }

}
