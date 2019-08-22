package quan.config;

import com.alibaba.fastjson.JSONObject;

import java.util.*;

/**
 * Created by quanchangnai on 2019/7/11.
 */
@SuppressWarnings({"unchecked"})
public abstract class Config extends Bean {

    public Config(JSONObject json) {
        super(json);
    }

    protected abstract Config create(JSONObject json);

    protected static void index(Map configs, List<String> errors, Object config, boolean unique, List<String> keyNames, Object... keys) {
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

        String repeatedConfigs = config.getClass().getSimpleName();
        if (old.getClass() != config.getClass()) {
            repeatedConfigs += "," + old.getClass().getSimpleName();
        }
        List<Object> errorParams = new ArrayList<>();
        errorParams.add(repeatedConfigs);

        errorParams.add(String.join(",", keyNames));
        List<String> keysList = new ArrayList<>();
        for (int i = 0; i < keys.length; i++) {
            keysList.add(keys[i].toString());
        }
        errorParams.add(String.join(",", keysList));

        errors.add(String.format("配置[%s]有重复数据[%s = %s]", errorParams.toArray()));
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
