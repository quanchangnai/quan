package quan.mongo;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import java.io.File;
import java.io.IOException;

/**
 * JavaAgent字节码转换器
 * Created by quanchangnai on 2018/8/6.
 */
public class Transformer implements AgentBuilder.Transformer {

    @Override
    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
        return builder.method(ElementMatchers.isAnnotatedWith(Transactional.class)).intercept(Advice.to(InterceptAdvice.class));
    }
}
