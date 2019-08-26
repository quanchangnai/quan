package quan.config.item;

import java.util.*;
import com.alibaba.fastjson.*;
import quan.config.*;

/**
* 道具<br/>
* 自动生成
*/
public class ItemConfig extends Config {

    //ID
    protected final int id;

    //名字
    protected final String name;

    //类型
    protected final ItemType type;

    //奖励
    protected final Reward reward;

    //List
    protected final List<Integer> list;

    //Set
    protected final Set<Integer> set;

    //Map
    protected final Map<Integer, Integer> map;

    //生效时间
    protected final Date effectiveTime;

    protected final String effectiveTime$Str;


    public ItemConfig(JSONObject json) {
        super(json);

        this.id = json.getIntValue("id");
        this.name = json.getOrDefault("name", "").toString();

        int $type = json.getIntValue("type");
        this.type = $type > 0 ? ItemType.valueOf($type) : null;

        JSONObject $reward = json.getJSONObject("reward");
        if ($reward != null) {
            this.reward = new Reward($reward);
        } else {
            this.reward = null;
        }

        JSONArray $list$1 = json.getJSONArray("list");
        List<Integer> $list$2 = new ArrayList<>();
        if ($list$1 != null) {
            for (int i = 0; i < $list$1.size(); i++) {
                $list$2.add($list$1.getInteger(i));
            }
        }
        this.list = Collections.unmodifiableList($list$2);

        JSONArray $set$1 = json.getJSONArray("set");
        Set<Integer> $set$2 = new HashSet<>();
        if ($set$1 != null) {
            for (int i = 0; i < $set$1.size(); i++) {
                $set$2.add($set$1.getInteger(i));
            }
        }
        this.set = Collections.unmodifiableSet($set$2);

        JSONObject $map$1 = json.getJSONObject("map");
        Map<Integer, Integer> $map$2 = new HashMap<>();
        if ($map$1 != null) {
            for (String $map$Key : $map$1.keySet()) {
                $map$2.put(Integer.valueOf($map$Key), $map$1.getInteger($map$Key));
            }
        }
        this.map = Collections.unmodifiableMap($map$2);

        this.effectiveTime = json.getDate("effectiveTime");
        this.effectiveTime$Str = json.getOrDefault("effectiveTime$Str", "").toString();
    }

    /**
     * ID
     */
    public final int getId() {
        return id;
    }

    /**
     * 名字
     */
    public final String getName() {
        return name;
    }

    /**
     * 类型
     */
    public final ItemType getType() {
        return type;
    }

    /**
     * 奖励
     */
    public final Reward getReward() {
        return reward;
    }

    /**
     * List
     */
    public final List<Integer> getList() {
        return list;
    }

    /**
     * Set
     */
    public final Set<Integer> getSet() {
        return set;
    }

    /**
     * Map
     */
    public final Map<Integer, Integer> getMap() {
        return map;
    }

    /**
     * 生效时间
     */
    public final Date getEffectiveTime() {
        return effectiveTime;
    }

    /**
     * 生效时间
     */
    public final String getEffectiveTime$Str() {
        return effectiveTime$Str;
    }


    @Override
    protected ItemConfig create(JSONObject json) {
        return new ItemConfig(json);
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


    // 所有ItemConfig
    private static volatile List<ItemConfig> configs = new ArrayList<>();

    //ID
    private static volatile Map<Integer, ItemConfig> idConfigs = new HashMap<>();

    public static List<ItemConfig> getConfigs() {
        return configs;
    }

    public static Map<Integer, ItemConfig> getIdConfigs() {
        return idConfigs;
    }

    public static ItemConfig getById(int id) {
        return idConfigs.get(id);
    }


    @SuppressWarnings({"unchecked"})
    public static List<String> index(List<ItemConfig> configs) {
        Map<Integer, ItemConfig> idConfigs = new HashMap<>();

        List<String> errors = new ArrayList<>();

        for (ItemConfig config : configs) {
            index(idConfigs, errors, config, true, Arrays.asList("id"), config.id);
        }

        configs = Collections.unmodifiableList(configs);
        idConfigs = unmodifiableMap(idConfigs);

        ItemConfig.configs = configs;
        ItemConfig.idConfigs = idConfigs;

        return errors;
    }

}
