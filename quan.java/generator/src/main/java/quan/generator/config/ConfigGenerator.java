package quan.generator.config;

import com.alibaba.fastjson.JSONObject;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;
import quan.config.ConfigConverter;
import quan.config.TableType;
import quan.config.WithDefinitionConfigLoader;
import quan.definition.BeanDefinition;
import quan.definition.Category;
import quan.definition.ClassDefinition;
import quan.definition.FieldDefinition;
import quan.definition.config.ConfigDefinition;
import quan.definition.config.ConstantDefinition;
import quan.generator.Generator;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Created by quanchangnai on 2019/7/11.
 */
public abstract class ConfigGenerator extends Generator {

    //配置加载器用于生成常量
    protected WithDefinitionConfigLoader configLoader;

    protected TableType tableType;

    protected String tablePath;

    public ConfigGenerator() {
    }

    public ConfigGenerator(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean initProps(Properties properties) {
        if (!super.initProps(properties)) {
            return false;
        }

        String tableType_ = properties.getProperty(category() + ".tableType");
        tablePath = properties.getProperty(category() + ".tablePath");

        if (!StringUtils.isBlank(tableType_)) {
            try {
                tableType = TableType.valueOf(tableType_);
            } catch (Exception e) {
                logger.info("配置表格类型错误，可用枚举值{}", Arrays.toString(TableType.values()));
            }
        }

        ConfigConverter.setDateTimePattern(properties.getProperty(category() + ".dateTimePattern"));
        ConfigConverter.setDatePattern(properties.getProperty(category() + ".datePattern"));
        ConfigConverter.setTimePattern(properties.getProperty(category() + ".timePattern"));

        return true;
    }

    public void setTableType(String tableType) {
        this.tableType = TableType.valueOf(tableType);
    }

    public void setTableType(TableType tableType) {
        this.tableType = tableType;
    }

    public void setTablePath(String tablePath) {
        this.tablePath = tablePath;
    }

    @Override
    protected void initFreemarker() {
        super.initFreemarker();
        try {
            Template configTemplate = freemarkerCfg.getTemplate("config." + supportLanguage() + ".ftl");
            Template constantTemplate = freemarkerCfg.getTemplate("constant." + supportLanguage() + ".ftl");

            templates.put(BeanDefinition.class, configTemplate);
            templates.put(ConfigDefinition.class, configTemplate);
            templates.put(ConstantDefinition.class, constantTemplate);
        } catch (IOException e) {
            logger.error("", e);
        }
    }

    @Override
    public final Category category() {
        return Category.config;
    }

    @Override
    public void generate(boolean printError) {
        initConfigLoader(tableType, tablePath);
        super.generate(printError);
    }

    @Override
    protected void checkProps() {
        super.checkProps();
        if (tableType == null) {
            throw new IllegalArgumentException(category().comment() + "的表格类型[tableType]不能为空");
        }
        if (tablePath == null) {
            throw new IllegalArgumentException(category().comment() + "的表格文件路径[tablePath]不能为空");
        }
    }

    @Override
    protected boolean support(ClassDefinition classDefinition) {
        if (classDefinition instanceof ConfigDefinition) {
            return true;
        }
        if (classDefinition instanceof ConstantDefinition) {
            return true;
        }
        return super.support(classDefinition);
    }

    @Override
    protected void generate(ClassDefinition classDefinition) {
        if (configLoader != null && classDefinition instanceof ConstantDefinition) {
            ConstantDefinition constantDefinition = (ConstantDefinition) classDefinition;
            List<JSONObject> configJsons = configLoader.loadJsons(constantDefinition.getConfigDefinition(), false);
            constantDefinition.setConfigs(configJsons);
        }
        super.generate(classDefinition);
    }

    @Override
    protected void processClass(ClassDefinition classDefinition) {
        if (classDefinition instanceof ConfigDefinition) {
            ConfigDefinition configDefinition = (ConfigDefinition) classDefinition;
            for (FieldDefinition fieldDefinition : configDefinition.getSelfFields()) {
                processField(classDefinition, fieldDefinition);
            }
            ConfigDefinition parentDefinition = configDefinition.getParentConfig();
            if (parentDefinition != null && !parentDefinition.getFullPackageName(supportLanguage()).equals(configDefinition.getFullPackageName(supportLanguage()))) {
                configDefinition.getImports().add(parentDefinition.getFullName(supportLanguage()));
            }
        } else if (classDefinition instanceof ConstantDefinition) {
            processConstantDependency((ConstantDefinition) classDefinition);
        } else {
            super.processClass(classDefinition);
        }
    }

    protected void processConstantDependency(ConstantDefinition constantDefinition) {
        FieldDefinition valueField = constantDefinition.getValueField();
        if (valueField.isCollectionType()) {
            constantDefinition.getImports().add("java.util.*");
            if (!valueField.isBuiltinValueType()) {
                constantDefinition.getImports().add(valueField.getValueBean().getFullName(supportLanguage()));
            }
        } else if (!valueField.isBuiltinType()) {
            constantDefinition.getImports().add(valueField.getClassDefinition().getFullName(supportLanguage()));
        }
    }

    /**
     * 初始化配置加载器，读取常量key用于常量类生成
     */
    protected void initConfigLoader(TableType tableType, String tablePath) {
        if (definitionParser == null) {
            throw new IllegalArgumentException(category().comment() + "的定义解析器[definitionParser]不能为空");
        }
        configLoader = new WithDefinitionConfigLoader(tablePath);
        configLoader.setDefinitionParser(definitionParser);
        configLoader.setTableType(tableType);
    }
}
