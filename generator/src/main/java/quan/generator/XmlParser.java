package quan.generator;

import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import quan.generator.database.DataDefinition;
import quan.generator.message.MessageDefinition;

import java.io.File;
import java.util.Arrays;

/**
 * Created by quanchangnai on 2017/7/10.
 */
public class XmlParser extends Parser {

    @Override
    public void setSrcPath(String srcPath) {
        super.setSrcPath(srcPath);
        File file = new File(srcPath);
        File[] files = file.listFiles((File dir, String name) -> name.endsWith(".xml"));
        if (files != null) {
            srcFiles = Arrays.asList(files);
        }
    }

    private Element parseFile(File srcFile) throws Exception {
        Element root = new SAXReader().read(srcFile).getRootElement();
        if (!root.getName().equals("package")) {
            return null;
        }
        return root;
    }

    @Override
    protected void parseClasses(File srcFile) throws Exception {
        Element root = parseFile(srcFile);
        if (root == null) {
            return;
        }

        String packageName = srcFile.getName().substring(0, srcFile.getName().lastIndexOf("."));
        if (packagePrefix != null) {
            packageName = packagePrefix + "." + packageName;
        }

        for (int i = 0; i < root.nodeCount(); i++) {
            Node node = root.node(i);
            if (!(node instanceof Element)) {
                continue;
            }

            Element element = (Element) node;
            ClassDefinition classDefinition = null;

            if (element.getName().equals("enum")) {
                classDefinition = new EnumDefinition();
            } else if (element.getName().equals("bean")) {
                classDefinition = new BeanDefinition();
            } else if (element.getName().equals("message")) {
                MessageDefinition messageDefinition = new MessageDefinition();
                messageDefinition.setId(element.attributeValue("id"));
                classDefinition = messageDefinition;
            } else if (element.getName().equals("data")) {
                DataDefinition dataDefinition = new DataDefinition();
                dataDefinition.setKeyName(element.attributeValue("key"));
                String persistent = element.attributeValue("persistent");
                if (persistent != null && persistent.equals("false")) {
                    dataDefinition.setPersistent(false);
                }
                classDefinition = dataDefinition;
            }

            if (classDefinition == null) {
                continue;
            }

            classDefinition.setDefinitionFile(srcFile.getName());
            classDefinition.setDefinitionText(element.asXML());

            classDefinition.setPackageName(packageName);
            classDefinition.setName(element.attributeValue("name"));

            String comment = root.node(i - 1).getText();
            comment = comment.replaceAll("\r|\n", "").trim();
            if (comment.equals("")) {
                comment = element.node(0).getText();
                comment = comment.replaceAll("\r|\n", "").trim();
            }
            classDefinition.setComment(comment);

            addClassDefinition(classDefinition);

        }
    }

    @Override
    protected void parseFields(File srcFile) throws Exception {
        Element root = parseFile(srcFile);
        if (root == null) {
            return;
        }

        for (int i = 0; i < root.nodeCount(); i++) {
            Node node = root.node(i);
            if (!(node instanceof Element)) {
                continue;
            }

            Element element = (Element) node;
            if (element.getName().equals("enum")) {
                parseEnumFields(element);
            } else if (element.getName().equals("bean") || element.getName().equals("message") || element.getName().equals("data")) {
                parseBeanFields(element);
            }
        }
    }


    private void parseEnumFields(Element element) {
        EnumDefinition enumDefinition = (EnumDefinition) classDefinitions.get(element.attributeValue("name"));

        for (int i = 0; i < element.nodeCount(); i++) {
            Node node = element.node(i);
            if (node instanceof Element) {
                Element child = (Element) node;
                if (!child.getName().equals("field")) {
                    continue;
                }

                FieldDefinition fieldDefinition = new FieldDefinition();
                enumDefinition.getFields().add(fieldDefinition);

                fieldDefinition.setName(child.attributeValue("name"));
                fieldDefinition.setValue(child.attributeValue("value"));

                String comment = element.node(i + 1).getText();
                comment = comment.replaceAll("\r|\n", "").trim();
                fieldDefinition.setComment(comment);

            }
        }
    }

    private void parseBeanFields(Element element) {
        BeanDefinition beanDefinition = (BeanDefinition) classDefinitions.get(element.attributeValue("name"));

        for (int i = 0; i < element.nodeCount(); i++) {
            Node node = element.node(i);
            if (!(node instanceof Element)) {
                continue;
            }

            Element child = (Element) node;
            if (!child.getName().equals("field")) {
                continue;
            }

            FieldDefinition fieldDefinition = new FieldDefinition();
            beanDefinition.getFields().add(fieldDefinition);
            fieldDefinition.setBeanDefinition(beanDefinition);

            fieldDefinition.setName(child.attributeValue("name"));
            fieldDefinition.setType(child.attributeValue("type"));

            String optional = child.attributeValue("optional");
            if (optional != null && optional.equals("true")) {
                fieldDefinition.setOptional(true);
            }

            fieldDefinition.setKeyType(child.attributeValue("key-type"));
            fieldDefinition.setValueType(child.attributeValue("value-type"));

            String comment = element.node(i + 1).getText();
            comment = comment.replaceAll("\r|\n", "").trim();
            fieldDefinition.setComment(comment);

        }

    }

}
