package quan.message;

import org.junit.Test;
import quan.message.role.RoleInfo;
import quan.message.role.RoleType;
import quan.message.role.SRoleLogin;
import quan.message.user.UserInfo;
import quan.message.user.UserType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by quanchangnai on 2019/6/23.
 */
public class MessageTest {

    public static MessageRegistry messageRegistry = new MessageRegistry();

    static {
        messageRegistry.register("quan");
    }

    @Test
    public void test() throws Exception {
        long n = 1628419835799L;
        n = (n << 1) ^ (n >> 63);
        System.err.println(n);
        System.err.println(Long.toBinaryString(n));
        System.err.println(Long.toBinaryString(n).length());
    }

    @Test
    public void test1() throws Exception {
        System.err.println("test1=============================");

        Buffer buffer = new SimpleBuffer();
//        buffer = new NettyBuffer(Unpooled.buffer());

        buffer.writeBool(true);
        buffer.writeShort(Short.MAX_VALUE);
        buffer.writeInt(2423);
        buffer.writeFloat(13.43F);
        buffer.writeDouble(4242.432);
        buffer.writeFloat(132.32434F, 2);
        buffer.writeDouble(342254.653254, 2);
        buffer.writeString("搭顺风车");
        buffer.writeLong(424234);
        buffer.writeLong(Long.MAX_VALUE);
        buffer.writeLong(Long.MIN_VALUE);
        buffer.writeTag(253);

        System.err.println("buffer.readableCount()=" + buffer.readableCount());

//        FileOutputStream fos = new FileOutputStream(new File("D:\\buffer"));
//        fos.write(buffer.remainingBytes());
//
//        FileInputStream fis = new FileInputStream(new File("D:\\buffer"));
//        byte[] bytes = new byte[fis.available()];
//        fis.read(bytes);
//        System.err.println("bytes.length=" + bytes.length);
//        buffer = new SimpleBuffer(bytes);

        System.err.println(buffer.readBool());
        System.err.println(buffer.readShort());
        System.err.println(buffer.readInt());
        System.err.println(buffer.readFloat());
        System.err.println(buffer.readDouble());
        System.err.println(buffer.readFloat(2));
        System.err.println(buffer.readDouble(2));
        System.err.println(buffer.readString());
        System.err.println(buffer.readLong());
        System.err.println(Long.MAX_VALUE + ":" + buffer.readLong());
        System.err.println(buffer.readLong() == Long.MIN_VALUE);
        System.err.println(buffer.readTag());

//        buffer.reset();
//        buffer.writeInt(45);
//        buffer.writeString("奋斗服务");
//        System.err.println(buffer.readInt());
//        System.err.println(buffer.readString());
    }

    @Test
    public void test2() throws Exception {
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
        roleInfo2.setName("暗访方式的法人");
        roleInfo2.setType(RoleType.type2);
        roleInfo2.getSet().add(2213);

        sRoleLogin1.getRoleInfoList().add(roleInfo2);
        sRoleLogin1.getRoleInfoList().add(roleInfo2);
        sRoleLogin1.getRoleInfoSet().add(roleInfo2);
        sRoleLogin1.getRoleInfoSet().add(roleInfo2);
        sRoleLogin1.getRoleInfoMap().put(roleInfo2.getId(), roleInfo2);

        UserInfo userInfo = new UserInfo();
        userInfo.setId(1);
        userInfo.setLevel(2);
        userInfo.setName("addadas");
        userInfo.setType(UserType.type1);
        userInfo.setRoleInfo1(roleInfo1);
//        userInfo.setRoleInfo2(new quan.message.user.RoleInfo());
        userInfo.getRoleList().add(roleInfo1);
        userInfo.getRoleMap().put(roleInfo2.getId(), roleInfo2);
        userInfo.setF15(343.6F);
        userInfo.setF17(-Double.MAX_VALUE);
        userInfo.setF18(4534545);

        sRoleLogin1.setUserInfo(userInfo);

        System.err.println("sRoleLogin1:" + sRoleLogin1);

        byte[] encodedBytes = sRoleLogin1.encode();

        FileOutputStream fileOutputStream = new FileOutputStream(new File("D:\\SRoleLogin"));
        fileOutputStream.write(encodedBytes);

        FileInputStream fileInputStream = new FileInputStream(new File("D:\\SRoleLogin"));
        encodedBytes = new byte[fileInputStream.available()];
        fileInputStream.read(encodedBytes);

        System.err.println("encodedBytes.length:" + encodedBytes.length);

        SRoleLogin sRoleLogin2 = new SRoleLogin();
        sRoleLogin2.decode(encodedBytes);

        System.err.println("sRoleLogin2:" + sRoleLogin2);

        Buffer buffer = new SimpleBuffer();
        sRoleLogin1.encode(buffer);
        SRoleLogin sRoleLogin3 = new SRoleLogin();
        sRoleLogin3.decode(buffer);
        System.err.println("sRoleLogin3:" + sRoleLogin3);
    }

    @Test
    public void test3() throws Exception {
        System.err.println("test3=============================");

        UserInfo userInfo1 = new UserInfo();
        userInfo1.setId(1);
        userInfo1.setLevel(2);
        userInfo1.setName("addadas");

        System.err.println("userInfo1:" + userInfo1);

        byte[] encodedBytes = userInfo1.encode();

        System.err.println("encodedBytes.length:" + encodedBytes.length);

        FileOutputStream fileOutputStream = new FileOutputStream(new File("D:\\UserInfo"));
        fileOutputStream.write(encodedBytes);
        fileOutputStream.close();

        FileInputStream fileInputStream = new FileInputStream(new File("D:\\UserInfo"));
        encodedBytes = new byte[fileInputStream.available()];
        fileInputStream.read(encodedBytes);
        fileInputStream.close();


        UserInfo userInfo2 = new UserInfo();
        userInfo2.decode(encodedBytes);

        System.err.println("userInfo2:" + userInfo2);
    }

}
