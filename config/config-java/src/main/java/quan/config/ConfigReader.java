package quan.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.common.util.PathUtils;
import quan.generator.FieldDefinition;
import quan.generator.config.ConfigDefinition;

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

    //表格正文起始行号，默认是第3行,第1行固定是表头，中间是注释等，行号从1开始
    protected int bodyRowNum = 3;

    protected Config prototype;

    protected List<JSONObject> jsons = new ArrayList<>();

    protected LinkedHashSet<String> validatedErrors = new LinkedHashSet<>();

    private List<Config> configs = new ArrayList<>();

    protected ConfigReader() {
    }

    public ConfigReader(String tablePath, String table, ConfigDefinition configDefinition) {
        init(tablePath, table, configDefinition);
    }

    protected void init(String tablePath, String tableFileName, ConfigDefinition configDefinition) {
        tablePath = PathUtils.crossPlatPath(tablePath);
        tableFileName = PathUtils.crossPlatPath(tableFileName);
        this.tableFile = new File(tablePath, tableFileName);
        this.table = tableFileName.substring(0, tableFileName.lastIndexOf("."));
        this.configDefinition = configDefinition;

        initPrototype();
    }

    protected void initPrototype() {
        try {
            Class<Config> configClass = (Class<Config>) Class.forName(configDefinition.getFullName());
            prototype = configClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            logger.error("实例化配置类[{}]失败", configDefinition.getFullName(), e);
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
            Config config = prototype.create();
            config.parse(json);
            configs.add(config);
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

            if (fieldType.equals("map") || fieldDefinition.isBeanType()) {
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
                fieldValue = ConfigConverter.convertColumnBean(fieldDefinition, rowJson.getJSONObject(fieldDefinition.getName()), columnValue);
            } else if (fieldDefinition.getType().equals("map")) {
                fieldValue = ConfigConverter.convertColumnMap(fieldDefinition,rowJson, columnValue);
            } else {
                fieldValue = ConfigConverter.convert(fieldDefinition, columnValue);
            }
        } catch (Exception e) {
            handleConvertException(e, columnName, columnValue, row, column);
            return;
        }

        if (fieldType.equals("list") || fieldType.equals("set")) {
            JSONArray jsonArray = rowJson.getJSONArray(fieldName);
            if (jsonArray == null) {
                rowJson.put(fieldName, fieldValue);
            } else if (fieldValue instanceof JSONArray) {
                jsonArray.addAll((JSONArray) fieldValue);
            }
        } else {
            rowJson.put(fieldName, fieldValue);
        }

        //时间类型字段字符串格式
        if (fieldDefinition.isTimeType()) {
            rowJson.put(fieldName + "$Str", ConfigConverter.convertTimeType(fieldDefinition.getType(), (Date) fieldValue));
        }
    }

    protected void handleConvertException(Exception e, String columnName, String columnValue, int row, int column) {
        if (!(e instanceof ConvertException)) {
            validatedErrors.add(String.format("配置[%s]的第%d行第%d列[%s]数据[%s]格式错误", table, row, column, columnName, columnValue));
            return;
        }

        ConvertException convertException = (ConvertException) e;
        switch (convertException.getErrorType()) {
            case enumName:
                validatedErrors.add(String.format("配置[%s]的第%d行第%d列[%s]枚举名[%s]不合法", table, row, column, columnName, columnValue));
                break;
            case enumValue:
                validatedErrors.add(String.format("配置[%s]的第%d行第%d列[%s]枚举值[%s]不合法", table, row, column, columnName, columnValue));
                break;
            default:
                break;
        }
    }

}
