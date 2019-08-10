package quan.generator.message;

import freemarker.template.Template;
import quan.generator.BeanDefinition;
import quan.generator.ClassDefinition;
import quan.generator.DefinitionCategory;
import quan.generator.Generator;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public abstract class MessageGenerator extends Generator {

    private boolean allowConflict = true;

    public MessageGenerator(String codePath) throws Exception {
        super(codePath);

        Template messageTemplate = freemarkerCfg.getTemplate("message." + supportLanguage() + ".ftl");

        templates.put(MessageDefinition.class, messageTemplate);
        templates.put(BeanDefinition.class, messageTemplate);
    }

    @Override
    public final DefinitionCategory category() {
        return DefinitionCategory.message;
    }

    @Override
    protected boolean support(ClassDefinition classDefinition) {
        if (classDefinition instanceof MessageDefinition) {
            return true;
        }
        return super.support(classDefinition);
    }

    @Override
    protected void parseDefinition() throws Exception {
        super.parseDefinition();
        calcMessageId();
    }

    private void calcMessageId() {
        Map<Integer, List<MessageDefinition>> hashMessages = new HashMap<>();
        for (ClassDefinition classDefinition : definitionParser.getClasses().values()) {
            if (!(classDefinition instanceof MessageDefinition)) {
                continue;
            }
            MessageDefinition messageDefinition = (MessageDefinition) classDefinition;

            int hash = messageDefinition.getOriginalName().hashCode() % Short.MAX_VALUE;
            hashMessages.computeIfAbsent(hash, h -> new ArrayList<>()).add(messageDefinition);
        }

        for (Integer hash : hashMessages.keySet()) {
            List<MessageDefinition> messages = hashMessages.get(hash);
            messages.sort(Comparator.comparing(MessageDefinition::getOriginalName));
            if (!allowConflict && messages.size() > 1) {
                List<String> messageNames = messages.stream().map(m -> m.getName()).collect(Collectors.toList());
                definitionParser.addValidatedError(String.format("消息ID有冲突:", messageNames));
                continue;
            }
            for (int i = 0; i < messages.size(); i++) {
                messages.get(i).setId(hash);
            }
        }

    }
}
