package quan.generator;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.common.util.PathUtils;
import quan.definition.*;
import quan.definition.parser.DefinitionParser;
import quan.definition.parser.XmlDefinitionParser;
import quan.generator.config.CSharpConfigGenerator;
import quan.generator.config.JavaConfigGenerator;
import quan.generator.config.LuaConfigGenerator;
import quan.generator.database.DatabaseGenerator;
import quan.generator.message.CSharpMessageGenerator;
import quan.generator.message.JavaMessageGenerator;
import quan.generator.message.LuaMessageGenerator;

import java.io.*;
import java.util.*;

/**
 * Created by quanchangnai on 2019/6/23.
 */
public abstract class Generator {

    protected Map<String, String> basicTypes = new HashMap<>();

    protected Map<String, String> classTypes = new HashMap<>();

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected String packagePrefix;

    protected String enumPackagePrefix;

    protected DefinitionParser definitionParser;

    protected String codePath;

    protected Configuration freemarkerCfg;

    protected Map<Class<? extends ClassDefinition>, Template> templates = new HashMap<>();

    protected boolean ready = true;

    protected boolean printError;

    public Generator() {
        initFreemarker();
    }

    public Generator(Properties properties) {
        String definitionPath = properties.getProperty(category() + ".definitionPath");
        if (StringUtils.isBlank(definitionPath)) {
            ready = false;
            return;
        }

        String codePath = properties.getProperty(category() + "." + supportLanguage() + ".codePath");
        if (StringUtils.isBlank(codePath)) {
            ready = false;
            return;
        }
        setCodePath(codePath);

        List<String> definitionPaths = new ArrayList<>();
        for (String path : definitionPath.split(",")) {
            definitionPaths.add(path);
        }

        packagePrefix = properties.getProperty(category() + "." + supportLanguage() + ".packagePrefix");
        enumPackagePrefix = properties.getProperty(category() + "." + supportLanguage() + ".enumPackagePrefix");
        this.useXmlDefinitionParser(definitionPaths);
    }

    public void setCodePath(String codePath) {
        this.codePath = PathUtils.currentPlatPath(codePath);
    }

    public DefinitionParser useXmlDefinitionParser(List<String> definitionPaths) {
        definitionParser = new XmlDefinitionParser();
        definitionParser.setCategory(category());
        definitionParser.setDefinitionPaths(definitionPaths);
        return definitionParser;
    }

    public DefinitionParser useXmlDefinitionParser(String definitionPath) {
        return useXmlDefinitionParser(Collections.singletonList(definitionPath));
    }

    public void setDefinitionParser(DefinitionParser definitionParser) {
        if (definitionParser == null) {
            return;
        }
        definitionParser.setCategory(category());
        this.definitionParser = definitionParser;
    }

    public DefinitionParser getDefinitionParser() {
        return definitionParser;
    }

    public boolean isReady() {
        return ready;
    }

    public abstract DefinitionCategory category();

    protected abstract Language supportLanguage();

    protected boolean support(ClassDefinition classDefinition) {
        return classDefinition instanceof BeanDefinition || classDefinition instanceof EnumDefinition;
    }

    protected void parseDefinitions() {
        Objects.requireNonNull(definitionParser, "定义解析器不能为空");
        definitionParser.setPackagePrefix(packagePrefix);
        definitionParser.setEnumPackagePrefix(enumPackagePrefix);
        definitionParser.parse();
    }

    protected void initFreemarker() {
        freemarkerCfg = new Configuration(Configuration.VERSION_2_3_23);
        freemarkerCfg.setClassForTemplateLoading(Generator.class, "");
        freemarkerCfg.setDefaultEncoding("UTF-8");

        try {
            Template enumTemplate = freemarkerCfg.getTemplate("enum." + supportLanguage() + ".ftl");
            templates.put(EnumDefinition.class, enumTemplate);
        } catch (IOException e) {
            logger.error("", e);
            return;
        }

        freemarkerCfg.setClassForTemplateLoading(getClass(), "");
    }

