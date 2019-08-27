package quan.message;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * 基于VarInt和ZigZag编码的缓冲区，字节顺序采用小端模式<br/>
 * Created by quanchangnai on 2018/7/2.
 */
public class Buffer {
    /**
     * 字节缓冲区
     */
    private byte[] bytes;

    /**
     * 当前位置
     */
    private int position;

    /**
     * 当前是在读数据还是在写数据，true：读，false：写
     */
    private boolean reading;

    /**
     * 结束位置，后面的是无效数据
     */
    private int end;

    public Buffer() {
        this(64);
    }

    public Buffer(int capacity) {
        this.bytes = new byte[capacity];
        this.end = capacity() - 1;
    }

    public Buffer(byte[] bytes) {
        this.bytes = bytes;
        this.position = bytes.length - 1;
        this.end = capacity() - 1;
    }

    public Buffer(ByteBuffer buffer) {
        this(buffer.array());
    }

    public int capacity() {
        return bytes.length;
    }

    public void reset() {
        position = 0;
    }

    /**
     * 当前可用的字节数组<br/>
     * 当前正在读数据时：[0,end]<br/>
     * 当前正在写数据时：[0,position)<br/>
     */
    public byte[] availableBytes() {
        byte[] availableBytes = new byte[available()];
        if (reading) {
            System.arraycopy(this.bytes, position, availableBytes, 0, availableBytes.length);
        } else {
            System.arraycopy(this.bytes, 0, availableBytes, 0, availableBytes.length);
        }
        return availableBytes;
    }

    /**
     * 当前可用的字节数
     */
    public int available() {
        if (reading) {
            return end + 1;
        } else {
            return position;
        }
    }


    /**
     * 当前剩余可用的字节数组<br/>
     * 当前正在读数据时：[position,end]<br/>
     * 当前正在写数据时：[0,position)<br/>
     */
    public byte[] remainingBytes() {
        byte[] remainingBytes = new byte[remaining()];
        if (reading) {
            System.arraycopy(this.bytes, position, remainingBytes, 0, remainingBytes.length);
        } else {
            System.arraycopy(this.bytes, 0, remainingBytes, 0, remainingBytes.length);
        }
        return remainingBytes;
    }

    /**
     * 当前剩余可用的字节数
     */
    public int remaining() {
        if (reading) {
            return end - position + 1;
        } else {
            return position;
        }
    }


    /**
     * 读取VarInt，bits只能是[8,16,32,64]中的一种
     */
    protected long readVarInt(int bits) throws IOException {
        if (bits != 16 && bits != 32 && bits != 64) {
            throw new IllegalArgumentException("参数bits限定取值范围[16,32,64],实际值：" + bits);
        }
        int position = reading ? this.position : 0;
        int shift = 0;
        long temp = 0;
        while (shift < bits) {
            final byte b = bytes[position++];
            temp |= (b & 0b1111111L) << shift;
            if ((b & 0b10000000) == 0) {
                if (!reading) {
                    reading = true;
                    end = this.position - 1;
                }
                this.position = position;
                //ZigZag解码
                return (temp >>> 1) ^ -(temp & 1);
            }
            shift += 7;
        }
        throw new IOException("读取数据异常，编码错误");
    }

    public byte[] readBytes() throws IOException {
        int length = readInt();
        byte[] bytes = new byte[length];
        System.arraycopy(this.bytes, position, bytes, 0, length);
        position += length;
        return bytes;
    }

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

    public float readFloat() {
        int position = reading ? this.position : 0;
        int shift = 0;
        int temp = 0;
        while (shift < 32) {
            final byte b = bytes[position++];
            temp |= (b & 0b11111111L) << shift;
            shift += 8;
        }

        if (!reading) {
            reading = true;
            end = this.position - 1;
        }
        this.position = position;

        return Float.intBitsToFloat(temp);
    }

    public float readFloat(int scale) throws IOException {
        if (scale < 0) {
            return readFloat();
        } else {
            return (float) readDouble(scale);
        }
    }

    public double readDouble() {
        int position = reading ? this.position : 0;
        int shift = 0;
        long temp = 0;

        while (shift < 64) {
            final byte b = bytes[position++];
            temp |= (b & 0b11111111L) << shift;
            shift += 8;
        }

        if (!reading) {
            reading = true;
            end = this.position - 1;
        }
        this.position = position;

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

    /**
     * 检测并增加缓冲区容量
     *
     * @param minAddValue 最少增加的容量
     */
    protected void checkCapacity(int minAddValue) {
        int capacity = capacity();
        int position = reading ? 0 : this.position;
        if (position + minAddValue >= capacity) {
            int newCapacity = capacity;
            while (minAddValue > 0) {
                newCapacity += capacity;
                minAddValue -= capacity;
            }
            byte[] newBytes = new byte[newCapacity];
            System.arraycopy(this.bytes, 0, newBytes, 0, capacity);
            this.bytes = newBytes;
            if (!reading) {
                end = capacity - 1;
            }
        }
    }

    protected void writeVarInt(long n) {
        checkCapacity(10);
        //ZigZag编码
        n = (n << 1) ^ (n >> 63);
        int position = reading ? 0 : this.position;
        while (true) {
            if ((n & ~0b1111111) == 0) {
                bytes[position++] = (byte) (n & 0b1111111);
                if (reading) {
                    reading = false;
                    end = capacity() - 1;
                }
                this.position = position;
                return;
            } else {
                bytes[position++] = (byte) (n & 0b1111111 | 0b10000000);
                n >>>= 7;
            }
        }
    }


    public void writeBytes(byte[] bytes) {
        checkCapacity(10 + bytes.length);
        writeInt(bytes.length);
        System.arraycopy(bytes, 0, this.bytes, position, bytes.length);
        position += bytes.length;
    }


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
        checkCapacity(4);
        int position = reading ? 0 : this.position;
        int temp = Float.floatToIntBits(n);
        int shift = 0;
        while (shift < 32) {
            bytes[position++] = (byte) (temp >> shift & 0b11111111);
            shift += 8;
        }
        if (reading) {
            reading = false;
            end = capacity() - 1;
        }
        this.position = position;
    }

    public void writeFloat(float n, int scale) throws IOException {
        if (scale < 0) {
            writeFloat(n);
        } else {
            writeDouble(n, scale);
        }
    }

    public void writeDouble(double n) {
        checkCapacity(8);
        int position = reading ? 0 : this.position;
        long temp = Double.doubleToLongBits(n);
        int shift = 0;
        while (shift < 64) {
            bytes[position++] = (byte) (temp >>> shift & 0b11111111);
            shift += 8;
        }
        if (reading) {
            reading = false;
            end = capacity() - 1;
        }
        this.position = position;
    }

    public void writeDouble(double n, int scale) throws IOException {
        if (scale < 0) {
            writeDouble(n);
            return;
        }
        int times = (int) Math.pow(10, scale);
        long threshold = Long.MAX_VALUE / times;
        if (n < -threshold || n > threshold) {
            throw new IOException(String.format("参数[%s]超出了限定范围[%s,%s],无法转换为指定精度[%s]的定点型数据", n, -threshold, threshold, scale));
        }

        writeLong(Math.round(n * times));
    }


    public void writeString(String s) {
        writeBytes(s.getBytes(StandardCharsets.UTF_8));
    }

}
