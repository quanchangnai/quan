package quan.generator.message;

import freemarker.template.Template;
import quan.generator.BeanDefinition;
import quan.generator.ClassDefinition;
import quan.generator.DefinitionCategory;
import quan.generator.Generator;

import java.util.*;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public abstract class MessageGenerator extends Generator {

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
        Set<MessageDefinition> messageDefinitions = new HashSet<>();
        for (ClassDefinition classDefinition : definitionParser.getClasses().values()) {
            if (classDefinition instanceof MessageDefinition) {
                messageDefinitions.add((MessageDefinition) classDefinition);
            }
        }
        //0xFFFFF正好占用3个字节，100000个坑位用于解决冲突，每次用10000个
        calcMessageId(messageDefinitions, 1, 0xFFFFF - 100000);
    }

    private void calcMessageId(Set<MessageDefinition> messageDefinitions, int begin, int end) {
        Map<Integer, List<MessageDefinition>> conflictedMessagesMap = new HashMap<>();
        for (MessageDefinition messageDefinition : messageDefinitions) {
            int messageId = begin + (messageDefinition.getOriginalName().hashCode() & 0x7FFFFFFF) % (end - begin);
            conflictedMessagesMap.computeIfAbsent(messageId, h -> new ArrayList<>()).add(messageDefinition);
        }

        Set<MessageDefinition> conflictedMessages = new HashSet<>();
        for (Integer messageId : conflictedMessagesMap.keySet()) {
            List<MessageDefinition> messages = conflictedMessagesMap.get(messageId);
            messages.sort(Comparator.comparing(MessageDefinition::getOriginalName));
            messages.get(0).setId(messageId);
            if (messages.size() > 1) {
                conflictedMessages.addAll(messages.subList(1, messages.size()));
            }
        }

        //哈希冲突导致计算ID失败的的消息调整区间参数重新计算
        if (!conflictedMessages.isEmpty()) {
            calcMessageId(conflictedMessages, end, end + 10000);
        }
    }
}
