package quan.protocol.test;

import quan.protocol.VarintBuffer;
import java.io.IOException;
import quan.protocol.Protocol;

/**
 * 测试协议
 * Created by {@link quan.protocol.generator.JavaGenerator}
 */
public class Test extends Protocol {

    public static final int _ID = 1111;//协议id

    @Override
    public int getId() {
        return _ID;
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
    public void serialize(VarintBuffer buffer) throws IOException {
        buffer.writeInt(_ID);
        buffer.writeLong(testId);
    }

    @Override
    public void parse(VarintBuffer buffer) throws IOException {
        int _id = buffer.readInt();
        if (_id != _ID) {
            throw new IOException("协议ID不匹配,目标值：" + _ID + "，实际值：" + _id);
        }
        testId = buffer.readLong();
    }

    @Override
    public String toString() {
        return "Test{" +
                "testId=" + testId +
                '}';

    }

}
