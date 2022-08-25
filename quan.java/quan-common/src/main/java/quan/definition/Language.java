package quan.definition;

import org.apache.commons.lang3.tuple.Pair;
import quan.util.CommonUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 支持的语言枚举
 */
public enum Language {

    java {
        @Override
        public Set<String> reservedWords() {
            return JAVA_RESERVED_WORDS;
        }
    },

    cs {
        @Override
        public Set<String> reservedWords() {
            return CS_RESERVED_WORDS;
        }

        @Override
        public Pattern getPackageNamePattern() {
            return UPPER_PACKAGE_NAME_PATTERN;
        }
    },

    lua {
        @Override
        public Set<String> reservedWords() {
            return LUA_RESERVED_WORDS;
        }
    };


    private static Set<String> names;

    static {
        names = new HashSet<>();
        for (Language language : values()) {
            names.add(language.name());
        }
        names = Collections.unmodifiableSet(names);
    }

    public static Set<String> names() {
        return names;
    }

    public static Pair<Set<String>, Boolean> parse(String language) {
        language = language.trim();
        boolean exclude = false;
        Set<String> languages = new HashSet<>();

        if (language.startsWith("-")) {
            exclude = true;
            language = language.substring(1);
        }

        for (String lang : language.split(",", -1)) {
            if (!lang.isEmpty()) {
                languages.add(lang.trim());
            }
        }

        return Pair.of(languages, exclude);
    }

    public abstract Set<String> reservedWords();

    public boolean matchPackageName(String packageName) {
        return getPackageNamePattern().matcher(packageName).matches();
    }

    public Pattern getPackageNamePattern() {
        return LOWER_PACKAGE_NAME_PATTERN;
    }

    /**
     * Java保留字
     */
    public static final Set<String> JAVA_RESERVED_WORDS = CommonUtils.asSet(
            "abstract", "assert", "boolean", "break", "throws", "case", "catch", "char", "volatile",
            "const", "continue", "default", "do", "else", "enum", "extends", "finally", "long", "transient",
            "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "double",
            "native", "new", "try", "package", "private", "protected", "public", "void", "strictfp", "short",
            "static", "super", "switch", "synchronized", "throw", "byte", "final", "while", "class", "return"
    );

    /**
     * C#保留字
     */
    public static final Set<String> CS_RESERVED_WORDS = CommonUtils.asSet(
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
    public static final Set<String> LUA_RESERVED_WORDS = CommonUtils.asSet(
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
