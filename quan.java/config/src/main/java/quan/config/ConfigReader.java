package quan.config;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.definition.FieldDefinition;
import quan.definition.config.ConfigDefinition;

import java.io.File;
import java.util.*;

/**
 * Created by quanchangnai on 2019/7/11.
 */
@SuppressWarnings({"unchecked"})
public abstract class ConfigReader {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected String table;

    protected File tableFile;

    protected ConfigDefinition configDefinition;

    protected ConfigConverter converter;

    //表格正文起始行号，默认是第3行,第1行固定是表头，中间是注释等，行号从1开始
    protected int bodyRowNum = 3;

    protected Config prototype;

    protected List<JSONObject> jsons = new ArrayList<>();

    protected LinkedHashSet<String> validatedErrors = new LinkedHashSet<>();

    private List<Config> configs = new ArrayList<>();

    protected ConfigReader() {
    }

    public ConfigReader(File tableFile, ConfigDefinition configDefinition) {
        init(tableFile, configDefinition);
    }

    protected void init(File tableFile, ConfigDefinition configDefinition) {
        this.tableFile = tableFile;
        this.table = tableFile.getName().substring(0, tableFile.getName().lastIndexOf("."));
        this.configDefinition = configDefinition;
        if (configDefinition != null) {
            converter = new ConfigConverter(configDefinition.getParser());
        }

        initPrototype();
    }

    protected void initPrototype() {
        try {
            Class<Config> configClass = (Class<Config>) Class.forName(configDefinition.getFullName());
            prototype = configClass.getDeclaredConstructor(JSONObject.class).newInstance(new JSONObject());
        } catch (Exception e) {
//            logger.error("实例化配置类[{}]失败", configDefinition.getFullName(), e);
        }
    }

    public Config getPrototype() {
        return prototype;
    }

    public ConfigReader setBodyRowNum(int bodyRowNum) {
        if (bodyRowNum > 1) {
            this.bodyRowNum = bodyRowNum;
        }
        return this;
    }

    public ConfigDefinition getConfigDefinition() {
        return configDefinition;
    }

    public List<JSONObject> readJsons() {
        if (jsons.isEmpty()) {
            read();
        }
        return jsons;
    }


    public List<Config> readObjects() {
        if (prototype == null || !configs.isEmpty()) {
            return configs;
        }

        readJsons();

        for (JSONObject json : jsons) {
            configs.add(prototype.create(json));
        }

        return configs;
    }

    public List<String> getValidatedErrors() {
        return new ArrayList<>(validatedErrors);
    }

    protected abstract void read();

    public void clear() {
        jsons.clear();
        configs.clear();
        validatedErrors.clear();
    }

    protected void validateColumnNames(List<String> columns) {
        Set<FieldDefinition> fields = new HashSet<>(configDefinition.getFields());
        Set<String> validatedColumns = new HashSet<>();
        Map<FieldDefinition, Integer> mapAndBeanFieldColumnNums = new HashMap<>();

        for (String columnName : columns) {
            FieldDefinition fieldDefinition = configDefinition.getColumnFields().get(columnName);
            if (fieldDefinition == null) {
                continue;
            }
            fields.remove(fieldDefinition);

            String fieldType = fieldDefinition.getType();

            if (validatedColumns.contains(columnName) && !fieldDefinition.isCollectionType() && !fieldDefinition.isBeanType()) {
                validatedErrors.add(String.format("配置[%s]的字段类型[%s]不支持对应多列[%s]", table, fieldType, columnName));
            }

            validatedColumns.add(columnName);

            if (fieldDefinition.isCollectionType() || fieldDefinition.isBeanType()) {
                Integer columnNums = mapAndBeanFieldColumnNums.getOrDefault(fieldDefinition, 0);
                mapAndBeanFieldColumnNums.put(fieldDefinition, columnNums + 1);
            }
        }

        for (FieldDefinition fieldDefinition : mapAndBeanFieldColumnNums.keySet()) {
            Integer columnNum = mapAndBeanFieldColumnNums.get(fieldDefinition);
            if (columnNum != 1 && fieldDefinition.isBeanType() && columnNum != fieldDefinition.getBean().getFields().size()) {
                validatedErrors.add(String.format("配置[%s]的字段类型[%s]要么对应列数非法，要么单独对应1列，要么按字段拆开对应%s列", table, fieldDefinition.getType(), fieldDefinition.getBean().getFields().size()));
                fieldDefinition.setColumnNum(0);
            } else if (columnNum != 1 && columnNum % 2 != 0 && fieldDefinition.getType().equals("map")) {
                validatedErrors.add(String.format("配置[%s]的字段类型[%s]要么对应列数非法，要么单独对应1列，要么按键值对拆开对应偶数列", table, fieldDefinition.getType()));
                fieldDefinition.setColumnNum(0);
            } else {
                fieldDefinition.setColumnNum(columnNum);
            }
        }

        for (FieldDefinition field : fields) {
            validatedErrors.add(String.format("配置[%s]缺少字段[%s]对应的列[%s]", table, field.getName(), field.getColumn()));
        }
    }

