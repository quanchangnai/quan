package quan.generator;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.common.util.PathUtils;
import quan.definition.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * Created by quanchangnai on 2019/6/23.
 */
public abstract class Generator {

    protected Map<String, String> basicTypes = new HashMap<>();

    protected Map<String, String> classTypes = new HashMap<>();

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected DefinitionParser definitionParser;

    private String codePath;

    protected Configuration freemarkerCfg;

    protected Map<Class<? extends ClassDefinition>, Template> templates = new HashMap<>();

    public Generator(String codePath) {
        Configuration freemarkerCfg = new Configuration(Configuration.VERSION_2_3_23);
        freemarkerCfg.setClassForTemplateLoading(Generator.class, "");
        freemarkerCfg.setDefaultEncoding("UTF-8");
        freemarkerCfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        this.codePath = PathUtils.currentPlatPath(codePath);
        this.freemarkerCfg = freemarkerCfg;

        Template enumTemplate;
        try {
            enumTemplate = freemarkerCfg.getTemplate("enum." + supportLanguage() + ".ftl");
        } catch (IOException e) {
            logger.error("", e);
            return;
        }
        templates.put(EnumDefinition.class, enumTemplate);

        freemarkerCfg.setClassForTemplateLoading(getClass(), "");
    }


    public abstract DefinitionCategory category();

    public DefinitionParser useXmlDefinitionParser(List<String> definitionPaths, String packagePrefix) {
        definitionParser = new XmlDefinitionParser();
        definitionParser.setCategory(category());
        definitionParser.setDefinitionPaths(definitionPaths);
        definitionParser.setPackagePrefix(packagePrefix);
        return definitionParser;
    }


    public DefinitionParser useXmlDefinitionParser(String definitionPath, String packagePrefix) {
        return useXmlDefinitionParser(Collections.singletonList(definitionPath), packagePrefix);
    }

    public Generator setDefinitionParser(DefinitionParser definitionParser) {
        definitionParser.setCategory(category());
        this.definitionParser = definitionParser;
        return this;
    }

    protected abstract Language supportLanguage();


    protected boolean support(ClassDefinition classDefinition) {
        return classDefinition instanceof BeanDefinition || classDefinition instanceof EnumDefinition;
    }

    protected void parseDefinition() {
        Objects.requireNonNull(definitionParser, "定义解析器不能为空");
        definitionParser.parse();
    }

    public final void generate() {
        //解析定义文件
        parseDefinition();
        List<String> validatedErrors = definitionParser.getValidatedErrors();

        if (!validatedErrors.isEmpty()) {
            System.err.println(String.format("生成%s代码失败，解析定义文件%s共发现%d条错误。", supportLanguage(), definitionParser.getDefinitionPaths(), validatedErrors.size()));
            for (int i = 1; i <= validatedErrors.size(); i++) {
                String validatedError = validatedErrors.get(i - 1);
                System.err.println(i + ":" + validatedError);
            }
            return;
        }

        List<ClassDefinition> classDefinitions = new ArrayList<>();
        for (ClassDefinition classDefinition : definitionParser.getClasses().values()) {
            if (!support(classDefinition) || !classDefinition.supportLanguage(supportLanguage())) {
                continue;
            }
            processClassSelf(classDefinition);
            classDefinitions.add(classDefinition);
        }

        for (ClassDefinition classDefinition : classDefinitions) {
            processClassDependency(classDefinition);
        }

        generate(classDefinitions);
    }

    protected void generate(List<ClassDefinition> classDefinitions) {
        for (ClassDefinition classDefinition : classDefinitions) {
            Template template = templates.get(classDefinition.getClass());
            File destFilePath = new File(codePath + File.separator + classDefinition.getFullPackageName().replace(".", File.separator));
            if (!destFilePath.exists() && !destFilePath.mkdirs()) {
                logger.info("创建目录[{}]失败", destFilePath);
                continue;
            }

            String fileName = classDefinition.getName() + "." + supportLanguage();
            try {
                Writer writer = new FileWriter(new File(destFilePath, fileName));
                template.process(classDefinition, writer);
            } catch (Exception e) {
                logger.error("", e);
                continue;
            }

            logger.info("生成[{}]成功", destFilePath + File.separator + fileName);
        }

    }

    protected void processClassSelf(ClassDefinition classDefinition) {

    }

    protected void processClassDependency(ClassDefinition classDefinition) {
        for (FieldDefinition fieldDefinition : classDefinition.getFields()) {
            processField(classDefinition, fieldDefinition);
        }
    }

    protected void processField(ClassDefinition classDefinition, FieldDefinition fieldDefinition) {
        if (classDefinition instanceof BeanDefinition) {
            processBeanField((BeanDefinition) classDefinition, fieldDefinition);
        }
    }

    protected void processBeanField(BeanDefinition beanDefinition, FieldDefinition fieldDefinition) {
        String fieldType = fieldDefinition.getType();
        if (fieldDefinition.isBuiltinType()) {
            fieldDefinition.setBasicType(basicTypes.get(fieldType));
            fieldDefinition.setClassType(classTypes.get(fieldType));
        }

        if (fieldDefinition.isCollectionType()) {
            if (fieldType.equals("map") && fieldDefinition.isBuiltinKeyType()) {
                String fieldKeyType = fieldDefinition.getKeyType();
                fieldDefinition.setBasicKeyType(basicTypes.get(fieldKeyType));
                fieldDefinition.setClassKeyType(classTypes.get(fieldKeyType));
            }

            String fieldValueType = fieldDefinition.getValueType();
            if (fieldDefinition.isBuiltinValueType()) {
                fieldDefinition.setBasicValueType(basicTypes.get(fieldValueType));
                fieldDefinition.setClassValueType(classTypes.get(fieldValueType));
            }
        }

        processBeanFieldImports(beanDefinition, fieldDefinition);

    }

    protected void processBeanFieldImports(BeanDefinition beanDefinition, FieldDefinition fieldDefinition) {
        ClassDefinition fieldClass = fieldDefinition.getClassDefinition();
        if (fieldClass != null && !fieldClass.getFullPackageName().equals(beanDefinition.getFullPackageName())) {
            beanDefinition.getImports().add(fieldClass.getFullName());
        }

        BeanDefinition fieldValueBean = fieldDefinition.getValueBean();
        if (fieldValueBean != null && !fieldValueBean.getFullPackageName().equals(beanDefinition.getFullPackageName())) {
            beanDefinition.getImports().add(fieldValueBean.getFullName());
        }
    }

}
