package quan.message.common;

import java.util.*;
import java.io.IOException;
import quan.message.*;

/**
 * 消息头<br/>
 * 自动生成
 */
public abstract class HeadedMessage extends Message {

    //消息序号
    private long seq;

    //错误码
    private int error;


    /**
     * 消息序号
     */
    public long getSeq() {
        return seq;
    }

    /**
     * 消息序号
     */
    public HeadedMessage setSeq(long seq) {
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
    public HeadedMessage setError(int error) {
        this.error = error;
        return this;
    }

    @Override
    public void encode(Buffer buffer) throws IOException {
        super.encode(buffer);

        buffer.writeLong(this.seq);
        buffer.writeInt(this.error);
    }

    @Override
    public void decode(Buffer buffer) throws IOException {
        super.decode(buffer);

        this.seq = buffer.readLong();
        this.error = buffer.readInt();
    }

    @Override
    public String toString() {
        return "HeadedMessage{" +
                "seq=" + seq +
                ",error=" + error +
                '}';

    }

}
