package quan.network.nio.codec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.message.Buffer;
import quan.message.Message;
import quan.message.MessageRegistry;
import quan.message.SimpleBuffer;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

/**
 * 消息编解码器
 */
public class MessageCodec extends AbstractCodec {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private MessageRegistry messageRegistry;

    {
        encodeTypes.add(Message.class);
        decodeTypes.add(ByteBuffer.class);
        decodeTypes.add(byte[].class);
    }

    public MessageCodec(MessageRegistry messageRegistry) {
        this.messageRegistry = messageRegistry;
    }

    @Override
    protected List<Object> decode(Object msg) throws Exception {
        Buffer buffer;
        if (msg instanceof ByteBuffer) {
            buffer = new SimpleBuffer((ByteBuffer) msg);
        } else {
            buffer = new SimpleBuffer((byte[]) msg);
        }

        int msgId = buffer.readInt();
        buffer.reset();

        Message message = messageRegistry.create(msgId);
        if (message != null) {
            message.decode(buffer);
        } else {
            logger.error("消息{}创建失败", msgId);
        }

        return Collections.singletonList(message);
    }

    @Override
    protected Object encode(Object msg) throws Exception {
        return ((Message) msg).encode();
    }

}
