package quan.message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by quanchangnai on 2019/7/11.
 */
public class NettyMessageCodec extends ByteToMessageCodec<Message> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private MessageFactory messageFactory;

    public NettyMessageCodec(MessageFactory messageFactory) {
        super(Message.class);
        this.messageFactory = messageFactory;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf byteBuf) throws Exception {
        byteBuf.writeBytes(msg.encode());
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);

        Buffer buffer = new BytesBuffer(bytes);
        int msgId = buffer.readInt();
        Message message = messageFactory.create(msgId);
        if (message == null) {
            logger.error("消息{}创建失败", msgId);
            return;
        }

        message.decode(buffer);
        out.add(message);
    }
}
