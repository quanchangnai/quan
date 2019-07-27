package quan.generator.message;

import quan.generator.BeanDefinition;
import quan.generator.ClassDefinition;
import quan.generator.Language;
import quan.message.Message;

import java.util.Arrays;
import java.util.List;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public class JavaMessageGenerator extends MessageGenerator {

    public JavaMessageGenerator(List<String> srcPaths, String destPath) throws Exception {
        super(srcPaths, destPath);
    }

    public JavaMessageGenerator(String srcPath, String destPath) throws Exception {
        this(Arrays.asList(srcPath), destPath);
    }

    @Override
    protected Language supportLanguage() {
        return Language.java;
    }

    @Override
    protected void processClassSelf(ClassDefinition classDefinition) {
        super.processClassSelf(classDefinition);
        if (!(classDefinition instanceof BeanDefinition)) {
            return;
        }

        BeanDefinition beanDefinition = (BeanDefinition) classDefinition;
        beanDefinition.getImports().add(Message.class.getPackage().getName() + ".*");
    }


    public static void main(String[] args) throws Exception {
        String srcPath = "generator\\src\\test\\java\\quan\\generator\\message";
        String destPath = "message\\message-java\\src\\test\\java";
        String packagePrefix = "quan.message";

        JavaMessageGenerator messageGenerator = new JavaMessageGenerator(srcPath, destPath);
        messageGenerator.setPackagePrefix(packagePrefix);
        messageGenerator.generate();
    }
}
