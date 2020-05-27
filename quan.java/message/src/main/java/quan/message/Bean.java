package quan.message;

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

    public void encode(Buffer buffer) throws IOException {
    }

    public void decode(Buffer buffer) throws IOException {
    }

}
