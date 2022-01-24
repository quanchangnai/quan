package quan.message.common;

import quan.message.*;

/**
 * 消息头<br/>
 * 代码自动生成，请勿手动修改
 */
public abstract class MessageHeader extends Message {

    //消息序号
    protected int seq;

    //错误码
    protected int error;


    /**
     * 消息序号
     */
    public int getSeq() {
        return seq;
    }

    /**
     * 消息序号
     */
    public MessageHeader setSeq(int seq) {
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

        buffer.writeInt(this.seq);
        buffer.writeInt(this.error);
    }

    @Override
    public void decode(Buffer buffer) {
        super.decode(buffer);

        this.seq = buffer.readInt();
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
