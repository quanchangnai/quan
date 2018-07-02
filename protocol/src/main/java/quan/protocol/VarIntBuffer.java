package quan.protocol;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * VarInt缓冲区，使用了ZigZag和VarInt编码，字节流采用大端模式
 * Created by quanchangnai on 2018/7/2.
 */
public class VarIntBuffer {
    /**
     * 内部字节缓冲区
     */
    private byte[] buffer;
    /**
     * 初始容量
     */
    private final int initCapacity;
    /**
     * 当前操作位置
     */
    private int position;
    /**
     * 当前操作类型，true：读，false：写
     */
    private boolean reading;
    /**
     * 当前操作的结束位置
     */
    private int end;

    public VarIntBuffer() {
        this(128);
    }

    public VarIntBuffer(int initCapacity) {
        this.initCapacity = initCapacity;
        buffer = new byte[initCapacity];
        this.end = capacity() - 1;
    }

    public VarIntBuffer(byte[] buffer) {
        this.initCapacity = 128;
        this.buffer = buffer;
        this.position = capacity() - 1;
        this.end = capacity() - 1;
    }

    public int capacity() {
        return buffer.length;
    }

    public void reset() {
        position = 0;
    }

    /**
     * 当前可用的字节数组
     *
     * @return
     */
    public byte[] availableBytes() {
        byte[] bytes = new byte[available()];
        if (reading) {
            System.arraycopy(buffer, position, bytes, 0, bytes.length);
        } else {
            System.arraycopy(buffer, 0, bytes, 0, bytes.length);
        }

        return bytes;
    }

    /**
     * 当前可用的字节数。当前正在读数据时，有效数据：[position,end]。当前正在写数据时，可用数据:[0,position]
     *
     * @return
     */
    public int available() {
        if (reading) {
            return end - position + 1;
        } else {
            return position + 1;
        }
    }

    /**
     * 读取VarInt，只能是[8,16,32,64]位整数的一种
     *
     * @param bits
     * @return
     * @throws IOException
     */
    protected long readVarInt(int bits) throws IOException {
        if (bits != 8 && bits != 16 && bits != 32 && bits != 64) {
            throw new IllegalArgumentException("参数bits取值范围错误，限定范围[8,16,32,64]");
        }
        int position = reading ? this.position : 0;
        int shift = 0;
        long temp = 0;
        while (shift < bits) {
            final byte b = buffer[position++];
            temp |= (b & 0b1111111L) << shift;
            if ((b & 0b10000000) == 0) {
                if (!reading) {
                    reading = true;
                    end = this.position;
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
        System.arraycopy(buffer, position, bytes, 0, length);
        position += length;
        return bytes;
    }

    public boolean readBool() throws IOException {
        return readVarInt(8) != 0;
    }

    public byte readByte() throws IOException {
        return (byte) readVarInt(8);
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
        return Float.intBitsToFloat(readInt());
    }

    public double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    public String readString() throws IOException {
        return new String(readBytes(), "UTF-8");
    }

    public String readString(String charsetName) throws IOException {
        return new String(readBytes(), charsetName);
    }


    protected void checkCapacity(int addValue) {
        int realAddValue = addValue > 10 ? addValue : initCapacity;
        int capacity = capacity();
        int position = reading ? 0 : this.position;
        if (position + addValue >= capacity) {
            byte[] buffer = new byte[capacity + realAddValue];
            System.arraycopy(this.buffer, 0, buffer, 0, capacity);
            this.buffer = buffer;
            if (!reading) {
                end = capacity() - 1;
            }
        }
    }

    protected void writeVarInt(long n) throws IOException {
        //ZigZag编码
        n = (n << 1) ^ (n >> 63);
        checkCapacity(10);
        int position = reading ? 0 : this.position;
        while (true) {
            if ((n & ~0b1111111) == 0) {
                buffer[position++] = (byte) (n & 0b1111111);
                reading = false;
                end = capacity() - 1;
                this.position = position;
                return;
            } else {
                buffer[position++] = (byte) (n & 0b1111111 | 0b10000000);
                n >>>= 7;
            }
        }
    }


    public void writeBytes(byte[] bytes) throws IOException {
        checkCapacity(10 + bytes.length);
        writeVarInt(bytes.length);
        System.arraycopy(bytes, 0, buffer, position, bytes.length);
        position += bytes.length;
    }


    public void writeBool(boolean b) throws IOException {
        writeVarInt(b ? 1 : 0);
    }

    public void writeByte(byte n) throws IOException {
        writeVarInt(n);
    }

    public void writeShort(short n) throws IOException {
        writeVarInt(n);
    }

    public void writeInt(int n) throws IOException {
        writeVarInt(n);
    }

    public void writeLong(long n) throws IOException {
        writeVarInt(n);
    }

    public void writeFloat(float n) throws IOException {
        float f = new BigDecimal(n).setScale(3, BigDecimal.ROUND_DOWN).floatValue();
        writeVarInt(Float.floatToIntBits(f));
    }

    public void writeDouble(double n) throws IOException {
        double d = new BigDecimal(n).setScale(3, BigDecimal.ROUND_DOWN).doubleValue();
        writeVarInt(Double.doubleToLongBits(d));
    }

    public void writeString(String s) throws IOException {
        writeBytes(s.getBytes("UTF-8"));
    }

    public void writeString(String s, String charsetName) throws IOException {
        writeBytes(s.getBytes(charsetName));
    }
}
