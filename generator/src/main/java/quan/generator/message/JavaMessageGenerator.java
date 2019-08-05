package quan.generator.message;

import quan.generator.BeanDefinition;
import quan.generator.ClassDefinition;
import quan.generator.Language;
import quan.message.Message;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public class JavaMessageGenerator extends MessageGenerator {

    public JavaMessageGenerator(String codePath) throws Exception {
        super(codePath);
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

        JavaMessageGenerator messageGenerator = new JavaMessageGenerator(codePath);
        messageGenerator.useXmlDefinitionParser(definitionPath, packagePrefix);
        messageGenerator.generate();
    }
}
