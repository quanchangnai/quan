package quan.generator;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import quan.util.CommonUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static quan.definition.parser.DefinitionParser.createParser;

/**
 * 代码生成器
 */
public abstract class Generator {

    protected static final Logger logger = LoggerFactory.getLogger(Generator.class);

    //生成器选项
    protected Properties options;

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

    public Generator(Properties options) {
        parseOptions(options);
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
        this.codePath = CommonUtils.toPlatPath(codePath);
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
        parseOptions(options);
    }

    public void useXmlParser(String definitionPath) {
        setDefinitionPath(definitionPath);
        useXmlParser();
    }

    public void setParser(DefinitionParser parser) {
        if (parser == null) {
            return;
        }

        this.parser = parser;

        parseOptions(options);

        parser.setDefinitionFileEncoding(definitionFileEncoding);
        parser.setCategory(category());

        if (!parser.getDefinitionPaths().isEmpty() && definitionPaths.isEmpty()) {
            definitionPaths.addAll(parser.getDefinitionPaths());
        } else {
            parser.setDefinitionPaths(definitionPaths);
        }
    }

    public DefinitionParser getParser() {
        return parser;
    }

    public abstract Category category();

    protected abstract Language language();

    protected boolean support(ClassDefinition classDefinition) {
        return classDefinition instanceof BeanDefinition || classDefinition instanceof EnumDefinition;
    }

