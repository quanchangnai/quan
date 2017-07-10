package quan.protocol.test;

import java.io.IOException;
import quan.protocol.stream.WritableStream;
import quan.protocol.Protocol;
import quan.protocol.stream.ReadableStream;

/**
 * 测试协议
 * Created by {@link quan.protocol.generator.JavaGenerator}
 */
public class Test extends Protocol {

    public static final int ID = 1111;//协议id

    @Override
    public int getId() {
        return ID;
    }

    private long testId;

    public Test() {
        testId = 111L;
    }

    public long getTestId() {
        return testId;
    }

    public void setTestId(long testId) {
        this.testId = testId;
    }


    @Override
    public void serialize(WritableStream writable) throws IOException {
        writable.writeInt(ID);
        writable.writeLong(testId);
    }

    @Override
    public void parse(ReadableStream readable) throws IOException {
        if (readable.readInt() != ID) {
            readable.reset();
            throw new IOException("协议解析出错，id不匹配,目标值：" + ID + "，实际值：" + readable.readInt());
        }
        testId = readable.readLong();
    }

    @Override
    public String toString() {
        return "Test{" +
                "testId=" + testId +
                '}';

    }

}
