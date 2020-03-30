package quan.database.test;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.matcher.ElementMatchers;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * Created by quanchangnai on 2019/8/12.
 */
public class CglibTest {

    private static CglibInterceptor cglibInterceptor = new CglibInterceptor();

    public static void main(String[] args) throws Exception {


//        Role role1 = test1("aaa" );
//        System.err.println("role1.getClass():" + role1.getClass());
//        role1.update();
//        long start1 = System.nanoTime();
//        role1.update();
//        System.err.println("cost1:" + (System.nanoTime() - start1));//cost1:20779

//        Role role2 = test1("bbb" );
//        System.err.println("role2.getClass():" + role2.getClass());
//        role2.update();
//
//        System.err.println("role1.getClass()== role2.getClass():" + (role1.getClass() == role2.getClass()));
//
//        cglibInterceptor.setS("ccc" );
//        role1.update();
//        role2.update();

//        Role role3 = new Role();
//        role3.update();
//        long start3 = System.nanoTime();
//        role3.update();
//        System.err.println("cost3:" + (System.nanoTime() - start3));//cost3:24380

        Role role4 = subclass(Role.class).getDeclaredConstructor().newInstance();
        System.err.println("role4.getClass():" + role4.getClass());
        role4.update();
        long start4 = System.nanoTime();
        role4.update();
        System.err.println("cost4:" + (System.nanoTime() - start4));//cost4:17732


    }

    private static Role test1(String s) {
        System.err.println("test1()============================" );

        cglibInterceptor.setS(s);
        return (Role) Enhancer.create(Role.class, cglibInterceptor);

    }

    public synchronized static <T> Class<? extends T> subclass(Class<T> clazz) {

        return new ByteBuddy()
                .subclass(clazz)
                .method(ElementMatchers.any())
                .intercept(MethodDelegation.to(ByteBuddyDelegation.class))
                .make()
                .load(clazz.getClassLoader())
                .getLoaded();
    }

    private static class CglibInterceptor implements MethodInterceptor {

        private String s;

        public CglibInterceptor(String s) {
            this.s = s;
        }

        public CglibInterceptor() {
        }

        public CglibInterceptor setS(String s) {
            this.s = s;
            return this;
        }

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
//            System.err.println("intercept [" + method.getName() + "] start:" + s);
            Object result = proxy.invokeSuper(obj, args);
//            System.err.println("intercept [" + method.getName() + "] end:" + s);
            return result;
        }
    }

    public static class ByteBuddyDelegation {

        @RuntimeType
        public static Object delegate(@SuperCall Callable<?> callable, @Origin Method originMethod) throws Exception {
            Object result = callable.call();
            return result;

        }

    }


}
