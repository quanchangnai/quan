package quan.message;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * Created by quanchangnai on 2020/3/27.
 */
public class NettyBuffer extends Buffer {

    private ByteBuf byteBuf;

    public NettyBuffer(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }

    @Override
    public boolean reading() {
        return false;
    }

    @Override
    public void reset() {
        byteBuf.readerIndex(0);
    }

    @Override
    public int available() {
        return 0;
    }

    @Override
    public byte[] availableBytes() {
        return new byte[0];
    }

    @Override
    public int remaining() {
        return byteBuf.readableBytes();
    }

    @Override
    public byte[] remainingBytes() {
        return new byte[0];
    }

    @Override
    protected long readVarInt(int bits) throws IOException {
        return 0;
    }

    @Override
    public byte[] readBytes() throws IOException {
        int length = readInt();
        if (length > remaining()) {
            throw new IOException("读数据出错");
        }
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);
        return bytes;
    }

    @Override
    public float readFloat() throws IOException {
        return 0;
    }

    @Override
    public double readDouble() throws IOException {
        return 0;
    }

    @Override
    protected void writeVarInt(long n) {

    }

    @Override
    public void writeBytes(byte[] bytes) {
        writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }

    @Override
    public void writeFloat(float n) {

    }

    @Override
    public void writeDouble(double n) {

    }
}
