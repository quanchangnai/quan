package quan.config;

import com.alibaba.fastjson.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by quanchangnai on 2019/7/11.
 */
@SuppressWarnings({"unchecked"})
public abstract class Config extends Bean {

    public Config(JSONObject json) {
        super(json);
    }

    protected abstract Config create(JSONObject json);

    protected static Map unmodifiableMap(Map map) {
        for (Object key : map.keySet()) {
            Object value = map.get(key);
            if (value instanceof Map) {
                map.put(key, unmodifiableMap((Map) value));
            } else if (value instanceof List) {
                map.put(key, Collections.unmodifiableList((List) value));
            }
        }
        return Collections.unmodifiableMap(map);
    }

}