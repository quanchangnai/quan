package quan.definition;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import quan.definition.config.ConfigDefinition;
import quan.definition.config.ConstantDefinition;
import quan.definition.config.IndexDefinition;
import quan.definition.data.DataDefinition;
import quan.definition.message.MessageDefinition;

import java.io.File;
import java.util.regex.Pattern;

/**
 * Created by quanchangnai on 2017/7/10.
 */
public class XmlDefinitionParser extends DefinitionParser {

    @Override
    protected String definitionFileType() {
        return "xml";
    }

    @Override
    protected void parseClasses(File definitionFile) {
        Element rootElement;
        try {
            rootElement = new SAXReader().read(definitionFile).getRootElement();
            if (rootElement == null || !rootElement.getName().equals("package")) {
                return;
            }
        } catch (DocumentException e) {
            String error = String.format("解析定义文件[%s]出错", definitionFile);
            addValidatedError(error);
            logger.error(error, e);
            return;
        }

        String packageName = definitionFile.getName().substring(0, definitionFile.getName().lastIndexOf("."));
        if (!Pattern.matches(Constants.PACKAGE_NAME_PATTERN, packageName)) {
            addValidatedError("定义文件名[" + packageName + "]格式错误");
            return;
        }

        for (int i = 0; i < rootElement.nodeCount(); i++) {
            if (!(rootElement.node(i) instanceof Element)) {
                continue;
            }

            Element classElement = (Element) rootElement.node(i);
            ClassDefinition classDefinition = createClassDefinition(classElement);
            if (classDefinition == null) {
                continue;
            }
            parsedClasses.add(classDefinition);

            classDefinition.setParser(this);
            classDefinition.setDefinitionFile(definitionFile.getName());
            classDefinition.setDefinitionText(classElement.asXML());

            if (classDefinition instanceof EnumDefinition) {
                classDefinition.setPackagePrefix(packagePrefix);
            } else {
                classDefinition.setPackagePrefix(enumPackagePrefix == null ? packagePrefix : enumPackagePrefix);
            }
            classDefinition.setPackageName(packageName);

            classDefinition.setName(classElement.attributeValue("name"));
            classDefinition.setLang(classElement.attributeValue("lang"));

            String comment = rootElement.node(i - 1).getText();
            comment = comment.replaceAll("[\r\n]", "").trim();
            if (comment.equals("")) {
                comment = classElement.node(0).getText();
                comment = comment.replaceAll("[\r\n]", "").trim();
            }
            classDefinition.setComment(comment);

            parseFields(classDefinition, classElement);
        }
    }

    private ClassDefinition createClassDefinition(Element classElement) {
        ClassDefinition classDefinition = null;
        switch (classElement.getName()) {
            case "enum":
                classDefinition = new EnumDefinition().setCategory(getCategory());
                break;
            case "entity":
                if (category == DefinitionCategory.data) {
                    classDefinition = new BeanDefinition().setCategory(getCategory());
                }
                break;
            case "bean":
                if (category == DefinitionCategory.message || category == DefinitionCategory.config) {//临时
                    classDefinition = new BeanDefinition(classElement.attributeValue("delimiter")).setCategory(getCategory());
                }
                break;
            case "message":
                if (category == DefinitionCategory.message) {
                    classDefinition = new MessageDefinition();
                }
                break;
            case "data":
                if (category == DefinitionCategory.data) {
                    classDefinition = new DataDefinition(classElement.attributeValue("key"), classElement.attributeValue("persistent"));
                }
                break;
            case "config":
                if (category == DefinitionCategory.config) {
                    classDefinition = new ConfigDefinition(classElement.attributeValue("table"), classElement.attributeValue("parent"));
                }
                break;
        }

        return classDefinition;
    }

    private void parseFields(ClassDefinition classDefinition, Element classElement) {
        for (int i = 0; i < classElement.nodeCount(); i++) {
            if (!(classElement.node(i) instanceof Element)) {
                continue;
            }

            Element fieldElement = (Element) classElement.node(i);
            String fieldElementName = fieldElement.getName();
            if (fieldElementName.equals("field")) {
                parseField(classDefinition, classElement, fieldElement, i);
            }
            if (classDefinition instanceof ConfigDefinition) {
                if (fieldElementName.equals("index")) {
                    parseIndex((ConfigDefinition) classDefinition, classElement, fieldElement, i);
                }
                if (fieldElementName.equals("constant")) {
                    parseConstant((ConfigDefinition) classDefinition, classElement, fieldElement, i);
                }
            }
        }
    }

    private void parseField(ClassDefinition classDefinition, Element classElement, Element fieldElement, int i) {
        FieldDefinition fieldDefinition = new FieldDefinition();
        fieldDefinition.setParser(classDefinition.parser);
        fieldDefinition.setCategory(getCategory());

        fieldDefinition.setName(fieldElement.attributeValue("name"));
        fieldDefinition.setTypes(fieldElement.attributeValue("type"));
        fieldDefinition.setValue(fieldElement.attributeValue("value"));
        fieldDefinition.setColumn(fieldElement.attributeValue("column"));
        fieldDefinition.setOptional(fieldElement.attributeValue("optional"));
        fieldDefinition.setIgnore(fieldElement.attributeValue("ignore"));
        fieldDefinition.setIndex(fieldElement.attributeValue("index"));
        fieldDefinition.setDelimiter(fieldElement.attributeValue("delimiter"));
        fieldDefinition.setRef(fieldElement.attributeValue("ref"));

        String comment = classElement.node(i + 1).getText();
        comment = comment.replaceAll("[\r\n]", "").trim();
        fieldDefinition.setComment(comment);

        classDefinition.addField(fieldDefinition);
    }

    private void parseIndex(ConfigDefinition configDefinition, Element classElement, Element indexElement, int i) {
        IndexDefinition indexDefinition = new IndexDefinition();
        indexDefinition.setParser(configDefinition.parser);
        indexDefinition.setCategory(getCategory());

        indexDefinition.setName(indexElement.attributeValue("name"));
        indexDefinition.setType(indexElement.attributeValue("type"));
        indexDefinition.setFieldNames(indexElement.attributeValue("fields"));

        String comment = classElement.node(i + 1).getText();
        comment = comment.replaceAll("[\r\n]", "").trim();

        indexDefinition.setComment(comment);

        configDefinition.addIndex(indexDefinition);
    }

    private void parseConstant(ConfigDefinition configDefinition, Element classElement, Element constantElement, int i) {
        ConstantDefinition constantDefinition = new ConstantDefinition();
        constantDefinition.setConfigDefinition(configDefinition);
        constantDefinition.setDefinitionText(constantElement.asXML());

        constantDefinition.setName(constantElement.attributeValue("name"));
        constantDefinition.setKeyField(constantElement.attributeValue("key"));
        constantDefinition.setValueField(constantElement.attributeValue("value"));

        String comment = classElement.node(i + 1).getText();
        comment = comment.replaceAll("[\r\n]", "").trim();

        constantDefinition.setComment(comment);

        parsedClasses.add(constantDefinition);
    }

}
