package quan.protocol.generator;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by quanchangnai on 2017/7/10.
 */
public class Parser {

    private String mainSrcFilePath;

    private List<File> srcFiles = new ArrayList<>();

    private List<Definition> results = new ArrayList<>();

    private Map<String, String> enums = new HashMap<>();

    public Parser(String mainSrcFile) {
        this.mainSrcFilePath = mainSrcFile;
    }


    public List<Definition> parse() throws Exception {
        File mainSrcFile = new File(mainSrcFilePath);
        srcFiles.add(mainSrcFile);

        parseImport(mainSrcFile);

        for (File srcFile : srcFiles) {
            parseEnum(srcFile);
        }
        for (File srcFile : srcFiles) {
            parseBean(srcFile);
        }
        return results;
    }

    private void parseImport(File srcFile) throws Exception {
        Document document = new SAXReader().read(srcFile);
        Element root = document.getRootElement();
        for (int i = 0; i < root.nodeCount(); i++) {
            Node node = root.node(i);
            if (node instanceof Element) {
                Element element = (Element) node;
                if (element.getName().equals("import")) {
                    String importFilePath = element.attributeValue("file");
                    File importFile = new File(srcFile.getParent(), importFilePath);
                    if (!srcFiles.contains(importFile)) {
                        srcFiles.add(importFile);
                        parseImport(importFile);
                    }
                }
            }
        }
    }


    private void parseEnum(File srcFile) throws Exception {
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
                if (element.getName().equals("enum")) {
                    EnumDefinition enumDefinition = parseEnum(element);
                    enumDefinition.setPackageName(packageName);
                    String comment1 = root.node(i - 1).getText();
                    comment1 = comment1.replaceAll("\r|\n", "").trim();
                    String comment2 = element.node(0).getText();
                    comment2 = comment2.replaceAll("\r|\n", "").trim();
                    if (!comment1.equals("")) {
                        enumDefinition.setComment(comment1);
                    } else if (!comment2.equals("")) {
                        enumDefinition.setComment(comment2);
                    }
                    results.add(enumDefinition);

                    enums.put(enumDefinition.getName(), packageName + "." + enumDefinition.getName());
                }
            }
        }


    }

    private void parseBean(File srcFile) throws Exception {
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
                if (element.getName().equals("bean") || element.getName().equals("protocol")) {
                    BeanDefinition beanDefinition = parseBean(element);
                    beanDefinition.setPackageName(packageName);
                    String comment1 = root.node(i - 1).getText();
                    comment1 = comment1.replaceAll("\r|\n", "").trim();
                    String comment2 = element.node(0).getText();
                    comment2 = comment2.replaceAll("\r|\n", "").trim();
                    if (!comment1.equals("")) {
                        beanDefinition.setComment(comment1);
                    } else if (!comment2.equals("")) {
                        beanDefinition.setComment(comment2);
                    }
                    results.add(beanDefinition);
                }
            }
        }

    }


    private EnumDefinition parseEnum(Element element) {
        EnumDefinition enumDefinition = new EnumDefinition();
        enumDefinition.setName(element.attributeValue("name"));
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
                    if (!comment.trim().equals("")) {
                        fieldDefinition.setComment(comment);
                    }
                    enumDefinition.getFields().add(fieldDefinition);
                }
            }
        }
        return enumDefinition;
    }

    private BeanDefinition parseBean(Element element) {
        BeanDefinition beanDefinition;
        if (element.getName().equals("protocol")) {
            ProtocolDefinition protocolDefinition = new ProtocolDefinition();
            protocolDefinition.setId(element.attributeValue("id"));
            beanDefinition = protocolDefinition;
        } else {
            beanDefinition = new BeanDefinition();
        }

        beanDefinition.setName(element.attributeValue("name"));
        for (int i = 0; i < element.nodeCount(); i++) {
            Node node = element.node(i);
            if (node instanceof Element) {
                Element child = (Element) node;
                if (child.getName().equals("field")) {
                    FieldDefinition fieldDefinition = new FieldDefinition();
                    fieldDefinition.setName(child.attributeValue("name"));
                    fieldDefinition.setValue(child.attributeValue("value"));
                    fieldDefinition.setType(child.attributeValue("type"));
                    fieldDefinition.setKeyType(child.attributeValue("key-type"));
                    fieldDefinition.setValueType(child.attributeValue("value-type"));
                    fieldDefinition.setBeanDefinition(beanDefinition);
                    String comment = element.node(i + 1).getText();
                    comment = comment.replaceAll("\r|\n", "").trim();
                    if (!comment.trim().equals("")) {
                        fieldDefinition.setComment(comment);
                    }

                    if (enums.containsKey(fieldDefinition.getType()) || (enums.containsValue(fieldDefinition.getType()))) {
                        fieldDefinition.setEnumType(true);
                    }

                    beanDefinition.getFields().add(fieldDefinition);
                }
            }
        }

        return beanDefinition;
    }

}
