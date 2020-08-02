package quan.generator.config;

import com.alibaba.fastjson.JSONObject;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;
import quan.config.TableType;
import quan.config.loader.WithDefinitionConfigLoader;
import quan.config.reader.ConfigConverter;
import quan.definition.BeanDefinition;
import quan.definition.Category;
import quan.definition.ClassDefinition;
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

    protected String tableType;

    protected String tablePath;

    protected String tableBodyStartRow;

    public ConfigGenerator() {
    }

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
    protected void initOptions(Properties options) {
        super.initOptions(options);
        if (!enable) {
            return;
        }

        tableType = options.getProperty(category() + ".tableType");
        tablePath = options.getProperty(category() + ".tablePath");
        tableBodyStartRow = options.getProperty(category() + ".tableBodyStartRow");

        ConfigConverter.setDateTimePattern(options.getProperty(category() + ".dateTimePattern"));
        ConfigConverter.setDatePattern(options.getProperty(category() + ".datePattern"));
        ConfigConverter.setTimePattern(options.getProperty(category() + ".timePattern"));

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

        if (!StringUtils.isBlank(tableBodyStartRow)) {
            try {
                if (Integer.parseInt(tableBodyStartRow) <= 1) {
                    throw new Exception();
                }
            } catch (Exception e) {
                throw new IllegalArgumentException(category().alias() + "的表格正文开始行号[tableBodyStartRow]不合法，合法值为空值或者大于1的整数");
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
        configLoader = new WithDefinitionConfigLoader(tablePath);
        configLoader.setParser(parser);
        configLoader.setTableType(tableType);
        configLoader.setTableBodyStartRow(tableBodyStartRow);
    }

    @Override
    public void generate(boolean printError) {
        int tableBodyStartRow = 0;
        if (!StringUtils.isBlank(this.tableBodyStartRow)) {
            tableBodyStartRow = Integer.parseInt(this.tableBodyStartRow);
        }
        initConfigLoader(TableType.valueOf(tableType), tablePath, tableBodyStartRow);
        super.generate(printError);
    }

    @Override
    protected void prepareClass(ClassDefinition classDefinition) {
        super.prepareClass(classDefinition);
        if (classDefinition instanceof ConstantDefinition) {
            ConstantDefinition constantDefinition = (ConstantDefinition) classDefinition;
            prepareConstant(constantDefinition);
            if (configLoader != null) {
                List<JSONObject> configJsons = configLoader.loadJsons(constantDefinition.getConfigDefinition(), false);
                constantDefinition.setConfigs(configJsons);
            }
        }
    }

    protected void prepareConstant(ConstantDefinition constantDefinition) {
        prepareField(constantDefinition.getValueField());
    }

}
