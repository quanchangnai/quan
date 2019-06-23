package quan.message;

import quan.message.role.SRoleLogin;

/**
 * Created by quanchangnai on 2019/6/23.
 */
public class MessageTest {

    public static void main(String[] args) throws Exception {
        SRoleLogin sRoleLogin1 = new SRoleLogin();
        sRoleLogin1.setRoleId(1111);
        sRoleLogin1.setRoleName("aaa");
        System.err.println("sRoleLogin1:" + sRoleLogin1);

        SRoleLogin sRoleLogin2 = new SRoleLogin();
        sRoleLogin2.decode(sRoleLogin1.encode());

        System.err.println("sRoleLogin2:" + sRoleLogin2);
    }

}
