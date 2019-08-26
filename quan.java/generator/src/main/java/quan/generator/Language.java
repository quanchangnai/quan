package quan.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by quanchangnai on 2019/7/27.
 */
public enum Language {

    java, cs;

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

}
