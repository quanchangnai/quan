package quan.rpc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标记{@link Service}的方法可被远程调用
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface Endpoint {

    /**
     * 不会被修改的引用类型参数是安全的<br/>
     * 原生类型及其包装类型等不可变类型一定是安全的<br/>
     * {@link quan.util.CommonUtils#isConstantClass(Class)}
     */
    boolean paramSafe() default false;

    /**
     * 不会被修改的引用类型返回结果是安全的<br/>
     * 正常情况下一般不会修改返回结果
     */
    boolean resultSafe() default true;

}
