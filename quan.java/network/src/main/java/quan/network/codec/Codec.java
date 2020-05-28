package quan.network.codec;

import quan.network.handler.Handler;
import quan.network.handler.HandlerContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 抽象编解码器
 * Created by quanchangnai on 2020/5/27.
 */
public abstract class Codec implements Handler<Object> {

    protected final Set<Class<?>> encodeTypes = new HashSet<>();

    protected final Set<Class<?>> decodeTypes = new HashSet<>();

    @Override
    public void onReceived(HandlerContext handlerContext, Object msg) throws Exception {
        if (decodeTypes.stream().anyMatch(t -> t.isAssignableFrom(msg.getClass()))) {
            List<Object> decodedMsgs = decode(msg);
            for (Object decodedMsg : decodedMsgs) {
                handlerContext.triggerReceived(decodedMsg);
            }
        } else {
            handlerContext.triggerReceived(msg);
        }
    }

    @Override
    public void onSend(HandlerContext handlerContext, Object msg) throws Exception {
        if (encodeTypes.stream().anyMatch(t -> t.isAssignableFrom(msg.getClass()))) {
            handlerContext.send(encode(msg));
        } else {
            handlerContext.send(msg);
        }
    }

    protected abstract List<Object> decode(Object msg) throws Exception;

    protected abstract Object encode(Object msg) throws Exception;

}
