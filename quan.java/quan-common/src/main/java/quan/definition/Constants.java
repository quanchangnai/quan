package quan.definition;

import quan.util.CollectionUtils;

import java.util.Collections;
import java.util.Set;
import java.util.regex.Pattern;

import static quan.util.CollectionUtils.asSet;

/**
 * 常量
 */
public final class Constants {

    /**
     * 整数类型
     */
    public static final Set<String> INTEGRAL_NUMBER_TYPES = asSet("short", "int", "long");

    /**
     * 数字类型
     */
    public static final Set<String> NUMBER_TYPES = asSet(INTEGRAL_NUMBER_TYPES, "float", "double");

    /**
     * 原生类型
     */
    public static final Set<String> PRIMITIVE_TYPES = asSet(NUMBER_TYPES, "bool", "string");

    /**
     * 集合类型
     */
    public static final Set<String> COLLECTION_TYPES = asSet("list", "set", "map");

    /**
     * 时间类型
     */
    public static final Set<String> TIME_TYPES = asSet("date", "time", "datetime");

    /**
     * 数据支持的内建类型
     */
    public static final Set<String> DATA_BUILTIN_TYPES = asSet(PRIMITIVE_TYPES, COLLECTION_TYPES);

    /**
     * 消息支持的内建类型
     */
    public static final Set<String> MESSAGE_BUILTIN_TYPES = asSet(PRIMITIVE_TYPES, COLLECTION_TYPES, Collections.singleton("bytes"));

    /**
     * 配置支持的内建类型
     */
    public static final Set<String> CONFIG_BUILTIN_TYPES = asSet(PRIMITIVE_TYPES, COLLECTION_TYPES, TIME_TYPES);

    /**
     * 配置支持的合法分隔符
     */
    public static final Set<String> LEGAL_DELIMITERS = asSet(",", ";", ":", "-", "_", "*", "|", "$", "@", "&", "?");

    /**
     * 配置支持的需要转义的分隔符(正则表达式特殊字符)
     */
    public static final Set<String> NEED_ESCAPE_DELIMITERS = asSet("-", "*", "|", "$", "?");

    /**
     * Java保留字
     */
    public static final Set<String> JAVA_RESERVED_WORDS = CollectionUtils.asSet(
            "abstract", "assert", "boolean", "break", "throws", "case", "catch", "char", "volatile",
            "const", "continue", "default", "do", "else", "enum", "extends", "finally", "long", "transient",
            "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "double",
            "native", "new", "try", "package", "private", "protected", "public", "void", "strictfp", "short",
            "static", "super", "switch", "synchronized", "throw", "byte", "final", "while", "class", "return"
    );

    /**
     * C#保留字
     */
    public static final Set<String> CS_RESERVED_WORDS = CollectionUtils.asSet(
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
    public static final Set<String> LUA_RESERVED_WORDS = CollectionUtils.asSet(
            "and", "break", "do", "else", "elseif", "end", "false", "for", "function", "goto", "if", "in",
            "local", "nil", "not", "or", "repeat", "return", "then", "true", "until", "while"
    );

    /**
     * 首字母小写包名格式
     */
    public static final Pattern LOWER_PACKAGE_NAME_PATTERN = Pattern.compile("[a-z][a-z\\d]*(\\.[a-z][a-z\\d]*)*");

    /**
     * 首字母大写包名格式
     */
    public static final Pattern UPPER_PACKAGE_NAME_PATTERN = Pattern.compile("[A-Z][a-z\\d]*(\\.[A-Z][a-z\\d]*)*");

}
