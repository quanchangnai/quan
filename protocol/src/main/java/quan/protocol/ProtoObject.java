package quan.protocol;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public abstract class ProtoObject {

    public byte[] serialize() {
        try {
            VarintBuffer buffer = new VarintBuffer();
            serialize(buffer);
            return buffer.remainingBytes();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public abstract void serialize(VarintBuffer buffer) throws IOException;

    public void parse(byte[] bytes) {
        try {
            VarintBuffer buffer = new VarintBuffer(bytes);
            parse(buffer);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public abstract void parse(VarintBuffer buffer) throws IOException;


}
