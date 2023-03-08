package quan.generator;

import com.alibaba.fastjson.JSON;
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
import quan.generator.config.ConfigGenerator;
import quan.generator.data.DataGenerator;
import quan.generator.message.MessageGenerator;
import quan.util.ClassUtils;
import quan.util.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

/**
 * 代码生成器
 */
public abstract class Generator {

    protected static final Logger logger = LoggerFactory.getLogger(Generator.class);

    //生成器选项
    protected Properties options;

    protected Map<String, String> basicTypes = new HashMap<>();

    protected Map<String, String> classTypes = new HashMap<>();


    //类的简单名对应全名
    protected Map<String, String> classNames = new HashMap<>();

    protected boolean enable = true;

    //是否开启增量生成
    protected boolean increment;

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


    //生成或删除代码文件数量
    protected int count;

    //上一次代码生成记录
    protected Map<String, String> oldRecords = new HashMap<>();

    //当前代码生成记录
    protected Map<String, String> newRecords = new HashMap<>();

    protected Set<String> addClasses = new HashSet<>();

    protected Set<String> deleteClasses = new HashSet<>();

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
        this.codePath = FileUtils.toPlatPath(codePath);
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

        String optionPrefix1 = optionPrefix(false);
        String optionPrefix2 = optionPrefix(true);

        String enable = options.getProperty(optionPrefix1 + "enable");
        if (!StringUtils.isBlank(enable)) {
            this.enable = enable.trim().equals("true");
        }
        enable = options.getProperty(optionPrefix2 + "enable");
        if (!StringUtils.isBlank(enable)) {
            this.enable = enable.trim().equals("true");
        }

        String increment = options.getProperty(optionPrefix1 + "increment");
        if (!StringUtils.isBlank(increment)) {
            this.increment = increment.trim().equals("true");
        }
        increment = options.getProperty(optionPrefix2 + "increment");
        if (!StringUtils.isBlank(increment)) {
            this.increment = increment.trim().equals("true");
        }

        String definitionPath = options.getProperty(optionPrefix1 + "definitionPath");
        if (!StringUtils.isBlank(definitionPath)) {
            definitionPaths.addAll(Arrays.asList(definitionPath.split("[,，]")));
        }

        String definitionFileEncoding = options.getProperty(optionPrefix1 + "definitionFileEncoding");
        if (!StringUtils.isBlank(definitionFileEncoding)) {
            this.definitionFileEncoding = definitionFileEncoding;
        }

        if (parser != null) {
            parser.setDefinitionFileEncoding(definitionFileEncoding);
            parser.setBeanNamePattern(options.getProperty(optionPrefix1 + "beanNamePattern"));
            parser.setEnumNamePattern(options.getProperty(optionPrefix1 + "enumNamePattern"));
        }

        String codePath = options.getProperty(optionPrefix2 + "codePath");
        if (!StringUtils.isBlank(codePath)) {
            setCodePath(codePath);
        }

