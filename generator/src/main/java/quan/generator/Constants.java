package quan.generator;

import java.util.*;

/**
 * Created by quanchangnai on 2019/8/3.
 */
public class Constants {

    /**
     * 内建类型
     */
    public static final List<String> builtInTypes = Arrays.asList("bool", "byte", "short", "int", "long", "float", "double", "string", "bytes", "list", "set", "map");

    /**
     * 原生类型
     */
    public static final List<String> primitiveTypes = Arrays.asList("bool", "short", "int", "long", "float", "double", "string");

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
