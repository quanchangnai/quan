package quan.generator.config;

import com.alibaba.fastjson.JSONObject;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import quan.config.TableType;
import quan.config.load.DefinitionConfigLoader;
import quan.config.read.ConfigConverter;
import quan.definition.BeanDefinition;
import quan.definition.Category;
import quan.definition.ClassDefinition;
import quan.definition.Language;
import quan.definition.config.ConfigDefinition;
import quan.definition.config.ConstantDefinition;
import quan.definition.parser.DefinitionParser;
import quan.definition.parser.TableDefinitionParser;
import quan.generator.Generator;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;


/**
 * 配置生成器
 */
public abstract class ConfigGenerator extends Generator {

    //配置加载器用于生成常量
    protected DefinitionConfigLoader configLoader;

    protected String definitionType;

    protected String tableType;

    protected String tablePath;

    protected String tableBodyStartRow;

    public ConfigGenerator(Properties options) {
        super(options);
    }

    public void setTableType(String tableType) {
        this.tableType = tableType;
    }

    public void setTableType(TableType tableType) {
        this.tableType = tableType.name();
    }

    public void setTablePath(String tablePath) {
        this.tablePath = tablePath;
    }

    @Override
    public final Category category() {
        return Category.config;
    }


    @Override
    public void setParser(DefinitionParser parser) {
        super.setParser(parser);
        if (enable) {
            checkOptions();
        }
    }

    @Override
    protected void parseOptions(Properties options) {
        super.parseOptions(options);

        if (!enable) {
            return;
        }

        String optionPrefix = optionPrefix(false);

        definitionType = options.getProperty(optionPrefix + "definitionType");
        if (StringUtils.isBlank(definitionType)) {
            definitionType = "xml";
        }

        if (parser != null) {
            parser.setConfigNamePattern(options.getProperty(optionPrefix + "namePattern"));
            parser.setConstantNamePattern(options.getProperty(optionPrefix + "constantNamePattern"));
            if (parser instanceof TableDefinitionParser) {
                TableDefinitionParser tableDefinitionParser = (TableDefinitionParser) parser;
                for (String language : Language.names()) {
                    String alias = options.getProperty(optionPrefix + language + ".alias");
                    if (!StringUtils.isBlank(alias)) {
                        tableDefinitionParser.getLanguageAliases().put(language, alias);
                    }
                }
            }
        }

        tableType = options.getProperty(optionPrefix + "tableType");
        tablePath = options.getProperty(optionPrefix + "tablePath");
        tableBodyStartRow = options.getProperty(optionPrefix + "tableBodyStartRow");

        ConfigConverter.setDatetimePattern(options.getProperty(optionPrefix + "datetimePattern"));
        ConfigConverter.setDatePattern(options.getProperty(optionPrefix + "datePattern"));
        ConfigConverter.setTimePattern(options.getProperty(optionPrefix + "timePattern"));

    }

    @Override
    protected void checkOptions() {
        super.checkOptions();
        if (tableType == null) {
            throw new IllegalArgumentException(category().alias() + "的表格类型[tableType]不能为空");
        }

        try {
            TableType.valueOf(tableType);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(category().alias() + "的表格类型[tableType]不合法,当前值:" + tableType + ",合法值:" + Arrays.toString(TableType.values()));
        }
        if (StringUtils.isBlank(tablePath)) {
            throw new IllegalArgumentException(category().alias() + "的表格文件路径[tablePath]不能为空");
        }

        if (parser != null) {
            if (parser instanceof TableDefinitionParser) {
                ((TableDefinitionParser) parser).checkLanguageAlias();
            }

            int minTableBodyStartRow = parser.getMinTableBodyStartRow();
            if (!StringUtils.isBlank(this.tableBodyStartRow)) {
                try {
                    int tableBodyStartRow = Integer.parseInt(this.tableBodyStartRow);
                    Validate.isTrue(tableBodyStartRow >= minTableBodyStartRow);
                } catch (Exception e) {
                    throw new IllegalArgumentException(category().alias() + "的表格正文开始行号[tableBodyStartRow]不合法，合法值为空值或者大于等于" + minTableBodyStartRow + "的整数");
                }
            } else {
                tableBodyStartRow = String.valueOf(minTableBodyStartRow);
            }
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
    protected void initFreemarker() {
        super.initFreemarker();
        try {
            Template configTemplate = freemarkerCfg.getTemplate("config." + language() + ".ftl");
            Template constantTemplate = freemarkerCfg.getTemplate("constant." + language() + ".ftl");

            templates.put(BeanDefinition.class, configTemplate);
            templates.put(ConfigDefinition.class, configTemplate);
            templates.put(ConstantDefinition.class, constantTemplate);
        } catch (IOException e) {
            logger.error("", e);
        }
    }

    /**
     * 初始化配置加载器，读取常量key用于常量类生成
     */
    protected void initConfigLoader(TableType tableType, String tablePath, int tableBodyStartRow) {
        if (parser == null) {
            throw new IllegalArgumentException(category().alias() + "的定义解析器[definitionParser]不能为空");
        }
        configLoader = new DefinitionConfigLoader(tablePath);
        configLoader.setParser(parser);
        configLoader.setTableType(tableType);
        configLoader.setTableBodyStartRow(tableBodyStartRow);
    }

    @Override
    public void generate(boolean printErrors) {
        if (!enable) {
            return;
        }
        int tableBodyStartRow = 0;
        if (!StringUtils.isBlank(this.tableBodyStartRow)) {
            tableBodyStartRow = Integer.parseInt(this.tableBodyStartRow);
        }
        initConfigLoader(TableType.valueOf(tableType), tablePath, tableBodyStartRow);
        super.generate(printErrors);
    }

    @Override
    protected void prepareClass(ClassDefinition classDefinition) {
        super.prepareClass(classDefinition);
        if (classDefinition instanceof ConstantDefinition) {
            prepareConstant((ConstantDefinition) classDefinition);

        }
    }

    protected void prepareConstant(ConstantDefinition constantDefinition) {
        prepareField(constantDefinition.getValueField());
        if (configLoader != null) {
            constantDefinition.updateVersion(configLoader.getConfigVersion(constantDefinition.getOwnerDefinition(), false));
            if (checkChange(constantDefinition)) {
                List<JSONObject> configJsons = configLoader.loadJsons(constantDefinition.getOwnerDefinition(), false);
                constantDefinition.setConfigs(configJsons);
            }
        }
    }

}
