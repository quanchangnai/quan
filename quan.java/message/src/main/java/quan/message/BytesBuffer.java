package quan.message;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 直接使用字节数组实现的字节缓冲区，编码采用VarInt和ZigZag算法，字节顺序采用小端模式<br/>
 * Created by quanchangnai on 2018/7/2.
 */
public class BytesBuffer extends Buffer {

    /**
     * 字节缓冲区
     */
    private byte[] bytes;

    /**
     * 下一个读的位置,该位置的数据还未读
     */
    private int readIndex;

    /**
     * 下一个写的位置,该位置还未写数据
     */
    private int writeIndex;

    /**
     * 标记的读位置
     */
    private int markedIndex;

    public BytesBuffer() {
        this(128);
    }

    public BytesBuffer(int capacity) {
        this.bytes = new byte[capacity];
    }

    public BytesBuffer(byte[] bytes) {
        this.bytes = bytes;
        this.writeIndex = bytes.length;
    }

    public BytesBuffer(ByteBuffer buffer) {
        this(buffer.array());
    }

    @Override
    public int capacity() {
        return bytes.length;
    }

    @Override
    public void reset() {
        readIndex = markedIndex;
    }

    @Override
    public void clear() {
        readIndex = 0;
        writeIndex = 0;
    }

    @Override
    public void mark() {
        markedIndex = readIndex;
    }

    /**
     * 当前剩余可读的字节数
     */
    @Override
    public int readableCount() {
        return writeIndex - readIndex;
    }

    @Override
    public byte[] remainingBytes() {
        byte[] remainingBytes = new byte[readableCount()];
        System.arraycopy(this.bytes, readIndex, remainingBytes, 0, remainingBytes.length);
        readIndex += remainingBytes.length;
        return remainingBytes;
    }

    @Override
    public void discardReadBytes() {
        byte[] newBytes = new byte[capacity() - readIndex];
        System.arraycopy(bytes, readIndex, newBytes, 0, newBytes.length);
        bytes = newBytes;
        writeIndex -= readIndex;
        readIndex = 0;
    }

    @Override
    protected void onRead() throws IOException {
        if (readableCount() < 1) {
            throw new IOException("读数据出错，剩余字节数不够");
        }
    }

    @Override
    protected byte readByte() {
        return bytes[readIndex++];
    }

    @Override
    public byte[] readBytes() throws IOException {
        int length = readInt();
        int readable = readableCount();
        if (length > readableCount()) {
            throw new IOException(String.format("读数据出错，希望读%d字节,实际剩余%d字节", length, readable));
        }

        byte[] bytes = new byte[length];

        System.arraycopy(this.bytes, readIndex, bytes, 0, length);
        readIndex += length;

        return bytes;
    }

    @Override
    protected void onWrite(int writeCount) {
        if (writeIndex + writeCount < capacity()) {
            return;
        }

        //当缓冲区容量不够时进行扩容
        int capacity = capacity();
        int newCapacity = capacity;

        while (writeCount > 0) {
            newCapacity += capacity;
            writeCount -= capacity;
        }

        byte[] newBytes = new byte[newCapacity];
        System.arraycopy(this.bytes, 0, newBytes, 0, capacity);
        this.bytes = newBytes;
    }

    @Override
    protected void writeByte(byte b) {
        bytes[writeIndex++] = b;
    }

    @Override
    public void writeBytes(byte[] bytes) {
        onWrite(10 + bytes.length);

        writeInt(bytes.length);
        System.arraycopy(bytes, 0, this.bytes, writeIndex, bytes.length);
        writeIndex += bytes.length;
    }
}
