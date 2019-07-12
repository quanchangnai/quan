package quan.generator;

import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import quan.generator.config.ConfigDefinition;
import quan.generator.database.DataDefinition;
import quan.generator.message.MessageDefinition;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Created by quanchangnai on 2017/7/10.
 */
public class XmlParser extends Parser {

    @Override
    protected String getSrcFileType() {
        return "xml";
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
            ClassDefinition classDefinition;

            if (element.getName().equals("enum")) {
                classDefinition = new EnumDefinition();
            } else if (element.getName().equals("bean")) {
                classDefinition = new BeanDefinition();
            } else if (element.getName().equals("message")) {
                classDefinition = new MessageDefinition(element.attributeValue("id"));
            } else if (element.getName().equals("data")) {
                classDefinition = new DataDefinition(element.attributeValue("key"), element.attributeValue("persistent"));
            } else if (element.getName().equals("config")) {
                classDefinition = new ConfigDefinition(element.attributeValue("source"));
            } else {
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

            List<String> elementNames = Arrays.asList("enum", "bean", "message", "data", "config");
            if (!elementNames.contains(element.getName())) {
                continue;
            }

            parseFields(element);

        }
    }


    private void parseFields(Element element) {
        ClassDefinition classDefinition = classDefinitions.get(element.attributeValue("name"));

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
            classDefinition.getFields().add(fieldDefinition);
            fieldDefinition.setClassDefinition(classDefinition);

            fieldDefinition.setName(child.attributeValue("name"));
            fieldDefinition.setType(child.attributeValue("type"));
            fieldDefinition.setValue(child.attributeValue("value"));
            fieldDefinition.setSource(child.attributeValue("source"));
            fieldDefinition.setOptional(child.attributeValue("optional"));
            fieldDefinition.setKeyType(child.attributeValue("key-type"));
            fieldDefinition.setValueType(child.attributeValue("value-type"));

            String comment = element.node(i + 1).getText();
            comment = comment.replaceAll("\r|\n", "").trim();
            fieldDefinition.setComment(comment);

        }

    }

}
