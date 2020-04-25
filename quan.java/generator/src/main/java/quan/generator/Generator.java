package quan.generator;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.common.PathUtils;
import quan.definition.*;
import quan.definition.config.ConfigDefinition;
import quan.definition.parser.DefinitionParser;
import quan.definition.parser.XmlDefinitionParser;
import quan.generator.config.CSharpConfigGenerator;
import quan.generator.config.JavaConfigGenerator;
import quan.generator.config.LuaConfigGenerator;
import quan.generator.data.DataGenerator;
import quan.generator.message.CSharpMessageGenerator;
import quan.generator.message.JavaMessageGenerator;
import quan.generator.message.LuaMessageGenerator;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 代码生成器
 * Created by quanchangnai on 2019/6/23.
 */
public abstract class Generator {

    protected static final Logger logger = LoggerFactory.getLogger(Generator.class);

    protected Map<String, String> basicTypes = new HashMap<>();

    protected Map<String, String> classTypes = new HashMap<>();

    protected boolean enable = true;

    protected String definitionFileEncoding;

    protected Set<String> definitionPaths = new HashSet<>();

    protected String packagePrefix;

    protected String enumPackagePrefix;

    protected String codePath;

    protected DefinitionParser parser;

    protected Configuration freemarkerCfg;

    protected Map<Class<? extends ClassDefinition>, Template> templates = new HashMap<>();

    public Generator() {
    }

    public Generator(Properties options) {
        initOptions(options);
        if (enable) {
            checkOptions();
        }
    }

    public void setDefinitionPath(Collection<String> definitionPaths) {
        this.definitionPaths.clear();
        this.definitionPaths.addAll(definitionPaths);
    }

    public void setDefinitionPath(String definitionPath) {
        this.definitionPaths.clear();
        this.definitionPaths.add(definitionPath);
    }

    public void setCodePath(String codePath) {
        this.codePath = PathUtils.toPlatPath(codePath);
    }

    public void setPackagePrefix(String packagePrefix) {
        this.packagePrefix = packagePrefix;
    }

    public void setEnumPackagePrefix(String enumPackagePrefix) {
        this.enumPackagePrefix = enumPackagePrefix;
    }

    public void useXmlParser() {
        parser = new XmlDefinitionParser();
        parser.setCategory(category());
        parser.setDefinitionPaths(definitionPaths);
    }

    public void useXmlParser(String definitionPath) {
        setDefinitionPath(definitionPath);
        useXmlParser();
    }

    public void setParser(DefinitionParser parser) {
        if (parser == null) {
            return;
        }

        parser.setDefinitionFileEncoding(definitionFileEncoding);
        parser.setCategory(category());

        if (!parser.getDefinitionPaths().isEmpty() && definitionPaths.isEmpty()) {
            definitionPaths.addAll(parser.getDefinitionPaths());
        } else {
            parser.setDefinitionPaths(definitionPaths);
        }

        this.parser = parser;
    }

    public DefinitionParser getParser() {
        return parser;
    }

    public abstract Category category();

    protected abstract Language supportLanguage();

    protected boolean support(ClassDefinition classDefinition) {
        return classDefinition instanceof BeanDefinition || classDefinition instanceof EnumDefinition;
    }

    protected void initOptions(Properties options) {
        String enable = options.getProperty(category() + ".enable");
        if (enable == null || !enable.equals("true")) {
            this.enable = false;
        }

        enable = options.getProperty(category() + "." + supportLanguage() + ".enable");
        if (enable == null || !enable.equals("true")) {
            this.enable = false;
        }

        String definitionPath = options.getProperty(category() + ".definitionPath");
        if (!StringUtils.isBlank(definitionPath)) {
            definitionPaths.addAll(Arrays.asList(definitionPath.split(",")));
        }

        String definitionFileEncoding = options.getProperty(category() + ".definitionFileEncoding");
        if (!StringUtils.isBlank(definitionFileEncoding)) {
            this.definitionFileEncoding = definitionFileEncoding;
        }

        String codePath = options.getProperty(category() + "." + supportLanguage() + ".codePath");
        if (!StringUtils.isBlank(codePath)) {
            setCodePath(codePath);
        }

        packagePrefix = options.getProperty(category() + "." + supportLanguage() + ".packagePrefix");
        enumPackagePrefix = options.getProperty(category() + "." + supportLanguage() + ".enumPackagePrefix");
    }


