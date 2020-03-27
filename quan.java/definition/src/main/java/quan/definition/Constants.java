package quan.definition;

import java.util.*;
import java.util.regex.Pattern;

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


    private static Set<String> unmodifiableSet(String... strings) {
        return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(strings)));
    }

    private static Set<String> unmodifiableSet(Collection<String>... collections) {
        Set<String> set = new HashSet<>();
        for (Collection<String> collection : collections) {
            set.addAll(collection);
        }
        return Collections.unmodifiableSet(set);
    }

    /**
     * Java保留字
     */
    public static final Set<String> JAVA_RESERVED_WORDS = unmodifiableSet(
            "abstract", "assert", "boolean", "break", "throws", "case", "catch", "char", "volatile",
            "const", "continue", "default", "do", "else", "enum", "extends", "finally", "long", "transient",
            "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "double",
            "native", "new", "try", "package", "private", "protected", "public", "void", "strictfp", "short",
            "static", "super", "switch", "synchronized", "throw", "byte", "final", "while", "class", "return"
    );

    /**
     * C#保留字
     */
    public static final Set<String> CS_RESERVED_WORDS = unmodifiableSet(
            "abstract", "as", "base", "bool", "break", "static", "case", "catch", "char", "checked", "class",
            "const", "continue", "decimal", "default", "delegate", "goto", "double", "for", "enum", "event", "ulong",
            "extern", "false", "finally", "fixed", "float", "else", "foreach", "if", "do", "implicit", "in", "sizeof",
            "interface", "internal", "lock", "long", "namespace", "new", "null", "object", "operator", "void", "uint",
            "params", "private", "protected", "public", "readonly", "ref", "return", "sbyte", "sealed", "short", "is",
            "stackalloc", "static", "string", "struct", "switch", "this", "throw", "true", "try", "typeof", "explicit",
            "unchecked", "unsafe", "ushort", "using", "byte", "override", "virtual", "volatile", "while", "out", "int"
    );

    /**
     * Lua保留字
     */
    public static final Set<String> LUA_RESERVED_WORDS = unmodifiableSet(
            "and", "break", "do", "else", "elseif", "end", "false", "for", "function", "goto", "if", "in",
            "local", "nil", "not", "or", "repeat", "return", "then", "true", "until", "while"
    );


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

    /**
     * 配置支持的合法分隔符
     */
    public static final Set<String> LEGAL_DELIMITERS = unmodifiableSet(";", ":", "_", "*", "|", "$", "@", "#", "&", "?");

    /**
     * 配置支持的需要转义的分隔符(正则表达式特殊字符)
     */
    public static final Set<String> NEED_ESCAPE_DELIMITERS = unmodifiableSet("*", "|", "?");

}
