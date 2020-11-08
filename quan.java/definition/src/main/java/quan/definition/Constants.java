package quan.definition;

import java.util.Collections;
import java.util.Set;

import static quan.common.utils.CollectionUtils.unmodifiableSet;

/**
 * Created by quanchangnai on 2019/8/3.
 */
public final class Constants {

    /**
     * 整数类型
     */
    public static final Set<String> INTEGER_TYPES = unmodifiableSet("short", "int", "long");

    /**
     * 数字类型
     */
    public static final Set<String> NUMBER_TYPES = unmodifiableSet(INTEGER_TYPES, "float", "double");

    /**
     * 原生类型
     */
    public static final Set<String> PRIMITIVE_TYPES = unmodifiableSet(NUMBER_TYPES, "bool", "string");

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
    public static final Set<String> LEGAL_DELIMITERS = unmodifiableSet(";", ":", "_", "*", "|", "$", "@", "&", "?");

    /**
     * 配置支持的需要转义的分隔符(正则表达式特殊字符)
     */
    public static final Set<String> NEED_ESCAPE_DELIMITERS = unmodifiableSet("*", "|", "?");

}
