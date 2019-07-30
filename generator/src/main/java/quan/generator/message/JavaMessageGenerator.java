package quan.generator.message;

import quan.generator.BeanDefinition;
import quan.generator.ClassDefinition;
import quan.generator.Language;
import quan.message.Message;

import java.util.Collections;
import java.util.List;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public class JavaMessageGenerator extends MessageGenerator {

    public JavaMessageGenerator(List<String> definitionPaths, String codePath) throws Exception {
        super(definitionPaths, codePath);
    }

    public JavaMessageGenerator(String definitionPath, String codePath) throws Exception {
        this(Collections.singletonList(definitionPath), codePath);
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
        String definitionPath = "generator\\src\\test\\java\\quan\\generator\\message";
        String codePath = "message\\message-java\\src\\test\\java";
        String packagePrefix = "quan.message";

        JavaMessageGenerator messageGenerator = new JavaMessageGenerator(definitionPath, codePath);
        messageGenerator.setPackagePrefix(packagePrefix);
        messageGenerator.generate();
    }
}
