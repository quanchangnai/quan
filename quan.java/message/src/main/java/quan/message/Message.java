package quan.message;

import java.io.IOException;

/**
 * 基于VarInt和ZigZag编码的消息<br/>
 * Created by quanchangnai on 2019/6/20.
 */
public abstract class Message extends Bean {

    /**
     * 消息ID
     */
    public abstract int getId();

    public abstract Message create();

    @Override
    public void encode(Buffer buffer) throws IOException {
        buffer.writeInt(getId());
    }

    @Override
    public void decode(Buffer buffer) throws IOException {
        int msgId = buffer.readInt();
        if (msgId != getId()) {
            throw new IOException(String.format("消息ID不匹配,期望值[%s],实际值[%s]", getId(), msgId));
        }
    }
}