    /**
     * 检查生成器必须要设置的选项
     */
    protected void checkOptions() {
        if (definitionPaths.isEmpty()) {
            throw new IllegalArgumentException(category().comment() + "的定义文件路径[definitionPaths]不能为空");
        }
        if (codePath == null) {
            throw new IllegalArgumentException(category().comment() + "的目标代码[" + supportLanguage() + "]文件路径[codePath]不能为空");
        }
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

    protected void parseDefinitions() {
        if (parser == null) {
            throw new IllegalArgumentException(category().comment() + "的定义解析器[definitionParser]不能为空");
        }
        parser.setPackagePrefix(packagePrefix);
        parser.setEnumPackagePrefix(enumPackagePrefix);
        parser.parse();
    }

    public boolean tryGenerate(boolean printError) {
        if (!enable) {
            return false;
        }
        generate(printError);
        return true;
    }

    public boolean tryGenerate() {
        return tryGenerate(true);
    }

    public void generate() {
        generate(true);
    }

    public void generate(boolean printError) {
        checkOptions();
        parseDefinitions();

        if (!parser.getValidatedErrors().isEmpty()) {
            if (printError) {
                printErrors();
            }
            return;
        }

        if (parser.getClasses().isEmpty()) {
            return;
        }

        initFreemarker();

        List<ClassDefinition> classDefinitions = new ArrayList<>();
        for (ClassDefinition classDefinition : parser.getClasses().values()) {
            if (!support(classDefinition) || !classDefinition.supportLanguage(this.supportLanguage())) {
                continue;
            }
            classDefinition.reset();
            prepareClass(classDefinition);
            classDefinitions.add(classDefinition);
        }

        generate(classDefinitions);
        logger.info("生成{}{}完成\n", supportLanguage(), category().comment());
    }

    protected void generate(List<ClassDefinition> classDefinitions) {
        for (ClassDefinition classDefinition : classDefinitions) {
            generate(classDefinition);
        }
    }

    protected void generate(ClassDefinition classDefinition) {
        Template template = templates.get(classDefinition.getClass());
        File filePath = new File(codePath + File.separator + classDefinition.getFullPackageName(supportLanguage()).replace(".", File.separator));
        String fileName = classDefinition.getName() + "." + supportLanguage();

        if (!filePath.exists() && !filePath.mkdirs()) {
            logger.info("生成{}[{}]失败，无法创建目录[{}]", category().comment(), filePath + File.separator + fileName, filePath);
            return;
        }

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(new File(filePath, fileName)), StandardCharsets.UTF_8)) {
            template.process(classDefinition, writer);
        } catch (Exception e) {
            logger.info("生成{}[{}]失败", category().comment(), filePath + File.separator + fileName, e);
            return;
        }

