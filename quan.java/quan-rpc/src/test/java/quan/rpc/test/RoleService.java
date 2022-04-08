package quan.rpc.test;

import quan.rpc.Endpoint;
import quan.rpc.Service;

/**
 * 角色服务
 */
public class RoleService<T extends Object & Runnable> extends Service {

    private T id;

    @Override
    public Object getId() {
        return "role";
    }

    /**
     * 角色登陆1
     */
    @Endpoint
    public void login1(T id) {
        this.id = id;
        System.err.println("login1:" + this.id);
    }

    @Endpoint
    public <T> void login2(T id) {
        System.err.println("login2:" + id);
    }

    @Endpoint
    public <R extends Runnable> R login3(T id) {
        System.err.println("login3:" + id);
        return null;
    }

}
