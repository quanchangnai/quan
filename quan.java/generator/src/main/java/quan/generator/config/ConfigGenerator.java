package quan.generator.config;

import com.alibaba.fastjson.JSONObject;
import freemarker.template.Template;
import quan.config.TableType;
import quan.config.WithDefinitionConfigLoader;
import quan.definition.BeanDefinition;
import quan.definition.ClassDefinition;
import quan.definition.DefinitionCategory;
import quan.definition.FieldDefinition;
import quan.definition.config.ConfigDefinition;
import quan.definition.config.ConstantDefinition;
import quan.generator.Generator;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Created by quanchangnai on 2019/7/11.
 */
public abstract class ConfigGenerator extends Generator {

    //配置加载器用于生成常量
    protected WithDefinitionConfigLoader configLoader;

    public ConfigGenerator(String codePath) {
        super(codePath);

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
    public final DefinitionCategory category() {
        return DefinitionCategory.config;
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
            List<JSONObject> configJsons = configLoader.loadJsons(constantDefinition.getConfigDefinition(),false);
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
    public void initConfigLoader(TableType tableType, String tablePath) {
        Objects.requireNonNull(definitionParser, "必须先设置定义解析器");
        configLoader = new WithDefinitionConfigLoader(tablePath);
        configLoader.setDefinitionParser(definitionParser);
        configLoader.setTableType(tableType);
    }

    public void initConfigLoader(String tableType, String tablePath) {
        TableType tableTypeEnum;
        try {
            tableTypeEnum = TableType.valueOf(tableType);
        } catch (Exception e) {
            logger.error("表格类型错误，可用枚举值{}", Arrays.toString(TableType.values()));
            return;
        }
        initConfigLoader(tableTypeEnum, tablePath);
    }
}
