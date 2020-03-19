package quan.generator.message;

import freemarker.template.Template;
import quan.definition.BeanDefinition;
import quan.definition.ClassDefinition;
import quan.definition.FieldDefinition;
import quan.definition.Language;
import quan.definition.message.MessageDefinition;
import quan.definition.message.MessageHeadDefinition;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * Created by quanchangnai on 2019/9/5.
 */
public class LuaMessageGenerator extends MessageGenerator {

    private Template messageFactoryTemplate;

    public LuaMessageGenerator() {
    }

    public LuaMessageGenerator(Properties properties) {
        super(properties);
    }

    @Override
    protected void initFreemarker() {
        super.initFreemarker();
        try {
            messageFactoryTemplate = freemarkerCfg.getTemplate("factory." + supportLanguage() + ".ftl");
        } catch (IOException e) {
            logger.error("", e);
        }
    }

    @Override
    protected Language supportLanguage() {
        return Language.lua;
    }

    @Override
    protected void generate(List<ClassDefinition> classDefinitions) {
        List<MessageDefinition> messageDefinitions = new ArrayList<>();

        for (ClassDefinition classDefinition : classDefinitions) {
            if (classDefinition instanceof MessageHeadDefinition) {
                continue;
            }
            if (classDefinition instanceof MessageDefinition) {
                messageDefinitions.add((MessageDefinition) classDefinition);
            }
            generate(classDefinition);

        }

        generateFactory(messageDefinitions);
    }

    protected void generateFactory(List<MessageDefinition> messageDefinitions) {
        File destFilePath = new File(codePath + File.separator + getDefinitionParser().getPackagePrefix().replace(".", File.separator));
        if (!destFilePath.exists() && !destFilePath.mkdirs()) {
            logger.info("创建目录[{}]失败", destFilePath);
            return;
        }

        String fileName = "MessageFactory." + supportLanguage();
        try (Writer writer = new FileWriter(new File(destFilePath, fileName))) {
            Map<String, List<MessageDefinition>> messages = new HashMap<>();
            messages.put("messages", messageDefinitions);
            messageFactoryTemplate.process(messages, writer);
        } catch (Exception e) {
            logger.error("", e);
            return;
        }

        logger.info("生成MessageFactory[{}]成功", destFilePath + File.separator + fileName);
    }

    @Override
    protected void processBeanFieldImports(BeanDefinition beanDefinition, FieldDefinition fieldDefinition) {
        BeanDefinition fieldBean = fieldDefinition.getBean();
        if (fieldBean != null) {
            beanDefinition.getImports().add(fieldBean.getFullName(supportLanguage()));
        }

        BeanDefinition fieldValueBean = fieldDefinition.getValueBean();
        if (fieldValueBean != null) {
            beanDefinition.getImports().add(fieldValueBean.getFullName(supportLanguage()));
        }
    }

}
