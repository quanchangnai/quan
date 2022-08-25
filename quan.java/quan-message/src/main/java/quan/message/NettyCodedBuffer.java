package quan.message;

import io.netty.buffer.ByteBuf;

/**
 * 使用Netty的{@link ByteBuf}实现的{@link CodedBuffer}
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
    public void release() {
        buf.release();
        if (temp != null) {
            temp.release();
        }
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
    public void remainingBytes(byte[] bytes, int startPos) {
        buf.readBytes(bytes, startPos, buf.readableBytes());
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
    protected void readBytes(byte[] bytes, int startPos, int length) {
        buf.readBytes(bytes, startPos, length);
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

    @Override
    public void writeBuffer(CodedBuffer buffer) {
        if (!(buffer instanceof NettyCodedBuffer)) {
            super.writeBuffer(buffer);
            return;
        }

        NettyCodedBuffer _buffer = (NettyCodedBuffer) buffer;
        int readableCount = _buffer.readableCount();

        writeInt(readableCount);
        buf.writeBytes(_buffer.buf, readableCount);
    }

    @Override
    public CodedBuffer getTemp() {
        if (temp == null) {
            temp = new NettyCodedBuffer(buf.alloc().buffer());
        }
        return temp;
    }

}
