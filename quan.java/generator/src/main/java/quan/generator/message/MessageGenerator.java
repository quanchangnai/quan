package quan.generator.message;

import freemarker.template.Template;
import quan.definition.BeanDefinition;
import quan.definition.ClassDefinition;
import quan.definition.DefinitionCategory;
import quan.definition.message.MessageDefinition;
import quan.generator.Generator;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public abstract class MessageGenerator extends Generator {

    //ID冲突时重新计算
    private boolean recalcIdOnConflicted = false;

    public MessageGenerator(String codePath) {
        super(codePath);

        try {
            Template messageTemplate = freemarkerCfg.getTemplate("message." + supportLanguage() + ".ftl");
            templates.put(MessageDefinition.class, messageTemplate);
            templates.put(BeanDefinition.class, messageTemplate);
        } catch (IOException e) {
            logger.error("", e);
        }
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
    protected void parseDefinition() {
        super.parseDefinition();
        calcMessageId();
    }

    public MessageGenerator setRecalcIdOnConflicted(boolean recalculateIdOnConflicted) {
        this.recalcIdOnConflicted = recalculateIdOnConflicted;
        return this;
    }

    private void calcMessageId() {
        Set<MessageDefinition> messageDefinitions = new HashSet<>();
        for (ClassDefinition classDefinition : definitionParser.getClasses().values()) {
            if (classDefinition instanceof MessageDefinition) {
                messageDefinitions.add((MessageDefinition) classDefinition);
            }
        }
        //0xFFFFF正好占用3个字节,当设置了[ID冲突时重新计算]时，100000个坑位用于解决冲突，每次用10000个
        calcMessageId(messageDefinitions, 1, 0xFFFFF - 100000);
    }

    private void calcMessageId(Set<MessageDefinition> messageDefinitions, int begin, int end) {
        Map<Integer, List<MessageDefinition>> conflictedMessagesMap = new HashMap<>();
        for (MessageDefinition messageDefinition : messageDefinitions) {
            int messageId = begin + (messageDefinition.getOriginalName().hashCode() & 0x7FFFFFFF) % (end - begin);
            conflictedMessagesMap.computeIfAbsent(messageId, h -> new ArrayList<>()).add(messageDefinition);
        }

        Set<MessageDefinition> allConflictedMessages = new HashSet<>();
        for (Integer messageId : conflictedMessagesMap.keySet()) {
            List<MessageDefinition> conflictedMessages = conflictedMessagesMap.get(messageId);
            conflictedMessages.sort(Comparator.comparing(MessageDefinition::getOriginalName));
            conflictedMessages.get(0).setId(messageId);
            if (conflictedMessages.size() > 1) {
                if (recalcIdOnConflicted) {
                    allConflictedMessages.addAll(conflictedMessages.subList(1, conflictedMessages.size()));
                } else {
                    List<String> conflictedNames = conflictedMessages.stream().map(MessageDefinition::getName).collect(Collectors.toList());
                    definitionParser.addValidatedError(String.format("消息%sID有冲突,改名可以解决冲突", conflictedNames));
                }
            }
        }

        //ID冲突的消息调整区间参数重新计算
        if (!allConflictedMessages.isEmpty()) {
            calcMessageId(allConflictedMessages, end, end + 10000);
        }
    }
}
