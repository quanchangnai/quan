package quan.protocol.generator;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.*;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public abstract class Generator {

    public static void main(String[] args) throws Exception {
        String srcPath = "protocol\\src\\test\\java\\quan\\protocol\\protocols.xml";
        String destPath = "protocol\\src\\test\\java";
        String language = "java";

        Generator generator = null;
        switch (language) {
            case "java":
                generator = new JavaGenerator(srcPath, destPath);
                break;
            default:
                break;
        }
        generator.generate();
    }

    public static void usage() {
        System.err.println("");
    }

    protected String srcFile;
    protected String destPath;
    protected Configuration cfg;
    protected List<Definition> definitions;


    public Generator(String srcFile, String destPath) throws Exception {

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
        cfg.setClassForTemplateLoading(JavaGenerator.class, "");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        this.srcFile = srcFile;
        this.destPath = destPath;
        this.cfg = cfg;
        this.definitions = parse(srcFile);

    }

    protected abstract String getLanguage();

    protected void preprocess(List<Definition> definitions) {
    }

    protected void generate() throws Exception {
        Template protocolTemplate = cfg.getTemplate("protocol." + getLanguage() + ".ftl");
        Template enumTemplate = cfg.getTemplate("enum." + getLanguage() + ".ftl");

        preprocess(definitions);

        for (Definition definition : definitions) {
            Template template;
            String packageName;
            if (definition instanceof BeanDefinition) {
                BeanDefinition beanDefinition = (BeanDefinition) definition;
                packageName = beanDefinition.getPackageName();
                template = protocolTemplate;
            } else {
                EnumDefinition enumDefinition = (EnumDefinition) definition;
                packageName = enumDefinition.getPackageName();
                template = enumTemplate;
            }

            String packagePath = packageName.replace(".", "\\");
            File destFilePath = new File(destPath + "\\" + packagePath);
            if (!destFilePath.exists()) {
                destFilePath.mkdir();
            }
            Writer file = new FileWriter(new File(destFilePath, definition.getName() + "." + getLanguage()));
            template.process(definition, file);
        }
    }


    public static List<Definition> parse(String srcFile) throws Exception {
        File file = new File(srcFile);
        Document document = new SAXReader().read(file);

        List<Definition> list = new ArrayList<>();
        Map<String, String> enums = new HashMap<>();

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
                    list.add(enumDefinition);

                    enums.put(enumDefinition.getName(), packageName + "." + enumDefinition.getName());
                }
            }
        }

        for (int i = 0; i < root.nodeCount(); i++) {
            Node node = root.node(i);
            if (node instanceof Element) {
                Element element = (Element) node;
                if (element.getName().equals("bean") || element.getName().equals("protocol")) {
                    BeanDefinition beanDefinition = parseBean(element, enums);
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
                    list.add(beanDefinition);
                }
            }
        }

        return list;
    }

    private static BeanDefinition parseBean(Element element, Map<String, String> enums) {
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

    private static EnumDefinition parseEnum(Element element) {
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
}
