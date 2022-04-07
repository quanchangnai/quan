package quan.rpc.test;

import quan.rpc.RPC;
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
    @RPC
    public void login1(T id) {
        this.id = id;
        System.err.println("login1:" + this.id);
    }

    @RPC
    public <T> void login2(T id) {
        System.err.println("login2:" + id);
    }

    @RPC
    public <R extends Object> R login3(T id) {
        System.err.println("login3:" + id);
        return (R) "";
    }

}
