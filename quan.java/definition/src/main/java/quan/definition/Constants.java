package quan.definition;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.regex.Pattern;

import static quan.common.utils.CollectionUtils.unmodifiableSet;

/**
 * Created by quanchangnai on 2019/8/3.
 */
@SuppressWarnings("unchecked")
public final class Constants {

    /**
     * 首字母小写包名格式
     */
    public static final Pattern LOWER_PACKAGE_NAME_PATTERN = Pattern.compile("[a-z][a-z\\d]*(\\.[a-z][a-z\\d]*)*");

    /**
     * 首字母大写包名格式
     */
    public static final Pattern UPPER_PACKAGE_NAME_PATTERN = Pattern.compile("[A-Z][a-z\\d]*(\\.[A-Z][a-z\\d]*)*");

    /**
     * 类名格式
     */
    public static final Pattern CLASS_NAME_PATTERN = Pattern.compile("[A-Z][a-zA-Z\\d]*");

    /**
     * 数据类名格式
     */
    public static final Pattern DATA_NAME_PATTERN = Pattern.compile("[A-Z][a-zA-Z\\d]*Data");

    /**
     * 数据实体类名格式
     */
    public static final Pattern ENTITY_NAME_PATTERN = Pattern.compile("[A-Z][a-zA-Z\\d]*Entity");

    /**
     * 配置类名格式
     */
    public static final Pattern CONFIG_NAME_PATTERN = Pattern.compile("[A-Z][a-zA-Z\\d]*Config");

    /**
     * 字段名格式
     */
    public static final Pattern FIELD_NAME_PATTERN = Pattern.compile("[a-z][a-zA-Z\\d]*");

    /**
     * 数字类型
     */
    public static final Set<String> NUMBER_TYPES = unmodifiableSet("short", "int", "long", "float", "double");

    /**
     * 原生类型
     */
    public static final Set<String> PRIMITIVE_TYPES = unmodifiableSet(NUMBER_TYPES, Arrays.asList("bool", "string"));

    /**
     * 集合类型
     */
    public static final Set<String> COLLECTION_TYPES = unmodifiableSet("list", "set", "map");

    /**
     * 时间类型
     */
    public static final Set<String> TIME_TYPES = unmodifiableSet("date", "time", "datetime");

    /**
     * 数据支持的内建类型
     */
    public static final Set<String> DATA_BUILTIN_TYPES = unmodifiableSet(PRIMITIVE_TYPES, COLLECTION_TYPES);

    /**
     * 消息支持的内建类型
     */
    public static final Set<String> MESSAGE_BUILTIN_TYPES = unmodifiableSet(PRIMITIVE_TYPES, COLLECTION_TYPES, Collections.singleton("bytes"));

    /**
     * 配置支持的内建类型
     */
    public static final Set<String> CONFIG_BUILTIN_TYPES = unmodifiableSet(PRIMITIVE_TYPES, COLLECTION_TYPES, TIME_TYPES);

    public static final Set<String> DATA_RESERVED_CLASS_NAMES = unmodifiableSet("Boolean", "Short", "Integer",
            "Long", "Float", "Double", "String", "Set", "List", "Map", "HashSet", "ArrayList", "HashMap");

    public static final Set<String> CS_RESERVED_CLASS_NAMES = unmodifiableSet("HashSet", "List", "Dictionary");

    /**
     * 配置支持的合法分隔符
     */
    public static final Set<String> LEGAL_DELIMITERS = unmodifiableSet(";", ":", "_", "*", "|", "$", "@", "&", "?");

    /**
     * 配置支持的需要转义的分隔符(正则表达式特殊字符)
     */
    public static final Set<String> NEED_ESCAPE_DELIMITERS = unmodifiableSet("*", "|", "?");

}
