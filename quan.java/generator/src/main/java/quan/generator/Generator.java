package quan.generator;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.common.utils.PathUtils;
import quan.definition.*;
import quan.definition.DependentSource.DependentType;
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

    //具体语言包下的类定义,<包名,<类名,类定义>
    protected Map<String, Map<String, ClassDefinition>> packagesClasses = new HashMap<>();

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

    protected abstract Language language();

    protected boolean support(ClassDefinition classDefinition) {
        return classDefinition instanceof BeanDefinition || classDefinition instanceof EnumDefinition;
    }

    protected void initOptions(Properties options) {
        String enable = options.getProperty(category() + ".enable");
        if (enable == null || !enable.equals("true")) {
            this.enable = false;
        }

        enable = options.getProperty(category() + "." + language() + ".enable");
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

        String codePath = options.getProperty(category() + "." + language() + ".codePath");
        if (!StringUtils.isBlank(codePath)) {
            setCodePath(codePath);
        }

        packagePrefix = options.getProperty(category() + "." + language() + ".packagePrefix");
        enumPackagePrefix = options.getProperty(category() + "." + language() + ".enumPackagePrefix");
    }


    /**
     * 检查生成器必须要设置的选项
     */
    protected void checkOptions() {
        if (definitionPaths.isEmpty()) {
            throw new IllegalArgumentException(category().alias() + "的定义文件路径[definitionPaths]不能为空");
        }
        if (codePath == null) {
            throw new IllegalArgumentException(category().alias() + "的目标代码[" + language() + "]文件路径[codePath]不能为空");
        }
    }

    protected void initFreemarker() {
        freemarkerCfg = new Configuration(Configuration.VERSION_2_3_23);
        freemarkerCfg.setClassForTemplateLoading(Generator.class, "");
        freemarkerCfg.setDefaultEncoding("UTF-8");

        try {
            Template enumTemplate = freemarkerCfg.getTemplate("enum." + language() + ".ftl");
            templates.put(EnumDefinition.class, enumTemplate);
        } catch (IOException e) {
            logger.error("", e);
            return;
        }

        freemarkerCfg.setClassForTemplateLoading(getClass(), "");
    }

    protected void parseDefinitions() {
        if (parser == null) {
            throw new IllegalArgumentException(category().alias() + "的定义解析器[definitionParser]不能为空");
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

        for (ClassDefinition classDefinition : parser.getClasses().values()) {
            packagesClasses.computeIfAbsent(classDefinition.getPackageName(language()), k -> new HashMap<>()).put(classDefinition.getName(), classDefinition);
        }

        List<ClassDefinition> classDefinitions = new ArrayList<>();
        for (ClassDefinition classDefinition : parser.getClasses().values()) {
            if (!support(classDefinition) || !classDefinition.isSupportLanguage(this.language())) {
                continue;
            }
            classDefinition.reset();
            prepareClass(classDefinition);
            classDefinitions.add(classDefinition);
        }

        generate(classDefinitions);
        packagesClasses.clear();

        logger.info("生成{}{}完成\n", language(), category().alias());
    }

    protected void generate(List<ClassDefinition> classDefinitions) {
        classDefinitions.forEach(this::generate);
    }

    protected void generate(ClassDefinition classDefinition) {
        Template template = templates.get(classDefinition.getClass());
        File filePath = new File(codePath + File.separator + classDefinition.getFullPackageName(language()).replace(".", File.separator));
        String fileName = classDefinition.getName() + "." + language();

        if (!filePath.exists() && !filePath.mkdirs()) {
            logger.info("生成{}[{}]失败，无法创建目录[{}]", category().alias(), filePath + File.separator + fileName, filePath);
            return;
        }

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(new File(filePath, fileName)), StandardCharsets.UTF_8)) {
            template.process(classDefinition, writer);
        } catch (Exception e) {
            logger.info("生成{}[{}]失败", category().alias(), filePath + File.separator + fileName, e);
            return;
        }

        logger.info("生成{}[{}]完成", category().alias(), filePath + File.separator + fileName);

    }

    protected void prepareClass(ClassDefinition classDefinition) {
        //不同包下的同名类依赖
        Map<String, TreeMap<DependentSource, ClassDefinition>> dependentsClasses = classDefinition.getDependentsClasses();
        for (String dependentName : dependentsClasses.keySet()) {
            ClassDefinition importedClassDefinition = null;
            for (DependentSource dependentSource : dependentsClasses.get(dependentName).keySet()) {
                ClassDefinition dependentClassDefinition = dependentsClasses.get(dependentName).get(dependentSource);
                if (dependentClassDefinition == importedClassDefinition) {
                    continue;
                } else if (importedClassDefinition == null) {
                    int howImport = howImportDependent(classDefinition, dependentClassDefinition);
                    if (howImport >= 0) {
                        if (howImport == 0) {
                            classDefinition.getImports().add(dependentClassDefinition.getOtherImport(language()));
                        }
                        importedClassDefinition = dependentClassDefinition;
                        continue;
                    }
                }

                String dependentFullName = dependentClassDefinition.getFullName(language());
                if (dependentSource.getType() == DependentType.field) {
                    ((FieldDefinition) dependentSource.getDefinition()).setClassType(dependentFullName);
                } else if (dependentSource.getType() == DependentType.fieldValue) {
                    ((FieldDefinition) dependentSource.getDefinition()).setClassValueType(dependentFullName);
                } else if (dependentSource.getType() == DependentType.parent) {
                    ((BeanDefinition) dependentSource.getDefinition()).setParentClassName(dependentFullName);
                } else if (dependentSource.getType() == DependentType.child) {
                    ((BeanDefinition) dependentSource.getDefinition()).getDependentChildren().get(classDefinition.getLongName()).setRight(dependentFullName);
                }
                //消息头没有同名类
            }
        }

        if (classDefinition instanceof BeanDefinition) {
            prepareBean((BeanDefinition) classDefinition);
        }
    }

    /**
     * 判断怎么依赖类怎么导入
     *
     * @return 0:import(using)语句导入,1:直接使用简单类名,-1:需要使用全类名
     */
    protected int howImportDependent(ClassDefinition classDefinition, ClassDefinition dependentClassDefinition) {
        Language language = language();
        String fullPackageName = classDefinition.getFullPackageName(language);
        String dependentFullPackageName = dependentClassDefinition.getFullPackageName(language);

        if (language == Language.java) {
            return fullPackageName.equals(dependentFullPackageName) ? 1 : 0;
        } else if (language == Language.cs) {
            Map<String, ClassDefinition> packageClasses = this.packagesClasses.get(classDefinition.getPackageName(language));
            ClassDefinition packageClassDefinition = packageClasses.get(dependentClassDefinition.getName());
            if (packageClassDefinition == null) {
                return 0;
            }
            return packageClassDefinition == dependentClassDefinition ? 1 : -1;
        }

        return 0;
    }

    protected void prepareBean(BeanDefinition beanDefinition) {
        beanDefinition.getFields().forEach(this::prepareField);
    }

    protected void prepareField(FieldDefinition fieldDefinition) {
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

    }

    protected void printErrors() {
        if (parser == null) {
            return;
        }

        List<String> errors = new ArrayList<>(parser.getValidatedErrors());
        if (errors.isEmpty()) {
            return;
        }

        logger.error("生成{}代码失败，解析目录{}下的定义文件共发现{}条错误", category().alias(), parser.getDefinitionPaths(), errors.size());
        for (int i = 1; i <= errors.size(); i++) {
            String error = errors.get(i - 1);
            logger.error(error);
        }
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        boolean test = false;
        String optionsFileName = "generator.properties";

        if (args.length > 0) {
            if (args[0].equals("test")) {
                test = true;
            } else {
                optionsFileName = args[0];
            }
        } else {
            logger.info("使用默认位置的生成器选项配置文件[{}]\n", optionsFileName);
        }

        Properties options = new Properties();
        InputStream inputStream = null;
        try {
            if (test) {
                inputStream = Generator.class.getResourceAsStream(optionsFileName);
            } else {
                inputStream = new FileInputStream(optionsFileName);
            }
            options.load(inputStream);
        } catch (IOException e) {
            logger.info("加载生成器选项配置文件[{}]出错", optionsFileName, e);
            return;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.error("", e);
                }
            }
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
