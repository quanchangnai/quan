package quan.generator;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.generator.database.DataDefinition;
import quan.generator.message.EnumDefinition;
import quan.generator.message.MessageDefinition;
import quan.message.Message;

import java.io.File;
import java.util.*;

/**
 * Created by quanchangnai on 2017/7/10.
 */
public class Parser {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected List<File> srcFiles = new ArrayList<>();

    protected Map<String, ClassDefinition> results = new HashMap<>();

    private String packagePrefix;

    public Parser(String srcPath, String packagePrefix) {
        File file = new File(srcPath);
        File[] files = file.listFiles((File dir, String name) -> name.endsWith(".xml"));
        if (files != null) {
            srcFiles = Arrays.asList(files);
        }
        this.packagePrefix = packagePrefix;
    }


    public List<ClassDefinition> parse() throws Exception {

        for (File srcFile : srcFiles) {
            parseClasses(srcFile);
        }

        for (File srcFile : srcFiles) {
            parseFields(srcFile);
        }

        return new ArrayList<>(results.values());
    }

    protected void parseClasses(File srcFile) throws Exception {
        Document document = new SAXReader().read(srcFile);
        Element root = document.getRootElement();
        if (!root.getName().equals("package")) {
            return;
        }

        String packageName = srcFile.getName().substring(0, srcFile.getName().lastIndexOf("."));
        if (packagePrefix != null) {
            packageName = packagePrefix + "." + packageName;
        }

        for (int i = 0; i < root.nodeCount(); i++) {
            Node node = root.node(i);
            if (node instanceof Element) {
                Element element = (Element) node;
                ClassDefinition classDefinition = null;

                if (element.getName().equals("enum")) {
                    classDefinition = new EnumDefinition();
                } else if (element.getName().equals("bean")) {
                    classDefinition = new BeanDefinition();
                } else if (element.getName().equals("message")) {
                    MessageDefinition messageDefinition = new MessageDefinition();
                    classDefinition = messageDefinition;
                    messageDefinition.setId(element.attributeValue("id"));
                } else if (element.getName().equals("data")) {
                    DataDefinition dataDefinition = new DataDefinition();
                    classDefinition = dataDefinition;
                    dataDefinition.setKeyName(element.attributeValue("key"));
                }

                if (classDefinition != null) {
                    classDefinition.setFileName(srcFile.getName());
                    classDefinition.setPackageName(packageName);
                    classDefinition.setName(element.attributeValue("name"));

                    String comment = root.node(i - 1).getText();
                    comment = comment.replaceAll("\r|\n", "").trim();
                    if (comment.equals("")) {
                        comment = element.node(0).getText();
                        comment = comment.replaceAll("\r|\n", "").trim();
                    }
                    classDefinition.setComment(comment);

                    if (results.containsKey(classDefinition.getName())) {
                        String errorStr = "定义文件[" + results.get(classDefinition.getName()).getFileName() +
                                "]和[" + classDefinition.getFileName() +
                                "]有同名类[" + classDefinition.getName() + "]";
                        throw new RuntimeException(errorStr);
                    }

                    results.put(classDefinition.getName(), classDefinition);
                }
            }
        }

    }

    protected void parseFields(File srcFile) throws Exception {
        Document document = new SAXReader().read(srcFile);
        Element root = document.getRootElement();

        for (int i = 0; i < root.nodeCount(); i++) {
            Node node = root.node(i);
            if (node instanceof Element) {
                Element element = (Element) node;
                if (element.getName().equals("enum")) {
                    parseEnumFields(element);
                } else if (element.getName().equals("bean")
                        || element.getName().equals("message")
                        || element.getName().equals("data")) {
                    parseBeanFields(element);
                }
            }
        }
    }


    private void parseEnumFields(Element element) {
        EnumDefinition enumDefinition = null;
        ClassDefinition classDefinition = results.get(element.attributeValue("name"));
        if (classDefinition instanceof EnumDefinition) {
            enumDefinition = (EnumDefinition) classDefinition;
        }
        if (enumDefinition == null) {
            return;
        }

        for (int i = 0; i < element.nodeCount(); i++) {
            Node node = element.node(i);
            if (node instanceof Element) {
                Element child = (Element) node;
                if (child.getName().equals("field")) {
                    FieldDefinition fieldDefinition = new FieldDefinition();

                    fieldDefinition.setName(child.attributeValue("name"));
                    fieldDefinition.setValue(child.attributeValue("value"));

                    String comment = element.node(i + 1).getText();
                    comment = comment.replaceAll("\r|\n", "").trim();
                    fieldDefinition.setComment(comment);

                    validField(enumDefinition, fieldDefinition);

                    enumDefinition.getFields().add(fieldDefinition);
                }
            }
        }
    }

