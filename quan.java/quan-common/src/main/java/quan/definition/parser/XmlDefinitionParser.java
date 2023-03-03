package quan.definition.parser;

import org.apache.commons.codec.digest.DigestUtils;
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
import java.util.*;
import java.util.regex.Pattern;

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
            if (rootElement == null || !rootElement.getName().equals("package") && !rootElement.getName().equals("classes")) {
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

        //默认以定义文件路径名作为包名
        String packageName = definitionFilePath.substring(0, definitionFilePath.lastIndexOf(".")).replaceAll(String.format("\\%s", File.separator), ".");
        if (!Constants.LOWER_PACKAGE_NAME_PATTERN.matcher(packageName).matches()) {
            addValidatedError("定义文件[" + definitionFilePath + "]的路径格式错误");
        }

        //具体语言对应的包名
        Map<String, String> languagePackageNames = new HashMap<>();

        if (rootElement.getName().equals("package")) {
            validateElementAttributes(definitionFilePath, rootElement, "name", Pattern.compile("(" + String.join("|", Language.names()) + ")-name"));

            String packageName0 = rootElement.attributeValue("name");
            if (packageName0 != null && Constants.LOWER_PACKAGE_NAME_PATTERN.matcher(packageName0).matches()) {
                packageName = packageName0;
            } else if (!StringUtils.isBlank(packageName0)) {
                addValidatedError("定义文件[" + definitionFilePath + "]的包名[" + packageName0 + "]格式错误,正确格式:" + Constants.LOWER_PACKAGE_NAME_PATTERN);
            }

            for (Attribute attribute : rootElement.attributes()) {
                String attrName = attribute.getName();
                String attrValue = attribute.getValue();
                if (!attrName.endsWith("-name")) {
                    continue;
                }

                Language language;
                try {
                    language = Language.valueOf(attrName.substring(0, attrName.indexOf("-name")));
                } catch (IllegalArgumentException e) {
                    continue;
                }
                if (!language.matchPackageName(attrValue)) {
                    addValidatedError("定义文件[" + definitionFile + "]自定义语言[" + language + "]包名[" + attrValue + "]格式错误,正确格式:" + language.getPackageNamePattern());
                    continue;
                }

                languagePackageNames.put(language.name(), attrValue);
            }
        } else {
            validateElementAttributes(definitionFilePath, rootElement);
            if (packageName.contains(".")) {
                packageName = packageName.substring(0, packageName.lastIndexOf("."));
            } else {
                packageName = null;
            }
        }

        for (int index = 0; index < rootElement.nodeCount(); index++) {
            if (!(rootElement.node(index) instanceof Element)) {
                continue;
            }

            Element classElement = (Element) rootElement.node(index);

            ClassDefinition classDefinition = null;
            try {
                classDefinition = parseClassDefinition(definitionFilePath, classElement, index);
            } catch (IllegalArgumentException e) {
                addValidatedError("定义文件[" + definitionFile + "]不支持定义元素:" + classElement.getName());
            }

            if (classDefinition == null) {
                continue;
            }

            parsedClasses.add(classDefinition);

            if (packageName != null) {
                classDefinition.setPackageName(packageName);
                classDefinition.getPackageNames().putAll(languagePackageNames);
            }

            parseClassChildren(classDefinition, classElement);
        }
    }

    private void validateElementAttributes(String definitionFile, Element element, Collection<Object> legalAttributes) {
        List<String> illegalAttributes = new ArrayList<>();

        outer:
        for (int i = 0; i < element.attributeCount(); i++) {
            String attrName = element.attribute(i).getName();
            if (legalAttributes != null) {
                for (Object legalAttribute : legalAttributes) {
                    if (legalAttribute instanceof Pattern && ((Pattern) legalAttribute).matcher(attrName).matches()
                            || legalAttribute instanceof String && attrName.equals(legalAttribute)) {
                        continue outer;
                    }
                }
            }
            illegalAttributes.add(attrName);
        }

        if (!illegalAttributes.isEmpty()) {
            addValidatedError(String.format("定义文件[%s]的元素[%s]不支持属性%s", definitionFile, element.getUniquePath().substring(1), illegalAttributes));
        }
    }

    private void validateElementAttributes(String definitionFile, Element element, Object... legalAttributes) {
        validateElementAttributes(definitionFile, element, Arrays.asList(legalAttributes));
    }

    /**
     * 提取注释
     */
    protected String getComment(Element element, int indexInParent) {
        if (!element.isRootElement() && element.getParent().isRootElement()) {
            List<String> list = new ArrayList<>();
            for (int i = indexInParent - 1; i >= 0; i--) {
                Node node = element.getParent().node(i);
                if (node instanceof Element) {
                    break;
                } else {
                    list.add(node.getText());
                }
            }

            StringBuilder builder = new StringBuilder();

            for (int i = list.size() - 1; i >= 0; i--) {
                builder.append(list.get(i).replaceAll("[\t ]", ""));
            }

            if (StringUtils.isBlank(builder)) {
                return null;
            }

            int start = builder.lastIndexOf("\n\n") + 2;
            if (start < 2) {
                start = 0;
            }

            String comment = builder.substring(start);
            if (StringUtils.isBlank(comment)) {
                return null;
            }

            if (comment.endsWith("\n")) {
                comment = comment.substring(0, comment.length() - 1);
            }
            comment = comment.replace("\n", "，");

            return comment;
        }

        Node commentNode = null;

        if (element.nodeCount() > 0) {
            commentNode = element.node(0);
        } else if (element.getParent().nodeCount() > indexInParent + 1) {
            commentNode = element.getParent().node(indexInParent + 1);
        }

        if (commentNode instanceof Text) {
            String text = commentNode.getText();
            if (!text.startsWith("\n")) {
                return text.trim().split("\n")[0];
            }
        }

        return null;
    }

    private ClassDefinition parseClassDefinition(String definitionFile, Element element, int indexInParent) {
        ClassDefinition classDefinition = createClassDefinition(definitionFile, element);

        if (classDefinition != null) {
            classDefinition.setParser(getDefinitionParser());
            classDefinition.setCategory(getCategory());
            classDefinition.setName(element.attributeValue("name"));
            classDefinition.setLanguageStr(element.attributeValue("lang"));
            classDefinition.setComment(getComment(element, indexInParent));
            classDefinition.setDefinitionFile(definitionFile);
            classDefinition.setVersion(DigestUtils.md5Hex(element.asXML()).trim());
        }

        return classDefinition;
    }

    protected ClassDefinition createClassDefinition(String definitionFile, Element element) {
        switch (element.getName()) {
            case "enum":
                validateElementAttributes(definitionFile, element, "name");
                return new EnumDefinition();
            case "bean":
                if (this.category == Category.config) {
                    if (element.attribute("parent") != null) {
                        validateElementAttributes(definitionFile, element, "name", "parent");
                    } else {
                        validateElementAttributes(definitionFile, element, "name", "delimiter");
                    }
                } else {
                    validateElementAttributes(definitionFile, element, "name");
                }
                String beanCategory = element.attributeValue("category");
                if (StringUtils.isBlank(beanCategory) || beanCategory.equals(this.category.name())) {
                    return new BeanDefinition(element.attributeValue("parent"), element.attributeValue("delimiter"));
                }
            case "message":
                if (this.category == Category.message) {
                    validateElementAttributes(definitionFile, element, "name", "id", "lang");
                    return new MessageDefinition(element.attributeValue("id"));
                }
            case "data":
                if (this.category == Category.data) {
                    validateElementAttributes(definitionFile, element, "name", "id");
                    return new DataDefinition(element.attributeValue("id"));
                }
            case "config":
                if (this.category == Category.config) {
                    validateElementAttributes(definitionFile, element, "name", "table", "lang", "parent");
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
                    configDefinition.addIndex(parseIndex(classDefinition, childElement, index));
                    continue;
                }
                if (childName.equals("constant")) {
                    parseConstant(configDefinition, childElement, index);
                    continue;
                }
            }

            if (classDefinition instanceof DataDefinition && childName.equals("index")) {
                DataDefinition dataDefinition = (DataDefinition) classDefinition;
                dataDefinition.addIndex(parseIndex(classDefinition, childElement, index));
                continue;
            }

            if (category == Category.config && classDefinition instanceof BeanDefinition && childName.equals("bean")) {
                BeanDefinition beanDefinition = (BeanDefinition) parseClassDefinition(classDefinition.getDefinitionFile(), childElement, index);
                beanDefinition.setParentName(classDefinition.getName());
                parsedClasses.add(beanDefinition);

                beanDefinition.setPackageName(classDefinition.getPackageName());
                beanDefinition.getPackageNames().putAll(classDefinition.getPackageNames());

                parseClassChildren(beanDefinition, childElement);

                continue;
            }

            addValidatedError("定义文件[" + classDefinition.getDefinitionFile() + "]中的元素[" + classElement.getName() + "]不支持定义子元素:" + childName);
        }
    }

    protected FieldDefinition parseField(ClassDefinition classDefinition, Element fieldElement, int indexInParent) {
        FieldDefinition fieldDefinition = new FieldDefinition();
        fieldDefinition.setParser(classDefinition.getParser());
        fieldDefinition.setCategory(category);
        classDefinition.addField(fieldDefinition);

        fieldDefinition.setName(fieldElement.attributeValue("name"));
        String typeInfo = fieldElement.attributeValue("type");
        fieldDefinition.setTypeInfo(typeInfo);
        fieldDefinition.setMin(fieldElement.attributeValue("min"));
        fieldDefinition.setMax(fieldElement.attributeValue("max"));
        fieldDefinition.setValue(fieldElement.attributeValue("value"));
        fieldDefinition.setColumn(fieldElement.attributeValue("column"));
        fieldDefinition.setOptional(fieldElement.attributeValue("optional"));
        fieldDefinition.setId(fieldElement.attributeValue("id"));
        fieldDefinition.setIgnore(fieldElement.attributeValue("ignore"));
        fieldDefinition.setIndex(fieldElement.attributeValue("index"));
        fieldDefinition.setDelimiter(fieldElement.attributeValue("delimiter"));
        fieldDefinition.setRef(fieldElement.attributeValue("ref"));
        fieldDefinition.setComment(getComment(fieldElement, indexInParent));
        fieldDefinition.setLanguageStr(fieldElement.attributeValue("lang"));

        String type = typeInfo == null ? null : typeInfo.split("[:：]")[0];

        List<Object> illegalAttributes = new ArrayList<>(Collections.singleton("name"));

        if (classDefinition instanceof BeanDefinition && type != null && Constants.NUMBER_TYPES.contains(type)) {
            illegalAttributes.addAll(Arrays.asList("min", "max"));
        }

        if (classDefinition instanceof EnumDefinition) {
            illegalAttributes.add("value");
        } else if (category == Category.data) {
            illegalAttributes.addAll(Arrays.asList("type", "ignore"));
        } else if (category == Category.config) {
            if (classDefinition instanceof ConfigDefinition) {
                illegalAttributes.addAll(Arrays.asList("type", "ref", "lang", "optional", "column"));
                if (type != null && !Constants.COLLECTION_TYPES.contains(type) && !Constants.TIME_TYPES.contains(type)) {
                    //只支持原生类型和枚举类型，但是在这里没法判断是不是枚举类型
                    illegalAttributes.add("index");
                }
            } else if (classDefinition instanceof BeanDefinition) {
                illegalAttributes.addAll(Arrays.asList("type", "ref", "optional"));
            }
            if (type != null && Constants.COLLECTION_TYPES.contains(type)) {
                illegalAttributes.add("delimiter");
            }
        } else if (category == Category.message) {
            illegalAttributes.addAll(Arrays.asList("id", "type", "ignore", "optional"));
        }

        validateElementAttributes(classDefinition.getDefinitionFile(), fieldElement, illegalAttributes);

        return fieldDefinition;
    }

    protected IndexDefinition parseIndex(ClassDefinition classDefinition, Element indexElement, int indexInParent) {
        validateElementAttributes(classDefinition.getDefinitionFile(), indexElement, "name", "type", "fields");

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

    protected void parseConstant(ConfigDefinition configDefinition, Element constantElement, int indexInParent) {
        validateElementAttributes(configDefinition.getDefinitionFile(), constantElement, "name", "enum", "key", "value", "comment");

        ConstantDefinition constantDefinition = new ConstantDefinition();
        constantDefinition.setParser(getDefinitionParser());
        constantDefinition.setCategory(getCategory());

        constantDefinition.setName(constantElement.attributeValue("name"));
        constantDefinition.setUseEnum(constantElement.attributeValue("enum"));
        constantDefinition.setKeyField(constantElement.attributeValue("key"));
        constantDefinition.setValueField(constantElement.attributeValue("value"));
        constantDefinition.setCommentField(constantElement.attributeValue("comment"));
        constantDefinition.setVersion(DigestUtils.md5Hex(constantElement.asXML().trim()));

        if (indexInParent >= 0) {
            constantDefinition.setComment(getComment(constantElement, indexInParent));
        }

        parsedClasses.add(constantDefinition);

        constantDefinition.setOwnerDefinition(configDefinition);
    }

}
