package quan.message;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Created by quanchangnai on 2020/3/27.
 */
public abstract class Buffer {

    public abstract boolean reading();

    public abstract void reset();

    /**
     * 当前可用的字节数
     */
    public abstract int available();

    /**
     * 当前可用的字节数组
     */
    public abstract byte[] availableBytes();

    /**
     * 当前剩余可用的字节数
     */
    public abstract int remaining();

    /**
     * 当前剩余可用的字节数组
     */
    public abstract byte[] remainingBytes();

    /**
     * 读取VarInt
     *
     * @param bits 读取的最大比特位,只能是[8,16,32,64]中的一种
     */
    protected abstract long readVarInt(int bits) throws IOException;

    public abstract byte[] readBytes() throws IOException;

    public boolean readBool() throws IOException {
        return readInt() != 0;
    }


    public short readShort() throws IOException {
        return (short) readVarInt(16);
    }

    public int readInt() throws IOException {
        return (int) readVarInt(32);
    }

    public long readLong() throws IOException {
        return readVarInt(64);
    }

    public abstract float readFloat() throws IOException;

    public float readFloat(int scale) throws IOException {
        if (scale < 0) {
            return readFloat();
        } else {
            return (float) (readLong() / Math.pow(10, scale));
        }
    }

    public abstract double readDouble() throws IOException;

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


    protected abstract void writeVarInt(long n);


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

    public abstract void writeFloat(float n);

    public void writeFloat(float n, int scale) throws IOException {
        if (scale < 0) {
            writeFloat(n);
        } else {
            writeDouble(n, scale);
        }
    }

    public abstract void writeDouble(double n);

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
