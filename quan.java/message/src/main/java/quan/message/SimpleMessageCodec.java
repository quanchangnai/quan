package quan.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.network.handler.Handler;
import quan.network.handler.HandlerContext;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 简单的消息编解码器
 */
public class SimpleMessageCodec implements Handler<Object> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private MessageRegistry messageRegistry;

    public SimpleMessageCodec(MessageRegistry messageRegistry) {
        this.messageRegistry = messageRegistry;
    }

    @Override
    public void onReceived(HandlerContext handlerContext, Object msg) throws Exception {
        if (msg instanceof ByteBuffer) {
            Buffer buffer = new SimpleBuffer((ByteBuffer) msg);
            decode(handlerContext, buffer);
        } else if (msg instanceof byte[]) {
            Buffer buffer = new SimpleBuffer((byte[]) msg);
            decode(handlerContext, buffer);
        } else {
            handlerContext.triggerReceived(msg);
        }
    }

    private void decode(HandlerContext handlerContext, Buffer buffer) throws IOException {
        int msgId = buffer.readInt();
        buffer.reset();
        Message message = messageRegistry.create(msgId);
        if (message == null) {
            logger.error("消息{}创建失败", msgId);
            return;
        }

        message.decode(buffer);
        handlerContext.triggerReceived(message);
    }

    @Override
    public void onSend(HandlerContext handlerContext, Object msg) throws Exception {
        if (msg instanceof Message) {
            handlerContext.send(((Message) msg).encode());
        } else {
            handlerContext.send(msg);
        }
    }

}
