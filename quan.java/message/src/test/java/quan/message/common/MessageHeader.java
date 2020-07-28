package quan.message.common;

import java.util.*;
import quan.message.*;

/**
 * 消息头<br/>
 * 自动生成
 */
public abstract class MessageHeader extends Message {

    //消息序号
    protected long seq;

    //错误码
    protected int error;


    /**
     * 消息序号
     */
    public long getSeq() {
        return seq;
    }

    /**
     * 消息序号
     */
    public MessageHeader setSeq(long seq) {
        this.seq = seq;
        return this;
    }

    /**
     * 错误码
     */
    public int getError() {
        return error;
    }

    /**
     * 错误码
     */
    public MessageHeader setError(int error) {
        this.error = error;
        return this;
    }

    @Override
    public void encode(Buffer buffer) {
        super.encode(buffer);

        buffer.writeLong(this.seq);
        buffer.writeInt(this.error);
    }

    @Override
    public void decode(Buffer buffer) {
        super.decode(buffer);

        this.seq = buffer.readLong();
        this.error = buffer.readInt();
    }

    @Override
    public String toString() {
        return "MessageHeader{" +
                "seq=" + seq +
                ",error=" + error +
                '}';

    }

}
