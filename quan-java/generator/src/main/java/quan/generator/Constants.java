package quan.generator;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by quanchangnai on 2019/8/3.
 */
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
     * 合法的分隔符
     */
    public static final Set<String> LEGAL_DELIMITERS = unmodifiableSet(";", ":", "_", "*", "|", "$", "@", "#", "&", "?");

    /**
     * 需要转义的正则表达式特殊字符
     */
    public static final Set<String> NEED_ESCAPE_CHARS = unmodifiableSet("*", "|", "?");

}
