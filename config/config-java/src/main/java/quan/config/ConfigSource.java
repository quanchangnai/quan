package quan.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.generator.BeanDefinition;
import quan.generator.ClassDefinition;
import quan.generator.FieldDefinition;
import quan.generator.config.ConfigDefinition;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 配置源
 * Created by quanchangnai on 2019/7/11.
 */
public abstract class ConfigSource {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected File sourceFile;

    protected ConfigDefinition configDefinition;

    public ConfigSource(File sourceFile, ConfigDefinition configDefinition) {
        this.sourceFile = sourceFile;
        this.configDefinition = configDefinition;
    }


    public abstract List<JSONObject> read();


    public Object convertPrimitiveType(String type, String value) {
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
                return null;
        }
    }

    public JSONArray convertList(FieldDefinition fieldDefinition, String value) {
        String[] values = value.split("[;]");//TODO 分隔符需要改成可配置
        return convertArray(fieldDefinition, values);
    }

    public JSONArray convertArray(FieldDefinition fieldDefinition, String[] values) {
        JSONArray array = new JSONArray();
        for (String v : values) {
            if (fieldDefinition.isValuePrimitiveType()) {
                array.add(convertPrimitiveType(fieldDefinition.getValueType(), v));
            } else {
                array.add(convertBean((BeanDefinition) ClassDefinition.getAll().get(fieldDefinition.getValueType()), v));
            }
        }
        return array;
    }

    public JSONArray convertSet(FieldDefinition fieldDefinition, String value) {
        //set需要去重
        String[] values = value.split("[;]");//TODO 分隔符需要改成可配置
        Set<String> set = new HashSet<>();
        set.addAll(Arrays.asList(values));
        return convertArray(fieldDefinition, set.toArray(new String[0]));
    }

    public JSONObject convertMap(FieldDefinition fieldDefinition, String value) {
        String[] values = value.split("[;|\\*]");//TODO 分隔符需要改成可配置

        JSONObject object = new JSONObject();
        for (int i = 0; i < values.length; i = i + 2) {
            Object k = convertPrimitiveType(fieldDefinition.getKeyType(), values[i]);
            Object v;
            if (fieldDefinition.isValuePrimitiveType()) {
                v = convertPrimitiveType(fieldDefinition.getValueType(), values[i + 1]);
            } else {
                v = convertBean((BeanDefinition) ClassDefinition.getAll().get(fieldDefinition.getValueType()), values[i + 1]);
            }
            object.put(k.toString(), v);
        }

        return object;
    }

    public JSONObject convertBean(BeanDefinition beanDefinition, String value) {
        String[] values = value.split("[_]");//TODO 分隔符需要改成可配置

        JSONObject object = new JSONObject();
        for (int i = 0; i < beanDefinition.getFields().size(); i++) {
            FieldDefinition fieldDefinition = beanDefinition.getFields().get(i);
            Object v = convert(fieldDefinition, values[i]);
            object.put(fieldDefinition.getName(), v);
        }

        return object;
    }


    public Object convert(FieldDefinition fieldDefinition, String value) {
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
            return convertBean((BeanDefinition) ClassDefinition.getAll().get(fieldDefinition.getType()), value);
        }
        return value;
    }
}
