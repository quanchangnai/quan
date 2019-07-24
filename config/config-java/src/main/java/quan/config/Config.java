package quan.config;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by quanchangnai on 2019/7/11.
 */
public abstract class Config extends Bean {

    protected static Map unmodifiable(Map map) {
        for (Object key : map.keySet()) {
            Object value = map.get(key);
            if (value instanceof Map) {
                map.put(key, unmodifiable((Map) value));
            } else if (value instanceof List) {
                map.put(key, Collections.unmodifiableList((List) value));
            }
        }

        return Collections.unmodifiableMap(map);
    }

}
