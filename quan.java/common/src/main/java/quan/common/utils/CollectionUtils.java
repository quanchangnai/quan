package quan.common.utils;

import java.util.*;

public class CollectionUtils {

    @SafeVarargs
    public static <E> Set<E> asSet(E... elements) {
        Set<E> set = new HashSet<>();
        Collections.addAll(set, elements);
        return Collections.unmodifiableSet(set);
    }

    @SafeVarargs
    public static <E> Set<E> asSet(Collection<E> collection, E... elements) {
        Set<E> set = new HashSet<>(collection);
        Collections.addAll(set, elements);
        return Collections.unmodifiableSet(set);
    }

    @SafeVarargs
    public static <E> Set<E> asSet(Collection<E>... collections) {
        Set<E> set = new HashSet<>();
        for (Collection<E> collection : collections) {
            set.addAll(collection);
        }
        return Collections.unmodifiableSet(set);
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

}
