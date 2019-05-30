package quan.protocol;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;

/**
 * Varint缓冲区，使用了Varint和ZigZag编码
 * Created by quanchangnai on 2018/7/2.
 */
public class VarintBuffer {
    /**
     * 字节缓冲区
     */
    private byte[] buffer;
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

    public VarintBuffer() {
        this(64);
    }

    public VarintBuffer(int capacity) {
        this.buffer = new byte[capacity];
        this.end = capacity() - 1;
    }

    public VarintBuffer(byte[] buffer) {
        this.buffer = buffer;
        this.position = buffer.length - 1;
        this.end = capacity() - 1;
    }

    public VarintBuffer(ByteBuffer buffer) {
        this(buffer.array());
    }

    public int capacity() {
        return buffer.length;
    }

    public void reset() {
        position = 0;
    }

    /**
     * 当前可用的字节数组<br/>
     * 当前正在读数据时：[0,end]<br/>
     * 当前正在写数据时：[0,position)<br/>
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
     * 当前可用的字节数
     *
     * @return
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
     *
     * @return
     */
    public byte[] remainingBytes() {
        byte[] bytes = new byte[remaining()];
        if (reading) {
            System.arraycopy(buffer, position, bytes, 0, bytes.length);
        } else {
            System.arraycopy(buffer, 0, bytes, 0, bytes.length);
        }
        return bytes;
    }

    /**
     * 当前剩余可用的字节数
     *
     * @return
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
     *
     * @param bits
     * @return
     * @throws IOException
     */
    protected long readVarInt(int bits) throws IOException {
        if (bits != 8 && bits != 16 && bits != 32 && bits != 64) {
            throw new IllegalArgumentException("参数bits限定取值范围[8,16,32,64],实际值：" + bits);
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
        int position = reading ? this.position : 0;
        int shift = 0;
        int temp = 0;
        while (shift < 32) {
            final byte b = buffer[position++];
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

    public double readDouble() throws IOException {
        int position = reading ? this.position : 0;
        int shift = 0;
        long temp = 0;

        while (shift < 64) {
            final byte b = buffer[position++];
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
        return new String(readBytes(), "UTF-8");
    }

    public String readString(String charsetName) throws IOException {
        return new String(readBytes(), charsetName);
    }


    /**
     * 检测并增加缓冲区容量
     *
     * @param needValue 需要增加的容量
     */
    protected void checkCapacity(int needValue) {
        int capacity = capacity();
        int position = reading ? 0 : this.position;
        if (position + needValue >= capacity) {
            int newCapacity = capacity;
            while (needValue > 0) {
                newCapacity += capacity;
                needValue -= capacity;
            }
            byte[] buffer = new byte[newCapacity];
            System.arraycopy(this.buffer, 0, buffer, 0, capacity);
            this.buffer = buffer;
            if (!reading) {
                end = capacity - 1;
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
                if (reading) {
                    reading = false;
                    end = capacity() - 1;
                }
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
        checkCapacity(4);
        int position = reading ? 0 : this.position;
        int temp = Float.floatToIntBits(n);
        int shift = 0;
        while (shift < 32) {
            buffer[position++] = (byte) (temp >> shift & 0b11111111);
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

    public void writeDouble(double n) throws IOException {
        checkCapacity(8);
        int position = reading ? 0 : this.position;
        long temp = Double.doubleToLongBits(n);
        int shift = 0;
        while (shift < 64) {
            buffer[position++] = (byte) (temp >>> shift & 0b11111111);
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
        } else {
            n = new BigDecimal(n).setScale(scale, BigDecimal.ROUND_FLOOR).doubleValue();
            int times = (int) Math.pow(10, scale);
            long threshold = Long.MAX_VALUE / times;
            if (n >= -threshold && n <= threshold) {
                writeLong((long) Math.floor(n * times));
            } else {
                throw new IllegalArgumentException("参数n超出了限定范围[" + -threshold + "," + threshold + "]中，无法转换为指定精度的定点型数据");
            }
        }
    }


    public void writeString(String s) throws IOException {
        writeBytes(s.getBytes("UTF-8"));
    }

    public void writeString(String s, String charsetName) throws IOException {
        writeBytes(s.getBytes(charsetName));
    }
}
