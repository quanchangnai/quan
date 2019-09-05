package quan.message;

import quan.message.role.RoleInfo;
import quan.message.role.RoleType;
import quan.message.role.SRoleLogin;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by quanchangnai on 2019/6/23.
 */
public class MessageTest {

    public static MessageFactory messageFactory = new MessageFactory();

    static {
        messageFactory.register("quan");
    }

    public static void main(String[] args) throws Exception {

//        test1();
        test2();
//        test3();

    }

    private static void test1() throws Exception {
        System.err.println("test1=============================");
        Buffer buffer = new Buffer();
        buffer.writeBool(true);
        buffer.writeInt(70);
        buffer.writeInt(2423);
        buffer.writeFloat(13.43F);
        buffer.writeDouble(4242.432);
        buffer.writeFloat(132.32434F, 2);
        buffer.writeDouble(342254.653254, 2);
        buffer.writeString("搭顺风车");
        buffer.writeLong(12324);

        System.err.println("buffer.available()=" + buffer.available());

        FileInputStream fileInputStream = new FileInputStream(new File("E:\\buffer"));
        byte[] bytes = new byte[fileInputStream.available()];
        fileInputStream.read(bytes);
        System.err.println("bytes.length=" + bytes.length);
        buffer = new Buffer(bytes);

        System.err.println(buffer.readBool());
        System.err.println(buffer.readInt());
        System.err.println(buffer.readInt());
        System.err.println(buffer.readFloat());
        System.err.println(buffer.readDouble());
        System.err.println(buffer.readFloat(2));
        System.err.println(buffer.readDouble(2));
        System.err.println(buffer.readString());
        System.err.println(buffer.readLong());

//        buffer.reset();
//        buffer.writeInt(45);
//        buffer.writeString("奋斗服务");
//        System.err.println(buffer.readInt());
//        System.err.println(buffer.readString());
    }

    private static void test2() throws Exception {
        System.err.println("test2=============================");

        SRoleLogin sRoleLogin1 = new SRoleLogin();
        sRoleLogin1.setRoleId(1111);
        sRoleLogin1.setRoleName("张三1111");

        RoleInfo roleInfo1 = new RoleInfo();
        roleInfo1.setId(111);
        roleInfo1.setName("aaa");
        roleInfo1.setType(RoleType.type1);

        sRoleLogin1.setRoleInfo(roleInfo1);

        RoleInfo roleInfo2 = new RoleInfo();
        roleInfo2.setId(222);
        roleInfo2.setName("bbb");
        roleInfo2.setType(RoleType.type2);
        roleInfo2.getSet().add(2213);

        sRoleLogin1.getRoleInfoList().add(roleInfo2);
        sRoleLogin1.getRoleInfoSet().add(roleInfo2);
        sRoleLogin1.getRoleInfoMap().put(roleInfo2.getId(), roleInfo2);

        System.err.println("sRoleLogin1:" + sRoleLogin1);

        byte[] encodedBytes = sRoleLogin1.encode();

//        FileInputStream fileInputStream = new FileInputStream(new File("E:\\SRoleLogin"));
//        encodedBytes = new byte[fileInputStream.available()];
//        fileInputStream.read(encodedBytes);

        System.err.println("encodedBytes.length:" + encodedBytes.length);

        SRoleLogin sRoleLogin2 = new SRoleLogin();
        sRoleLogin2.decode(encodedBytes);

        System.err.println("sRoleLogin2:" + sRoleLogin2);
    }

    private static void test3() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(42);
        buffer.flip();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        System.err.println(Arrays.toString(bytes));
    }

}
