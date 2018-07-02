package quan.protocol;

import java.io.IOException;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public abstract class Bean {

    public byte[] serialize() {
        try {
            VarIntBuffer buffer = new VarIntBuffer();
            serialize(buffer);
            return buffer.availableBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract void serialize(VarIntBuffer buffer) throws IOException;

    public void parse(byte[] bytes) {
        try {
            VarIntBuffer buffer = new VarIntBuffer(bytes);
            parse(buffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract void parse(VarIntBuffer buffer) throws IOException;


}
