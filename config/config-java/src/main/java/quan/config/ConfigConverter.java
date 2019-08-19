package quan.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import quan.generator.BeanDefinition;
import quan.generator.DefinitionParser;
import quan.generator.EnumDefinition;
import quan.generator.FieldDefinition;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by quanchangnai on 2019/8/7.
 */
public class ConfigConverter {

    private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy.MM.dd hh.mm.ss");

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");

    private static SimpleDateFormat timeFormat = new SimpleDateFormat("hh.mm.ss");

    private DefinitionParser definitionParser;

    public static void setDateTimePattern(String pattern) {
        dateTimeFormat = new SimpleDateFormat(pattern);
    }

    public static String getDateTimePattern() {
        return dateTimeFormat.toPattern();
    }

    public static void setDatePattern(String pattern) {
        dateFormat = new SimpleDateFormat(pattern);
    }

    public static String getDatePattern() {
        return dateFormat.toPattern();
    }

    public static void setTimePattern(String pattern) {
        timeFormat = new SimpleDateFormat(pattern);
    }

    public static String getTimePattern() {
        return timeFormat.toPattern();
    }

    public ConfigConverter(DefinitionParser definitionParser) {
        this.definitionParser = definitionParser;
    }

    public Object convert(FieldDefinition fieldDefinition, String value) {
        String type = fieldDefinition.getType();
        if (fieldDefinition.isPrimitiveType()) {
            return convertPrimitiveType(fieldDefinition.getType(), value);
        } else if (fieldDefinition.isTimeType()) {
            return convertTimeType(fieldDefinition.getType(), value);
        } else if (type.equals("list")) {
            return convertList(fieldDefinition, value);
        } else if (type.equals("set")) {
            return convertSet(fieldDefinition, value);
        } else if (type.equals("map")) {
            return convertMap(fieldDefinition, value);
        } else if (fieldDefinition.isBeanType()) {
            return convertBean(fieldDefinition.getBean(), value);
        } else if (fieldDefinition.isEnumType()) {
            return convertEnumType(fieldDefinition.getEnum(), value);
        }
        return value;
    }

    public Object convertColumnBean(FieldDefinition fieldDefinition, JSONObject object, String columnValue) {
        BeanDefinition beanDefinition = fieldDefinition.getBean();

        //Bean类型字段对应1列
        if (fieldDefinition.getColumnNum() == 1) {
            return convertBean(beanDefinition, columnValue);
        }

        //Bean类型字段对应多列
        if (object == null) {
            object = new JSONObject();
        }

        for (FieldDefinition beanField : beanDefinition.getFields()) {
            if (!object.containsKey(beanField.getName())) {
                object.put(beanField.getName(), convert(beanField, columnValue));
                break;
            }
        }

        return object;
    }

    /**
     * 转换枚举字段，支持枚举值或者枚举名
     */
    private Object convertEnumType(EnumDefinition enumDefinition, String value) {
        int enumValue = 0;
        try {
            enumValue = Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
        }

        FieldDefinition enumField;

        if (enumValue > 0) {
            enumField = enumDefinition.getField(enumValue);
            if (enumField == null) {
                throw new ConvertException(ConvertException.ErrorType.enumValue, value);
            }
            return enumValue;
        }

        enumField = enumDefinition.getField(value);
        if (enumField == null) {
            throw new ConvertException(ConvertException.ErrorType.enumName, value);
        }
        enumValue = Integer.parseInt(enumField.getValue());

        return enumValue;
    }

    private Object convertPrimitiveType(String type, String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        switch (type) {
            case "bool":
                return Boolean.parseBoolean(value.toLowerCase());
            case "short":
                return Short.parseShort(value);
            case "int":
                return Integer.parseInt(value);
            case "long":
                return Long.parseLong(value);
            case "float":
                return Float.parseFloat(value);
            case "double":
                return Double.parseDouble(value);
            default:
                return value;
        }
    }

