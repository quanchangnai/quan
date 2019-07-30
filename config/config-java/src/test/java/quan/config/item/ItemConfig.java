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
    private int id;

    //名字
    private String name;

    //类型
    private ItemType type;

    //List
    private List<Integer> list = new ArrayList<>();

    //Set
    private Set<Integer> set = new HashSet<>();

    //Map
    private Map<Integer, Integer> map = new HashMap<>();

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
    protected void parse(JSONObject object) {
        id = object.getIntValue("id");
        name = object.getString("name");

        String $type = object.getString("type");
        if ($type != null) {
            type = ItemType.valueOf($type);
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
                ",list=" + list +
                ",set=" + set +
                ",map=" + map +
                '}';

        }


    //ID
	private static Map<Integer, ItemConfig> idConfigs = new HashMap<>();


    public static Map<Integer, ItemConfig> getIdConfigs() {
        return idConfigs;
    }

    public static ItemConfig getById(int id) {
        return idConfigs.get(id);
    }


    public static void index(List<ItemConfig> configs) {
        Map<Integer, ItemConfig> idConfigs = new HashMap<>();

        for (ItemConfig config : configs) {
            if (idConfigs.put(config.id, config) != null) {
                throw new RuntimeException("配置[ItemConfig]的索引[id]:[" + config.id + "]有重复");
            }
        }

        ItemConfig.idConfigs = unmodifiable(idConfigs);

    }

}
