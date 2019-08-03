package quan.generator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by quanchangnai on 2019/8/3.
 */
public class Constants {

    //内建类型
    public static final List<String> builtInTypes = Arrays.asList("bool", "byte", "short", "int", "long", "float", "double", "string", "bytes", "list", "set", "map");

    //时间类型
    public static final List<String> primitiveTypes = Arrays.asList("bool", "short", "int", "long", "float", "double", "string");

    //事件类型
    public static final List<String> timeTypes = Arrays.asList("date", "time", "datetime");

    //允许的分隔符
    public static final Set<String> allowDelimiters = new HashSet<>(Arrays.asList(";", "_", "*", "|", "$", "@", "#", "&", "?"));

    //需要转义的分隔符(正则表达式特殊字符)
    public static final Set<String> needEscapeChars = new HashSet<>(Arrays.asList("*", "|", "?"));

}