        packagePrefix = options.getProperty(optionPrefix2 + "packagePrefix");
        enumPackagePrefix = options.getProperty(optionPrefix2 + "enumPackagePrefix");
    }

    protected String optionPrefix(boolean useLanguage) {
        String prefix = category() + ".";
        if (useLanguage) {
            prefix += language() + ".";
        }
        return prefix;
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

        readRecords();

        for (ClassDefinition classDefinition : parser.getClasses().values()) {
            packagesClasses.computeIfAbsent(classDefinition.getPackageName(language()), k -> new HashMap<>()).put(classDefinition.getName(), classDefinition);
        }

        List<ClassDefinition> classDefinitions = new ArrayList<>();
        for (ClassDefinition classDefinition : parser.getClasses().values()) {
            if (support(classDefinition) && classDefinition.isSupportedLanguage(this.language())) {
                classDefinition.reset();
                prepareClass(classDefinition);
                classDefinitions.add(classDefinition);
            }
        }

        generate(classDefinitions);

        packagesClasses.clear();

        oldRecords.keySet().forEach(this::delete);

        writeRecords();

        logger.info("生成{}{}代码完成\n", language(), category().alias());
    }

    private void readRecords() {
        File recordsFile = new File(".records" + File.separator + getClass().getSimpleName() + ".json");
        if (recordsFile.exists()) {
            try {
                //noinspection unchecked
                oldRecords = JSON.parseObject(new String(Files.readAllBytes(recordsFile.toPath())), HashMap.class);
            } catch (IOException e) {
                logger.error("", e);
            }
        }
    }

    protected void writeRecords() {
        File recordsPath = new File(".records");
        File recordsFile = new File(recordsPath, getClass().getSimpleName() + ".json");
        try {
            if (!recordsPath.exists()) {
                recordsPath.mkdirs();
            }
            JSON.writeJSONString(new FileWriter(recordsFile), newRecords);
        } catch (IOException e) {
            logger.error("", e);
        }

        oldRecords.clear();
        newRecords.clear();
    }

    protected void putRecord(ClassDefinition classDefinition) {
        String fullName = classDefinition.getFullName(language());
        String version = classDefinition.getVersion();
        if (oldRecords.remove(fullName) == null) {
            addClasses.add(fullName);
        }
        newRecords.put(fullName, version);
    }


    /**
     * 删除失效的代码文件
     */
    protected void delete(String fullName) {
        count++;
        deleteClasses.add(fullName);
        File classFile = new File(codePath, fullName.replace(".", File.separator) + "." + language());
        if (classFile.delete()) {
            logger.error("删除{}[{}]完成", category().alias(), classFile);
        } else {
            logger.error("删除{}[{}]失败", category().alias(), classFile);
        }
    }

    protected void generate(List<ClassDefinition> classDefinitions) {
        classDefinitions.forEach(this::generate);
    }

    protected final boolean checkChange(ClassDefinition classDefinition) {
        if (increment) {
            return isChange(classDefinition);
        } else {
            return true;
        }
    }

    protected boolean isChange(ClassDefinition classDefinition) {
        String fullName = classDefinition.getFullName(language());
        String version = classDefinition.getVersion();
        return !version.equals(oldRecords.get(fullName));
    }

    protected void generate(ClassDefinition classDefinition) {
        if (!checkChange(classDefinition)) {
            putRecord(classDefinition);
            return;
        }

        File packagePath = new File(codePath, classDefinition.getFullPackageName(language()).replace(".", File.separator));
        File classFile = new File(packagePath, classDefinition.getName() + "." + language());

        if (!packagePath.exists() && !packagePath.mkdirs()) {
            logger.error("生成{}[{}]失败，无法创建目录[{}]", category().alias(), classFile, packagePath);
            return;
        }

        try (Writer writer = new OutputStreamWriter(Files.newOutputStream(classFile.toPath()), StandardCharsets.UTF_8)) {
            count++;
            templates.get(classDefinition.getClass()).process(classDefinition, writer);
        } catch (Exception e) {
            logger.error("生成{}[{}]失败", category().alias(), classFile, e);
            return;
        }

        putRecord(classDefinition);

        logger.info("生成{}[{}]完成", category().alias(), classFile);

    }

    protected void prepareClass(ClassDefinition classDefinition) {
        classDefinition.setCurrentLanguage(language());
        classDefinition.setDependentClassNames(this.classNames);

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
                        ((FieldDefinition) dependentSource.getOwnerDefinition()).setValueClassType(dependentClassFullName);
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
        ClassDefinition owner = fieldDefinition.getOwner();
        String fieldType = fieldDefinition.getType();
        if (fieldDefinition.isBuiltinType()) {
            fieldDefinition.setBasicType(owner.getDependentName(basicTypes.get(fieldType)));
            fieldDefinition.setClassType(owner.getDependentName(classTypes.get(fieldType)));
        }

        if (fieldDefinition.isCollectionType()) {
            if (fieldType.equals("map") && fieldDefinition.isBuiltinKeyType()) {
                String fieldKeyType = fieldDefinition.getKeyType();
                fieldDefinition.setKeyBasicType(owner.getDependentName(basicTypes.get(fieldKeyType)));
                fieldDefinition.setKeyClassType(owner.getDependentName(classTypes.get(fieldKeyType)));
            }

            String fieldValueType = fieldDefinition.getValueType();
            if (fieldDefinition.isBuiltinValueType()) {
                fieldDefinition.setValueBasicType(owner.getDependentName(basicTypes.get(fieldValueType)));
                fieldDefinition.setValueClassType(owner.getDependentName(classTypes.get(fieldValueType)));
            }
        }
    }

    protected void printErrors() {
        if (parser == null) {
            return;
        }

        LinkedHashSet<String> errors = parser.getValidatedErrors();
        if (errors.isEmpty()) {
            return;
        }

        logger.error("生成{}代码失败，路径{}下的定义文件共发现{}条错误", category().alias(), parser.getDefinitionPaths(), errors.size());

        int i = 0;
        for (String error : errors) {
            logger.error("{}{}", error, ++i == errors.size() ? "\n" : "");
        }
    }

    /**
     * 执行代码生成
     *
     * @param optionsFileName 选项文件名为空时使用默认文件
     * @param extraOptions    附加的选项会覆盖选项文件里的选项
     * @return 成功或失败，部分成功也会返回false
     */
    public static boolean generate(String optionsFileName, Properties extraOptions) {
        long startTime = System.currentTimeMillis();
        Properties options = new Properties();

        if (!StringUtils.isBlank(optionsFileName)) {
            File optionsFile = new File(optionsFileName);
            try (InputStream inputStream = Files.newInputStream(optionsFile.toPath())) {
                options.load(inputStream);
                logger.info("加载生成器选项配置文件成功：{}\n", optionsFile.getCanonicalPath());
            } catch (IOException e) {
                logger.error("加载生成器选项配置文件出错", e);
                return false;
            }
        }

        if (extraOptions != null) {
            options.putAll(extraOptions);
        }

        boolean success = generate(DataGenerator.class, options);
        success &= generate(MessageGenerator.class, options);
        success &= generate(ConfigGenerator.class, options);

        logger.info("生成完成，耗时{}s", (System.currentTimeMillis() - startTime) / 1000D);
        return success;
    }

    public static void generate(Properties options) {
        generate("", options);
    }

    public static void generate(String optionsFile) {
        if (StringUtils.isBlank(optionsFile)) {
            optionsFile = "generator.properties";
        }
        generate(optionsFile, null);
    }

    private static boolean generate(Class<? extends Generator> superClass, Properties options) {
        List<Generator> generators = new ArrayList<>();

        String definitionType = superClass == ConfigGenerator.class ? options.getProperty("config.definitionType") : "xml";
        DefinitionParser parser = DefinitionParser.createParser(definitionType);

        for (Class<?> clazz : ClassUtils.loadClasses(superClass.getPackage().getName(), superClass, false, null)) {
            try {
                Generator generator = (Generator) clazz.getConstructor(Properties.class).newInstance(options);
                generator.setParser(parser);
                generators.add(generator);
            } catch (Exception ignored) {
            }
        }

        boolean success = true;

        for (int i = 0; i < generators.size(); i++) {
            Generator generator = generators.get(i);
            generator.generate(i == generators.size() - 1);
            if (generator.getParser() != null) {
                success &= generator.getParser().getValidatedErrors().isEmpty();
            }
        }

        return success;
    }

    public static void main(String[] args) {
        String optionsFile = "";
        if (args.length > 0 && !args[0].startsWith("--")) {
            optionsFile = args[0];
        }

        boolean exit1OnFail = false;
        if (args.length > 1 && !args[1].startsWith("--")) {
            exit1OnFail = Boolean.parseBoolean(args[1]);
        }

        Properties extraOptions = new Properties();
        for (String arg : args) {
            if (!arg.startsWith("--")) {
                continue;
            }
            String option = arg.substring(2);
            String optionKey = option;
            String optionValue = "true";
            if (option.contains("=")) {
                optionKey = option.substring(0, option.indexOf("="));
                optionValue = option.substring(option.indexOf("=") + 1);
            }
            extraOptions.put(optionKey, optionValue);
        }

        if (!generate(optionsFile, extraOptions) && exit1OnFail) {
            System.exit(1);
        }
    }

}
