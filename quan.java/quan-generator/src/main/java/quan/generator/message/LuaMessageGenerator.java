package quan.generator.message;

import freemarker.template.Template;
import quan.definition.ClassDefinition;
import quan.definition.Language;
import quan.definition.message.MessageDefinition;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 生成Lua代码的消息生成器
 */
public class LuaMessageGenerator extends MessageGenerator {

    private Template registryTemplate;

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

}
