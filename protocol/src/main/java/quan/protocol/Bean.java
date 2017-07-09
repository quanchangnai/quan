package quan.protocol;

import quan.protocol.stream.ReadableStream;
import quan.protocol.stream.WritableStream;

import java.io.IOException;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public abstract class Bean {

    public byte[] serialize() {
        try {
            WritableStream writable = new WritableStream();
            serialize(writable);
            return writable.toBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract void serialize(WritableStream writable) throws IOException;

    public void parse(byte[] bytes) {
        try {
            ReadableStream readable = new ReadableStream(bytes);
            parse(readable);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract void parse(ReadableStream readable) throws IOException;


}
