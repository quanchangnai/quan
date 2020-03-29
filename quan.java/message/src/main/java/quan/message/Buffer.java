package quan.message;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Created by quanchangnai on 2020/3/27.
 */
public abstract class Buffer {

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
     * 读取VarInt
     *
     * @param readCount 最大读取字节数,合法值:2,4,8
     */
    protected long readVarInt(int readCount) throws IOException {
        onRead();

        int shift = 0;
        long temp = 0;
        int bits = readCount * 8;

        while (shift < bits) {
            final byte b = readByte();
            temp |= (b & 0b1111111L) << shift;
            shift += 7;

            if ((b & 0b10000000) != 0) {
                continue;
            }

            //ZigZag解码
            return (temp >>> 1) ^ -(temp & 1);
        }

        throw new IOException("读数据出错");
    }

    /**
     * 读数据之前的回调
     */
    protected void onRead() throws IOException {
    }

    /**
     * 实际读一个字节
     */
    protected abstract byte readByte();

    public abstract byte[] readBytes() throws IOException;

    public boolean readBool() throws IOException {
        return readInt() != 0;
    }

    public short readShort() throws IOException {
        return (short) readVarInt(2);
    }

    public int readInt() throws IOException {
        return (int) readVarInt(4);
    }

    public long readLong() throws IOException {
        return readVarInt(8);
    }

    public float readFloat() throws IOException {
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

    public float readFloat(int scale) throws IOException {
        if (scale < 0) {
            return readFloat();
        } else {
            return (float) (readLong() / Math.pow(10, scale));
        }
    }

    public double readDouble() throws IOException {
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

    public double readDouble(int scale) throws IOException {
        if (scale < 0) {
            return readDouble();
        } else {
            return readLong() / Math.pow(10, scale);
        }
    }

    public String readString() throws IOException {
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
    protected abstract void writeByte(byte b);


    public abstract void writeBytes(byte[] bytes);


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

    public void writeFloat(float n, int scale) throws IOException {
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

    public void writeDouble(double n, int scale) throws IOException {
        if (scale < 0) {
            writeDouble(n);
            return;
        }

        long lon;
        try {
            lon = checkScale(n, scale);
        } catch (IllegalArgumentException e) {
            throw new IOException(e.getMessage());
        }

        writeLong(lon);
    }


    public void writeString(String s) {
        writeBytes(s.getBytes(StandardCharsets.UTF_8));
    }
}
