package quan.rpc.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.rpc.Endpoint;
import quan.rpc.Promise;
import quan.rpc.Service;

import java.util.List;
import java.util.Map;

/**
 * @author quanchangnai
 */
public class TestService2 extends Service {

    private static Logger logger = LoggerFactory.getLogger(TestService2.class);

    private int id;

    private long lastTime;

    private TestService1Proxy testService1Proxy = new TestService1Proxy(1, 1);

    public TestService2(int id) {
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
    public int add1(Integer a, Integer b) {
        int r = a + b;
        logger.info("Execute TestService2:{}.add1({},{})={} at Worker:{}", id, a, b, r, this.getWorker().getId());
        return r;
    }

    @Endpoint
    public int add2(Integer a, Integer b) {
        int r = a + b;
        logger.info("Execute TestService2:{}.add2({},{})={} at Worker:{}", id, a, b, r, this.getWorker().getId());
        return r;
    }

    @Endpoint
    public void remove(Map<Integer, String> map, int a) {
        map.remove(a);
        logger.info("Execute TestService2:{}.remove({}) at Worker:{}", id, a, this.getWorker().getId());
    }

    @Endpoint
    public <E> Integer size(List<? super Runnable> list) {
        return list.size();
    }

    @Override
    protected void update() {
        long now = System.currentTimeMillis();
        if (lastTime > 0 && now < lastTime + 5000) {
            return;
        }
        lastTime = now;

        logger.info("TestService2:{} call TestService1:{}  at Worker:{}", this.id, testService1Proxy.serviceId, this.getWorker().getId());

        int a = (int) (now % 3);
        int b = (int) (now % 10);
        long startTime = System.nanoTime();
        Promise<Integer> promise = testService1Proxy.add1(a, b);
        promise.then(result -> {
            double costTime = (System.nanoTime() - startTime) / 1000000D;
            logger.info("TestService2:{} call TestService1:{}.add1({},{})={},costTime:{}", this.id, testService1Proxy.serviceId, a, b, result, costTime);
            System.err.println();
        });
    }

}
