package quan.common.utils;

import java.util.*;

public class CollectionUtils {

    public static Set<String> unmodifiableSet(String... strings) {
        return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(strings)));
    }

    public static Set<String> unmodifiableSet(Collection<String>... collections) {
        Set<String> set = new HashSet<>();
        for (Collection<String> collection : collections) {
            set.addAll(collection);
        }
        return Collections.unmodifiableSet(set);
    }

}
