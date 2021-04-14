package quan.definition.parser;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import quan.definition.*;
import quan.definition.config.ConfigDefinition;
import quan.definition.config.ConstantDefinition;
import quan.definition.data.DataDefinition;
import quan.definition.message.HeaderDefinition;
import quan.definition.message.MessageDefinition;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * 基于XML的定义文件解析器
 * Created by quanchangnai on 2017/7/10.
 */
public class XmlDefinitionParser extends DefinitionParser {

    {
        definitionFileEncoding = Charset.defaultCharset().name();
    }

    @Override
    protected String definitionFileType() {
        return "xml";
    }

    @Override
    protected void parseFile(File definitionFile) {
        Element rootElement;
        try (InputStreamReader definitionReader = new InputStreamReader(new FileInputStream(definitionFile), definitionFileEncoding)) {
            rootElement = new SAXReader().read(definitionReader).getRootElement();
            if (rootElement == null || !rootElement.getName().equals("package")) {
                return;
            }

        } catch (Exception e) {
            String error;
            try {
                error = String.format("解析定义文件[%s]出错", definitionFile.getCanonicalPath());
            } catch (Exception ex) {
                error = String.format("解析定义文件[%s]出错", definitionFile);
            }
            addValidatedError(error);
            logger.error(error, e);
            return;
        }

        //默认以定义文件名作为包名
        String packageName = definitionFile.getName().substring(0, definitionFile.getName().lastIndexOf("."));
        if (!Language.java.matchPackageName(packageName)) {
            addValidatedError("定义文件[" + definitionFile.getName() + "]的名字格式错误,正确格式:" + Language.LOWER_PACKAGE_NAME_PATTERN);
        }

        packageName = rootElement.attributeValue("name", packageName);
        if (!Language.java.matchPackageName(packageName)) {
            addValidatedError("定义文件[" + definitionFile.getName() + "]的包名[" + packageName + "]格式错误,正确格式:" + Language.LOWER_PACKAGE_NAME_PATTERN);
        }

        //具体语言对应的包名
        Map<String, String> languagePackageNames = new HashMap<>();
        for (Object attribute : rootElement.attributes()) {
            String languageName = ((Attribute) attribute).getName();
            String languagePackageName = ((Attribute) attribute).getValue();
            if (languageName.equals("name") || !Language.names().contains(languageName)) {
                continue;
            }
            Language language = Language.valueOf(languageName);
            if (!language.matchPackageName(languagePackageName)) {
                addValidatedError("定义文件[" + definitionFile + "]自定义语言[" + language + "]包名[" + languagePackageName + "]格式错误,正确格式:" + language.getPackageNamePattern());
                continue;
            }
            languagePackageNames.put(language.name(), languagePackageName);
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

            classDefinition.setDefinitionFile(definitionFile.getName());
            classDefinition.setDefinitionText(classElement.asXML());

            classDefinition.setPackageName(packageName);
            classDefinition.getPackageNames().putAll(languagePackageNames);

            String comment = rootElement.node(i - 1).getText().replaceAll("[\r\n]", "");
            if (StringUtils.isBlank(comment)) {
                comment = classElement.node(0).getText().replaceAll("[\r\n]", "");
            }
            classDefinition.setComment(comment.trim());

            parseClassChildren(classDefinition, classElement);
        }
    }

    private ClassDefinition createClassDefinition(Element classElement) {
        ClassDefinition classDefinition = null;
        switch (classElement.getName()) {
            case "enum":
                classDefinition = new EnumDefinition();
                break;
            case "bean":
                classDefinition = new BeanDefinition(classElement.attributeValue("parent"), classElement.attributeValue("delimiter"));
                break;
            case "message":
                if (category == Category.message) {
                    classDefinition = new MessageDefinition(classElement.attributeValue("id"));
                }
                break;
            case "message-header":
                if (category == Category.message) {
                    if (headerDefinition == null) {
                        headerDefinition = new HeaderDefinition();
                        classDefinition = headerDefinition;
                    } else {
                        addValidatedError("消息头不能重复定义");
                    }
                }
                break;
            case "data":
                if (category == Category.data) {
                    classDefinition = new DataDefinition(classElement.attributeValue("id"));
                }
                break;
            case "config":
                if (category == Category.config) {
                    classDefinition = new ConfigDefinition(classElement.attributeValue("table"), classElement.attributeValue("parent"));
                }
                break;
        }

        if (classDefinition != null) {
            classDefinition.setParser(this);
            classDefinition.setCategory(getCategory());
            classDefinition.setName(classElement.attributeValue("name"));
            classDefinition.setLang(classElement.attributeValue("lang"));
        }

        return classDefinition;
    }

