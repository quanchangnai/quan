package quan.protocol.test;

import quan.protocol.VarIntBuffer;
import java.io.IOException;
import quan.protocol.Protocol;

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
    public void serialize(VarIntBuffer buffer) throws IOException {
        buffer.writeInt(ID);
        buffer.writeLong(testId);
    }

    @Override
    public void parse(VarIntBuffer buffer) throws IOException {
        if (buffer.readInt() != ID) {
            buffer.reset();
            throw new IOException("协议解析出错，id不匹配,目标值：" + ID + "，实际值：" + buffer.readInt());
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
