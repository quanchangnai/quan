package quan.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.common.util.PathUtils;
import quan.generator.BeanDefinition;
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

    protected List<JSONObject> jsons = new ArrayList<>();

    protected LinkedHashSet<String> validatedErrors = new LinkedHashSet<>();

    private List<Config> configs = new ArrayList<>();

    private Config prototype;

    public ConfigReader(String tablePath, String table, ConfigDefinition configDefinition) {
        tablePath = PathUtils.crossPlatPath(tablePath);
        table = PathUtils.crossPlatPath(table);
        this.tableFile = new File(tablePath, table);
        this.table = table.substring(0, table.lastIndexOf("."));
        this.configDefinition = configDefinition;

        try {
            Class<Config> configClass = (Class<Config>) Class.forName(configDefinition.getFullName());
            prototype = configClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            logger.error("实例化配置类[{}]失败", configDefinition.getFullName(), e);
        }
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

        for (String columnName : columns) {
            FieldDefinition fieldDefinition = configDefinition.getColumnFields().get(columnName);
            if (fieldDefinition != null) {
                fields.remove(fieldDefinition);
            }
        }

        for (FieldDefinition field : fields) {
            String error = String.format("配置[%s]缺少字段[%s]对应的列[%s]", table, field.getName(), field.getColumn());
            validatedErrors.add(error);
        }
    }

    protected void addColumnToRow(JSONObject rowJson, String columnName, String columnValue, int row, int column) {
        FieldDefinition fieldDefinition = configDefinition.getColumnFields().get(columnName);
        if (fieldDefinition == null) {
            return;
        }

        String fieldName = fieldDefinition.getName();
        String fieldType = fieldDefinition.getType();
        Object fieldValue;

        try {
            fieldValue = convert(fieldDefinition, columnValue);
        } catch (Exception e) {
            validatedErrors.add(String.format("配置[%s]的第%d行第%d列[%s]数据[%s]格式错误", table, row, column, columnName, columnValue));
            return;
        }

        if (fieldType.equals("list") || fieldType.equals("set")) {
            JSONArray jsonArray = rowJson.getJSONArray(fieldName);
            if (jsonArray == null) {
                rowJson.put(fieldName, fieldValue);
            } else {
                jsonArray.addAll((JSONArray) fieldValue);
            }
        } else {
            rowJson.put(fieldName, fieldValue);
        }
    }

    public static Object convert(FieldDefinition fieldDefinition, String value) {
        String type = fieldDefinition.getType();
        if (fieldDefinition.isPrimitiveType()) {
            return convertPrimitiveType(fieldDefinition.getType(), value);
        } else if (type.equals("list")) {
            return convertList(fieldDefinition, value);
        } else if (type.equals("set")) {
            return convertSet(fieldDefinition, value);
        } else if (type.equals("map")) {
            return convertMap(fieldDefinition, value);
        } else if (fieldDefinition.isBeanType()) {
            return convertBean(fieldDefinition.getBean(), value);
        }
        return value;
    }

    public static Object convertPrimitiveType(String type, String value) {
        switch (type) {
            case "bool":
                return Boolean.parseBoolean(value.toLowerCase());
            case "short":
                return value.equals("") ? 0 : Short.parseShort(value);
            case "int":
                return value.equals("") ? 0 : Integer.parseInt(value);
            case "long":
                return value.equals("") ? 0 : Long.parseLong(value);
            case "float":
                return value.equals("") ? 0 : Float.parseFloat(value);
            case "double":
                return value.equals("") ? 0 : Double.parseDouble(value);
            default:
                return value;
        }
    }

    public static JSONArray convertList(FieldDefinition fieldDefinition, String value) {
        String[] values = value.split(fieldDefinition.getEscapedDelimiter());
        return convertArray(fieldDefinition, values);
    }

    public static JSONArray convertArray(FieldDefinition fieldDefinition, String[] values) {
        JSONArray array = new JSONArray();
        for (String v : values) {
            if (fieldDefinition.isPrimitiveValueType()) {
                array.add(convertPrimitiveType(fieldDefinition.getValueType(), v));
            } else {
                array.add(convertBean(BeanDefinition.getBean(fieldDefinition.getValueType()), v));
            }
        }
        return array;
    }

    public static JSONArray convertSet(FieldDefinition fieldDefinition, String value) {
        //set需要去重
        String[] values = value.split(fieldDefinition.getEscapedDelimiter());
        Set<String> setValues = new HashSet<>(Arrays.asList(values));
        return convertArray(fieldDefinition, setValues.toArray(new String[0]));
    }

    public static JSONObject convertMap(FieldDefinition fieldDefinition, String value) {
        String[] values = value.split(fieldDefinition.getEscapedDelimiter());

        JSONObject object = new JSONObject();
        for (int i = 0; i < values.length; i = i + 2) {
            Object k = convertPrimitiveType(fieldDefinition.getKeyType(), values[i]);
            Object v;
            if (fieldDefinition.isPrimitiveValueType()) {
                v = convertPrimitiveType(fieldDefinition.getValueType(), values[i + 1]);
            } else {
                v = convertBean(BeanDefinition.getBean(fieldDefinition.getValueType()), values[i + 1]);
            }
            object.put(k.toString(), v);
        }

        return object;
    }

    public static JSONObject convertBean(BeanDefinition beanDefinition, String value) {
        String[] values = value.split(beanDefinition.getEscapedDelimiter());

        JSONObject object = new JSONObject();
        for (int i = 0; i < beanDefinition.getFields().size(); i++) {
            FieldDefinition fieldDefinition = beanDefinition.getFields().get(i);
            Object v = convert(fieldDefinition, values[i]);
            object.put(fieldDefinition.getName(), v);
        }

        return object;
    }

}
