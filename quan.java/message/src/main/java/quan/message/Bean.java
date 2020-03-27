package quan.message;

import java.io.IOException;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public abstract class Bean {

    public final byte[] encode() throws IOException {
        Buffer buffer = new BytesBuffer();
        encode(buffer);
        return buffer.availableBytes();
    }

    public void encode(Buffer buffer) throws IOException {
    }

    public final void decode(byte[] bytes) throws IOException {
        Buffer buffer = new BytesBuffer(bytes);
        decode(buffer);
    }

    public void decode(Buffer buffer) throws IOException {
    }

}
