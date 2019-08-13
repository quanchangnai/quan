package quan.generator;

import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import quan.generator.config.ConfigDefinition;
import quan.generator.config.IndexDefinition;
import quan.generator.database.DataDefinition;
import quan.generator.message.MessageDefinition;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by quanchangnai on 2017/7/10.
 */
public class XmlDefinitionParser extends DefinitionParser {

    @Override
    protected String getFileType() {
        return "xml";
    }

    @Override
    @SuppressWarnings({"unchecked"})
    protected List<ClassDefinition> parseClasses(File definitionFile) throws Exception {
        Element rootElement = new SAXReader().read(definitionFile).getRootElement();
        if (rootElement == null || !rootElement.getName().equals("package" )) {
            return Collections.EMPTY_LIST;
        }
        String packageName = definitionFile.getName().substring(0, definitionFile.getName().lastIndexOf("." ));

        List<ClassDefinition> classDefinitions = new ArrayList<>();
        for (int i = 0; i < rootElement.nodeCount(); i++) {
            if (!(rootElement.node(i) instanceof Element)) {
                continue;
            }

            Element classElement = (Element) rootElement.node(i);
            ClassDefinition classDefinition = createClassDefinition(classElement);
            if (classDefinition == null) {
                continue;
            }

            classDefinition.setParser(this);
            classDefinition.setDefinitionFile(definitionFile.getName());
            classDefinition.setDefinitionText(classElement.asXML());

            if (classDefinition instanceof EnumDefinition) {
                classDefinition.setPackagePrefix(packagePrefix);
            } else {
                classDefinition.setPackagePrefix(enumPackagePrefix == null ? packagePrefix : enumPackagePrefix);
            }
            classDefinition.setPackageName(packageName);

            classDefinition.setName(classElement.attributeValue("name" ));
            classDefinition.setLang(classElement.attributeValue("lang" ));

            String comment = rootElement.node(i - 1).getText();
            comment = comment.replaceAll("[\r\n]", "" ).trim();
            if (comment.equals("" )) {
                comment = classElement.node(0).getText();
                comment = comment.replaceAll("[\r\n]", "" ).trim();
            }
            classDefinition.setComment(comment);

            classDefinitions.add(classDefinition);

            parseFields(classDefinition, classElement);
        }
        return classDefinitions;
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
                    classDefinition = new BeanDefinition(classElement.attributeValue("delimiter" )).setCategory(getCategory());
                }
                break;
            case "message":
                if (category == DefinitionCategory.message) {
                    classDefinition = new MessageDefinition();
                }
                break;
            case "data":
                if (category == DefinitionCategory.data) {
                    classDefinition = new DataDefinition(classElement.attributeValue("key" ), classElement.attributeValue("persistent" ));
                }
                break;
            case "config":
                if (category == DefinitionCategory.config) {
                    classDefinition = new ConfigDefinition(classElement.attributeValue("table" ), classElement.attributeValue("parent" ));
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
            if (fieldElement.getName().equals("field" )) {
                parseField(classDefinition, classElement, fieldElement, i);
            } else if (fieldElement.getName().equals("index" ) && classDefinition instanceof ConfigDefinition) {
                parseIndex((ConfigDefinition) classDefinition, classElement, fieldElement, i);
            }

        }

    }

    private void parseField(ClassDefinition classDefinition, Element classElement, Element fieldElement, int i) {
        FieldDefinition fieldDefinition = new FieldDefinition();
        fieldDefinition.setParser(classDefinition.parser);
        fieldDefinition.setCategory(getCategory());

        fieldDefinition.setName(fieldElement.attributeValue("name" ));
        fieldDefinition.setTypes(fieldElement.attributeValue("type" ));
        fieldDefinition.setValue(fieldElement.attributeValue("value" ));
        fieldDefinition.setColumn(fieldElement.attributeValue("column" ));
        fieldDefinition.setOptional(fieldElement.attributeValue("optional" ));
        fieldDefinition.setIndex(fieldElement.attributeValue("index" ));
        fieldDefinition.setDelimiter(fieldElement.attributeValue("delimiter" ));
        fieldDefinition.setRef(fieldElement.attributeValue("ref" ));

        String comment = classElement.node(i + 1).getText();
        comment = comment.replaceAll("[\r\n]", "" ).trim();
        fieldDefinition.setComment(comment);

        classDefinition.addField(fieldDefinition);
    }

    private void parseIndex(ConfigDefinition configDefinition, Element classElement, Element indexElement, int i) {
        IndexDefinition indexDefinition = new IndexDefinition();
        indexDefinition.setParser(configDefinition.parser);
        indexDefinition.setCategory(getCategory());

        indexDefinition.setName(indexElement.attributeValue("name" ));
        indexDefinition.setType(indexElement.attributeValue("type" ));
        indexDefinition.setFieldNames(indexElement.attributeValue("fields" ));

        String comment = classElement.node(i + 1).getText();
        comment = comment.replaceAll("[\r\n]", "" ).trim();

        indexDefinition.setComment(comment);

        configDefinition.addIndex(indexDefinition);
    }

}
