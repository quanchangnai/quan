package quan.common.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CollectionUtils {

    @SafeVarargs
    public static <E> Set<E> unmodifiableSet(E... elements) {
        Set<E> set = new HashSet<>();
        Collections.addAll(set, elements);
        return Collections.unmodifiableSet(set);
    }

    @SafeVarargs
    public static <E> Set<E> unmodifiableSet(Collection<E>... collections) {
        Set<E> set = new HashSet<>();
        for (Collection<E> collection : collections) {
            set.addAll(collection);
        }
        return Collections.unmodifiableSet(set);
    }

}
