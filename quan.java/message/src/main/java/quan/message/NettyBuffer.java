package quan.message;

import io.netty.buffer.ByteBuf;

/**
 * 使用Netty的ByteBuf实现的字节缓冲区
 * Created by quanchangnai on 2020/3/27.
 */
public class NettyBuffer extends Buffer {

    private ByteBuf buf;

    public NettyBuffer(ByteBuf buf) {
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
    protected byte readByte() {
        return buf.readByte();
    }

    @Override
    protected byte[] readBytes(int length) {
        byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        return bytes;
    }

    @Override
    protected void writeByte(byte b) {
        buf.writeByte(b);
    }


    @Override
    public void writeBytes(byte[] bytes) {
        writeInt(bytes.length);
        buf.writeBytes(bytes);
    }

}
