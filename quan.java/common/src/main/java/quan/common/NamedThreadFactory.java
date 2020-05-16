package quan.common;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ThreadFactory;

/**
 * Created by quanchangnai on 2020/5/16.
 */
public class NamedThreadFactory implements ThreadFactory {

    private final String name;

    private boolean daemon = true;

    private ThreadFactory targetFactory;

    public NamedThreadFactory(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("线程名字不能为空");
        }
        this.name = name;
    }

    public NamedThreadFactory setDaemon(boolean daemon) {
        this.daemon = daemon;
        return this;
    }

    public NamedThreadFactory setTargetFactory(ThreadFactory targetFactory) {
        this.targetFactory = targetFactory;
        return this;
    }

    @Override
    public Thread newThread(Runnable task) {
        Thread thread;
        if (targetFactory != null) {
            thread = targetFactory.newThread(task);
        } else {
            thread = new Thread(task);
        }

        thread.setName(name);
        thread.setDaemon(daemon);

        return thread;
    }

}
