package quan.definition;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 支持的语言枚举
 */
public enum Language{

    java {
        @Override
        public Set<String> reservedWords() {
            return Constants.JAVA_RESERVED_WORDS;
        }
    },

    cs {
        @Override
        public Set<String> reservedWords() {
            return Constants.CS_RESERVED_WORDS;
        }

        @Override
        public Pattern getPackageNamePattern() {
            return Constants.UPPER_PACKAGE_NAME_PATTERN;
        }
    },

    lua {
        @Override
        public Set<String> reservedWords() {
            return Constants.LUA_RESERVED_WORDS;
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

        for (String lang : language.split("[,，]", -1)) {
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
        return Constants.LOWER_PACKAGE_NAME_PATTERN;
    }

}
