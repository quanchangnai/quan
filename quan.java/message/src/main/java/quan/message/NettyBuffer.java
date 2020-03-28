package quan.message;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * 使用Netty的ByteBuf实现的字节缓冲区，编码采用VarInt和ZigZag算法，字节顺序采用小端模式<br/>
 * Created by quanchangnai on 2020/3/27.
 */
public class NettyBuffer extends Buffer {

    private ByteBuf byteBuf;

    public NettyBuffer(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }

    public ByteBuf getByteBuf() {
        return byteBuf;
    }

    @Override
    public int capacity() {
        return byteBuf.capacity();
    }

    @Override
    public void reset() {
        byteBuf.readerIndex(0);
    }

    @Override
    public void clear() {
        byteBuf.clear();
    }

    @Override
    public int readableCount() {
        return byteBuf.readableBytes();
    }

    @Override
    public byte[] remainingBytes() {
        byte[] remainingBytes = new byte[readableCount()];
        byteBuf.readBytes(remainingBytes);
        return remainingBytes;
    }

    @Override
    public void discardReadBytes() {
        byteBuf.discardReadBytes();
    }

    @Override
    protected byte readByte() {
        return byteBuf.readByte();
    }

    @Override
    public byte[] readBytes() throws IOException {
        int length = readInt();
        if (length > readableCount()) {
            throw new IOException("读数据出错");
        }

        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);
        return bytes;
    }


    @Override
    protected void writeByte(byte b) {
        byteBuf.writeByte(b);
    }


    @Override
    public void writeBytes(byte[] bytes) {
        writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }

}
