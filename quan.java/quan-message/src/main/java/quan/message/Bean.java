package quan.message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

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

    public void validate() {

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

    public String toJson() {
        return JSON.toJSONString(this, SerializerFeature.DisableCircularReferenceDetect);
    }

}
