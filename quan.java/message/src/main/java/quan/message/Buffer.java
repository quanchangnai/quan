package quan.message;

import java.nio.charset.StandardCharsets;

/**
 * 采用VarInt和ZigZag编码的字节缓冲区，字节顺序采用小端模式
 * Created by quanchangnai on 2020/3/27.
 */
public abstract class Buffer {

    private Buffer temp;

    /**
     * 容量
     */
    public abstract int capacity();

    /**
     * 标记当前读位置
     */
    public abstract void mark();

    /**
     * 重置读位置到[标记的位置]
     */
    public abstract void reset();

    /**
     * 清除数据，读写位置都置为0
     */
    public abstract void clear();

    /**
     * 当前剩余可读的字节数
     */
    public abstract int readableCount();

    /**
     * 读取当前剩余的字节数组
     */
    public abstract byte[] remainingBytes();

    /**
     * 丢弃已经读过的数据
     */
    public abstract void discardReadBytes();

    /**
     * 读取变长整数
     *
     * @param readBits 最多读几个bit位，合法值:16,32,64
     */
    protected long readVarInt(int readBits) {
        onRead();

        int shift = 0;
        long temp = 0;

        while (shift < readBits) {
            final byte b = readByte();
            temp |= (b & 0b1111111L) << shift;
            shift += 7;

            if ((b & 0b10000000) != 0) {
                continue;
            }

            //ZigZag解码
            return (temp >>> 1) ^ -(temp & 1);
        }

        throw new RuntimeException("读数据出错");
    }

    /**
     * 读数据之前的回调
     */
    protected void onRead() {
    }

    /**
     * 实际读一个字节
     */
    public abstract byte readByte();

    public boolean readBool() {
        return readInt() != 0;
    }

    public short readShort() {
        return (short) readVarInt(16);
    }

    public int readInt() {
        return (int) readVarInt(32);
    }

    public long readLong() {
        return readVarInt(64);
    }

    public float readFloat() {
        onRead();

        int shift = 0;
        int temp = 0;

        while (shift < 32) {
            final byte b = readByte();
            temp |= (b & 0b11111111L) << shift;
            shift += 8;
        }

        return Float.intBitsToFloat(temp);
    }

    public float readFloat(int scale) {
        if (scale < 0) {
            return readFloat();
        } else {
            return (float) (readLong() / Math.pow(10, scale));
        }
    }

    public double readDouble() {
        onRead();

        int shift = 0;
        long temp = 0;

        while (shift < 64) {
            final byte b = readByte();
            temp |= (b & 0b11111111L) << shift;
            shift += 8;
        }

        return Double.longBitsToDouble(temp);
    }

    public double readDouble(int scale) {
        if (scale < 0) {
            return readDouble();
        } else {
            return readLong() / Math.pow(10, scale);
        }
    }

    public byte[] readBytes() {
        int length = readInt();
        int readableCount = readableCount();
        if (length > readableCount) {
            throw new RuntimeException(String.format("读数据出错，希望读取%d字节,实际剩余%d字节", length, readableCount));
        }

        return readBytes(length);
    }

    protected abstract byte[] readBytes(int length);

    public void skipBytes() {
        int length = readInt();
        int readableCount = readableCount();
        if (length > readableCount) {
            throw new RuntimeException(String.format("读数据出错，希望跳过%d字节,实际剩余%d字节", length, readableCount));
        }

        skipBytes(length);
    }

    protected abstract void skipBytes(int length);

    public String readString() {
        return new String(readBytes(), StandardCharsets.UTF_8);
    }

    protected void writeVarInt(long n) {
        onWrite(10);

        //ZigZag编码
        n = (n << 1) ^ (n >> 63);

        while (true) {
            if ((n & ~0b1111111) == 0) {
                writeByte((byte) (n & 0b1111111));
                return;
            } else {
                writeByte((byte) (n & 0b1111111 | 0b10000000));
                n >>>= 7;
            }
        }
    }

    /**
     * 写数据之前的回调
     *
     * @param writeCount 未压缩之前要写入的字节数量
     */
    protected void onWrite(int writeCount) {
    }

    /**
     * 实际写一个字节
     */
    public abstract void writeByte(byte b);

    public void writeBool(boolean b) {
        writeInt(b ? 1 : 0);
    }

    public void writeShort(short n) {
        writeVarInt(n);
    }

    public void writeInt(int n) {
        writeVarInt(n);
    }

    public void writeLong(long n) {
        writeVarInt(n);
    }

    public void writeFloat(float n) {
        onWrite(4);

        int temp = Float.floatToIntBits(n);
        int shift = 0;

        while (shift < 32) {
            writeByte((byte) (temp >> shift & 0b11111111));
            shift += 8;
        }
    }

    public void writeFloat(float n, int scale) {
        if (scale < 0) {
            writeFloat(n);
        } else {
            writeDouble(n, scale);
        }
    }

    public void writeDouble(double n) {
        onWrite(8);

        long temp = Double.doubleToLongBits(n);
        int shift = 0;

        while (shift < 64) {
            writeByte((byte) (temp >>> shift & 0b11111111));
            shift += 8;
        }
    }

    public static long checkScale(double n, int scale) {
        int times = (int) Math.pow(10, scale);
        long threshold = Long.MAX_VALUE / times;
        if (n < -threshold || n > threshold) {
            throw new IllegalArgumentException(String.format("参数[%s]超出了限定范围[%s,%s],无法转换为指定精度[%s]的定点型数据", n, -threshold, threshold, scale));
        }
        return (long) Math.floor(n * times);
    }

    public void writeDouble(double n, int scale) {
        if (scale < 0) {
            writeDouble(n);
            return;
        }

        writeLong(checkScale(n, scale));
    }

    public abstract void writeBytes(byte[] bytes);

    public void writeBuffer(Buffer buffer) {
        writeBytes(buffer.remainingBytes());
    }

    public void writeString(String s) {
        writeBytes(s.getBytes(StandardCharsets.UTF_8));
    }

    public void writeTag(int tag) {
        writeByte((byte) tag);
    }

    public int readTag() {
        return readByte() & 0b11111111;
    }

    public Buffer getTemp() {
        if (temp == null) {
            temp = new SimpleBuffer();
        }
        return temp;
    }

    public void writeTemp() {
        writeBuffer(temp);
        temp.clear();
    }

}
