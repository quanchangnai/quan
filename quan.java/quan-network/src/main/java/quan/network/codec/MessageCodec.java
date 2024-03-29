package quan.network.codec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.message.CodedBuffer;
import quan.message.DefaultCodedBuffer;
import quan.message.Message;
import quan.message.MessageRegistry;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 消息编码解码器
 */
public class MessageCodec extends Codec {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final MessageRegistry messageRegistry;

    {
        encodeTypes.add(Message.class);
        decodeTypes.add(ByteBuffer.class);
        decodeTypes.add(byte[].class);
    }

    public MessageCodec(MessageRegistry messageRegistry) {
        this.messageRegistry = Objects.requireNonNull(messageRegistry, "参数消息注册表[messageRegistry]不能为空");
    }

    @Override
    protected List<Object> decode(Object msg) {
        CodedBuffer buffer;
        if (msg instanceof ByteBuffer) {
            buffer = new DefaultCodedBuffer((ByteBuffer) msg);
        } else {
            buffer = new DefaultCodedBuffer((byte[]) msg);
        }

        int msgId = buffer.readInt();
        buffer.reset();

        Message message = messageRegistry.create(msgId);
        if (message != null) {
            message.decode(buffer);
        } else {
            logger.error("消息[{}]创建失败", msgId);
        }

        return Collections.singletonList(message);
    }

    @Override
    protected List<Object> encode(Object msg) {
        return Collections.singletonList(((Message) msg).encode());
    }

}
