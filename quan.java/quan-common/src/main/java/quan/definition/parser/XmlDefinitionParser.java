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
    public int getMinTableBodyStartRow() {
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

        String definitionFilePath = getDefinitionParser().definitionFilePaths.get(definitionFile);
        String definitionFileName = definitionFilePath.substring(0, definitionFilePath.lastIndexOf("."));

        //默认以定义文件路径名作为包名
        String packageName = definitionFileName.replaceAll(String.format("\\%s", File.separator), ".");
        if (!Constants.LOWER_PACKAGE_NAME_PATTERN.matcher(packageName).matches()) {
            addValidatedError("定义文件[" + definitionFilePath + "]的路径格式错误");
        }else {
            packageName = rootElement.attributeValue("name", packageName);
            if (!Constants.LOWER_PACKAGE_NAME_PATTERN.matcher(packageName).matches()) {
                addValidatedError("定义文件[" + definitionFilePath + "]的包名[" + packageName + "]格式错误,正确格式:" + Constants.LOWER_PACKAGE_NAME_PATTERN);
            }
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
                addValidatedError("定义文件[" + definitionFile + "]不支持定义元素:" + classElement.getName());
            }

            if (classDefinition == null) {
                continue;
            }

            parsedClasses.add(classDefinition);

            classDefinition.setDefinitionFile(definitionFilePath);
            classDefinition.setDefinitionText(classElement.asXML());

            boolean packageUnwrap = rootElement.attributeValue("unwrap", packageName).trim().equals("true");
            if (!packageUnwrap) {
                classDefinition.setPackageName(packageName);
                classDefinition.getPackageNames().putAll(languagePackageNames);
            }

            parseClassChildren(classDefinition, classElement);
        }
    }

    /**
     * 提取注释
     */
    protected String getComment(Element element, int indexInParent) {
        List<Node> nodes = new ArrayList<>();
        nodes.add(element.node(0));
        if (element.nodeCount() == 0) {
            nodes.add(element.getParent().node(indexInParent + 1));
        }

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


    private ClassDefinition parseClassDefinition(Element element, int indexInParent) {
        ClassDefinition classDefinition = createClassDefinition(element);

        if (classDefinition != null) {
            classDefinition.setParser(getDefinitionParser());
            classDefinition.setCategory(getCategory());
            classDefinition.setName(element.attributeValue("name"));
            classDefinition.setLang(element.attributeValue("lang"));
            classDefinition.setComment(getComment(element, indexInParent));
        }

        return classDefinition;
    }

    protected ClassDefinition createClassDefinition(Element element) {
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

    protected void parseClassChildren(ClassDefinition classDefinition, Element classElement) {
        for (int index = 0; index < classElement.nodeCount(); index++) {
            if (!(classElement.node(index) instanceof Element)) {
                continue;
            }

            Element childElement = (Element) classElement.node(index);
            String childName = childElement.getName();

            if (childName.equals("field") && parseField(classDefinition, childElement, index) != null) {
                continue;
            }

            if (classDefinition instanceof ConfigDefinition) {
                ConfigDefinition configDefinition = (ConfigDefinition) classDefinition;
                if (childName.equals("index")) {
                    configDefinition.addIndex(parseIndex(childElement, index));
                    continue;
                }
                if (childName.equals("constant")) {
                    parseConstant(childElement, index).setOwnerDefinition(configDefinition);
                    continue;
                }
            }

            if (classDefinition instanceof DataDefinition && childName.equals("index")) {
                DataDefinition dataDefinition = (DataDefinition) classDefinition;
                dataDefinition.addIndex(parseIndex(childElement, index));
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

                parseClassChildren(beanDefinition, childElement);

                continue;
            }

            addValidatedError("定义文件[" + classDefinition.getDefinitionFile() + "]中的元素[" + classElement.getName() + "]不支持定义子元素:" + childName);
        }
    }

    protected FieldDefinition parseField(ClassDefinition classDefinition, Element fieldElement, int indexInParent) {
        FieldDefinition fieldDefinition = new FieldDefinition();
        fieldDefinition.setParser(classDefinition.getParser());
        fieldDefinition.setCategory(getCategory());

        fieldDefinition.setName(fieldElement.attributeValue("name"));
        fieldDefinition.setTypeInfo(fieldElement.attributeValue("type"));
        fieldDefinition.setValue(fieldElement.attributeValue("value"));
        fieldDefinition.setColumn(fieldElement.attributeValue("column"));
        fieldDefinition.setOptional(fieldElement.attributeValue("optional"));
        fieldDefinition.setId(fieldElement.attributeValue("id"));
        fieldDefinition.setIgnore(fieldElement.attributeValue("ignore"));
        fieldDefinition.setIndex(fieldElement.attributeValue("index"));
        fieldDefinition.setDelimiter(fieldElement.attributeValue("delimiter"));
        fieldDefinition.setRef(fieldElement.attributeValue("ref"));
        fieldDefinition.setComment(getComment(fieldElement, indexInParent));

        if (classDefinition instanceof ConfigDefinition) {
            fieldDefinition.setLanguage(fieldElement.attributeValue("lang"));
        }

        classDefinition.addField(fieldDefinition);

        return fieldDefinition;
    }

    protected IndexDefinition parseIndex(Element indexElement, int indexInParent) {
        IndexDefinition indexDefinition = new IndexDefinition();

        indexDefinition.setParser(getDefinitionParser());
        indexDefinition.setCategory(getCategory());

        indexDefinition.setName(indexElement.attributeValue("name"));
        indexDefinition.setType(indexElement.attributeValue("type"));
        indexDefinition.setFieldNames(indexElement.attributeValue("fields"));

        if (indexInParent >= 0) {
            indexDefinition.setComment(getComment(indexElement, indexInParent));
        }

        return indexDefinition;
    }

    protected ConstantDefinition parseConstant(Element constantElement, int indexInParent) {
        ConstantDefinition constantDefinition = new ConstantDefinition();

        constantDefinition.setParser(getDefinitionParser());
        constantDefinition.setCategory(getCategory());

        constantDefinition.setName(constantElement.attributeValue("name"));
        constantDefinition.setUseEnum(constantElement.attributeValue("enum"));
        constantDefinition.setKeyField(constantElement.attributeValue("key"));
        constantDefinition.setValueField(constantElement.attributeValue("value"));
        constantDefinition.setCommentField(constantElement.attributeValue("comment"));
        constantDefinition.setDefinitionText(constantElement.asXML());

        if (indexInParent >= 0) {
            constantDefinition.setComment(getComment(constantElement, indexInParent));
        }

        parsedClasses.add(constantDefinition);

        return constantDefinition;
    }

}
