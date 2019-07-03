package quan.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.database.role.RoleData;

/**
 * Created by quanchangnai on 2019/7/2.
 */
public class Role {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private RoleData roleData;

    @Transactional
    public String login() {
        logger.error("currentThread:{}", Thread.currentThread());
        String result = login("1111");
        System.err.println("login(1111):" + result);
        return result;
    }

    @Transactional
    public String login(String password) {
        System.err.println("login======================================================");
        roleData = RoleData.getOrInsert(1L);
        logger.error("RoleData.getOrInsert(1L):{}", roleData);

        roleData.setName("role" + System.currentTimeMillis());//role1562131925281
        roleData.getList().add(System.currentTimeMillis() + "");
        roleData.getMap().put(1, 111);
        logger.error("roleData.setName:{}", roleData);

        logger.error("currentThread:{}", Thread.currentThread());

        RuntimeException runtimeException = new RuntimeException();

        if (roleData.getId() == 1) {
//            throw runtimeException;
        }

//        runtimeException.printStackTrace();

        if (roleData.getId() == 1) {
//            return false;
        }

        return "success";
    }

    @Override
    public String toString() {
        return "Role{" +
                "roleData=" + roleData +
                '}';
    }
}
