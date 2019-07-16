package quan.message;

import quan.message.role.RoleInfo;
import quan.message.role.RoleType;
import quan.message.role.SRoleLogin;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by quanchangnai on 2019/6/23.
 */
public class MessageTest {

    public static MessageFactory messageFactory = new MessageFactory();

    static {
        messageFactory.autoRegister();
    }

    public static void main(String[] args) throws Exception {

//        test1();
//
//        test2();

        test3();

    }

    private static void test1() throws Exception {
        System.err.println("test1=============================");
        Buffer buffer = new Buffer(100);
        buffer.writeBool(false);
        buffer.writeInt(231);
        buffer.writeFloat(424.4F);
        buffer.writeDouble(23421.424D);
        buffer.writeString("张三1111");

        System.err.println("buffer.available():" + buffer.available());
    }

    private static void test2() throws Exception {
        System.err.println("test2=============================");

        SRoleLogin sRoleLogin1 = new SRoleLogin();
        sRoleLogin1.setRoleId(1111);
        sRoleLogin1.setRoleName("张三1111");

        RoleInfo roleInfo1 = new RoleInfo();
        roleInfo1.setId(111);
        roleInfo1.setRoleName("aaa");
        roleInfo1.setRoleType(RoleType.type1);

        sRoleLogin1.setRoleInfo(roleInfo1);

        RoleInfo roleInfo2 = new RoleInfo();
        roleInfo2.setId(222);
        roleInfo2.setRoleName("bbb");
        roleInfo2.setRoleType(RoleType.type2);
        roleInfo2.getSet().add(2213);

        sRoleLogin1.getRoleInfoList().add(roleInfo2);
        sRoleLogin1.getRoleInfoSet().add(roleInfo2);
        sRoleLogin1.getRoleInfoMap().put(roleInfo2.getId(), roleInfo2);

        System.err.println("sRoleLogin1:" + sRoleLogin1);

        byte[] encodedBytes = sRoleLogin1.encode();

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
