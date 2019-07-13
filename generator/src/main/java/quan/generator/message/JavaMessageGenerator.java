package quan.generator.message;

import quan.generator.BeanDefinition;
import quan.message.Bean;
import quan.message.Buffer;
import quan.message.Message;

import java.io.IOException;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public class JavaMessageGenerator extends MessageGenerator {

    public JavaMessageGenerator(String srcPath, String destPath) throws Exception {
        super(srcPath, destPath);

        basicTypes.put("bytes", "byte[]");
        classTypes.put("bytes", "byte[]");

    }

    @Override
    protected String getLanguage() {
        return "java";
    }

    protected void processBean(BeanDefinition beanDefinition) {
        super.processBean(beanDefinition);

        if (beanDefinition instanceof MessageDefinition) {
            beanDefinition.getImports().add(Message.class.getName());
        } else {
            beanDefinition.getImports().add(Bean.class.getName());
        }

        beanDefinition.getImports().add("java.util.*");
        beanDefinition.getImports().add(IOException.class.getName());
        beanDefinition.getImports().add(Buffer.class.getName());
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
