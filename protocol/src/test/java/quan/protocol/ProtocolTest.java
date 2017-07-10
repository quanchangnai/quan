package quan.protocol;

import quan.protocol.role.RoleInfo;
import quan.protocol.role.SRoleLogin;
import quan.protocol.user.UserInfo;
import quan.protocol.stream.ReadableStream;
import quan.protocol.stream.WritableStream;

/**
 * Created by quanchangnai on 2017/7/5.
 */
public class ProtocolTest {

    public static void main(String[] args) throws Exception {
//        test1();
//        test2();
        test3();
    }

    private static void test1() throws Exception {
        System.err.println("====================test1================================");
        WritableStream writableStream = new WritableStream();

//        writableStream.writeBool(false);
//        writableStream.writeByte((byte) -100);
//        writableStream.writeShort((short) -1);
//        writableStream.writeInt(-1);
//        writableStream.writeInt(23);
        writableStream.writeFloat(42.1425F);
//        writableStream.writeDouble(Float.MAX_VALUE);
//        writableStream.writeLong(System.currentTimeMillis());
//        writableStream.writeString("hello，你好");

        byte[] bytes = writableStream.toBytes();
        System.err.println("字节数:" + bytes.length);

        ReadableStream readableStream = new ReadableStream(bytes);

//        System.err.println(readableStream.readBool());
//        System.err.println(readableStream.readByte());
//        System.err.println(readableStream.readShort());
//        System.err.println(readableStream.readInt());
//        System.err.println(readableStream.readInt());
        System.err.println(readableStream.readFloat());
//        System.err.println(readableStream.readDouble());
//        System.err.println(readableStream.readLong());
//        System.err.println(readableStream.readString());
    }

    private static void test2() throws Exception {
        System.err.println("====================test2================================");

        WritableStream writableStream = new WritableStream();
        RoleInfo roleInfo1 = new RoleInfo();
        roleInfo1.setRoleId(1);
        roleInfo1.setRoleName("role");
        roleInfo1.setBy((byte) 33);
        roleInfo1.getList().add(1);
        roleInfo1.getList().add(2);
        roleInfo1.getSet().add(22);
        roleInfo1.getMap().put(11, 11);
        roleInfo1.getMap().put(22, 22);
        roleInfo1.setData("你好".getBytes());

        SRoleLogin sRoleLogin1 = new SRoleLogin();
        sRoleLogin1.setRoleId(111);
        sRoleLogin1.setRoleInfo(roleInfo1);
        sRoleLogin1.getRoleInfoList().add(roleInfo1);
        sRoleLogin1.getRoleInfoSet().add(roleInfo1);
        sRoleLogin1.getRoleInfoMap().put(roleInfo1.getRoleId(), roleInfo1);

        sRoleLogin1.serialize(writableStream);

        byte[] bytes = writableStream.toBytes();
        System.err.println("字节数:" + bytes.length);

        ReadableStream readableStream = new ReadableStream(bytes);

        SRoleLogin sRoleLogin2 = new SRoleLogin();
        sRoleLogin2.parse(readableStream);
        System.err.println("sRoleLogin2=" + sRoleLogin2);

    }

    private static void test3() throws Exception {
        System.err.println("====================test3================================");

        UserInfo userInfo1 = new UserInfo();
        userInfo1.setName("sasdasds");
        userInfo1.setLevel(-1232);
        userInfo1.setExperience(42343);
        userInfo1.setIcon(432432);
        userInfo1.setPower(424);
        userInfo1.setModifyNameCount(435);
        userInfo1.setEventState("daasd");
        userInfo1.setFunctionState("dasdas");
        userInfo1.setLucky(424);
        userInfo1.setCurrentState(42234);
        userInfo1.setBuyPowerCount(5345);

        WritableStream writableStream = new WritableStream();
        userInfo1.serialize(writableStream);
        System.err.println("字节数:" + writableStream.toBytes().length);

        long start = System.nanoTime();
        long end = 0;
        for (int i = 1; i <= 100000; i++) {
            byte[] bytes = userInfo1.serialize();
            UserInfo userInfo2 = new UserInfo();
            userInfo2.parse(bytes);
            if (i == 100000) {
                end = System.nanoTime();
                System.err.println("userInfo2:" + userInfo2);
            }
        }


        System.err.println("test 耗时(ns)：" + (end - start));
        System.err.println("test 耗时(ms)：" + (end - start) / 1000000);
    }

}
