package quan.network.handler.codec;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import quan.network.handler.Handler;
import quan.network.handler.HandlerContext;
import quan.network.message.Buffer;
import quan.network.message.Message;
import quan.network.message.MessageRegistry;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 消息编解码器，{@link Message}
 * Created by quanchangnai on 2019/6/20.
 */
public class MessageCodec implements Handler<Object> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private MessageRegistry messageRegistry;

    public MessageCodec(MessageRegistry messageRegistry) {
        this.messageRegistry = messageRegistry;
    }

    @Override
    public void onReceived(HandlerContext handlerContext, Object msg) throws Exception {
        if (msg instanceof ByteBuffer) {
            Buffer buffer = new Buffer((ByteBuffer) msg);
            decode(handlerContext, buffer);
        } else if (msg instanceof byte[]) {
            Buffer buffer = new Buffer((byte[]) msg);
            decode(handlerContext, buffer);
        } else {
            handlerContext.triggerReceived(msg);
        }
    }

    private void decode(HandlerContext handlerContext, Buffer buffer) throws IOException {
        int msgId = buffer.readInt();
        buffer.reset();

        Message message = messageRegistry.createMessage(msgId);
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
