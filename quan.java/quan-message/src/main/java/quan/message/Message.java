package quan.message;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 基于VarInt和ZigZag编码的消息
 */
public abstract class Message extends Bean {

    /**
     * 消息ID
     */
    @JSONField(name = "_id")
    public abstract int getId();

    public abstract Message create();

    @Override
    public void encode(CodedBuffer buffer) {
        buffer.writeInt(getId());
    }

    @Override
    public void decode(CodedBuffer buffer) {
        int msgId = buffer.readInt();
        if (msgId != getId()) {
            throw new RuntimeException(String.format("消息ID不匹配,期望值[%s],实际值[%s]", getId(), msgId));
        }
    }

}
