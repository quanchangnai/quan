package quan.rpc.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.rpc.Promise;
import quan.rpc.Service;

/**
 * @author quanchangnai
 */
public class TestService extends Service {

    private static Logger logger = LoggerFactory.getLogger(TestService.class);

    private int id;

    private long lastTime;

    private TestServiceProxy testServiceProxy2 = TestServiceProxy.newInstance(1, 2);

    public TestService(int id) {
        this.id = id;
    }

    @Override
    public Object getId() {
        return id;
    }

    public Integer add(Integer a, Integer b) {
        logger.info("execute TestService{}.add({},{})", id, a, b);
        return a + b;
    }

    @Override
    protected void update() {
        long now = System.currentTimeMillis();
        if (lastTime > 0 && now < lastTime + 3000) {
            return;
        }
        lastTime = now;

        if (this.id == 1) {
            logger.info("TestService{} call TestService{}", this.id, testServiceProxy2.getServiceId());

            int a = (int) (now % 3);
            int b = (int) (now % 10);
            Promise<Integer> promise = testServiceProxy2.add(a, b);
            promise.then(result -> {
                logger.info("TestService{} call TestService{}.add({},{})={}", this.id, testServiceProxy2.getServiceId(), a, b, result);
                System.err.println();
            });
        }
    }
}
