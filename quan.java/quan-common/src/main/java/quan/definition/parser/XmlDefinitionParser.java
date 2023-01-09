package quan.definition.parser;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.Text;
import org.dom4j.io.SAXReader;
import quan.definition.*;
import quan.definition.config.ConfigDefinition;
import quan.definition.config.ConstantDefinition;
import quan.definition.data.DataDefinition;
import quan.definition.message.MessageDefinition;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基于XML的【定义】解析器
 */
public class XmlDefinitionParser extends DefinitionParser {

    {
        definitionFileEncoding = Charset.defaultCharset().name();
    }

    public XmlDefinitionParser() {
    }

    public XmlDefinitionParser(Category category) {
        this.category = category;
    }


    @Override
    public String getDefinitionType() {
        return "xml";
    }

    @Override
    public  int getMinTableBodyStartRow(){
        return 2;
    }

    @Override
    protected void parseFile(File definitionFile) {
        Element rootElement;
        try (InputStreamReader definitionReader = new InputStreamReader(Files.newInputStream(definitionFile.toPath()), definitionFileEncoding)) {
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
            String attrName = ((Attribute) attribute).getName();
            String attrValue = ((Attribute) attribute).getValue();
            if (!attrName.endsWith("-name")) {
                continue;
            }
            Language language;
            try {
                language = Language.valueOf(attrName.substring(0, attrName.indexOf("-name")));
            } catch (IllegalArgumentException e) {
                addValidatedError("定义文件[" + definitionFile + "]不支持包名属性:" + attrName);
                continue;
            }
            if (!language.matchPackageName(attrValue)) {
                addValidatedError("定义文件[" + definitionFile + "]自定义语言[" + language + "]包名[" + attrValue + "]格式错误,正确格式:" + language.getPackageNamePattern());
                continue;
            }
            languagePackageNames.put(language.name(), attrValue);
        }

        for (int index = 0; index < rootElement.nodeCount(); index++) {
            if (!(rootElement.node(index) instanceof Element)) {
                continue;
            }

            Element classElement = (Element) rootElement.node(index);

            ClassDefinition classDefinition = null;
            try {
                classDefinition = parseClassDefinition(classElement, index);
            } catch (Exception e) {
                addValidatedError("定义文件[" + definitionFile.getName() + "]不支持定义元素:" + classElement.getName());
            }

            if (classDefinition == null) {
                continue;
            }

            parsedClasses.add(classDefinition);

            classDefinition.setDefinitionFile(definitionFile.getName());
            classDefinition.setDefinitionText(classElement.asXML());

            boolean packageUnwrap = rootElement.attributeValue("unwrap", packageName).trim().equals("true");
            if (!packageUnwrap) {
                classDefinition.setPackageName(packageName);
                classDefinition.getPackageNames().putAll(languagePackageNames);
            }

            classDefinition.setComment(getComment(classElement, index));

            parseClassChildren(classDefinition, classElement);
        }
    }

    /**
     * 提取注释
     */
    protected String getComment(Element element, int indexInParent) {
        List<Node> nodes = new ArrayList<>();
        nodes.add(element.node(0));
        nodes.add(element.getParent().node(indexInParent + 1));

        StringBuilder builder = new StringBuilder();

        for (Node node : nodes) {
            if (node instanceof Text) {
                String text = node.getText();
                if (!text.startsWith("\n")) {
                    text = text.trim().split("\n")[0] + "，";
                    builder.append(text.trim());
                }
            }
        }

        if (builder.length() > 0) {
            return builder.substring(0, builder.length() - 1);
        } else {
            return "";
        }
    }


    private ClassDefinition parseClassDefinition(Element element, int index) {
        ClassDefinition classDefinition = createClassDefinition(element, index);

        if (classDefinition != null) {
            classDefinition.setParser(getDefinitionParser());
            classDefinition.setCategory(getCategory());
            classDefinition.setName(element.attributeValue("name"));
            classDefinition.setLang(element.attributeValue("lang"));
        }

        return classDefinition;
    }

