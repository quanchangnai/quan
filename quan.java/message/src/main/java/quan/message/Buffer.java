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
     * 下一个读或写的位置
     */
    private int position;

    /**
     * 当前是在读数据还是在写数据
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
        this.end = -1;
    }

    public Buffer(byte[] bytes) {
        this.bytes = bytes;
        this.position = bytes.length - 1;
        this.end = position;
    }

    public Buffer(ByteBuffer buffer) {
        this(buffer.array());
    }

    public int capacity() {
        return bytes.length;
    }

    public void reset() {
        position = 0;
        if (!reading) {
            end = position - 1;
        }
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
     * 读取VarInt
     *
     * @param bits 读取的最大比特位,只能是[8,16,32,64]中的一种
     */
    protected long readVarInt(int bits) throws IOException {
        if (bits != 16 && bits != 32 && bits != 64) {
            throw new IllegalArgumentException("参数[bits]限定取值范围[16,32,64],实际值[" + bits + "]");
        }

        int position = reading ? this.position : 0;
        reading = true;
        int shift = 0;
        long temp = 0;

        while (shift < bits) {
            if (position >= bytes.length) {
                break;
            }

            final byte b = bytes[position++];
            temp |= (b & 0b1111111L) << shift;
            shift += 7;

            if ((b & 0b10000000) == 0) {
                this.position = position;
                //ZigZag解码
                return (temp >>> 1) ^ -(temp & 1);
            }
        }

        throw new IOException("读数据出错");
    }

    public byte[] readBytes() throws IOException {
        int length = readInt();
        if (length > remaining()) {
            throw new IOException("读数据出错");
        }
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

    public float readFloat() throws IOException {
        int position = reading ? this.position : 0;
        reading = true;
        int shift = 0;
        int temp = 0;

        while (shift < 32) {
            if (position >= bytes.length) {
                throw new IOException("读数据出错");
            }
            final byte b = bytes[position++];
            temp |= (b & 0b11111111L) << shift;
            shift += 8;
        }

        this.position = position;
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
        int position = reading ? this.position : 0;
        reading = true;
        int shift = 0;
        long temp = 0;

        while (shift < 64) {
            if (position >= bytes.length) {
                throw new IOException("读数据出错");
            }
            final byte b = bytes[position++];
            temp |= (b & 0b11111111L) << shift;
            shift += 8;
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
        if (position + minAddValue < capacity) {
            return;
        }

        int newCapacity = capacity;
        while (minAddValue > 0) {
            newCapacity += capacity;
            minAddValue -= capacity;
        }
        byte[] newBytes = new byte[newCapacity];
        System.arraycopy(this.bytes, 0, newBytes, 0, capacity);
        this.bytes = newBytes;
    }

    protected void writeVarInt(long n) {
        checkCapacity(10);

        int position = reading ? 0 : this.position;
        int end = this.end;
        reading = false;
        //ZigZag编码
        n = (n << 1) ^ (n >> 63);

        while (true) {
            if ((n & ~0b1111111) == 0) {
                bytes[position++] = (byte) (n & 0b1111111);
                this.position = position;
                this.end = ++end;
                return;
            } else {
                bytes[position++] = (byte) (n & 0b1111111 | 0b10000000);
                n >>>= 7;
                end++;
            }
        }
    }


    public void writeBytes(byte[] bytes) {
        checkCapacity(10 + bytes.length);
        writeInt(bytes.length);
        System.arraycopy(bytes, 0, this.bytes, position, bytes.length);
        position += bytes.length;
        end += bytes.length;
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
        int end = this.end;
        reading = false;

        int temp = Float.floatToIntBits(n);
        int shift = 0;

        while (shift < 32) {
            bytes[position++] = (byte) (temp >> shift & 0b11111111);
            shift += 8;
            end++;
        }

        this.position = position;
        this.end = end;
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
        int end = this.end;
        reading = false;

        long temp = Double.doubleToLongBits(n);
        int shift = 0;

        while (shift < 64) {
            bytes[position++] = (byte) (temp >>> shift & 0b11111111);
            shift += 8;
            end++;
        }

        this.position = position;
        this.end = end;
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

        writeLong((long) Math.floor(n * times));
    }


    public void writeString(String s) {
        writeBytes(s.getBytes(StandardCharsets.UTF_8));
    }

}
