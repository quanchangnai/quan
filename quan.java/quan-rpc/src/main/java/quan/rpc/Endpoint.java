package quan.rpc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标记{@link Service}的方法可被远程调用
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface Endpoint {

    /**
     * 安全参数是指在方法执行时不会被修改<br/>
     * 原生类型及其包装类型等不可变类型一定是安全的<br/>
     * {@link quan.util.CommonUtils#isConstantClass(Class)}
     */
    boolean safeParam() default false;

    /**
     * 安全结果指在方法返回后不会被修改<br/>
     * 正常情况下一般不会修改返回结果
     */
    boolean safeResult() default true;

}
