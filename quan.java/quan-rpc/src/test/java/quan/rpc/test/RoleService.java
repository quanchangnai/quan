package quan.rpc.test;

import quan.rpc.Endpoint;
import quan.rpc.Promise;
import quan.rpc.Service;

/**
 * 角色服务
 */
public class RoleService<T extends Object & Runnable> extends Service {

    private long id;

    private T t;

    private TestServiceProxy testServiceProxy = new TestServiceProxy(1, 1);

    private long lastTime;

    public RoleService(long id) {
        this.id = id;
    }

    @Override
    public Object getId() {
        return id;
    }

    @Override
    protected void update() {
        long now = System.currentTimeMillis();
        if (lastTime > 0 && now < lastTime + 10000) {
            return;
        }
        lastTime = now;

        logger.info("RoleService:{} call TestService:{}-{}", this.id, testServiceProxy.serverId, testServiceProxy.serviceId);

        int a = (int) (now % 3);
        int b = (int) (now % 10);
        Promise<Integer> promise = testServiceProxy.add(a, b);
        promise.then(result -> {
            logger.info("RoleService:{} call TestService:{}-{}.add({},{})={}", this.id, testServiceProxy.serverId, testServiceProxy.serviceId, a, b, result);
            System.err.println();
        });
    }

    /**
     * 角色登陆1
     */
    @Endpoint
    public void login1(T t) {
        this.t = t;
        System.err.println("login1:" + t);
    }

    @Endpoint
    public <T> void login2(T t) {
        System.err.println("login2:" + t);
    }

    @Endpoint
    public <R extends Runnable> R login3(T t) {
        this.t = t;
        System.err.println("login3:" + t);
        return null;
    }

}
