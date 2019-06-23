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

    {
        basicTypes.put("bytes", "byte[]");
        classTypes.put("bytes", "byte[]");
    }

    public JavaMessageGenerator(String srcPath, String destPath) throws Exception {
        super(srcPath, destPath);
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

}
