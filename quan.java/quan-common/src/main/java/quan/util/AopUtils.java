package quan.util;

import org.aspectj.weaver.loadtime.ClassPreProcessorAgentAdapter;

/**
 * @author quanchangnai
 */
public class AopUtils {

    private static boolean enabled;

    /**
     * 开启AOP
     */
    public synchronized static void enable() {
        if (!enabled) {
            enabled = true;
            ClassUtils.getInstrumentation().addTransformer(new ClassPreProcessorAgentAdapter());
            try {
                //环绕通知内联支持
                Class.forName("quan.data.TransactionAspect");
            } catch (Exception ignored) {
            }
        }
    }

}