    protected void parseOptions(Properties options) {
        this.options = options;

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

        if (parser != null) {
            parser.setDefinitionFileEncoding(definitionFileEncoding);
            parser.setBeanNamePattern(options.getProperty(category() + ".beanNamePattern"));
            parser.setEnumNamePattern(options.getProperty(category() + ".enumNamePattern"));
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

    public void generate() {
        generate(true);
    }

    public void generate(boolean printErrors) {
        if (!enable) {
            return;
        }
        checkOptions();
        parseDefinitions();

        if (!parser.getValidatedErrors().isEmpty()) {
            if (printErrors) {
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
        if (classDefinition instanceof BeanDefinition) {
            prepareBean((BeanDefinition) classDefinition);
        }

        //不同包下的同名类依赖
        Map<String, TreeMap<DependentSource, ClassDefinition>> dependentsClasses = classDefinition.getDependentsClasses();
        for (String dependentName : dependentsClasses.keySet()) {
            ClassDefinition simpleNameClassDefinition = null;//同名类中只有一个可以使用简单类名
            TreeMap<DependentSource, ClassDefinition> dependentClasses = dependentsClasses.get(dependentName);
            for (DependentSource dependentSource : dependentClasses.keySet()) {
                ClassDefinition dependentClassDefinition = dependentClasses.get(dependentSource);
                String dependentClassFullName = dependentClassDefinition.getFullName(language());
                Pair<Boolean, Boolean> useDependent = howUseDependent(classDefinition, dependentClassDefinition, simpleNameClassDefinition);

                if (!useDependent.getLeft() && simpleNameClassDefinition == null) {
                    simpleNameClassDefinition = dependentClassDefinition;
                }

                if (useDependent.getRight()) {
                    if (useDependent.getLeft()) {
                        classDefinition.getImports().put(dependentClassDefinition.getOtherImport(language()), dependentClassFullName);
                    } else {
                        classDefinition.getImports().put(dependentClassDefinition.getOtherImport(language()), dependentClassDefinition.getName());
                    }
                }

                if (useDependent.getLeft()) {
                    if (dependentSource.getType() == DependentType.FIELD) {
                        ((FieldDefinition) dependentSource.getOwnerDefinition()).setClassType(dependentClassFullName);
                    } else if (dependentSource.getType() == DependentType.FIELD_VALUE) {
                        ((FieldDefinition) dependentSource.getOwnerDefinition()).setClassValueType(dependentClassFullName);
                    } else if (dependentSource.getType() == DependentType.FIELD_REF) {
                        ((FieldDefinition) dependentSource.getOwnerDefinition()).setRefType(dependentClassFullName);
                    } else if (dependentSource.getType() == DependentType.PARENT) {
                        ((BeanDefinition) dependentSource.getOwnerDefinition()).setParentClassName(dependentClassFullName);
                    } else if (dependentSource.getType() == DependentType.CHILD) {
                        ((BeanDefinition) dependentSource.getOwnerDefinition()).getDependentChildren().put(dependentClassDefinition.getLongName(), dependentClassFullName);
                    }
                }
            }
        }
    }

    /**
     * 判断依赖类的使用方式
     *
     * @return Pair<使用全类名还是简单类名, 是否使用import或using或require>
     */
    protected Pair<Boolean, Boolean> howUseDependent(ClassDefinition ownerClassDefinition, ClassDefinition dependentClassDefinition, ClassDefinition simpleNameClassDefinition) {
        Language language = language();
        String packageName = ownerClassDefinition.getPackageName(language);
        String fullPackageName = ownerClassDefinition.getFullPackageName(language);
        String dependentFullPackageName = dependentClassDefinition.getFullPackageName(language);

        if (language == Language.java) {
            if (ownerClassDefinition.getName().equals(dependentClassDefinition.getName())) {
                return Pair.of(true, false);
            } else if (simpleNameClassDefinition == null) {
                return fullPackageName.equals(dependentFullPackageName) ? Pair.of(false, false) : Pair.of(false, true);
            } else {
                return dependentClassDefinition == simpleNameClassDefinition ? Pair.of(false, false) : Pair.of(true, false);
            }
        } else if (language == Language.cs) {
            ClassDefinition packagedClassDefinition = packagesClasses.get(packageName).get(dependentClassDefinition.getName());
            if (ownerClassDefinition.getName().equals(dependentClassDefinition.getName())) {
                return Pair.of(true, false);
            } else if (simpleNameClassDefinition == null) {
                if (packagedClassDefinition == null) {
                    return Pair.of(false, true);
                }
                return packagedClassDefinition == dependentClassDefinition ? Pair.of(false, false) : Pair.of(true, false);
            } else {
                return dependentClassDefinition == simpleNameClassDefinition ? Pair.of(false, false) : Pair.of(true, false);
            }
        } else {
            //lua
            boolean require = !(dependentClassDefinition instanceof EnumDefinition);
            if (simpleNameClassDefinition == null || simpleNameClassDefinition == dependentClassDefinition) {
                return Pair.of(false, require);
            }
            return Pair.of(true, require);
        }
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
        if (parser == null || parser.getValidatedErrors().isEmpty()) {
            return;
        }

        logger.error("生成{}代码失败，解析目录{}下的定义文件共发现{}条错误", category().alias(), parser.getDefinitionPaths(), parser.getValidatedErrors().size());
        parser.getValidatedErrors().forEach(logger::error);
    }

    /**
     * 指定生成器选项配置文件执行代码生成
     *
     * @param optionsFile 选项文件名为空时使用默认文件
     */
    public static void generate(String optionsFile) {
        long startTime = System.currentTimeMillis();

        if (StringUtils.isBlank(optionsFile)) {
            optionsFile = "generator.properties";
            logger.info("使用默认位置的生成器选项配置文件[{}]\n", optionsFile);
        }

        Properties options = new Properties();
        try (InputStream inputStream = new FileInputStream(optionsFile.trim())) {
            options.load(inputStream);
        } catch (IOException e) {
            logger.info("加载生成器选项配置文件[{}]出错", optionsFile, e);
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

        DefinitionParser configParser = createParser(options.getProperty("config.definitionType").trim());
        JavaConfigGenerator javaConfigGenerator = new JavaConfigGenerator(options);
        CSharpConfigGenerator cSharpConfigGenerator = new CSharpConfigGenerator(options);
        LuaConfigGenerator luaConfigGenerator = new LuaConfigGenerator(options);

        javaConfigGenerator.setParser(configParser);
        cSharpConfigGenerator.setParser(configParser);
        luaConfigGenerator.setParser(configParser);

        dataGenerator.generate(true);

        javaMessageGenerator.generate(false);
        cSharpMessageGenerator.generate(false);
        luaMessageGenerator.generate(false);
        javaMessageGenerator.printErrors();

        javaConfigGenerator.generate(false);
        cSharpConfigGenerator.generate(false);
        luaConfigGenerator.generate(false);
        javaConfigGenerator.printErrors();

        logger.info("生成完成，耗时{}s", (System.currentTimeMillis() - startTime) / 1000D);
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            generate(args[0]);
        } else {
            generate("");
        }
    }

}
