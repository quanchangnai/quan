package quan.generator.message;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.*;

/**
 * Created by quanchangnai on 2017/7/10.
 */
public class MessageParser {

    private String srcPath;

    private List<File> srcFiles = new ArrayList<>();

    private Map<String, ClassDefinition> results = new HashMap<>();

    public MessageParser(String srcPath) {
        this.srcPath = srcPath;
    }


    public List<ClassDefinition> parse() throws Exception {
        File srcPath = new File(this.srcPath);
        File[] files = srcPath.listFiles((File dir, String name) -> name.endsWith(".xml"));
        if (files != null) {
            srcFiles = Arrays.asList(files);
        }

        for (File srcFile : srcFiles) {
            parseClasses(srcFile);
        }

        for (File srcFile : srcFiles) {
            parseFields(srcFile);
        }

        return new ArrayList<>(results.values());
    }


    private void parseClasses(File srcFile) throws Exception {
        Document document = new SAXReader().read(srcFile);
        Element root = document.getRootElement();

        String packageName = root.attributeValue("package");
        if (packageName == null) {
            packageName = ".";
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
                }

                if (classDefinition != null) {
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
                        throw new RuntimeException("类型名不能重复：" + classDefinition.getName());
                    }

                    results.put(classDefinition.getName(), classDefinition);
                }
            }
        }

    }

    private void parseFields(File srcFile) throws Exception {
        Document document = new SAXReader().read(srcFile);
        Element root = document.getRootElement();

        for (int i = 0; i < root.nodeCount(); i++) {
            Node node = root.node(i);
            if (node instanceof Element) {
                Element element = (Element) node;
                if (element.getName().equals("enum")) {
                    parseEnumFields(element);
                } else if (element.getName().equals("bean") || element.getName().equals("message")) {
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

                    enumDefinition.getFields().add(fieldDefinition);
                }
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
                    fieldDefinition.setValue(child.attributeValue("value"));
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

                    if (fieldDefinition.getType().contains(".")) {
                        fieldDefinition.setType(fieldDefinition.getType().substring(fieldDefinition.getType().lastIndexOf(".") + 1));
                    }

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
                }
            }
        }

    }

}
