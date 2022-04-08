package quan.rpc.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.rpc.Promise;
import quan.rpc.Endpoint;
import quan.rpc.Service;

import java.util.List;
import java.util.Map;

/**
 * @author quanchangnai
 */
public class TestService extends Service {

    private static Logger logger = LoggerFactory.getLogger(TestService.class);

    private int id;

    private long lastTime;

    private TestServiceProxy testServiceProxy2 = new TestServiceProxy(1, 2);

    public TestService(int id) {
        this.id = id;
    }

    @Override
    public Object getId() {
        return id;
    }

    /**
     * a+b
     */
    @Endpoint
    public int add(Integer a, Integer b) {
        logger.info("execute TestService{}.add({},{}) at worker{}", id, a, b, this.getWorker().getId());
        return a + b;
    }

    @Endpoint
    public void remove(Map<Integer, String> map, int a) {
        map.remove(a);
        logger.info("execute TestService{}.remove({}) at worker{}", id, a, this.getWorker().getId());
    }

    @Endpoint
    public <E> Integer size(List<? super Runnable> list) {
        return list.size();
    }

    @Override
    protected void update() {
        long now = System.currentTimeMillis();
        if (lastTime > 0 && now < lastTime + 3000) {
            return;
        }
        lastTime = now;

        if (this.id == 1) {
            logger.info("TestService{} call TestService{}  at worker{}", this.id, testServiceProxy2.serviceId, this.getWorker().getId());

            int a = (int) (now % 3);
            int b = (int) (now % 10);
            Promise<Integer> promise = testServiceProxy2.add(a, b);
            promise.then(result -> {
                logger.info("TestService{} call TestService{}.add({},{})={}", this.id, testServiceProxy2.serviceId, a, b, result);
                System.err.println();
            });
        }
    }

}
