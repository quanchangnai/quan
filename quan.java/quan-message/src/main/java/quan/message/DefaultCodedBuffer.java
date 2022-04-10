package quan.message;

import java.nio.ByteBuffer;

/**
 * 直接使用字节数组实现的{@link CodedBuffer}
 * Created by quanchangnai on 2018/7/2.
 */
public class DefaultCodedBuffer extends CodedBuffer {

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

    public DefaultCodedBuffer() {
        this(128);
    }

    public DefaultCodedBuffer(int capacity) {
        this.bytes = new byte[capacity];
    }

    public DefaultCodedBuffer(byte[] bytes) {
        this.bytes = bytes;
        this.writeIndex = bytes.length;
    }

    public DefaultCodedBuffer(ByteBuffer buffer) {
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
    protected void onRead() {
        if (readableCount() < 1) {
            throw new RuntimeException("读数据出错，剩余字节数不够");
        }
    }

    @Override
    public byte readByte() {
        return bytes[readIndex++];
    }

    @Override
    protected byte[] readBytes(int length) {
        byte[] bytes = new byte[length];
        System.arraycopy(this.bytes, readIndex, bytes, 0, length);
        readIndex += length;
        return bytes;
    }

    @Override
    protected void skipBytes(int length) {
        readIndex += length;
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
    public void writeByte(byte b) {
        bytes[writeIndex++] = b;
    }

    @Override
    public void writeBuffer(CodedBuffer buffer) {
        if (!(buffer instanceof DefaultCodedBuffer)) {
            super.writeBuffer(buffer);
            return;
        }

        DefaultCodedBuffer _buffer = (DefaultCodedBuffer) buffer;
        int readableCount = _buffer.readableCount();

        onWrite(10 + readableCount);

        writeInt(readableCount);
        System.arraycopy(_buffer.bytes, _buffer.readIndex, this.bytes, this.writeIndex, readableCount);

        _buffer.readIndex += readableCount;
        this.writeIndex += readableCount;
    }

    @Override
    public void writeBytes(byte[] bytes) {
        onWrite(10 + bytes.length);
        writeInt(bytes.length);
        System.arraycopy(bytes, 0, this.bytes, writeIndex, bytes.length);
        writeIndex += bytes.length;
    }

}
