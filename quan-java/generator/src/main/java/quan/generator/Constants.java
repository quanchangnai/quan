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
    public static final String packageNamePattern = "[a-z][a-z\\d]*(\\.[a-z][a-z\\d]*)*";

    /**
     * 类名格式
     */
    public static final String classNamePattern = "[A-Z][a-zA-Z\\d]*";

    /**
     * 字段名格式
     */
    public static final String fieldNamePattern = "[a-z][a-zA-Z\\d]*";


    private static Set<String> unmodifiableSet(String... params) {
        return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(params)));
    }

    /**
     * 原生类型
     */
    public static final Set<String> primitiveTypes = unmodifiableSet("bool", "short", "int", "long", "float", "double", "string");

    /**
     * 集合类型
     */
    public static final Set<String> collectionTypes = unmodifiableSet("list", "set", "map");

    /**
     * 时间类型
     */
    public static final Set<String> timeTypes = unmodifiableSet("date", "time", "datetime");

    /**
     * 合法的分隔符
     */
    public static final Set<String> legalDelimiters = unmodifiableSet(";", ":", "_", "*", "|", "$", "@", "#", "&", "?");

    /**
     * 需要转义的正则表达式特殊字符
     */
    public static final Set<String> needEscapeChars = unmodifiableSet("*", "|", "?");

}
