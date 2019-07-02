package quan.database;

import quan.database.role.RoleData;

/**
 * Created by quanchangnai on 2019/7/2.
 */
@Transactional
public class Role {

    private RoleData roleData;

    @Transactional
    public boolean login() {
        roleData = RoleData.getOrInsert(1L);
        roleData.setName("role" + System.currentTimeMillis());
        System.err.println("login:" + roleData);
        System.err.println("currentThread:" + Thread.currentThread());

        RuntimeException runtimeException = new RuntimeException();

        if (roleData.getId() == 1) {
//            throw runtimeException;
        }

//        runtimeException.printStackTrace();

        if (roleData.getId() == 1) {
//            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "Role{" +
                "roleData=" + roleData +
                '}';
    }
}
