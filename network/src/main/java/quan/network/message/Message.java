package quan.network.message;

import java.io.IOException;

/**
 * 消息
 * Created by quanchangnai on 2019/6/20.
 */
public abstract class Message extends Bean {

    /**
     * 消息ID
     */
    private final int _id;

    /**
     * 消息序号
     */
    private long sn;

    protected Message(int _id) {
        this._id = _id;
    }

    public final int getId() {
        return _id;
    }

    public final long getSn() {
        return sn;
    }

    public final void setSn(long sn) {
        this.sn = sn;
    }

    public abstract Message create();

    @Override
    public void encode(Buffer buffer) throws IOException {
        buffer.writeInt(getId());
        buffer.writeLong(sn);
    }

    @Override
    public void decode(Buffer buffer) throws IOException {
        int msgId = buffer.readInt();
        if (msgId != _id) {
            throw new IOException("消息ID不匹配,目标值：" + _id + "，实际值：" + msgId);
        }
        sn = buffer.readLong();
    }
}
