package quan.generator;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by quanchangnai on 2019/6/23.
 */
public abstract class Generator {

    protected Map<String, String> basicTypes = new HashMap<>();

    protected Map<String, String> classTypes = new HashMap<>();

    {
        basicTypes.put("bool", "boolean");
        basicTypes.put("int", "int");
        basicTypes.put("string", "String");
        basicTypes.put("byte", "byte");
        basicTypes.put("short", "short");
        basicTypes.put("long", "long");
        basicTypes.put("float", "float");
        basicTypes.put("double", "double");
        basicTypes.put("set", "Set");
        basicTypes.put("list", "List");
        basicTypes.put("map", "Map");

        classTypes.put("bool", "Boolean");
        classTypes.put("int", "Integer");
        classTypes.put("string", "String");
        classTypes.put("byte", "Byte");
        classTypes.put("short", "Short");
        classTypes.put("long", "Long");
        classTypes.put("float", "Float");
        classTypes.put("double", "Double");
        classTypes.put("set", "HashSet");
        classTypes.put("list", "ArrayList");
        classTypes.put("map", "HashMap");
    }

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected String srcPath;

    protected String destPath;

    protected String packagePrefix;

    protected Configuration freemarkerCfg;

    protected Map<Class<? extends ClassDefinition>, Template> templates = new HashMap<>();

    protected Parser parser = new XmlParser();

    public Generator(String srcPath, String destPath) throws Exception {
        Configuration freemarkerCfg = new Configuration(Configuration.VERSION_2_3_23);
        freemarkerCfg.setClassForTemplateLoading(Generator.class, "");
        freemarkerCfg.setDefaultEncoding("UTF-8");
        freemarkerCfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        this.srcPath = srcPath;
        this.destPath = destPath;
        this.freemarkerCfg = freemarkerCfg;

        Template enumTemplate = freemarkerCfg.getTemplate("enum." + getLanguage() + ".ftl");
        templates.put(EnumDefinition.class, enumTemplate);

        freemarkerCfg.setClassForTemplateLoading(getClass(), "");
    }


    public String getPackagePrefix() {
        return packagePrefix;
    }

    public Generator setPackagePrefix(String packagePrefix) {
        if (packagePrefix == null && packagePrefix.trim().equals("")) {
            return this;
        }
        this.packagePrefix = packagePrefix;
        return this;
    }

    public Generator setParser(Parser parser) {
        this.parser = parser;
        return this;
    }

    protected abstract String getLanguage();

    protected boolean support(ClassDefinition classDefinition) {
        if (classDefinition instanceof BeanDefinition || classDefinition instanceof EnumDefinition) {
            return true;
        }
        return false;
    }

    public final void generate() throws Exception {
        parser.setPackagePrefix(packagePrefix);
        parser.setSrcPath(srcPath);

        parser.parse();

        for (ClassDefinition classDefinition : ClassDefinition.getAll().values()) {
            if (!support(classDefinition)) {
                continue;
            }
            if (classDefinition instanceof BeanDefinition) {
                BeanDefinition beanDefinition = (BeanDefinition) classDefinition;
                processBean(beanDefinition);
            }
        }

        for (ClassDefinition classDefinition : ClassDefinition.getAll().values()) {
            if (!support(classDefinition)) {
                continue;
            }

            Template template = templates.get(classDefinition.getClass());
            String packageName = classDefinition.getPackageName();

            String packagePath = packageName.replace(".", "\\");
            File destFilePath = new File(destPath + "\\" + packagePath);
            if (!destFilePath.exists()) {
                destFilePath.mkdirs();
            }

            String fileName = classDefinition.getName() + "." + getLanguage();
            Writer writer = new FileWriter(new File(destFilePath, fileName));
            template.process(classDefinition, writer);

            logger.info("生成[{}]成功", fileName);
        }

    }


    protected void processBean(BeanDefinition beanDefinition) {
        for (FieldDefinition fieldDefinition : beanDefinition.getFields()) {
            processField(fieldDefinition);
        }
    }

    protected void processField(FieldDefinition fieldDefinition) {
        String fieldType = fieldDefinition.getType();
        if (FieldDefinition.BUILT_IN_TYPES.contains(fieldType)) {
            fieldDefinition.setBasicType(basicTypes.get(fieldType));
            fieldDefinition.setClassType(classTypes.get(fieldType));
        }

        if (fieldDefinition.isCollectionType()) {
            if (fieldType.equals("map")) {
                String fieldKeyType = fieldDefinition.getKeyType();
                fieldDefinition.setBasicKeyType(basicTypes.get(fieldKeyType));
                fieldDefinition.setClassKeyType(classTypes.get(fieldKeyType));
            }

            String fieldValueType = fieldDefinition.getValueType();
            if (FieldDefinition.BUILT_IN_TYPES.contains(fieldValueType)) {
                fieldDefinition.setBasicValueType(basicTypes.get(fieldValueType));
                fieldDefinition.setClassValueType(classTypes.get(fieldValueType));
            }
        }

        BeanDefinition beanDefinition = fieldDefinition.getBeanDefinition();

        ClassDefinition fieldTypeClassDefinition = ClassDefinition.getAll().get(fieldDefinition.getType());
        if (fieldTypeClassDefinition != null) {
            if (!fieldTypeClassDefinition.getPackageName().equals(beanDefinition.getPackageName())) {
                beanDefinition.getImports().add(fieldTypeClassDefinition.getFullName());
            }
            if (fieldTypeClassDefinition instanceof EnumDefinition) {
                fieldDefinition.setEnumType(true);
            }
        }

        ClassDefinition fieldValueTypeClassDefinition = ClassDefinition.getAll().get(fieldDefinition.getValueType());
        if (fieldValueTypeClassDefinition != null && !fieldValueTypeClassDefinition.getPackageName().equals(beanDefinition.getPackageName())) {
            beanDefinition.getImports().add(fieldValueTypeClassDefinition.getFullName());
        }

    }

}
