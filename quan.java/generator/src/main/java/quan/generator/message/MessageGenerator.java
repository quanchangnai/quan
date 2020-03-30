package quan.generator.message;

import freemarker.template.Template;
import quan.definition.BeanDefinition;
import quan.definition.Category;
import quan.definition.ClassDefinition;
import quan.definition.message.MessageDefinition;
import quan.definition.message.MessageHeadDefinition;
import quan.generator.Generator;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public abstract class MessageGenerator extends Generator {

    //ID冲突时重新哈希计算
    private boolean rehashId;

    public MessageGenerator() {
    }

    public MessageGenerator(Properties properties) {
        super(properties);
    }

    @Override
    protected void initProps(Properties properties) {
        super.initProps(properties);
        if (!enable) {
            return;
        }

        String rehashId = properties.getProperty(category() + ".rehashId");
        this.setRehashId(rehashId != null && rehashId.equals("true"));
    }

    @Override
    protected void initFreemarker() {
        super.initFreemarker();
        try {
            Template messageTemplate = freemarkerCfg.getTemplate("message." + supportLanguage() + ".ftl");
            templates.put(MessageDefinition.class, messageTemplate);
            templates.put(BeanDefinition.class, messageTemplate);
            templates.put(MessageHeadDefinition.class, messageTemplate);
        } catch (IOException e) {
            logger.error("", e);
        }
    }

    @Override
    public final Category category() {
        return Category.message;
    }

    @Override
    protected boolean support(ClassDefinition classDefinition) {
        if (classDefinition instanceof MessageDefinition) {
            return true;
        }
        return super.support(classDefinition);
    }

    @Override
    protected void parseDefinitions() {
        super.parseDefinitions();
        hashId();
    }

    public MessageGenerator setRehashId(boolean rehashId) {
        this.rehashId = rehashId;
        return this;
    }

    //使用类名哈希计算消息ID
    private void hashId() {
        Set<MessageDefinition> messageDefinitions = new HashSet<>();
        for (ClassDefinition classDefinition : parser.getClasses().values()) {
            if (classDefinition instanceof MessageDefinition) {
                messageDefinitions.add((MessageDefinition) classDefinition);
            }
        }
        //0xFFFFF正好占用3个字节,当设置了[ID冲突时重新哈希计算]时，100000个坑位用于解决冲突，每次用10000个
        hashId(messageDefinitions, 1, 0xFFFFF - 100000);
    }

    private void hashId(Set<MessageDefinition> messageDefinitions, int begin, int end) {
        Map<Integer, List<MessageDefinition>> conflictedMessagesMap = new HashMap<>();
        for (MessageDefinition messageDefinition : messageDefinitions) {
            int messageId = begin + (messageDefinition.getNameWithPackage().hashCode() & 0x7FFFFFFF) % (end - begin);
            conflictedMessagesMap.computeIfAbsent(messageId, h -> new ArrayList<>()).add(messageDefinition);
        }

        Set<MessageDefinition> allConflictedMessages = new HashSet<>();
        for (Integer messageId : conflictedMessagesMap.keySet()) {
            List<MessageDefinition> conflictedMessages = conflictedMessagesMap.get(messageId);
            conflictedMessages.sort(Comparator.comparing(MessageDefinition::getNameWithPackage));
            conflictedMessages.get(0).setId(messageId);
            if (conflictedMessages.size() > 1) {
                if (rehashId) {
                    allConflictedMessages.addAll(conflictedMessages.subList(1, conflictedMessages.size()));
                } else {
                    List<String> conflictedNames = conflictedMessages.stream().map(MessageDefinition::getName).collect(Collectors.toList());
                    parser.addValidatedError(String.format("消息%sID有冲突,改名可以解决冲突", conflictedNames));
                }
            }
        }

        //ID冲突的消息调整区间参数重新计算
        if (!allConflictedMessages.isEmpty()) {
            hashId(allConflictedMessages, end, end + 10000);
        }
    }
}
