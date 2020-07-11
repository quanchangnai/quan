package quan.generator.message;

import freemarker.template.Template;
import quan.definition.BeanDefinition;
import quan.definition.ClassDefinition;
import quan.definition.DependentSource;
import quan.definition.Language;
import quan.definition.message.HeaderDefinition;
import quan.definition.message.MessageDefinition;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by quanchangnai on 2019/9/5.
 */
public class LuaMessageGenerator extends MessageGenerator {

    private Template registryTemplate;

    public LuaMessageGenerator() {
    }

    public LuaMessageGenerator(Properties options) {
        super(options);
    }

    @Override
    protected Language language() {
        return Language.lua;
    }

    @Override
    protected void initFreemarker() {
        super.initFreemarker();
        try {
            registryTemplate = freemarkerCfg.getTemplate("registry." + language() + ".ftl");
        } catch (IOException e) {
            logger.error("", e);
        }
    }

    @Override
    protected void generate(List<ClassDefinition> classDefinitions) {
        List<MessageDefinition> messageDefinitions = new ArrayList<>();

        for (ClassDefinition classDefinition : classDefinitions) {
            if (classDefinition instanceof HeaderDefinition) {
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
        File filePath = new File(codePath + File.separator + getParser().getPackagePrefix().replace(".", File.separator));
        if (!filePath.exists() && !filePath.mkdirs()) {
            logger.info("创建目录[{}]失败", filePath);
            return;
        }

        String fileName = "MessageRegistry." + language();
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(new File(filePath, fileName)), StandardCharsets.UTF_8)) {
            Map<String, List<MessageDefinition>> messages = new HashMap<>();
            messages.put("messages", messageDefinitions);
            registryTemplate.process(messages, writer);
        } catch (Exception e) {
            logger.error("", e);
            return;
        }

        logger.info("生成消息注册表[{}]完成", filePath + File.separator + fileName);
    }

    @Override
    protected void prepareClass(ClassDefinition classDefinition) {
        if (classDefinition instanceof BeanDefinition) {
            prepareBean((BeanDefinition) classDefinition);
        }

        Map<String, TreeMap<DependentSource, ClassDefinition>> dependentClasses = classDefinition.getDependentClasses();
        for (String name : dependentClasses.keySet()) {
            ClassDefinition firstDependentClass = dependentClasses.get(name).firstEntry().getValue();
            classDefinition.getImports().add(firstDependentClass.getOtherImport(language()));
        }

        if (classDefinition instanceof MessageDefinition) {
            HeaderDefinition headerDefinition = ((MessageDefinition) classDefinition).getHeader();
            if (headerDefinition != null) {
                classDefinition.getImports().remove(headerDefinition.getOtherImport(language()));
            }
        }
    }
}
