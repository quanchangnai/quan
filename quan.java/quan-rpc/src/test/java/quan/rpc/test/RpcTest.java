package quan.rpc.test;

import org.junit.Test;
import quan.message.CodedBuffer;
import quan.message.DefaultCodedBuffer;
import quan.rpc.ObjectReader;
import quan.rpc.ObjectWriter;
import quan.rpc.RpcServer;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author quanchangnai
 */
public class RpcTest {

    public static void main(String[] args) {
        RpcServer rpcServer = new RpcServer(1, 5);
        rpcServer.addService(new TestService(1));
        rpcServer.addService(new TestService(2));
        rpcServer.start();

    }

    @Test
    public void test() {
        CodedBuffer buffer = new DefaultCodedBuffer();

        ObjectWriter objectWriter = new ObjectWriter(buffer);
        objectWriter.write(null);
        objectWriter.write(1);
        objectWriter.write(true);
        objectWriter.write(1.234F);
        objectWriter.write(34534.234D);
        objectWriter.write("你好aaa");
        objectWriter.write(new Object());
        objectWriter.write(Arrays.asList(1, 24, 543));
        Map<Integer, String> map = new TreeMap<>();
        for (int i = 10; i > 0; i--) {
            map.put(i, "aaa-" + i);
        }
        objectWriter.write(map);


        ObjectReader objectReader = new ObjectReader(buffer);
        System.err.println(objectReader.readObject());
        System.err.println(objectReader.readObject());
        System.err.println(objectReader.readObject());
        System.err.println(objectReader.readObject());
        System.err.println(objectReader.readObject());
        System.err.println(objectReader.readObject());
        System.err.println(objectReader.readObject());
        System.err.println(objectReader.readObject());
        System.err.println(objectReader.readObject());

    }

}
