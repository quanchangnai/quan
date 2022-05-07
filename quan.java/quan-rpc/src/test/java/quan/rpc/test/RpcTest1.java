package quan.rpc.test;

import org.junit.Test;
import quan.message.CodedBuffer;
import quan.message.DefaultCodedBuffer;
import quan.rpc.LocalServer;
import quan.rpc.NettyLocalServer;
import quan.rpc.serialize.ObjectReader;
import quan.rpc.serialize.ObjectWriter;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author quanchangnai
 */
public class RpcTest1 {

    public static void main(String[] args) {
        LocalServer localServer = new NettyLocalServer(1, "127.0.0.1", 8888, 5);
        localServer.addService(new TestService1(1));
        localServer.addService(new RoleService1<>(2));
        localServer.start();
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
        System.err.println(objectReader.readAny());
        System.err.println(objectReader.readAny());
        System.err.println(objectReader.readAny());
        System.err.println(objectReader.readAny());
        System.err.println(objectReader.readAny());
        System.err.println(objectReader.readAny());
        System.err.println(objectReader.readAny());
        System.err.println(objectReader.readAny());
        System.err.println(objectReader.readAny());

    }

}
