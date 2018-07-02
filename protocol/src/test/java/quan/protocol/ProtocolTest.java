package quan.protocol;

import quan.protocol.role.RoleInfo;
import quan.protocol.role.SRoleLogin;
import quan.protocol.user.UserInfo;

/**
 * Created by quanchangnai on 2017/7/5.
 */
public class ProtocolTest {

    public static void main(String[] args) throws Exception {
        test1();
        test2();
        test3();
        test4();
    }

    private static void test1() throws Exception {
        System.err.println("====================test1================================");
        VarIntBuffer buffer = new VarIntBuffer();
        buffer.writeLong(System.currentTimeMillis());
        System.err.println(buffer.availableBytes().length);
        System.err.println(Long.toBinaryString(buffer.readLong()).length());
    }

    private static void test2() throws Exception {
        System.err.println("====================test2================================");

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

        VarIntBuffer buffer = new VarIntBuffer();

        sRoleLogin1.serialize(buffer);

        byte[] bytes = buffer.availableBytes();
        System.err.println("字节数:" + bytes.length);


        SRoleLogin sRoleLogin2 = new SRoleLogin();
        sRoleLogin2.parse(buffer);
        System.err.println("sRoleLogin2=" + sRoleLogin2);
        System.err.println("sRoleLogin2.getRoleInfo().getData()=" + new String(sRoleLogin2.getRoleInfo().getData()));

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

        VarIntBuffer buffer = new VarIntBuffer();
        userInfo1.serialize(buffer);
        System.err.println("字节数:" + buffer.availableBytes().length);

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

    private static void test4() throws Exception {
        System.err.println("====================test4================================");
        VarIntBuffer buffer = new VarIntBuffer();
        buffer.writeBool(true);
//        buffer.reset();
        buffer.writeLong(System.currentTimeMillis());
        buffer.writeDouble(321.435454345);
        for (int i = 0; i < 1000; i++) {
            buffer.writeString("你好" + i);
        }

        System.err.println(buffer.readBool());
//        buffer.reset();
//        System.err.println(buffer.readBool());
        System.err.println(buffer.readLong());
        System.err.println(buffer.readDouble());
        for (int i = 0; i < 10; i++) {
            System.err.println(buffer.readString());
        }
        System.err.println(buffer.capacity());
    }

}
