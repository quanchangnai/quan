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
import java.util.stream.Collectors;

/**
 * Created by quanchangnai on 2019/6/23.
 */
public abstract class Generator {

    protected Map<String, String> basicTypes = new HashMap<>();

    protected Map<String, String> classTypes = new HashMap<>();

    {
        basicTypes.put("bool", "boolean");
        basicTypes.put("short", "short");
        basicTypes.put("int", "int");
        basicTypes.put("long", "long");
        basicTypes.put("float", "float");
        basicTypes.put("double", "double");
        basicTypes.put("string", "String");
        basicTypes.put("set", "Set");
        basicTypes.put("list", "List");
        basicTypes.put("map", "Map");

        classTypes.put("bool", "Boolean");
        classTypes.put("short", "Short");
        classTypes.put("int", "Integer");
        classTypes.put("long", "Long");
        classTypes.put("float", "Float");
        classTypes.put("double", "Double");
        classTypes.put("string", "String");
        classTypes.put("set", "HashSet");
        classTypes.put("list", "ArrayList");
        classTypes.put("map", "HashMap");
    }

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected Parser parser = new XmlParser();

    private List<String> srcPaths;

    private String destPath;

    private String packagePrefix;

    private String enumPackagePrefix;

    protected Configuration freemarkerCfg;

    protected Map<Class<? extends ClassDefinition>, Template> templates = new HashMap<>();

    public Generator(List<String> srcPaths, String destPath) throws Exception {
        Configuration freemarkerCfg = new Configuration(Configuration.VERSION_2_3_23);
        freemarkerCfg.setClassForTemplateLoading(Generator.class, "");
        freemarkerCfg.setDefaultEncoding("UTF-8");
        freemarkerCfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        this.srcPaths = srcPaths;
        this.destPath = destPath;
        this.freemarkerCfg = freemarkerCfg;

        Template enumTemplate = freemarkerCfg.getTemplate("enum." + supportLanguage() + ".ftl");
        templates.put(EnumDefinition.class, enumTemplate);

        freemarkerCfg.setClassForTemplateLoading(getClass(), "");
    }


    public String getPackagePrefix() {
        return packagePrefix;
    }

    public Generator setPackagePrefix(String packagePrefix) {
        this.packagePrefix = packagePrefix;
        return this;
    }

    public Generator setEnumPackagePrefix(String enumPackagePrefix) {
        this.enumPackagePrefix = enumPackagePrefix;
        return this;
    }

    public Generator setParser(Parser parser) {
        this.parser = parser;
        return this;
    }

    protected abstract Language supportLanguage();


    protected boolean support(ClassDefinition classDefinition) {
        return classDefinition instanceof BeanDefinition || classDefinition instanceof EnumDefinition;
    }

    public final void generate() throws Exception {
        parser.setPackagePrefix(packagePrefix);
        parser.setEnumPackagePrefix(enumPackagePrefix);
        parser.setSrcPaths(srcPaths);

        parser.parse();

        List<ClassDefinition> classDefinitions = ClassDefinition.getAll().values().stream().filter(this::support).collect(Collectors.toList());

        for (ClassDefinition classDefinition : classDefinitions) {
            processClassSelf(classDefinition);
        }

        for (ClassDefinition classDefinition : classDefinitions) {
            processClassDependency(classDefinition);
        }

        for (ClassDefinition classDefinition : classDefinitions) {
            if (!classDefinition.supportLanguage(supportLanguage())) {
                continue;
            }
            Template template = templates.get(classDefinition.getClass());
            String packageName = classDefinition.getPackageName();

            String packagePath = packageName.replace(".", "\\");
            File destFilePath = new File(destPath + "\\" + packagePath);
            if (!destFilePath.exists()) {
                destFilePath.mkdirs();
            }

            String fileName = classDefinition.getName() + "." + supportLanguage();
            Writer writer = new FileWriter(new File(destFilePath, fileName));
            template.process(classDefinition, writer);

            logger.info("生成[{}]成功", destFilePath + "\\" + fileName);
        }

    }

    protected void processClassSelf(ClassDefinition classDefinition) {

    }

    protected void processClassDependency(ClassDefinition classDefinition) {
        for (FieldDefinition fieldDefinition : classDefinition.getFields()) {
            processField(fieldDefinition);
        }
    }

    protected void processField(FieldDefinition fieldDefinition) {
        if (fieldDefinition.getClassDefinition() instanceof BeanDefinition) {
            processBeanField(fieldDefinition);
        }
    }

    protected void processBeanField(FieldDefinition fieldDefinition) {
        String fieldType = fieldDefinition.getType();
        if (fieldDefinition.isBuiltInType()) {
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
            if (fieldDefinition.isValueBuiltInType()) {
                fieldDefinition.setBasicValueType(basicTypes.get(fieldValueType));
                fieldDefinition.setClassValueType(classTypes.get(fieldValueType));
            }
        }

        processBeanFieldImports(fieldDefinition);

    }

    protected String resolveFieldImport(FieldDefinition fieldDefinition, boolean fieldSelf) {
        return fieldSelf ? fieldDefinition.getTypeWithPackage() : fieldDefinition.getValueTypeWithPackage();
    }

    protected String resolveClassImport(ClassDefinition classDefinition) {
        return classDefinition.getFullName();
    }

    protected void processBeanFieldImports(FieldDefinition fieldDefinition) {
        BeanDefinition beanDefinition = (BeanDefinition) fieldDefinition.getClassDefinition();

        ClassDefinition fieldTypeClassDefinition = ClassDefinition.getAll().get(fieldDefinition.getType());
        if (fieldTypeClassDefinition != null) {
            if (!fieldTypeClassDefinition.getPackageName().equals(beanDefinition.getPackageName())) {
                beanDefinition.getImports().add(resolveClassImport(fieldTypeClassDefinition));
            }
        } else if (fieldDefinition.isTypeWithPackage() && !fieldDefinition.getTypeWithPackage().equals(beanDefinition.getPackageName())) {
            beanDefinition.getImports().add(resolveFieldImport(fieldDefinition, true));
        }

        ClassDefinition fieldValueTypeClassDefinition = ClassDefinition.getAll().get(fieldDefinition.getValueType());
        if (fieldValueTypeClassDefinition != null && !fieldValueTypeClassDefinition.getPackageName().equals(beanDefinition.getPackageName())) {
            beanDefinition.getImports().add(resolveClassImport(fieldValueTypeClassDefinition));
        }
        if (fieldValueTypeClassDefinition == null && fieldDefinition.isValueTypeWithPackage() && !fieldDefinition.getValueTypePackage().equals(beanDefinition.getPackageName())) {
            beanDefinition.getImports().add(resolveFieldImport(fieldDefinition, false));
        }
    }

}
