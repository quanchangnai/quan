package quan.config.item;

import java.util.*;
import com.alibaba.fastjson.*;
import quan.config.*;

/**
* 道具<br/>
* Created by 自动生成
*/
@SuppressWarnings({"unchecked"})
public class ItemConfig extends Config {

    protected int id;

    protected String name;

    protected ItemType type;

    protected Reward reward;

    protected List<Integer> list = new ArrayList<>();

    protected Set<Integer> set = new HashSet<>();

    protected Map<Integer, Integer> map = new HashMap<>();

    protected Date effectiveTime;

    protected String effectiveTime$Str;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ItemType getType() {
        return type;
    }

    public Reward getReward() {
        return reward;
    }

    public List<Integer> getList() {
        return list;
    }

    public Set<Integer> getSet() {
        return set;
    }

    public Map<Integer, Integer> getMap() {
        return map;
    }

    public Date getEffectiveTime() {
        return effectiveTime;
    }

    public String getEffectiveTime$Str() {
        return effectiveTime$Str;
    }


    @Override
    public void parse(JSONObject object) {
        super.parse(object);

        id = object.getIntValue("id");
        name = object.getString("name");

        String $type = object.getString("type");
        if ($type != null) {
            type = ItemType.valueOf($type);
        }

        JSONObject $reward = object.getJSONObject("reward");
        if ($reward != null) {
            reward = new Reward();
            reward.parse($reward);
        }

        JSONArray $list = object.getJSONArray("list");
        if ($list != null) {
            for (int i = 0; i < $list.size(); i++) {
                list.add($list.getInteger(i));
            }
        }
        list = Collections.unmodifiableList(list);

        JSONArray $set = object.getJSONArray("set");
        if ($set != null) {
            for (int i = 0; i < $set.size(); i++) {
                set.add($set.getInteger(i));
            }
        }
        set = Collections.unmodifiableSet(set);

        JSONObject $map = object.getJSONObject("map");
        if ($map != null) {
            for (String $map$Key : $map.keySet()) {
                map.put(Integer.valueOf($map$Key), $map.getInteger($map$Key));
            }
        }
        map = Collections.unmodifiableMap(map);

        effectiveTime = object.getDate("effectiveTime");
        effectiveTime$Str = object.getString("effectiveTime$Str");
    }

    @Override
    public String toString() {
        return "ItemConfig{" +
                "id=" + id +
                ",name='" + name + '\'' +
                ",type=" + type +
                ",reward=" + reward +
                ",list=" + list +
                ",set=" + set +
                ",map=" + map +
                ",effectiveTime='" + effectiveTime$Str + '\'' +
                '}';

    }

    @Override
    public ItemConfig create() {
        return new ItemConfig();
    }

    private volatile static List<ItemConfig> configs = new ArrayList<>();

    private volatile static Map<Integer, ItemConfig> idConfigs = new HashMap<>();


    public static List<ItemConfig> getConfigs() {
        return configs;
    }

    public static Map<Integer, ItemConfig> getIdConfigs() {
        return idConfigs;
    }

    public static ItemConfig getById(int id) {
        return idConfigs.get(id);
    }


    public static List<String> index(List<ItemConfig> configs) {
        Map<Integer, ItemConfig> idConfigs = new HashMap<>();

        List<String> errors = new ArrayList<>();
        ItemConfig oldConfig;

        for (ItemConfig config : configs) {
            oldConfig = idConfigs.put(config.id, config);
            if (oldConfig != null) {
                String repeatedConfigs = config.getClass().getSimpleName();
                if (oldConfig.getClass() != config.getClass()) {
                    repeatedConfigs += "," + oldConfig.getClass().getSimpleName();
                }
                errors.add(String.format("配置[%s]有重复数据[%s = %s]", repeatedConfigs, "id", config.id));
            }
        }

        ItemConfig.configs = Collections.unmodifiableList(configs);
        ItemConfig.idConfigs = unmodifiableMap(idConfigs);

        return errors;
    }

}
