package quan.message;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public abstract class Bean {

    public final byte[] encode() throws IOException {
        Buffer buffer = new SimpleBuffer();
        encode(buffer);
        return buffer.remainingBytes();
    }

    public final void decode(byte[] bytes) throws IOException {
        decode(new SimpleBuffer(bytes));
    }

    public void encode(ByteBuf byteBuf) throws IOException {
        encode(new NettyBuffer(byteBuf));
    }

    public void decode(ByteBuf byteBuf) throws IOException {
        decode(new NettyBuffer(byteBuf));
    }

    public void encode(Buffer buffer) throws IOException {
    }

    public void decode(Buffer buffer) throws IOException {
    }

}
