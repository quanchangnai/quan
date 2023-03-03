package quan.definition;

import org.apache.commons.lang3.StringUtils;

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

    public abstract Set<String> reservedWords();

    public boolean matchPackageName(String packageName) {
        return getPackageNamePattern().matcher(packageName).matches();
    }

    public Pattern getPackageNamePattern() {
        return Constants.LOWER_PACKAGE_NAME_PATTERN;
    }

    public static Set<String> names() {
        return names;
    }

    public static Set<String> parse(String languageStr) {
        return parse(names(), languageStr);
    }

    public static Set<String> parse(Set<String> languageRanges, String languageStr) {
        if (StringUtils.isBlank(languageStr)) {
            return new HashSet<>(languageRanges);
        }

        languageStr = languageStr.trim();
        boolean exclude = false;

        if (languageStr.startsWith("-")) {
            exclude = true;
            languageStr = languageStr.substring(1);
        }

        Set<String> languages = new HashSet<>();

        for (String lang : languageStr.split("[,，]", -1)) {
            if (!lang.isEmpty()) {
                languages.add(lang.trim());
                if (!languageRanges.contains(lang)) {
                    throw new IllegalArgumentException(String.format("语言%s不在限制语言%s范围之内", lang, languageRanges));
                }
            }
        }

        if (!exclude) {
            if (languages.isEmpty()) {
                languages.addAll(languageRanges);
            }
            return languages;
        }

        Set<String> languages2 = new HashSet<>();
        for (String language : languageRanges) {
            boolean supported = !(languages.isEmpty() || languages.contains(language));
            if (supported) {
                languages2.add(language);
            }
        }

        return languages2;
    }

}
