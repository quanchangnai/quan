package quan.generator.message;

import freemarker.template.Template;
import quan.definition.ClassDefinition;
import quan.definition.Language;
import quan.definition.message.MessageDefinition;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
            registryTemplate = freemarkerCfg.getTemplate("registry.lua.ftl");
        } catch (IOException e) {
            logger.error("", e);
        }
    }

    @Override
    protected void generate(List<ClassDefinition> classDefinitions) {
        List<MessageDefinition> messageDefinitions = new ArrayList<>();

        for (ClassDefinition classDefinition : classDefinitions) {
            generate(classDefinition);
            if (classDefinition instanceof MessageDefinition && classDefinition.isSupportedLanguage(language())) {
                messageDefinitions.add((MessageDefinition) classDefinition);
            }
        }

        if (!increment || count > 0) {
            generateFactory(messageDefinitions);
        }
    }

    protected void generateFactory(List<MessageDefinition> messageDefinitions) {
        File filePath = new File(codePath + File.separator + getParser().getPackagePrefix().replace(".", File.separator));
        if (!filePath.exists() && !filePath.mkdirs()) {
            logger.error("创建目录[{}]失败", filePath);
            return;
        }

        String fileName = "MessageRegistry.lua";
        try (Writer writer = new OutputStreamWriter(Files.newOutputStream(new File(filePath, fileName).toPath()), StandardCharsets.UTF_8)) {
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
