package quan.message.common;

import java.util.*;
import java.io.IOException;
import quan.message.*;

/**
 * 自动生成
 */
public abstract class HeadedMessage extends Message {

    private long h1;

    private String h2 = "";


    public long getH1() {
        return h1;
    }

    public HeadedMessage setH1(long h1) {
        this.h1 = h1;
        return this;
    }

    public String getH2() {
        return h2;
    }

    public HeadedMessage setH2(String h2) {
        Objects.requireNonNull(h2);
        this.h2 = h2;
        return this;
    }

    @Override
    public void encode(Buffer buffer) throws IOException {
        super.encode(buffer);

        buffer.writeLong(this.h1);
        buffer.writeString(this.h2);
    }

    @Override
    public void decode(Buffer buffer) throws IOException {
        super.decode(buffer);

        this.h1 = buffer.readLong();
        this.h2 = buffer.readString();
    }

    @Override
    public String toString() {
        return "HeadedMessage{" +
                "h1=" + h1 +
                ",h2='" + h2 + '\'' +
                '}';

    }

}
