package quan.generator;

import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import quan.generator.config.ConfigDefinition;
import quan.generator.config.IndexDefinition;
import quan.generator.database.DataDefinition;
import quan.generator.message.MessageDefinition;

import java.io.File;
import java.util.*;

/**
 * Created by quanchangnai on 2017/7/10.
 */
public class XmlDefinitionParser extends DefinitionParser {

    private Map<File, Element> file2Roots = new HashMap<>();

    private Map<Element, ClassDefinition> element2Classes = new HashMap<>();

    @Override
    protected String getFileType() {
        return "xml";
    }

    private Element parseFile(File srcFile) throws Exception {
        Element root = file2Roots.get(srcFile);
        if (root == null) {
            root = new SAXReader().read(srcFile).getRootElement();
            if (root.getName().equals("package")) {
                file2Roots.put(srcFile, root);
            } else {
                root = null;
            }
        }
        return root;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    protected List<ClassDefinition> parseClasses(File srcFile) throws Exception {
        Element root = parseFile(srcFile);
        if (root == null) {
            return Collections.EMPTY_LIST;
        }

        String simplePackageName = srcFile.getName().substring(0, srcFile.getName().lastIndexOf("."));

        String packageName = simplePackageName;
        if (packagePrefix != null) {
            packageName = packagePrefix + "." + packageName;
        }

        String enumPackageName = packageName;
        if (enumPackagePrefix != null) {
            enumPackageName = enumPackagePrefix + "." + simplePackageName;
        }

        List<ClassDefinition> classDefinitions = new ArrayList<>();
        for (int i = 0; i < root.nodeCount(); i++) {
            Node node = root.node(i);
            if (!(node instanceof Element)) {
                continue;
            }

            Element element = (Element) node;
            ClassDefinition classDefinition;

            switch (element.getName()) {
                case "enum":
                    classDefinition = new EnumDefinition();
                    break;
                case "bean":
                    classDefinition = new BeanDefinition(element.attributeValue("delimiter"));
                    break;
                case "message":
                    classDefinition = new MessageDefinition(element.attributeValue("id"));
                    break;
                case "data":
                    classDefinition = new DataDefinition(element.attributeValue("key"), element.attributeValue("persistent"));
                    break;
                case "config":
                    classDefinition = new ConfigDefinition(element.attributeValue("table"), element.attributeValue("parent"));
                    break;
                default:
                    continue;
            }
            classDefinition.setCategory(getCategory());
            classDefinition.setDefinitionFile(srcFile.getName());
            classDefinition.setDefinitionText(element.asXML());

            if (classDefinition instanceof EnumDefinition) {
                classDefinition.setPackageName(enumPackageName);
            } else {
                classDefinition.setPackageName(packageName);
            }

            classDefinition.setName(element.attributeValue("name"));
            classDefinition.setLang(element.attributeValue("lang"));

            String comment = root.node(i - 1).getText();
            comment = comment.replaceAll("[\r|\n]", "").trim();
            if (comment.equals("")) {
                comment = element.node(0).getText();
                comment = comment.replaceAll("[\r|\n]", "").trim();
            }
            classDefinition.setComment(comment);

            element2Classes.put(element, classDefinition);
            classDefinitions.add(classDefinition);
        }
        return classDefinitions;
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


    private void parseFields(Element classElement) {
        ClassDefinition classDefinition = element2Classes.get(classElement);
        if (classDefinition == null) {
            return;
        }

        for (int i = 0; i < classElement.nodeCount(); i++) {
            Node node = classElement.node(i);
            if (!(node instanceof Element)) {
                continue;
            }

            Element child = (Element) node;
            if (child.getName().equals("field")) {
                parseField(classDefinition, classElement, child, i);
            } else if (child.getName().equals("index") && classDefinition instanceof ConfigDefinition) {
                parseIndex((ConfigDefinition) classDefinition, classElement, child, i);
            }

        }

    }

    private void parseField(ClassDefinition classDefinition, Element classElement, Element fieldElement, int i) {
        FieldDefinition fieldDefinition = new FieldDefinition();
        fieldDefinition.setCategory(getCategory());

        fieldDefinition.setName(fieldElement.attributeValue("name"));
        fieldDefinition.setType(fieldElement.attributeValue("type"));
        fieldDefinition.setValue(fieldElement.attributeValue("value"));
        fieldDefinition.setColumn(fieldElement.attributeValue("column"));
        fieldDefinition.setOptional(fieldElement.attributeValue("optional"));
        fieldDefinition.setKeyType(fieldElement.attributeValue("key-type"));
        fieldDefinition.setValueType(fieldElement.attributeValue("value-type"));
        fieldDefinition.setIndex(fieldElement.attributeValue("index"));
        fieldDefinition.setDelimiter(fieldElement.attributeValue("delimiter"));
        fieldDefinition.setRef(fieldElement.attributeValue("ref"));

        String comment = classElement.node(i + 1).getText();
        comment = comment.replaceAll("[\r|\n]", "").trim();
        fieldDefinition.setComment(comment);

        classDefinition.addField(fieldDefinition);
    }

    private void parseIndex(ConfigDefinition configDefinition, Element classElement, Element indexElement, int i) {
        IndexDefinition indexDefinition = new IndexDefinition(configDefinition);
        indexDefinition.setCategory(getCategory());

        indexDefinition.setName(indexElement.attributeValue("name"));
        indexDefinition.setType(indexElement.attributeValue("type"));
        indexDefinition.setFieldNames(indexElement.attributeValue("fields"));

        String comment = classElement.node(i + 1).getText();
        comment = comment.replaceAll("[\r|\n]", "").trim();

        indexDefinition.setComment(comment);

        configDefinition.addIndex(indexDefinition);
    }

}
