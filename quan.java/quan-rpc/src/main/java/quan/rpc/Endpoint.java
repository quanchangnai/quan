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
     * 参数是否有可能被修改<br/>
     * 如果所有参数都是基本类型等不可变类型，则此值无效
     */
    boolean paramMutable() default true;

    /**
     * 返回结果是否有可能被修改<br/>
     * 如果返回结果是基本类型等不可变类型，则此值无效
     */
    boolean resultMutable() default false;

}
