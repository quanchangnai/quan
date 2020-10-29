package quan.message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

/**
 * 集成Netty的消息编解码器
 * Created by quanchangnai on 2019/7/11.
 */
public class NettyMessageCodec extends ByteToMessageCodec<Message> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private MessageRegistry messageRegistry;

    public NettyMessageCodec(MessageRegistry messageRegistry) {
        super(Message.class);
        this.messageRegistry = Objects.requireNonNull(messageRegistry, "消息注册表不能为空");
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf byteBuf) {
        msg.encode(new NettyBuffer(byteBuf));
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) {
        Buffer buffer = new NettyBuffer(byteBuf);
        int msgId = buffer.readInt();
        buffer.reset();
        Message message = messageRegistry.create(msgId);
        if (message == null) {
            logger.error("消息{}创建失败", msgId);
            return;
        }

        message.decode(buffer);
        out.add(message);
    }
}
