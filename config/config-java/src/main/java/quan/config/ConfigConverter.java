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
import java.util.Arrays;
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

    public  Object convert(FieldDefinition fieldDefinition, String value) {
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

    public  Object convertColumnBean(FieldDefinition fieldDefinition, JSONObject object, String columnValue) {
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
    public  Object convertEnumType(EnumDefinition enumDefinition, String value) {
        int enumValue = 0;
        try {
            enumValue = Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
        }

        if (enumValue > 0) {
            FieldDefinition enumValueField = enumDefinition.getField(enumValue);
            if (enumValueField == null) {
                throw new ConvertException(ConvertException.ErrorType.enumValue);
            }
            return enumValueField.getName();
        } else if (enumDefinition.getField(value) == null) {
            throw new ConvertException(ConvertException.ErrorType.enumName);
        }

        return value;
    }

    public  Object convertPrimitiveType(String type, String value) {
        if (StringUtils.isBlank(value)) {
            return type.equals("string") ? "" : null;
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

    public  Date convertTimeType(String type, String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        try {
            if (type.equals("datetime")) {
                //日期加时间
                return dateTimeFormat.parse(value);
            }
            if (type.equals("date")) {
                //纯日期
                return dateFormat.parse(value);
            }
            //纯时间
            return timeFormat.parse(value);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public  String convertTimeType(String type, Date value) {
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

    public  JSONArray convertList(FieldDefinition fieldDefinition, String value) {
        String[] values = value.split(fieldDefinition.getEscapedDelimiter());
        return convertArray(fieldDefinition, values);
    }

    public  JSONArray convertArray(FieldDefinition fieldDefinition, String[] values) {
        JSONArray array = new JSONArray();
        if (values.length == 1 && values[0].equals("")) {
            return array;
        }

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

    public  JSONObject convertColumnMap(FieldDefinition fieldDefinition, JSONObject rowJson, String value) {
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
                object.put(null, null);
                throw new NullPointerException("map" + fieldDefinition.getName4Validate() + "的键不能为空");
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
                throw new NullPointerException("map" + fieldDefinition.getName4Validate() + "的值不能为空");
            }
            object.put(objectKey.toString(), objectValue);
        }

        return object;
    }

    public  JSONArray convertSet(FieldDefinition fieldDefinition, String value) {
        //set需要去重
        String[] values = value.split(fieldDefinition.getEscapedDelimiter());
        Set<String> setValues = new HashSet<>(Arrays.asList(values));
        return convertArray(fieldDefinition, setValues.toArray(new String[0]));
    }

    public  JSONObject convertMap(FieldDefinition fieldDefinition, String value) {
        JSONObject object = new JSONObject();
        if (StringUtils.isBlank(value)) {
            return object;
        }

        String[] values = value.split(fieldDefinition.getEscapedDelimiter());
        for (int i = 0; i < values.length; i = i + 2) {
            Object k = convertPrimitiveType(fieldDefinition.getKeyType(), values[i]);
            Object v;
            if (fieldDefinition.isPrimitiveValueType()) {
                v = convertPrimitiveType(fieldDefinition.getValueType(), values[i + 1]);
            } else {
                v = convertBean(definitionParser.getBean(fieldDefinition.getValueType()), values[i + 1]);
            }
            if (k == null || v == null) {
                throw new NullPointerException("map的键或者值不能为空");
            }
            object.put(k.toString(), v);
        }

        return object;
    }

    public  JSONObject convertBean(BeanDefinition beanDefinition, String value) {
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
