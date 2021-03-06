package quan.generator;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.util.PathUtils;
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

import static quan.definition.parser.DefinitionParser.createParser;

/**
 * 代码生成器<br/>
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

    protected void parseOptions(Properties options) {
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
        if (classDefinition instanceof BeanDefinition) {
            prepareBean((BeanDefinition) classDefinition);
        }

        //不同包下的同名类依赖
        Map<String, TreeMap<DependentSource, ClassDefinition>> dependentsClasses = classDefinition.getDependentsClasses();
        for (String dependentName : dependentsClasses.keySet()) {
            ClassDefinition simpleNameClassDefinition = null;
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
                        ((BeanDefinition) dependentSource.getOwnerDefinition()).getDependentChildren().get(classDefinition.getLongName()).setRight(dependentClassFullName);
                    }
                    //消息头没有同名类
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
        ClassDefinition packagedClassDefinition = packagesClasses.get(packageName).get(dependentClassDefinition.getName());

        if (language == Language.java) {
            if (simpleNameClassDefinition == null) {
                return fullPackageName.equals(dependentFullPackageName) ? Pair.of(false, false) : Pair.of(false, true);
            } else {
                return dependentClassDefinition == simpleNameClassDefinition ? Pair.of(false, false) : Pair.of(true, false);
            }
        } else if (language == Language.cs) {
            if (packagedClassDefinition == null) {
                return Pair.of(false, true);
            }
            return packagedClassDefinition == dependentClassDefinition ? Pair.of(false, false) : Pair.of(true, false);
        } else {
            //lua
            if (simpleNameClassDefinition == null || simpleNameClassDefinition == dependentClassDefinition) {
                return Pair.of(false, true);
            }
            return Pair.of(true, true);
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

        logger.error("生成{}代码失败，解析目录{}下的定义文件共发现{}条错误", category().alias(), parser.getDefinitionPaths(), parser.getValidatedErrors());
        parser.getValidatedErrors().forEach(logger::error);
    }

}
