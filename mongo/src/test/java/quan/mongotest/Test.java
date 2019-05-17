package quan.mongotest;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import quan.mongo.Transactional;

import java.io.File;
import java.util.List;

/**
 * Created by quanchangnai on 2019/5/14.
 */
public class Test {

    public static void main(String[] args) throws Exception{
        test1();
//        test2();
    }


    private static void test1()throws Exception{
        AgentBuilder.Transformer transformer = (builder, typeDescription, classLoader, module) -> builder
                .method(ElementMatchers.isAnnotatedWith(Transactional.class))
                .intercept(MethodDelegation.to(TransactionInterceptor.class));

        new AgentBuilder.Default()
                .type(ElementMatchers.any())
                .transform(transformer)
                .installOn( ByteBuddyAgent.install());

        MemoryDatabase loggingDatabase = new MemoryDatabase();

        List<String> loadResult = loggingDatabase.load("aaa");
        System.err.println("loadResult:"+loadResult);
    }

    private static void test2()throws Exception{

        DynamicType.Loaded<MemoryDatabase> loaded = new ByteBuddy()
                .rebase(MemoryDatabase.class)
                .method(ElementMatchers.named("load")).intercept(MethodDelegation.to(TransactionInterceptor.class))
                .make()
                .load(Test.class.getClassLoader());

        loaded.saveIn(new File("C:\\Users\\admin\\IdeaProjects\\quan\\mongo\\target"));

        MemoryDatabase loggingDatabase = loaded
                .getLoaded()
                .newInstance();

        List<String> loadResult = loggingDatabase.load("aaa");
        System.err.println("loadResult:"+loadResult);
    }

}
