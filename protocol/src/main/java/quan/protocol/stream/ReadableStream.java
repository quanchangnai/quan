package quan.protocol.stream;

import java.io.IOException;

/**
 * Created by quanchangnai on 2017/6/30.
 */
public class ReadableStream {

    private byte[] buffer;

    private int index;

    public ReadableStream(byte[] buffer) {
        this.buffer = buffer;
    }

    public void reset() {
        index = 0;
    }

    protected long readVarLong() throws IOException {
        int index = this.index;
        int shift = 0;
        long result = 0;
        while (shift < 64) {
            final byte b = buffer[index++];
            result |= (b & 0b1111111L) << shift;
            if ((b & 0b10000000) == 0) {
                this.index = index;
                return (result >>> 1) ^ -(result & 1);////ZigZag
            }
            shift += 7;
        }
        throw new IOException("数据异常");
    }

    public boolean readBool() throws IOException {
        return readVarLong() != 0;
    }

    public byte readByte() throws IOException {
        return (byte) readVarLong();
    }

    public short readShort() throws IOException {
        return (short) readVarLong();
    }

    public int readInt() throws IOException {
        return (int) readVarLong();
    }

    public long readLong() throws IOException {
        return readVarLong();
    }

    public float readFloat() throws IOException {
        return Float.intBitsToFloat((int) readVarLong());
    }

    public double readDouble() throws IOException {
        return Double.longBitsToDouble(readVarLong());
    }

    public byte[] readBytes() throws IOException {
        int length = readInt();
        byte[] bytes = new byte[length];
        System.arraycopy(buffer, index, bytes, 0, length);
        index += length;
        return bytes;
    }

    public String readString() throws IOException {
        return new String(readBytes(), "UTF-8");
    }

    public String readString(String charsetName) throws IOException {
        return new String(readBytes(), charsetName);
    }

}
