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
import java.util.List;
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

    protected List<ClassDefinition> definitions;

    protected Configuration freemarkerCfg;

    protected Map<Class<? extends ClassDefinition>, Template> templates = new HashMap<>();

    public Generator(String srcPath, String destPath) throws Exception {
        Configuration freemarkerCfg = new Configuration(Configuration.VERSION_2_3_23);
        freemarkerCfg.setClassForTemplateLoading(getClass(), "");
        freemarkerCfg.setDefaultEncoding("UTF-8");
        freemarkerCfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        this.srcPath = srcPath;
        this.destPath = destPath;
        this.freemarkerCfg = freemarkerCfg;
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

    protected abstract String getLanguage();

    protected void generate() throws Exception {
        this.definitions = new Parser(srcPath, packagePrefix).parse();

        for (ClassDefinition definition : definitions) {
            if (definition instanceof BeanDefinition) {
                BeanDefinition beanDefinition = (BeanDefinition) definition;
                processBean(beanDefinition);
            }
        }

        for (ClassDefinition definition : definitions) {
            Template template = templates.get(definition.getClass());
            String packageName = definition.getPackageName();

            String packagePath = packageName.replace(".", "\\");
            File destFilePath = new File(destPath + "\\" + packagePath);
            if (!destFilePath.exists()) {
                destFilePath.mkdirs();
            }

            String fileName = definition.getName() + "." + getLanguage();
            Writer writer = new FileWriter(new File(destFilePath, fileName));
            template.process(definition, writer);

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

        if (fieldType.equals("set") || fieldType.equals("list") || fieldType.equals("map")) {
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
    }

}