    public void generate() {
        if (!ready) {
            return;
        }
        //解析定义文件
        parseDefinitions();

        if (!definitionParser.getValidatedErrors().isEmpty()) {
            if (printError) {
                printErrors();
            }
            return;
        }

        if (definitionParser.getClasses().isEmpty()) {
            return;
        }

        initFreemarker();

        List<ClassDefinition> classDefinitions = new ArrayList<>();
        for (ClassDefinition classDefinition : definitionParser.getClasses().values()) {
            if (!support(classDefinition) || !classDefinition.supportLanguage(this.supportLanguage())) {
                continue;
            }
            classDefinition.reset();
            processClass(classDefinition);
            classDefinitions.add(classDefinition);
        }

        generate(classDefinitions);
        logger.info("生成{}完成\n", category());
    }

    protected void generate(List<ClassDefinition> classDefinitions) {
        for (ClassDefinition classDefinition : classDefinitions) {
            generate(classDefinition);
        }
    }

    protected void generate(ClassDefinition classDefinition) {
        Template template = templates.get(classDefinition.getClass());
        File destFilePath = new File(codePath + File.separator + classDefinition.getFullPackageName(supportLanguage()).replace(".", File.separator));
        if (!destFilePath.exists() && !destFilePath.mkdirs()) {
            logger.error("创建目录[{}]失败", destFilePath);
            return;
        }

        String fileName = classDefinition.getName() + "." + supportLanguage();
        try (Writer writer = new FileWriter(new File(destFilePath, fileName))) {
            template.process(classDefinition, writer);
        } catch (Exception e) {
            logger.info("生成{}[{}]失败", category(), destFilePath + File.separator + fileName, e);
            return;
        }

        logger.info("生成{}[{}]成功", category(), destFilePath + File.separator + fileName);

    }

    protected void processClass(ClassDefinition classDefinition) {
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
        if (fieldClass != null && !fieldClass.getFullPackageName(supportLanguage()).equals(beanDefinition.getFullPackageName(supportLanguage()))) {
            beanDefinition.getImports().add(fieldClass.getFullName(supportLanguage()));
        }

        BeanDefinition fieldValueBean = fieldDefinition.getValueBean();
        if (fieldValueBean != null && !fieldValueBean.getFullPackageName(supportLanguage()).equals(beanDefinition.getFullPackageName(supportLanguage()))) {
            beanDefinition.getImports().add(fieldValueBean.getFullName(supportLanguage()));
        }
    }

    protected void printErrors() {
        List<String> errors = new ArrayList<>(definitionParser.getValidatedErrors());
        logger.error("生成{}代码失败，解析定义文件{}共发现{}条错误", category(), definitionParser.getDefinitionPaths(), errors.size());
        for (int i = 1; i <= errors.size(); i++) {
            String error = errors.get(i - 1);
            logger.error("{}:{}", i, error);
        }
    }

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(Generator.class);

        String propertiesFile = "generator.properties";
        if (args.length > 0) {
            propertiesFile = args[0];
        } else {
            logger.info("使用默认生成器属性文件[{}]", propertiesFile);
        }

        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(propertiesFile));
        } catch (IOException e) {
            logger.info("加载生成器属性文件[{}]出错", propertiesFile, e);
            return;
        }

        List<Generator> generators = new ArrayList<>();
        generators.add(new DatabaseGenerator(properties));

        JavaMessageGenerator javaMessageGenerator = new JavaMessageGenerator(properties);
        javaMessageGenerator.printError = true;
        CSharpMessageGenerator cSharpMessageGenerator = new CSharpMessageGenerator(properties);
        cSharpMessageGenerator.setDefinitionParser(javaMessageGenerator.definitionParser);
        LuaMessageGenerator luaMessageGenerator = new LuaMessageGenerator(properties);
        luaMessageGenerator.setDefinitionParser(javaMessageGenerator.definitionParser);

        generators.add(javaMessageGenerator);
        generators.add(cSharpMessageGenerator);
        generators.add(luaMessageGenerator);

        JavaConfigGenerator javaConfigGenerator = new JavaConfigGenerator(properties);
        javaConfigGenerator.printError = true;
        CSharpConfigGenerator cSharpConfigGenerator = new CSharpConfigGenerator(properties);
        cSharpConfigGenerator.setDefinitionParser(javaConfigGenerator.definitionParser);
        LuaConfigGenerator luaConfigGenerator = new LuaConfigGenerator(properties);
        luaConfigGenerator.setDefinitionParser(javaConfigGenerator.definitionParser);

        generators.add(javaConfigGenerator);
        generators.add(cSharpConfigGenerator);
        generators.add(luaConfigGenerator);

        for (Generator generator : generators) {
            generator.generate();
        }
    }
}