    protected ClassDefinition createClassDefinition(Element element, int index) {
        switch (element.getName()) {
            case "enum":
                return new EnumDefinition();
            case "bean":
                String beanCategory = element.attributeValue("category");
                if (StringUtils.isBlank(beanCategory) || beanCategory.equals(this.category.name())) {
                    return new BeanDefinition(element.attributeValue("parent"), element.attributeValue("delimiter"));
                }
            case "message":
                if (this.category == Category.message) {
                    return new MessageDefinition(element.attributeValue("id"));
                }
            case "data":
                if (this.category == Category.data) {
                    return new DataDefinition(element.attributeValue("id"));
                }
            case "config":
                if (this.category == Category.config) {
                    return new ConfigDefinition(element.attributeValue("table"), element.attributeValue("parent"));
                }
        }

        throw new IllegalArgumentException();
    }

    protected DefinitionParser getDefinitionParser() {
        return this;
    }

    private void parseClassChildren(ClassDefinition classDefinition, Element classElement) {
        for (int index = 0; index < classElement.nodeCount(); index++) {
            if (!(classElement.node(index) instanceof Element)) {
                continue;
            }

            Element childElement = (Element) classElement.node(index);
            String childName = childElement.getName();

            if (childName.equals("field")) {
                parseField(classDefinition, childElement, index);
                continue;
            }

            if (classDefinition instanceof ConfigDefinition) {
                ConfigDefinition configDefinition = (ConfigDefinition) classDefinition;
                if (childName.equals("index")) {
                    configDefinition.addIndex(parseIndex(childElement, classElement, index));
                }
                if (childName.equals("constant")) {
                    parseConstant(childElement, configDefinition, classElement, index);
                }
                continue;
            }

            if (classDefinition instanceof DataDefinition && childName.equals("index")) {
                DataDefinition dataDefinition = (DataDefinition) classDefinition;
                dataDefinition.addIndex(parseIndex(childElement, classElement, index));
                continue;
            }

            if (category == Category.config && classDefinition instanceof BeanDefinition && childName.equals("bean")) {
                BeanDefinition beanDefinition = (BeanDefinition) parseClassDefinition(childElement, index);
                beanDefinition.setParentName(classDefinition.getName());
                parsedClasses.add(beanDefinition);

                beanDefinition.setDefinitionFile(classDefinition.getDefinitionFile());
                beanDefinition.setDefinitionText(classDefinition.getDefinitionText());
                beanDefinition.setPackageName(classDefinition.getPackageName());
                beanDefinition.getPackageNames().putAll(classDefinition.getPackageNames());
                beanDefinition.setExcludeLanguage(classDefinition.isExcludeLanguage());
                beanDefinition.getLanguages().addAll(classDefinition.getLanguages());
                beanDefinition.setComment(getComment(childElement, index));

                parseClassChildren(beanDefinition, childElement);
            } else {
                addValidatedError("定义文件[" + classDefinition.getDefinitionFile() + "]不支持定义元素:" + classElement.getName());
            }
        }
    }

    private void parseField(ClassDefinition classDefinition, Element fieldElement, int index) {
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

        fieldDefinition.setComment(getComment(fieldElement, index));

        classDefinition.addField(fieldDefinition);
    }

    protected IndexDefinition parseIndex(Element indexElement, Element classElement, int index) {
        IndexDefinition indexDefinition = new IndexDefinition();

        indexDefinition.setParser(getDefinitionParser());
        indexDefinition.setCategory(getCategory());

        indexDefinition.setName(indexElement.attributeValue("name"));
        indexDefinition.setType(indexElement.attributeValue("type"));
        indexDefinition.setFieldNames(indexElement.attributeValue("fields"));

        if (classElement != null) {
            indexDefinition.setComment(getComment(indexElement, index));
        }

        return indexDefinition;
    }

    protected ConstantDefinition parseConstant(Element constantElement, ConfigDefinition configDefinition, Element classElement, int index) {
        ConstantDefinition constantDefinition = new ConstantDefinition();

        constantDefinition.setParser(getDefinitionParser());
        constantDefinition.setCategory(getCategory());

        constantDefinition.setName(constantElement.attributeValue("name"));
        constantDefinition.setUseEnum(constantElement.attributeValue("enum"));
        constantDefinition.setKeyField(constantElement.attributeValue("key"));
        constantDefinition.setValueField(constantElement.attributeValue("value"));
        constantDefinition.setCommentField(constantElement.attributeValue("comment"));
        constantDefinition.setDefinitionText(constantElement.asXML());


        if (configDefinition != null) {
            constantDefinition.setOwnerDefinition(configDefinition);
        }

        if (classElement != null) {
            constantDefinition.setComment(getComment(constantElement, index));
        }

        parsedClasses.add(constantDefinition);

        return constantDefinition;
    }

}
