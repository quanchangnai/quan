package quan.network.handler;

/**
 * 处理器配置器
 *
 * @author quanchangnai
 */
public abstract class HandlerConfigurer implements Handler<Object> {

    @Override
    public void onConnected(HandlerContext handlerContext) {
        try {
            configureHandler(handlerContext.getHandlerChain());
            handlerContext.triggerConnected();
        } catch (Exception e) {
            handlerContext.triggerExceptionCaught(e);
        } finally {
            handlerContext.getHandlerChain().remove(this);
        }
    }

    protected abstract void configureHandler(HandlerChain handlerChain);

}