    private void validField(ClassDefinition classDefinition, FieldDefinition fieldDefinition) {
        String errorPosition = "文件:" + classDefinition.getFileName() + "，类:" + classDefinition.getName();
        //校验字段名
        if (fieldDefinition.getName() == null || fieldDefinition.getName().trim().equals("")) {
            throw new RuntimeException("字段名不能为空，" + errorPosition);
        }
        fieldDefinition.setName(fieldDefinition.getName().trim());

        //校验枚举值
        if (classDefinition instanceof EnumDefinition) {
            try {
                int enumValue = Integer.parseInt(fieldDefinition.getValue());
                if (enumValue <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                throw new RuntimeException("枚举值必须为正整数，" + errorPosition);
            }

            fieldDefinition.setValue(fieldDefinition.getValue().trim());

            for (FieldDefinition field : classDefinition.getFields()) {
                if (field.getValue().equals(fieldDefinition.getValue())) {
                    throw new RuntimeException("枚举值不能重复，" + errorPosition);
                }
            }
            return;
        }

        //校验字段类型
        if (fieldDefinition.getType() == null || fieldDefinition.getType().trim().equals("")) {
            throw new RuntimeException("字段类型不能为空，" + errorPosition);
        }
        fieldDefinition.setType(fieldDefinition.getType().trim());
        if (fieldDefinition.isCollectionType()) {
            if (fieldDefinition.getValueType() == null || fieldDefinition.getValueType().trim().equals("")) {
                throw new RuntimeException(fieldDefinition.getType() + "的值类型不能为空，" + errorPosition);
            }
            if (fieldDefinition.getType().equals("map") && (fieldDefinition.getKeyType() == null || fieldDefinition.getKeyType().trim().equals(""))) {
                throw new RuntimeException(fieldDefinition.getType() + "的键类型不能为空，" + errorPosition);
            }
        }

    }

    private void parseBeanFields(Element element) {
        BeanDefinition beanDefinition = null;
        ClassDefinition classDefinition = results.get(element.attributeValue("name"));
        if (classDefinition instanceof BeanDefinition) {
            beanDefinition = (BeanDefinition) classDefinition;
        }
        if (beanDefinition == null) {
            return;
        }

        for (int i = 0; i < element.nodeCount(); i++) {
            Node node = element.node(i);
            if (node instanceof Element) {
                Element child = (Element) node;
                if (child.getName().equals("field")) {
                    FieldDefinition fieldDefinition = new FieldDefinition();
                    fieldDefinition.setName(child.attributeValue("name"));
                    fieldDefinition.setType(child.attributeValue("type"));

                    String optional = child.attributeValue("optional");
                    if (optional != null && optional.equals("true")) {
                        fieldDefinition.setOptional(true);
                    }

                    fieldDefinition.setKeyType(child.attributeValue("key-type"));
                    fieldDefinition.setValueType(child.attributeValue("value-type"));

                    fieldDefinition.setBeanDefinition(beanDefinition);

                    String comment = element.node(i + 1).getText();
                    comment = comment.replaceAll("\r|\n", "").trim();
                    fieldDefinition.setComment(comment);

                    validField(beanDefinition, fieldDefinition);

                    ClassDefinition fieldTypeClassDefinition = results.get(fieldDefinition.getType());
                    if (fieldTypeClassDefinition != null) {
                        if (!fieldTypeClassDefinition.getPackageName().equals(beanDefinition.getPackageName())) {
                            beanDefinition.getImports().add(fieldTypeClassDefinition.getFullName());
                        }
                        if (fieldTypeClassDefinition instanceof EnumDefinition) {
                            fieldDefinition.setEnumType(true);
                        }
                    }

                    beanDefinition.getFields().add(fieldDefinition);

                    if (beanDefinition instanceof DataDefinition) {
                        DataDefinition dataDefinition = (DataDefinition) beanDefinition;
                        if (dataDefinition.getKeyName().equals(fieldDefinition.getName())) {
                            dataDefinition.setKeyType(fieldDefinition.getType());
                        }
                    }
                }
            }
        }
        if (beanDefinition instanceof DataDefinition) {
            DataDefinition dataDefinition = (DataDefinition) beanDefinition;
            if (dataDefinition.getKeyName() == null || dataDefinition.getKeyName().trim().equals("")) {
                String errorPosition = "文件:" + classDefinition.getFileName() + "，类:" + classDefinition.getName();
                throw new RuntimeException("主键不能为空，" + errorPosition);
            }
            if (dataDefinition.getKeyType() == null || dataDefinition.getKeyType().trim().equals("")) {
                String errorPosition = "文件:" + classDefinition.getFileName() + "，类:" + classDefinition.getName();
                throw new RuntimeException("主键不存在，" + errorPosition);
            }
        }

    }

}
