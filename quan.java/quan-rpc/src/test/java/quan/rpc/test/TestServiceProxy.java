package quan.rpc.test;

import quan.rpc.Promise;
import quan.rpc.RpcThread;

/**
 * @author quanchangnai
 */
public class TestServiceProxy {

    private int targetServerId;

    private Object serviceId;

    private TestServiceProxy(int targetServerId, Object serviceId) {
        this.targetServerId = targetServerId;
        this.serviceId = serviceId;
    }

    public static TestServiceProxy newInstance(int targetServerId, Object serviceId) {
        return new TestServiceProxy(targetServerId, serviceId);
    }

    public int getTargetServerId() {
        return targetServerId;
    }

    public Object getServiceId() {
        return serviceId;
    }

    public Promise<Integer> add(Integer a, Integer b) {
        return RpcThread.current().sendRequest(targetServerId, serviceId, "add", a, b);
    }

    private static class Caller extends quan.rpc.Caller<TestService> {

        public Object call(TestService service, String methodId, Object... methodParams) {
            switch (methodId) {
                case "add":
                    return service.add((Integer) methodParams[0], (Integer) methodParams[1]);
                case "remove":
                    return service.remove((Integer) methodParams[0]);
                default:
                    return null;
            }
        }

    }
}
