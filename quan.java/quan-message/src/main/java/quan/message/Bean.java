package quan.message;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public abstract class Bean {

    public final byte[] encode() {
        CodedBuffer buffer = new DefaultCodedBuffer();
        encode(buffer);
        return buffer.remainingBytes();
    }

    public final void decode(byte[] bytes) {
        decode(new DefaultCodedBuffer(bytes));
    }

    public void encode(CodedBuffer buffer) {
    }

    public void decode(CodedBuffer buffer) {
    }

    protected static void writeTag(CodedBuffer buffer, int tag) {
        buffer.writeByte((byte) tag);
    }

    protected static int readTag(CodedBuffer buffer) {
        return buffer.readByte() & 0xFF;
    }

    protected static void skipField(int tag, CodedBuffer buffer) {
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
