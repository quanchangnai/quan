package quan.network.handler;

/**
 * @author quanchangnai
 */
public abstract class HandlerInitializer implements InboundHandler {

    @Override
    public void onConnected(HandlerContext handlerContext) throws Exception {
        try {
            initHandler(handlerContext.getHandlerChain());
            handlerContext.triggerConnected();
        } catch (Exception e) {
            handlerContext.triggerExceptionCaught(e);
        } finally {
            handlerContext.getHandlerChain().remove(this);
        }
    }

    public abstract void initHandler(HandlerChain handlerChain) throws Exception;

}
