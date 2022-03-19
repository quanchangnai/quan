package quan.config.reader;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import quan.definition.BeanDefinition;
import quan.definition.ClassDefinition;
import quan.definition.EnumDefinition;
import quan.definition.FieldDefinition;
import quan.definition.parser.DefinitionParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static quan.definition.ClassDefinition.getLongClassName;

/**
 * Created by quanchangnai on 2019/8/7.
 */
public class ConfigConverter {

    private static SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    private DefinitionParser parser;

    public static void setDatetimePattern(String pattern) {
        if (!StringUtils.isBlank(pattern)) {
            datetimeFormat = new SimpleDateFormat(pattern);
        }
    }

    public static String getDateTimePattern() {
        return datetimeFormat.toPattern();
    }

    public static void setDatePattern(String pattern) {
        if (!StringUtils.isBlank(pattern)) {
            dateFormat = new SimpleDateFormat(pattern);
        }
    }

    public static String getDatePattern() {
        return dateFormat.toPattern();
    }

    public static void setTimePattern(String pattern) {
        if (!StringUtils.isBlank(pattern)) {
            timeFormat = new SimpleDateFormat(pattern);
        }
    }

    public static String getTimePattern() {
        return timeFormat.toPattern();
    }

    public ConfigConverter(DefinitionParser parser) {
        this.parser = parser;
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
            return convertBean(fieldDefinition.getOwner(), fieldDefinition.getTypeBean(), value);
        } else if (fieldDefinition.isEnumType()) {
            return convertEnumType(fieldDefinition.getEnum(), value);
        }
        return value;
    }

    public Object convertColumnBean(FieldDefinition fieldDefinition, JSONObject object, String columnValue, int columnNum) {
        BeanDefinition beanDefinition = fieldDefinition.getTypeBean();

        //字段对应一列
        if (fieldDefinition.getColumnNums().size() == 1) {
            return convertBean(fieldDefinition.getOwner(), beanDefinition, columnValue);
        }

        //字段对应多列
        if (columnNum == fieldDefinition.getColumnNums().get(0)) {
            if (beanDefinition.hasChild()) {
                //第1列是类名
                String className = columnValue;
                if (!columnValue.contains(".")) {
                    className = getLongClassName(beanDefinition, columnValue);
                }
                if (beanDefinition.getMeAndDescendants().contains(className)) {
                    object = new JSONObject();
                    object.put("class", columnValue);
                } else if (!StringUtils.isBlank(columnValue)) {
                    throw new ConvertException(ConvertException.ErrorType.TYPE_ERROR, columnValue, beanDefinition.getName());
                }
                return object;
            } else {
                object = new JSONObject();
            }
        }

        if (object == null) {
            return null;
        }

        if (beanDefinition.hasChild()) {
            String className = object.getString("class");
            if (!className.contains(".")) {
                className = getLongClassName(fieldDefinition.getOwner(), className);
            }
            beanDefinition = parser.getBean(className);
        }

        if (beanDefinition == null) {
            return object;
        }

        for (FieldDefinition beanField : beanDefinition.getFields()) {
            if (!object.containsKey(beanField.getName())) {
                Object convertedColumnValue = convert(beanField, columnValue);
                if (convertedColumnValue != null) {
                    object.put(beanField.getName(), convertedColumnValue);
                }
                break;
            }
        }

        return object;
    }

    /**
     * 转换枚举字段，支持枚举值或者枚举名
     */
    private Object convertEnumType(EnumDefinition enumDefinition, String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        int enumValue = 0;
        try {
            enumValue = Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
        }

        FieldDefinition enumField;

        if (enumValue > 0) {
            enumField = enumDefinition.getField(enumValue);
            if (enumField == null) {
                throw new ConvertException(ConvertException.ErrorType.ENUM_VALUE, value);
            }
            return enumValue;
        }

        enumField = enumDefinition.getField(value);
        if (enumField == null) {
            throw new ConvertException(ConvertException.ErrorType.ENUM_NAME, value);
        }
        enumValue = Integer.parseInt(enumField.getValue());

        return enumValue;
    }

    private Object convertPrimitiveType(String type, String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        try {
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
        } catch (Exception e) {
            throw new ConvertException(ConvertException.ErrorType.TYPE_ERROR, e, value, type);
        }
    }

    private Date convertTimeType(String type, String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        try {
            if (type.equals("datetime")) {
                //日期加时间
                return datetimeFormat.parse(value);
            } else if (type.equals("date")) {
                //纯日期
                return dateFormat.parse(value);
            } else {
                //纯时间
                return timeFormat.parse(value);
            }
        } catch (ParseException e) {
            throw new ConvertException(ConvertException.ErrorType.COMMON, e);
        }
    }

    public String convertTimeType(String type, Date value) {
        if (value == null) {
            return null;
        }
        if (type.equals("datetime")) {
            return datetimeFormat.format(value);
        } else if (type.equals("date")) {
            return dateFormat.format(value);
        } else {
            return timeFormat.format(value);
        }
    }

    private JSONArray convertList(FieldDefinition fieldDefinition, String value) {
        if (StringUtils.isBlank(value)) {
            return new JSONArray();
        }
        String[] values = value.split(fieldDefinition.getEscapedDelimiter(), -1);
        return convertArray(fieldDefinition, values);
    }

    private JSONArray convertSet(FieldDefinition fieldDefinition, String value) {
        if (StringUtils.isBlank(value)) {
            return new JSONArray();
        }

        String[] values = value.split(fieldDefinition.getEscapedDelimiter(), -1);
        Set<String> setValues = new HashSet<>();
        Set<String> duplicateValues = new HashSet<>();

        for (String v : values) {
            if (setValues.contains(v)) {
                duplicateValues.add(v);
            }
            setValues.add(v);
        }

        if (!duplicateValues.isEmpty()) {
            throw new ConvertException(ConvertException.ErrorType.SET_DUPLICATE_VALUE, new ArrayList<>(duplicateValues));
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
                o = convertBean(fieldDefinition.getOwner(), fieldDefinition.getValueTypeBean(), v);
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

        if (fieldDefinition.isListType()) {
            array.addAll(convertList(fieldDefinition, value));
            return array;
        }

        //set
        JSONArray setArray = convertSet(fieldDefinition, value);
        if (fieldDefinition.getColumnNums().size() == 1) {
            array.addAll(setArray);
            return array;
        }

        String[] values = value.split(fieldDefinition.getEscapedDelimiter(), -1);
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
            throw new ConvertException(ConvertException.ErrorType.SET_DUPLICATE_VALUE, new ArrayList<>(duplicate));
        }

        return array;
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    public JSONObject convertColumnMap(FieldDefinition fieldDefinition, JSONObject rowJson, String value) {
        //map类型字段对应1列
        if (fieldDefinition.getColumnNums().size() == 1) {
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
                throw new ConvertException(ConvertException.ErrorType.MAP_INVALID_KEY, value);
            } else if (object.containsKey(objectKey)) {
                throw new ConvertException(ConvertException.ErrorType.MAP_DUPLICATE_KEY, value);
            }
            object.put(objectKey.toString(), null);
        } else {
            Object objectValue = null;
            try {
                if (fieldDefinition.isPrimitiveValueType()) {
                    objectValue = convertPrimitiveType(fieldDefinition.getValueType(), value);
                } else {
                    objectValue = convertBean(fieldDefinition.getOwner(), fieldDefinition.getValueTypeBean(), value);
                }
            } catch (Exception ignored) {
            }
            if (objectValue == null) {
                //value无效删除对应的key
                object.remove(objectKey);
                throw new ConvertException(ConvertException.ErrorType.MAP_INVALID_VALUE, value);
            }
            object.put(objectKey.toString(), objectValue);
        }

        return object;
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    private JSONObject convertMap(FieldDefinition fieldDefinition, String value) {
        JSONObject object = new JSONObject();
        if (StringUtils.isBlank(value)) {
            return object;
        }

        String[] values = value.split(fieldDefinition.getEscapedDelimiter(), -1);

        for (int i = 0; i < values.length; i = i + 2) {
            String vi = values[i];
            Object k = null;
            try {
                k = convertPrimitiveType(fieldDefinition.getKeyType(), vi);
            } catch (Exception ignored) {
            }
            if (k == null) {
                throw new ConvertException(ConvertException.ErrorType.MAP_INVALID_KEY, vi);
            }
            if (object.containsKey(k)) {
                throw new ConvertException(ConvertException.ErrorType.MAP_DUPLICATE_KEY, vi);
            }

            Object v = null;
            try {
                if (fieldDefinition.isPrimitiveValueType()) {
                    v = convertPrimitiveType(fieldDefinition.getValueType(), values[i + 1]);
                } else {
                    v = convertBean(fieldDefinition.getOwner(), fieldDefinition.getValueTypeBean(), values[i + 1]);
                }
            } catch (Exception ignored) {
            }
            if (v == null) {
                throw new ConvertException(ConvertException.ErrorType.MAP_INVALID_VALUE, vi);
            }

            object.put(k.toString(), v);
        }

        return object;
    }


    private JSONObject convertBean(ClassDefinition owner, BeanDefinition beanDefinition, String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        String[] values = value.split(beanDefinition.getEscapedDelimiter(), -1);

        JSONObject object = new JSONObject();

        //有子类，按具体类型转换
        boolean beanHasChild = beanDefinition.hasChild();
        if (beanHasChild) {
            String className = values[0];
            if (!values[0].contains(".")) {
                className = getLongClassName(beanDefinition, values[0]);
            }
            if (beanDefinition.getMeAndDescendants().contains(className)) {
                object.put("class", values[0]);
                beanDefinition = parser.getBean(getLongClassName(owner, className));
            } else {
                throw new ConvertException(ConvertException.ErrorType.TYPE_ERROR, values[0], beanDefinition.getName());
            }
        }

        for (int i = 0; i < beanDefinition.getFields().size(); i++) {
            int valueIndex = i;
            if (beanHasChild) {
                valueIndex++;
            }
            FieldDefinition fieldDefinition = beanDefinition.getFields().get(i);
            Object v = convert(fieldDefinition, values[valueIndex]);
            object.put(fieldDefinition.getName(), v);
        }

        return object;
    }

}
