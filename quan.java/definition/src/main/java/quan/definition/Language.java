package quan.definition;


import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by quanchangnai on 2019/7/27.
 */
public enum Language {

    java, cs, lua;

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

    public static Pair<Boolean, Set<String>> parse(String language) {
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

        return Pair.of(exclude, languages);
    }
}
