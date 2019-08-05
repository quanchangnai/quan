package quan.generator;

import java.util.*;

/**
 * Created by quanchangnai on 2019/8/3.
 */
public class Constants {

    /**
     * 原生类型
     */
    public static final List<String> primitiveTypes = Arrays.asList("bool", "short", "int", "long", "float", "double", "string");

    /**
     * 集合类型
     */
    public static final List<String> collectionTypes = Arrays.asList("list", "set", "map");

    /**
     * 时间类型
     */
    public static final List<String> timeTypes = Arrays.asList("date", "time", "datetime");

    /**
     * 合法的分隔符
     */
    public static final Set<String> legalDelimiters = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(";", "_", "*", "|", ":", "$", "@", "#", "&", "?")));

    /**
     * 需要转义的正则表达式特殊字符
     */
    public static final Set<String> needEscapeChars = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("*", "|", "?")));

}
