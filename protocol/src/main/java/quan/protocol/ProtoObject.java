package quan.protocol;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public abstract class ProtoObject {

    public byte[] encode() {
        try {
            VarintBuffer buffer = new VarintBuffer();
            encode(buffer);
            return buffer.remainingBytes();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public abstract void encode(VarintBuffer buffer) throws IOException;

    public void decode(byte[] bytes) {
        try {
            VarintBuffer buffer = new VarintBuffer(bytes);
            decode(buffer);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public abstract void decode(VarintBuffer buffer) throws IOException;


}
