package quan.database.test;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Created by quanchangnai on 2019/8/12.
 */
public class CglibTest {

    private static CglibInterceptor cglibInterceptor = new CglibInterceptor();

    public static void main(String[] args) {
        Role role1 = test1("aaa" );
        System.err.println("role1.getClass():" + role1.getClass());
        role1.update();

        Role role2 = test1("bbb" );
        System.err.println("role2.getClass():" + role2.getClass());
        role2.update();

        System.err.println("role1.getClass()== role2.getClass():" + (role1.getClass() == role2.getClass()));

        cglibInterceptor.setS("ccc" );
        role1.update();
        role2.update();
    }

    private static Role test1(String s) {
        System.err.println("test1()============================" );

        cglibInterceptor.setS(s);
        return (Role) Enhancer.create(Role.class, cglibInterceptor);

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
            System.err.println("intercept [" + method.getName() + "] start:" + s);
            proxy.invokeSuper(obj, args);
            System.err.println("intercept [" + method.getName() + "] end:" + s);
            return null;
        }
    }

}
