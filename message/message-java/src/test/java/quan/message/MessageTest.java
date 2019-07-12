package quan.message;

import quan.message.role.RoleInfo;
import quan.message.role.RoleType;
import quan.message.role.SRoleLogin;

/**
 * Created by quanchangnai on 2019/6/23.
 */
public class MessageTest {

    public static MessageFactory messageFactory = new MessageFactory();

    static {
        messageFactory.autoRegister();
    }

    public static void main(String[] args) throws Exception {
        SRoleLogin sRoleLogin1 = new SRoleLogin();
        sRoleLogin1.setRoleId(1111);
        sRoleLogin1.setRoleName("aaa");
        sRoleLogin1.setRoleInfo(new RoleInfo());

        sRoleLogin1.getRoleInfoList().add(new RoleInfo());
        sRoleLogin1.getRoleInfoSet().add(new RoleInfo());

        RoleInfo roleInfo = new RoleInfo();
        roleInfo.setRoleId(111111);
        roleInfo.setRoleType(RoleType.type1);
        roleInfo.getSet().add(2213);
        sRoleLogin1.getRoleInfoMap().put(roleInfo.getRoleId(), roleInfo);

        System.err.println("sRoleLogin1:" + sRoleLogin1);

        SRoleLogin sRoleLogin2 = new SRoleLogin();
        sRoleLogin2.decode(sRoleLogin1.encode());

        System.err.println("sRoleLogin2:" + sRoleLogin2);

    }

}
