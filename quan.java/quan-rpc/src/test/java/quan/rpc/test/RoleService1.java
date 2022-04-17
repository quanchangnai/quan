package quan.rpc.test;

import quan.rpc.Endpoint;
import quan.rpc.Service;

/**
 * 角色服务
 */
public class RoleService1<T extends Object & Runnable> extends Service {

    private long id;

    private T t;

    public RoleService1(long id) {
        this.id = id;
    }

    @Override
    public Object getId() {
        return id;
    }

    @Endpoint
    public int login1(Integer a, Integer b) {
        int r = a + b;
        logger.info("Execute RoleService1:{}.login1({},{})={} at Worker:{}", id, a, b, r, this.getWorker().getId());
        return r;
    }

    @Endpoint
    public int login2(Integer a, Integer b) {
        int r = a + b;
        logger.info("Execute RoleService1:{}.login2({},{})={} at Worker:{}", id, a, b, r, this.getWorker().getId());
        return r;
    }

    @Endpoint
    public int login3(Integer a, Integer b) {
        int r = a + b;
        logger.info("Execute RoleService1:{}.login3({},{})={} at Worker:{}", id, a, b, r, this.getWorker().getId());
        return r;
    }

    /**
     * 角色登陆1
     */
    @Endpoint
    public void logout1(T t){
        this.t = t;
        logger.info("logout1:{}", t);
    }

    @Endpoint
    public <T> void logout2(T t) {
        logger.info("logout2:{}", t);
    }

    @Endpoint
    public <R extends Runnable> R logout3(T t) {
        this.t = t;
        logger.info("logout3:{}", t);
        return null;
    }

}
