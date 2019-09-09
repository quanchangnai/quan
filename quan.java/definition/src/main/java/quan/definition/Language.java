package quan.definition;


import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/**
 * Created by quanchangnai on 2019/7/27.
 */
public enum Language {

    java, cs, lua;

    private static List<String> names;

    static {
        names = new ArrayList<>();
        for (Language language : values()) {
            names.add(language.name());
        }
        names = Collections.unmodifiableList(names);
    }

    public static List<String> names() {
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
