package quan.protocol.stream;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Created by quanchangnai on 2017/6/30.
 */
public class WritableStream {

    private byte[] buffer;

    private int index;

    private int bufferInitLength = 128;

    public WritableStream() {
        buffer = new byte[bufferInitLength];
    }

    public WritableStream(int bufferInitLength) {
        this.bufferInitLength = bufferInitLength;
        buffer = new byte[bufferInitLength];
    }

    public byte[] toBytes() {
        byte[] bytes = new byte[index];
        System.arraycopy(buffer, 0, bytes, 0, index);
        return bytes;
    }

    public void clear() {
        index = 0;
    }

    private void checkBufferSize(int add) {
        int realAdd = add > 10 ? add : bufferInitLength;
        if (index + add >= this.buffer.length) {
            byte[] buffer = new byte[this.buffer.length + realAdd];
            System.arraycopy(this.buffer, 0, buffer, 0, this.buffer.length);
            this.buffer = buffer;
        }
    }

    protected void writeVarLong(long n) throws IOException {
        n = (n << 1) ^ (n >> 63);//ZigZag
        checkBufferSize(10);
        int index = this.index;
        while (true) {
            if ((n & ~0b1111111) == 0) {
                buffer[index++] = (byte) (n & 0b1111111);
                this.index = index;
                return;
            } else {
                buffer[index++] = (byte) (n & 0b1111111 | 0b10000000);
                n >>>= 7;
            }
        }
    }

    public void writeBool(boolean b) throws IOException {
        writeVarLong(b ? 1 : 0);
    }

    public void writeByte(byte n) throws IOException {
        writeVarLong(n);
    }

    public void writeShort(short n) throws IOException {
        writeVarLong(n);
    }

    public void writeInt(int n) throws IOException {
        writeVarLong(n);
    }

    public void writeLong(long n) throws IOException {
        writeVarLong(n);
    }

    public void writeFloat(float n) throws IOException {
        boolean flag = true;
        float f;
        if (flag && n > -2147483 && n < 2147483) {//Integer.MAX_VALUE / 1000;
            f = (float) ((int)(n * 1000)) / 1000;
        } else {
            BigDecimal bigDecimal = new BigDecimal(n);
            f = bigDecimal.setScale(3, BigDecimal.ROUND_DOWN).floatValue();
        }
        writeVarLong(Float.floatToIntBits(f));
    }

    public void writeDouble(double n) throws IOException {
        double d;
        if (n > -9223372036854775L && n < 9223372036854775L) {//Long.MAX_VALUE / 1000;
            d = (double) ((int)(n * 1000)) / 1000;
        } else {
            BigDecimal bigDecimal = new BigDecimal(n);
            d = bigDecimal.setScale(3, BigDecimal.ROUND_DOWN).doubleValue();
        }
        writeVarLong(Double.doubleToLongBits(d));
    }

    public void writeBytes(byte[] bytes) throws IOException {
        checkBufferSize(10 + bytes.length);
        writeVarLong(bytes.length);
        System.arraycopy(bytes, 0, buffer, index, bytes.length);
        index += bytes.length;
    }

    public void writeString(String s) throws IOException {
        writeBytes(s.getBytes("UTF-8"));
    }

    public void writeString(String s, String charsetName) throws IOException {
        writeBytes(s.getBytes(charsetName));
    }
}
