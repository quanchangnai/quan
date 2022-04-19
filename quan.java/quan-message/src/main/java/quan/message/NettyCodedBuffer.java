package quan.message;

import io.netty.buffer.ByteBuf;

/**
 * 使用Netty的{@link ByteBuf}实现的{@link CodedBuffer}
 * Created by quanchangnai on 2020/3/27.
 */
public class NettyCodedBuffer extends CodedBuffer {

    private ByteBuf buf;

    public NettyCodedBuffer(ByteBuf buf) {
        this.buf = buf;
    }

    public ByteBuf getBuf() {
        return buf;
    }

    @Override
    public int capacity() {
        return buf.capacity();
    }

    @Override
    public void mark() {
        buf.markReaderIndex();
    }

    @Override
    public void reset() {
        buf.resetReaderIndex();
    }

    @Override
    public void clear() {
        buf.clear();
    }

    @Override
    public int readableCount() {
        return buf.readableBytes();
    }

    @Override
    public byte[] remainingBytes() {
        byte[] remainingBytes = new byte[buf.readableBytes()];
        buf.readBytes(remainingBytes);
        return remainingBytes;
    }

    @Override
    public void discardReadBytes() {
        buf.discardReadBytes();
    }

    @Override
    public byte readByte() {
        return buf.readByte();
    }

    @Override
    protected byte[] readBytes(int length) {
        byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        return bytes;
    }

    @Override
    protected void skipBytes(int length) {
        buf.skipBytes(length);
    }

    @Override
    public void writeByte(byte b) {
        buf.writeByte(b);
    }

    @Override
    public void writeBytes(byte[] bytes) {
        writeInt(bytes.length);
        buf.writeBytes(bytes);
    }

}