    private Date convertTimeType(String type, String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        try {
            if (type.equals("datetime")) {
                //日期加时间
                return dateTimeFormat.parse(value);
            } else if (type.equals("date")) {
                //纯日期
                return dateFormat.parse(value);
            }
            //纯时间
            return timeFormat.parse(value);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public String convertTimeType(String type, Date value) {
        if (value == null) {
            return null;
        }
        if (type.equals("datetime")) {
            return dateTimeFormat.format(value);
        }
        if (type.equals("date")) {
            return dateFormat.format(value);
        }
        return timeFormat.format(value);

    }

    private JSONArray convertList(FieldDefinition fieldDefinition, String value) {
        if (StringUtils.isBlank(value)) {
            return new JSONArray();
        }
        String[] values = value.split(fieldDefinition.getEscapedDelimiter());
        return convertArray(fieldDefinition, values);
    }

    private JSONArray convertSet(FieldDefinition fieldDefinition, String value) {
        if (StringUtils.isBlank(value)) {
            return new JSONArray();
        }

        String[] values = value.split(fieldDefinition.getEscapedDelimiter());
        Set<String> setValues = new HashSet<>();
        Set<String> duplicateValues = new HashSet<>();

        for (String v : values) {
            if (setValues.contains(v)) {
                duplicateValues.add(v);
            }
            setValues.add(v);
        }

        if (!duplicateValues.isEmpty()) {
            throw new ConvertException(ConvertException.ErrorType.setDuplicateValue, new ArrayList<>(duplicateValues));
        }

        return convertArray(fieldDefinition, setValues.toArray(new String[0]));
    }

    private JSONArray convertArray(FieldDefinition fieldDefinition, String[] values) {
        JSONArray array = new JSONArray();
        for (String v : values) {
            Object o;
            if (fieldDefinition.isPrimitiveValueType()) {
                o = convertPrimitiveType(fieldDefinition.getValueType(), v);
            } else {
                o = convertBean(definitionParser.getBean(fieldDefinition.getValueType()), v);
            }
            if (o != null) {
                array.add(o);
            }
        }
        return array;
    }

    public JSONArray convertColumnArray(FieldDefinition fieldDefinition, JSONObject rowJson, String value) {
        JSONArray array = rowJson.getJSONArray(fieldDefinition.getName());
        if (array == null) {
            array = new JSONArray();
            rowJson.put(fieldDefinition.getName(), array);
        }

        if (fieldDefinition.getType().equals("list")) {
            array.addAll(convertList(fieldDefinition, value));
            return array;
        }

        //set
        JSONArray setArray = convertSet(fieldDefinition, value);
        if (fieldDefinition.getColumnNum() == 1) {
            array.addAll(setArray);
            return array;
        }

        String[] values = value.split(fieldDefinition.getEscapedDelimiter());
        Set<Object> set = new HashSet<>(array);
        Set<String> duplicate = new HashSet<>();
        for (int i = 0; i < setArray.size(); i++) {
            Object o = setArray.get(i);
            if (set.contains(o)) {
                duplicate.add(values[i]);
            } else {
                array.add(o);
            }
        }
        if (!duplicate.isEmpty()) {
            throw new ConvertException(ConvertException.ErrorType.setDuplicateValue, new ArrayList<>(duplicate));
        }

        return array;
    }

    public JSONObject convertColumnMap(FieldDefinition fieldDefinition, JSONObject rowJson, String value) {
        //map类型字段对应1列
        if (fieldDefinition.getColumnNum() == 1) {
            return convertMap(fieldDefinition, value);
        }

        //map类型字段对应多列
        JSONObject object = rowJson.getJSONObject(fieldDefinition.getName());
        if (object == null) {
            object = new JSONObject();
            rowJson.put(fieldDefinition.getName(), object);
        }

        if (object.containsKey(null)) {
            //上一次转换的key无效忽略这次的value
            object.remove(null);
            return object;
        }

        Object objectKey = null;

        for (String k : object.keySet()) {
            Object v = object.get(k);
            if (v == null) {
                objectKey = k;
                break;
            }
        }

        if (objectKey == null) {
            try {
                objectKey = convertPrimitiveType(fieldDefinition.getKeyType(), value);
            } catch (Exception ignored) {
            }
            if (objectKey == null) {
                object.put(null, null);//标记接下来的value作废
                throw new ConvertException(ConvertException.ErrorType.mapInvalidKey, value);
            } else if (object.containsKey(objectKey)) {
                throw new ConvertException(ConvertException.ErrorType.mapDuplicateKey, value);
            }
            object.put(objectKey.toString(), null);
        } else {
            Object objectValue = null;
            try {
                if (fieldDefinition.isPrimitiveValueType()) {
                    objectValue = convertPrimitiveType(fieldDefinition.getValueType(), value);
                } else {
                    objectValue = convertBean(definitionParser.getBean(fieldDefinition.getValueType()), value);
                }
            } catch (Exception ignored) {
            }
            if (objectValue == null) {
                //value无效删除对应的key
                object.remove(objectKey);
                throw new ConvertException(ConvertException.ErrorType.mapInvalidValue, value);
            }
            object.put(objectKey.toString(), objectValue);
        }

        return object;
    }

    private JSONObject convertMap(FieldDefinition fieldDefinition, String value) {
        JSONObject object = new JSONObject();
        if (StringUtils.isBlank(value)) {
            return object;
        }

        String[] values = value.split(fieldDefinition.getEscapedDelimiter());

        for (int i = 0; i < values.length; i = i + 2) {
            String vi = values[i];
            Object k = null;
            try {
                k = convertPrimitiveType(fieldDefinition.getKeyType(), vi);
            } catch (Exception ignored) {
            }
            if (k == null) {
                throw new ConvertException(ConvertException.ErrorType.mapInvalidKey, vi);
            }
            if (object.containsKey(k)) {
                throw new ConvertException(ConvertException.ErrorType.mapDuplicateKey, vi);
            }

            Object v = null;
            try {
                if (fieldDefinition.isPrimitiveValueType()) {
                    v = convertPrimitiveType(fieldDefinition.getValueType(), values[i + 1]);
                } else {
                    v = convertBean(definitionParser.getBean(fieldDefinition.getValueType()), values[i + 1]);
                }
            } catch (Exception ignored) {
            }
            if (v == null) {
                throw new ConvertException(ConvertException.ErrorType.mapInvalidValue, vi);
            }

            object.put(k.toString(), v);
        }

        return object;
    }

    private JSONObject convertBean(BeanDefinition beanDefinition, String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        JSONObject object = new JSONObject();
        String[] values = value.split(beanDefinition.getEscapedDelimiter());
        for (int i = 0; i < beanDefinition.getFields().size(); i++) {
            FieldDefinition fieldDefinition = beanDefinition.getFields().get(i);
            Object v = convert(fieldDefinition, values[i]);
            object.put(fieldDefinition.getName(), v);
        }

        return object;
    }

}
