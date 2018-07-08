package quan.protocol;

import quan.protocol.role.RoleInfo;
import quan.protocol.role.SRoleLogin;
import quan.protocol.user.UserInfo;

/**
 * Created by quanchangnai on 2017/7/5.
 */
public class ProtocolTest {

    public static void main(String[] args) throws Exception {
//        test1();
        test2();
        test3();
//        test4();
    }

    private static void test1() throws Exception {
        System.err.println("====================test1================================");
        VarintBuffer buffer = new VarintBuffer();
//        buffer.writeLong(System.currentTimeMillis());
//        System.err.println(buffer.available());
//        System.err.println(buffer.remaining());
//        System.err.println(Long.toBinaryString(buffer.readLong()).length());
//        System.err.println(buffer.available());
//        System.err.println(buffer.remaining());
//
//        buffer.reset();
        int scale = -1;
        float f1 = 115252524523.4342425F;
        buffer.writeFloat(f1,-1);
        float f2 = buffer.readFloat(-1);
        System.err.println(f1-f2);
//
//        buffer.writeDouble(321.5453);
//        System.err.println(buffer.available());
//        buffer.reset();
//        buffer.writeDouble(321.5453, 1);
//        System.err.println(buffer.available());
//        System.err.println(buffer.readDouble(1));
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

        VarintBuffer buffer = new VarintBuffer();

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

        VarintBuffer buffer = new VarintBuffer();
        userInfo1.serialize(buffer);
        System.err.println("实际占用字节:" + buffer.availableBytes().length);

        long start = System.nanoTime();
        long end = 0;
        int n = 1000000;
        for (int i = 1; i <= n; i++) {
            byte[] bytes = userInfo1.serialize();
            UserInfo userInfo2 = new UserInfo();
            userInfo2.parse(bytes);
            if (i == n) {
                end = System.nanoTime();
//                System.err.println("userInfo2:" + userInfo2);
            }
        }


        System.err.println("编码解码次数：" + n);
        System.err.println("test 耗时(ns)：" + (end - start));
        System.err.println("test 耗时(ms)：" + (end - start) / 1000000);
        System.err.println("test 平均耗时(ms)：" + (end - start) / (n * 1000000.));
    }

    private static void test4() throws Exception {
        System.err.println("====================test4================================");
        VarintBuffer buffer = new VarintBuffer();

        long start = System.currentTimeMillis();

        int n = 100000;
        for (int i = 0; i < n; i++) {
//            buffer.writeFloat(321.43545434F);
//            buffer.writeFloat(321.43545434F,3);
//            buffer.writeDouble(321.43545434D);
//            buffer.writeDouble(321.43545434D, 3);
            buffer.writeLong(321L);
        }

//        System.err.println(buffer.available());

        for (int i = 0; i < n; i++) {
//            buffer.readFloat();
            //读写次数:100000,耗时：10，占用字节:400000,缓存区总大小:524288
//            buffer.readFloat(3);
            //读写次数:100000,耗时：63，占用字节:300000,缓存区总大小:524288
//            buffer.readDouble();
            //读写次数:100000,耗时：12，占用字节:800000,缓存区总大小:1048576
//            buffer.readDouble(3);
            //读写次数:100000,耗时：156，占用字节:300000,缓存区总大小:524288
            buffer.readLong();
            //读写次数:100000,耗时：10，占用字节:200000,缓存区总大小:262144
        }
        long end = System.currentTimeMillis();
        ;
        System.err.println("读写次数:" + n + ",耗时：" + (end - start) + "，占用字节:" + buffer.available()+",缓存区总大小:"+buffer.capacity());
    }

}
