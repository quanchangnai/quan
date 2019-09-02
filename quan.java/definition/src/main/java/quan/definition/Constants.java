package quan.definition;

import java.util.*;

/**
 * Created by quanchangnai on 2019/8/3.
 */
@SuppressWarnings("unchecked")
public class Constants {

    /**
     * 包名格式
     */
    public static final String PACKAGE_NAME_PATTERN = "[a-z][a-z\\d]*(\\.[a-z][a-z\\d]*)*";

    /**
     * 类名格式
     */
    public static final String CLASS_NAME_PATTERN = "[A-Z][a-zA-Z\\d]*";

    /**
     * 字段名格式
     */
    public static final String FIELD_NAME_PATTERN = "[a-z][a-zA-Z\\d]*";


    private static Set<String> unmodifiableSet(String... params) {
        return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(params)));
    }

    private static Set<String> unmodifiableSet(Collection<String>... collections) {
        Set<String> set = new HashSet<>();
        for (Collection<String> collection : collections) {
            set.addAll(collection);
        }
        return Collections.unmodifiableSet(set);
    }

    /**
     * 原生类型
     */
    public static final Set<String> PRIMITIVE_TYPES = unmodifiableSet("bool", "short", "int", "long", "float", "double", "string");

    /**
     * 集合类型
     */
    public static final Set<String> COLLECTION_TYPES = unmodifiableSet("list", "set", "map");

    /**
     * 时间类型
     */
    public static final Set<String> TIME_TYPES = unmodifiableSet("date", "time", "datetime");

    /**
     * 数据库支持的内建类型
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
     * 合法的分隔符
     */
    public static final Set<String> LEGAL_DELIMITERS = unmodifiableSet(";", ":", "_", "*", "|", "$", "@", "#", "&", "?");

    /**
     * 需要转义的正则表达式特殊字符
     */
    public static final Set<String> NEED_ESCAPE_CHARS = unmodifiableSet("*", "|", "?");

}
