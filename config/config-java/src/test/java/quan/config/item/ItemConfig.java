package quan.config.item;

import java.util.*;
import com.alibaba.fastjson.*;
import quan.config.*;
import quan.common.ItemType;

/**
* 道具<br/>
* Created by 自动生成
*/
@SuppressWarnings({"unchecked"})
public class ItemConfig extends Config {

    //ID
    protected int id;

    //名字
    protected String name;

    //类型
    protected ItemType type;

    //奖励
    protected Reward reward;

    //List
    protected List<Integer> list = new ArrayList<>();

    //Set
    protected Set<Integer> set = new HashSet<>();

    //Map
    protected Map<Integer, Integer> map = new HashMap<>();

    /**
     * ID
     */
    public int getId() {
        return id;
    }

    /**
     * 名字
     */
    public String getName() {
        return name;
    }

    /**
     * 类型
     */
    public ItemType getType() {
        return type;
    }

    /**
     * 奖励
     */
    public Reward getReward() {
        return reward;
    }

    /**
     * List
     */
    public List<Integer> getList() {
        return list;
    }

    /**
     * Set
     */
    public Set<Integer> getSet() {
        return set;
    }

    /**
     * Map
     */
    public Map<Integer, Integer> getMap() {
        return map;
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
                '}';

    }

    @Override
    public ItemConfig create() {
        return new ItemConfig();
    }

    public static class get {
        
        private get() {
        }

        //ID
	    private static Map<Integer, ItemConfig> idConfigs = new HashMap<>();


        public static Map<Integer, ItemConfig> idConfigs() {
            return idConfigs;
        }

        public static ItemConfig byId(int id) {
            return idConfigs.get(id);
        }


        public static void index(List<ItemConfig> configs) {
            Map<Integer, ItemConfig> idConfigs = new HashMap<>();

            ItemConfig oldConfig;
            for (ItemConfig config : configs) {
                oldConfig = idConfigs.put(config.id, config);
                if (oldConfig != null) {
                    String repeatedConfigs = config.getClass().getSimpleName();
                    if (oldConfig.getClass() != config.getClass()) {
                        repeatedConfigs += "," + oldConfig.getClass().getSimpleName();
                    }
                    throw new ConfigException("配置[" + repeatedConfigs + "]有重复索引[id:" + config.id + "]");
                }
            }

            get.idConfigs = unmodifiable(idConfigs);

        }

    }

}
