package quan.config;

import com.alibaba.fastjson.JSONObject;

import java.util.*;

/**
 * 配置
 * Created by quanchangnai on 2019/7/11.
 */
@SuppressWarnings({"unchecked"})
public abstract class Config extends Bean {

    public Config(JSONObject json) {
        super(json);
    }

    protected abstract Config create(JSONObject json);

    protected static void load(Map configs, List<String> errors, Object config, boolean unique, List<String> keyNames, Object... keys) {
        Map map = configs;
        for (int i = 0; i < keys.length - 1; i++) {
            Object key = keys[i];
            map = (Map) map.computeIfAbsent(key, k -> new HashMap<>());
        }

        if (!unique) {
            List list = (List) map.computeIfAbsent(keys[keys.length - 1], k -> new ArrayList<>());
            list.add(config);
            return;
        }

        Object old = map.put(keys[keys.length - 1], config);
        if (old == null) {
            return;
        }

        String configNames = config.getClass().getSimpleName();
        if (old.getClass() != config.getClass()) {
            configNames += "," + old.getClass().getSimpleName();
        }

        List<String> keysList = new ArrayList<>();
        for (Object key : keys) {
            keysList.add(key.toString());
        }

        List<Object> params = new ArrayList<>();
        params.add(configNames);
        if (keyNames.size() > 1) {
            params.add("(" + String.join(",", keyNames) + ")");
        } else {
            params.addAll(keyNames);
        }
        if (keysList.size() > 1) {
            params.add("(" + String.join(",", keysList) + ")");
        } else {
            params.addAll(keysList);
        }


        errors.add(String.format("配置[%s]有重复数据[%s = %s]", params.toArray()));
    }

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