        logger.info("生成{}[{}]完成", category().comment(), filePath + File.separator + fileName);

    }

    protected void prepareClass(ClassDefinition classDefinition) {
        if (classDefinition instanceof BeanDefinition) {
            BeanDefinition beanDefinition = (BeanDefinition) classDefinition;
            for (FieldDefinition fieldDefinition : beanDefinition.getSelfFields()) {
                prepareField(classDefinition, fieldDefinition);
            }

            Set<BeanDefinition> dependentBeans = new HashSet<>(beanDefinition.getChildren());
            if (beanDefinition.getParent() != null && !(beanDefinition instanceof ConfigDefinition)) {
                dependentBeans.add(beanDefinition.getParent());
            }

            for (BeanDefinition dependentBean : dependentBeans) {
                if (!dependentBean.getFullPackageName(supportLanguage()).equals(classDefinition.getFullPackageName(supportLanguage()))) {
                    beanDefinition.getImports().add(dependentBean.getOtherImport(supportLanguage()));
                }
            }
        } else {
            for (FieldDefinition fieldDefinition : classDefinition.getFields()) {
                prepareField(classDefinition, fieldDefinition);
            }
        }
    }

    protected void prepareField(ClassDefinition classDefinition, FieldDefinition fieldDefinition) {
        if (classDefinition instanceof BeanDefinition) {
            prepareBeanField((BeanDefinition) classDefinition, fieldDefinition);
        }
    }

    protected void prepareBeanField(BeanDefinition beanDefinition, FieldDefinition fieldDefinition) {
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

        prepareBeanFieldImports(beanDefinition, fieldDefinition);
    }

    protected void prepareBeanFieldImports(BeanDefinition beanDefinition, FieldDefinition fieldDefinition) {
        ClassDefinition fieldClass = fieldDefinition.getClassDefinition();
        if (fieldClass != null && !fieldClass.getFullPackageName(supportLanguage()).equals(beanDefinition.getFullPackageName(supportLanguage()))) {
            beanDefinition.getImports().add(fieldClass.getOtherImport(supportLanguage()));
        }

        BeanDefinition fieldValueBean = fieldDefinition.getValueBean();
        if (fieldValueBean != null && !fieldValueBean.getFullPackageName(supportLanguage()).equals(beanDefinition.getFullPackageName(supportLanguage()))) {
            beanDefinition.getImports().add(fieldValueBean.getOtherImport(supportLanguage()));
        }
    }

    protected void printErrors() {
        if (parser == null) {
            return;
        }

        List<String> errors = new ArrayList<>(parser.getValidatedErrors());
        if (errors.isEmpty()) {
            return;
        }

        logger.error("生成{}代码失败，解析目录{}下的定义文件共发现{}条错误", category().comment(), parser.getDefinitionPaths(), errors.size());
        for (int i = 1; i <= errors.size(); i++) {
            String error = errors.get(i - 1);
            logger.error(error);
        }
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        String optionsFileName = "generator.properties";
        if (args.length > 0) {
            optionsFileName = args[0];
        } else {
            logger.info("使用默认位置的生成器选项配置文件[{}]\n", optionsFileName);
        }

        Properties options = new Properties();
        try (FileInputStream inputStream = new FileInputStream(optionsFileName)) {
            options.load(inputStream);
        } catch (IOException e) {
            logger.info("加载生成器选项配置文件[{}]出错", optionsFileName, e);
            return;
        }

        DataGenerator dataGenerator = new DataGenerator(options);
        dataGenerator.useXmlParser();

        JavaMessageGenerator javaMessageGenerator = new JavaMessageGenerator(options);
        CSharpMessageGenerator cSharpMessageGenerator = new CSharpMessageGenerator(options);
        LuaMessageGenerator luaMessageGenerator = new LuaMessageGenerator(options);

        DefinitionParser messageParser = new XmlDefinitionParser();
        javaMessageGenerator.setParser(messageParser);
        cSharpMessageGenerator.setParser(messageParser);
        luaMessageGenerator.setParser(messageParser);

        DefinitionParser configParser = new XmlDefinitionParser();
        JavaConfigGenerator javaConfigGenerator = new JavaConfigGenerator(options);
        CSharpConfigGenerator cSharpConfigGenerator = new CSharpConfigGenerator(options);
        LuaConfigGenerator luaConfigGenerator = new LuaConfigGenerator(options);

        javaConfigGenerator.setParser(configParser);
        cSharpConfigGenerator.setParser(configParser);
        luaConfigGenerator.setParser(configParser);

        dataGenerator.tryGenerate(true);

        javaMessageGenerator.tryGenerate(false);
        cSharpMessageGenerator.tryGenerate(false);
        luaMessageGenerator.tryGenerate(false);
        javaMessageGenerator.printErrors();

        javaConfigGenerator.tryGenerate(false);
        cSharpConfigGenerator.tryGenerate(false);
        luaConfigGenerator.tryGenerate(false);
        javaConfigGenerator.printErrors();

        logger.info("生成完成，耗时{}s", (System.currentTimeMillis() - startTime) / 1000D);
    }
}
