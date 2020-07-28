package quan.message;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public abstract class Bean {

    public final byte[] encode() {
        Buffer buffer = new SimpleBuffer();
        encode(buffer);
        return buffer.remainingBytes();
    }

    public final void decode(byte[] bytes) {
        decode(new SimpleBuffer(bytes));
    }

    public void encode(Buffer buffer) {
    }

    public void decode(Buffer buffer) {
    }

    protected void skipField(int tag, Buffer buffer) {
        switch (tag & 0b11) {
            case 0:
                buffer.readLong();
                break;
            case 1:
                buffer.readFloat();
                break;
            case 2:
                buffer.readDouble();
                break;
            case 3:
                buffer.skipBytes();
                break;
        }
    }
}