    private void parseClassChildren(ClassDefinition classDefinition, Element classElement) {
        for (int i = 0; i < classElement.nodeCount(); i++) {
            if (!(classElement.node(i) instanceof Element)) {
                continue;
            }

            Element childElement = (Element) classElement.node(i);
            String childName = childElement.getName();

            if (childName.equals("field")) {
                parseField(classDefinition, classElement, childElement, i);
            }

            if (classDefinition instanceof ConfigDefinition) {
                ConfigDefinition configDefinition = (ConfigDefinition) classDefinition;
                if (childName.equals("index")) {
                    configDefinition.addIndex(parseIndex(classElement, childElement, i));
                }
                if (childName.equals("constant")) {
                    parseConstant(configDefinition, classElement, childElement, i);
                }
            }

            if (classDefinition instanceof DataDefinition && childName.equals("index")) {
                DataDefinition dataDefinition = (DataDefinition) classDefinition;
                dataDefinition.addIndex(parseIndex(classElement, childElement, i));
            }

            if (category == Category.config && classDefinition instanceof BeanDefinition && childName.equals("bean")) {
                BeanDefinition beanDefinition = (BeanDefinition) createClassDefinition(childElement);
                beanDefinition.setParentName(classDefinition.getName());
                parsedClasses.add(beanDefinition);

                beanDefinition.setDefinitionFile(classDefinition.getDefinitionFile());
                beanDefinition.setDefinitionText(classDefinition.getDefinitionText());
                beanDefinition.setPackageName(classDefinition.getPackageName());
                beanDefinition.getPackageNames().putAll(classDefinition.getPackageNames());
                beanDefinition.setExcludeLanguage(classDefinition.isExcludeLanguage());
                beanDefinition.getLanguages().addAll(classDefinition.getLanguages());
                String comment = childElement.node(0).getText().replaceAll("[\r\n]", "").trim();
                beanDefinition.setComment(comment);

                parseClassChildren(beanDefinition, childElement);
            }
        }
    }

    private void parseField(ClassDefinition classDefinition, Element classElement, Element fieldElement, int i) {
        FieldDefinition fieldDefinition = new FieldDefinition();
        fieldDefinition.setParser(classDefinition.getParser());
        fieldDefinition.setCategory(getCategory());

        fieldDefinition.setName(fieldElement.attributeValue("name"));
        fieldDefinition.setTypes(fieldElement.attributeValue("type"));
        fieldDefinition.setValue(fieldElement.attributeValue("value"));
        fieldDefinition.setColumn(fieldElement.attributeValue("column"));
        fieldDefinition.setOptional(fieldElement.attributeValue("optional"));
        fieldDefinition.setId(fieldElement.attributeValue("id"));
        fieldDefinition.setIgnore(fieldElement.attributeValue("ignore"));
        fieldDefinition.setIndex(fieldElement.attributeValue("index"));
        fieldDefinition.setDelimiter(fieldElement.attributeValue("delimiter"));
        fieldDefinition.setRef(fieldElement.attributeValue("ref"));
        if (classDefinition instanceof ConfigDefinition) {
            fieldDefinition.setLanguage(fieldElement.attributeValue("lang"));
        }

        String comment = classElement.node(i + 1).getText().replaceAll("[\r\n]", "").trim();
        fieldDefinition.setComment(comment);

        classDefinition.addField(fieldDefinition);
    }

    private IndexDefinition parseIndex(Element classElement, Element indexElement, int i) {
        IndexDefinition indexDefinition = new IndexDefinition();
        indexDefinition.setParser(this);
        indexDefinition.setCategory(getCategory());

        indexDefinition.setName(indexElement.attributeValue("name"));
        indexDefinition.setType(indexElement.attributeValue("type"));
        indexDefinition.setFieldNames(indexElement.attributeValue("fields"));

        String comment = classElement.node(i + 1).getText().replaceAll("[\r\n]", "").trim();
        indexDefinition.setComment(comment);

        return indexDefinition;
    }

    private void parseConstant(ConfigDefinition configDefinition, Element classElement, Element constantElement, int i) {
        ConstantDefinition constantDefinition = new ConstantDefinition();
        constantDefinition.setConfigDefinition(configDefinition);
        constantDefinition.setDefinitionText(constantElement.asXML());

        constantDefinition.setName(constantElement.attributeValue("name"));
        constantDefinition.setUseEnum(constantElement.attributeValue("enum"));
        constantDefinition.setKeyField(constantElement.attributeValue("key"));
        constantDefinition.setValueField(constantElement.attributeValue("value"));
        constantDefinition.setCommentField(constantElement.attributeValue("comment"));

        String comment = classElement.node(i + 1).getText().replaceAll("[\r\n]", "").trim();
        constantDefinition.setComment(comment);

        parsedClasses.add(constantDefinition);
    }

}
