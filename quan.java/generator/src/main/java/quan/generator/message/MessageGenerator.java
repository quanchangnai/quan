package quan.generator.message;

import freemarker.template.Template;
import quan.definition.BeanDefinition;
import quan.definition.Category;
import quan.definition.ClassDefinition;
import quan.definition.message.MessageDefinition;
import quan.definition.message.HeaderDefinition;
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
    protected void parseOptions(Properties options) {
        super.parseOptions(options);
        if (!enable) {
            return;
        }

        String rehashId = options.getProperty(category() + ".rehashId");
        this.setRehashId(rehashId != null && rehashId.equals("true"));
    }

    @Override
    protected void initFreemarker() {
        super.initFreemarker();
        try {
            Template messageTemplate = freemarkerCfg.getTemplate("message." + language() + ".ftl");
            templates.put(MessageDefinition.class, messageTemplate);
            templates.put(BeanDefinition.class, messageTemplate);
            templates.put(HeaderDefinition.class, messageTemplate);
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
    protected void hashId() {
        Map<Integer, MessageDefinition> definedIdMessageDefinitions = new HashMap<>();
        Set<MessageDefinition> hashIdMessageDefinitions = new HashSet<>();

        for (ClassDefinition classDefinition : parser.getClasses().values()) {
            if (!(classDefinition instanceof MessageDefinition)) {
                continue;
            }
            MessageDefinition messageDefinition = (MessageDefinition) classDefinition;
            if (messageDefinition.isDefinedId()) {
                MessageDefinition oldMessageDefinition = definedIdMessageDefinitions.put(messageDefinition.getId(), messageDefinition);
                if (oldMessageDefinition != null) {
                    parser.addValidatedError(String.format(messageDefinition.getValidatedName("和") + oldMessageDefinition.getValidatedName() + "的ID[%d]冲突", messageDefinition.getId()));
                }
            } else {
                hashIdMessageDefinitions.add(messageDefinition);
            }
        }
        //0xFFFFF正好占用3个字节,当设置了[rehashId]时，100000个坑位用于解决冲突，每次用10000个
        hashId(definedIdMessageDefinitions, hashIdMessageDefinitions, 1, 0xFFFFF - 100000);
    }

    protected void hashId(Map<Integer, MessageDefinition> definedIdMessageDefinitions, Set<MessageDefinition> hashIdMessageDefinitions, int begin, int end) {
        Map<Integer, List<MessageDefinition>> conflictedMessagesMap = new HashMap<>();
        for (MessageDefinition messageDefinition : hashIdMessageDefinitions) {
            int messageId = begin + (messageDefinition.getLongName().hashCode() & 0x7FFFFFFF) % (end - begin);
            if (definedIdMessageDefinitions.containsKey(messageId)) {
                parser.addValidatedError(String.format(messageDefinition.getValidatedName("和自定义ID的") + definedIdMessageDefinitions.get(messageId).getValidatedName() + "的ID[%d]冲突", messageId));
            }
            conflictedMessagesMap.computeIfAbsent(messageId, h -> new ArrayList<>()).add(messageDefinition);
        }

        Set<MessageDefinition> allConflictedMessages = new HashSet<>();
        for (Integer messageId : conflictedMessagesMap.keySet()) {
            List<MessageDefinition> conflictedMessages = conflictedMessagesMap.get(messageId);
            conflictedMessages.sort(Comparator.comparing(MessageDefinition::getLongName));
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
            hashId(definedIdMessageDefinitions, allConflictedMessages, end, end + 10000);
        }
    }
}
