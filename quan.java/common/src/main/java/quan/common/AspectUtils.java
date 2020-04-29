package quan.common;

import org.aspectj.weaver.loadtime.ClassPreProcessorAgentAdapter;

/**
 * Created by quanchangnai on 2020/4/28.
 */
public class AspectUtils {

    private static boolean enabled;

    /**
     * 启用AOP
     */
    public synchronized static void enable() {
        if (enabled) {
            return;
        }
        enabled = true;
        ClassUtils.getInstrumentation().addTransformer(new ClassPreProcessorAgentAdapter());
    }

}
