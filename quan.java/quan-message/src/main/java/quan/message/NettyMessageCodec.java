package quan.message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * 集成Netty的消息编解码器
 * Created by quanchangnai on 2019/7/11.
 */
public class NettyMessageCodec extends ByteToMessageCodec<Message> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private Function<Integer, Message> messageFactory;

    public NettyMessageCodec(Function<Integer, Message> messageFactory) {
        super(Message.class);
        this.messageFactory = Objects.requireNonNull(messageFactory, "消息工厂不能为空");
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf byteBuf) {
        msg.encode(new NettyCodedBuffer(byteBuf));
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) {
        CodedBuffer buffer = new NettyCodedBuffer(byteBuf);
        int msgId = buffer.readInt();
        buffer.reset();
        Message message = messageFactory.apply(msgId);
        if (message == null) {
            logger.error("消息{}创建失败", msgId);
            return;
        }

        message.decode(buffer);
        out.add(message);
    }
}