    protected void addColumnToRow(JSONObject rowJson, String columnName, String columnValue, int row, int column) {
        FieldDefinition fieldDefinition = configDefinition.getColumnFields().get(columnName);
        if (fieldDefinition == null) {
            return;
        }
        if (!fieldDefinition.isLegalColumnNum()) {
            //Bean或者map类型字段对应的列数不合法
            return;
        }

        String fieldName = fieldDefinition.getName();
        String fieldType = fieldDefinition.getType();
        Object fieldValue;

        try {
            if (fieldDefinition.isBeanType()) {
                fieldValue = converter.convertColumnBean(fieldDefinition, rowJson.getJSONObject(fieldDefinition.getName()), columnValue);
            } else if (fieldDefinition.getType().equals("map")) {
                fieldValue = converter.convertColumnMap(fieldDefinition, rowJson, columnValue);
            } else if (fieldType.equals("list") || fieldType.equals("set")) {
                fieldValue = converter.convertColumnArray(fieldDefinition, rowJson, columnValue);
            } else {
                fieldValue = converter.convert(fieldDefinition, columnValue);
            }
        } catch (Exception e) {
            handleConvertException(e, columnName, columnValue, row, column);
            return;
        }

        //索引字段不能为空
        if (fieldValue == null) {
            if (configDefinition.isIndexField(fieldDefinition) && !configDefinition.isConstantKeyField(fieldDefinition)) {
                validatedErrors.add(String.format("配置[%s]的第%d行第%d列[%s]的索引值不能为空", table, row, column, columnName));
            }
            return;
        }

        rowJson.put(fieldName, fieldValue);

        //时间类型字段字符串格式
        if (fieldDefinition.isTimeType() && fieldValue instanceof Date) {
            rowJson.put(fieldName + "$Str", converter.convertTimeType(fieldDefinition.getType(), (Date) fieldValue));
        }
    }

    protected void handleConvertException(Exception e, String columnName, String columnValue, int row, int column) {
        String commonError = String.format("配置[%s]的第%d行第%d列[%s]数据[%s]错误", table, row, column, columnName, columnValue);
        if (columnValue.length() > 20) {
            commonError = String.format("配置[%s]的第%d行第%d列[%s]数据错误", table, row, column, columnName);
        }
        if (!(e instanceof ConvertException)) {
            validatedErrors.add(commonError);
            return;
        }

        ConvertException e1 = (ConvertException) e;
        switch (e1.getErrorType()) {
            case enumName:
                validatedErrors.add(String.format(commonError + ",枚举名[%s]不合法", e1.getParam(0)));
                break;
            case enumValue:
                validatedErrors.add(String.format(commonError + ",枚举值[%s]不合法", e1.getParam(0)));
                break;
            case setDuplicateValue:
                validatedErrors.add(String.format(commonError + ",set不能有重复值%s", e1.getParams()));
                break;
            case mapInvalidKey:
                validatedErrors.add(String.format(commonError + ",map不能有无效键[%s]", e1.getParam(0)));
                break;
            case mapInvalidValue:
                validatedErrors.add(String.format(commonError + ",map不能有无效值[%s]", e1.getParam(0)));
                break;
            case mapDuplicateKey:
                validatedErrors.add(String.format(commonError + ",map不能有重复键[%s]", e1.getParam(0)));
                break;
            default:
                validatedErrors.add(commonError);
                break;
        }
    }

}
