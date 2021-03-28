package quan.config.reader;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.config.Config;
import quan.definition.BeanDefinition;
import quan.definition.FieldDefinition;
import quan.definition.Language;
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

    //表格正文开始行号，默认是第3行，第1行固定是表头，中间是注释等，行号从1开始
    protected int tableBodyStartRow = 3;

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
        this.table = tableFile.getName();
        this.configDefinition = configDefinition;
        if (configDefinition != null) {
            converter = new ConfigConverter(configDefinition.getParser());
        }

        if (!tableFile.exists()) {
            validatedErrors.add(String.format("配置[%s]不存在", tableFile.getName()));
        }

        initPrototype();
    }

    protected void initPrototype() {
        try {
            Class<Config> configClass = (Class<Config>) Class.forName(configDefinition.getFullName(Language.java));
            prototype = configClass.getDeclaredConstructor(JSONObject.class).newInstance(new JSONObject());
        } catch (Exception e) {
//            logger.error("实例化配置类[{}]失败", configDefinition.getFullName(Language.java), e);
        }
    }

    public void setTable(String table) {
        if (!StringUtils.isBlank(table)) {
            this.table = table;
        }
    }

    public Config getPrototype() {
        return prototype;
    }

    public ConfigReader setTableBodyStartRow(int tableBodyStartRow) {
        if (tableBodyStartRow > 1) {
            this.tableBodyStartRow = tableBodyStartRow;
        }
        return this;
    }

    public ConfigDefinition getConfigDefinition() {
        return configDefinition;
    }

    public List<JSONObject> readJsons() {
        if (jsons.isEmpty() && tableFile.exists()) {
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
        configDefinition.getFields().forEach(f -> f.getColumnNums().clear());

        Set<FieldDefinition> lackingFields = new HashSet<>(configDefinition.getFields());
        Set<String> validatedColumns = new HashSet<>();

        for (int i = 0; i < columns.size(); i++) {
            String columnName = columns.get(i);
            FieldDefinition fieldDefinition = configDefinition.getColumnFields().get(columnName);
            if (fieldDefinition == null) {
                continue;
            }
            lackingFields.remove(fieldDefinition);

            if (validatedColumns.contains(columnName) && !fieldDefinition.isCollectionType() && !fieldDefinition.isBeanType()) {
                validatedErrors.add(String.format("配置[%s]的字段类型[%s]不支持对应多列[%s]", table, fieldDefinition.getType(), columnName));
            }

            validatedColumns.add(columnName);
            fieldDefinition.getColumnNums().add(i + 1);
        }

        for (FieldDefinition fieldDefinition : configDefinition.getFields()) {
            BeanDefinition fieldBean = fieldDefinition.getTypeBean();
            if (!fieldDefinition.isLegalColumnCount() && fieldBean != null) {
                validatedErrors.add(String.format("配置[%s]的字段类型[%s]对应列数非法，要么单独对应1列，要么按字段拆开对应%s列", table, fieldDefinition.getType(), fieldBean.getFields().size()));
            }
            if (!fieldDefinition.isLegalColumnCount() && fieldDefinition.getType().equals("map")) {
                validatedErrors.add(String.format("配置[%s]的字段类型[%s]对应列数非法，要么单独对应1列，要么按键值对拆开对应偶数列", table, fieldDefinition.getType()));
            }
        }

        for (FieldDefinition fieldDefinition : lackingFields) {
            validatedErrors.add(String.format("配置[%s]缺少字段[%s]对应的列[%s]", table, fieldDefinition.getName(), fieldDefinition.getColumn()));
        }
    }

    protected void addColumnToRow(JSONObject rowJson, String columnName, String columnValue, int row, int column) {
        FieldDefinition fieldDefinition = configDefinition.getColumnFields().get(columnName);
        if (fieldDefinition == null) {
            return;
        }
        if (!fieldDefinition.isLegalColumnCount()) {
            //Bean或者map类型字段对应的列数不合法
            return;
        }

        String fieldName = fieldDefinition.getName();
        String fieldType = fieldDefinition.getType();
        Object fieldValue;

        String columnStr = buildColumnStr(column);

        try {
            if (fieldDefinition.isBeanType()) {
                fieldValue = converter.convertColumnBean(fieldDefinition, rowJson.getJSONObject(fieldDefinition.getName()), columnValue, column);
            } else if (fieldDefinition.getType().equals("map")) {
                fieldValue = converter.convertColumnMap(fieldDefinition, rowJson, columnValue);
            } else if (fieldType.equals("list") || fieldType.equals("set")) {
                fieldValue = converter.convertColumnArray(fieldDefinition, rowJson, columnValue);
            } else {
                fieldValue = converter.convert(fieldDefinition, columnValue);
            }
        } catch (Exception e) {
            handleConvertException(e, columnName, columnValue, row, columnStr);
            return;
        }

        boolean constantKeyField = configDefinition.isConstantKeyField(fieldDefinition);
        if (fieldValue == null) {
            //索引字段不能为空，常量key除外
            if (configDefinition.isIndexField(fieldDefinition) && !constantKeyField) {
                validatedErrors.add(String.format("配置[%s]的第[%d]行第[%s]列[%s]的索引值不能为空", table, row, columnStr, columnName));
            }
            //没有默认值的必填字段校验
            boolean notNull = fieldDefinition.isEnumType() || fieldDefinition.isTimeType() || fieldDefinition.isBeanType() && fieldDefinition.getColumnNums().size() == 1;
            if (!fieldDefinition.isOptional() && notNull) {
                validatedErrors.add(String.format("配置[%s]的第[%d]行第[%s]列[%s]不能为空", table, row, columnStr, columnName));
            }
            return;
        } else {
            //必填字段校验
            if (fieldDefinition.isBeanType() && column == fieldDefinition.getLastColumnNum() && ((JSONObject) fieldValue).isEmpty()) {
                rowJson.remove(fieldName);
                if (!fieldDefinition.isOptional()) {
                    StringBuilder columnsStr = new StringBuilder();
                    for (int columnNum : fieldDefinition.getColumnNums()) {
                        if (columnNum != fieldDefinition.getColumnNums().get(0)) {
                            columnsStr.append(",");
                        }
                        columnsStr.append(buildColumnStr(columnNum));
                    }
                    validatedErrors.add(String.format("配置[%s]的第[%d]行第[%s]列[%s]不能为空", table, row, columnsStr.toString(), columnName));
                }
                return;
            }
        }

        if (constantKeyField && !FieldDefinition.NAME_PATTERN.matcher(fieldValue.toString()).matches()) {
            validatedErrors.add(String.format("配置[%s]的第[%d]行第[%s]列[%s]的常量key[%s]格式错误,正确格式:%s", table, row, column, columnName, fieldValue, FieldDefinition.NAME_PATTERN));
        }

        rowJson.put(fieldName, fieldValue);

        //时间类型字段字符串格式
        if (fieldDefinition.isTimeType() && fieldValue instanceof Date) {
            rowJson.put(fieldName + "_", converter.convertTimeType(fieldDefinition.getType(), (Date) fieldValue));
        }
    }

    private static String buildColumnStr(int c) {
        if (c < 1) {
            throw new IllegalArgumentException("参数[c]必须大于0");
        }

        StringBuilder s = new StringBuilder();
        int a = c, b;

        do {
            b = (a - 1) % 26 + 1;
            a = (a - 1) / 26;
            s.append((char) ('A' + b - 1));
        } while (a > 0);

        return c + "(" + s.reverse().toString() + ")";
    }

    protected void handleConvertException(Exception e, String columnName, String columnValue, int row, String columnStr) {
        String commonError = String.format("配置[%s]的第[%s]行第[%s]列[%s]数据[%s]错误", table, row, columnStr, columnName, columnValue);
        if (columnValue.isEmpty() || columnValue.length() > 20) {
            commonError = String.format("配置[%s]的第[%d]行第[%s]列[%s]数据错误", table, row, columnStr, columnName);
        }
        if (!(e instanceof ConvertException)) {
            validatedErrors.add(commonError);
            return;
        }

        ConvertException e1 = (ConvertException) e;
        switch (e1.getErrorType()) {
            case TYPE_ERROR:
                validatedErrors.add(String.format(commonError + ",[%s]不匹配期望类型[%s]", e1.getParam(0), e1.getParam(1)));
                break;
            case ENUM_NAME:
                validatedErrors.add(String.format(commonError + ",枚举名[%s]不合法", e1.getParam(0)));
                break;
            case ENUM_VALUE:
                validatedErrors.add(String.format(commonError + ",枚举值[%s]不合法", e1.getParam(0)));
                break;
            case SET_DUPLICATE_VALUE:
                validatedErrors.add(String.format(commonError + ",set不能有重复值%s", e1.getParams()));
                break;
            case MAP_INVALID_KEY:
                validatedErrors.add(String.format(commonError + ",map不能有无效键[%s]", e1.getParam(0)));
                break;
            case MAP_INVALID_VALUE:
                validatedErrors.add(String.format(commonError + ",map不能有无效值[%s]", e1.getParam(0)));
                break;
            case MAP_DUPLICATE_KEY:
                validatedErrors.add(String.format(commonError + ",map不能有重复键[%s]", e1.getParam(0)));
                break;
            default:
                validatedErrors.add(commonError);
                break;
        }
    }

}
