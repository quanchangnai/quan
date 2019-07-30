package quan.config.item;

import java.util.*;
import com.alibaba.fastjson.*;
import quan.config.*;
import quan.common.ItemType;

/**
* Created by 自动生成
*/
@SuppressWarnings({"unchecked"})
public class EquipConfig extends Config {

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

    //部位
    private int position;

    //颜色
    private int color;

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

    /**
     * 部位
     */
    public int getPosition() {
        return position;
    }

    /**
     * 颜色
     */
    public int getColor() {
        return color;
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

        position = object.getIntValue("position");
        color = object.getIntValue("color");
    }

    @Override
    public String toString() {
        return "EquipConfig{" +
                "id=" + id +
                ",name='" + name + '\'' +
                ",type=" + type +
                ",list=" + list +
                ",set=" + set +
                ",map=" + map +
                ",position=" + position +
                ",color=" + color +
                '}';

        }


    //ID
	private static Map<Integer, EquipConfig> idConfigs = new HashMap<>();

    //部位
    private static Map<Integer, List<EquipConfig>> positionConfigs = new HashMap<>();


    public static Map<Integer, EquipConfig> getIdConfigs() {
        return idConfigs;
    }

    public static EquipConfig getById(int id) {
        return idConfigs.get(id);
    }

    public static Map<Integer, List<EquipConfig>> getPositionConfigs() {
        return positionConfigs;
    }

    public static List<EquipConfig> getByPosition(int position) {
        return positionConfigs.getOrDefault(position, Collections.emptyList());
    }


    public static void index(List<EquipConfig> configs) {
        Map<Integer, EquipConfig> idConfigs = new HashMap<>();
        Map<Integer, List<EquipConfig>> positionConfigs = new HashMap<>();

        for (EquipConfig config : configs) {
            if (idConfigs.put(config.id, config) != null) {
                throw new RuntimeException("配置[EquipConfig]的索引[id]:[" + config.id + "]有重复");
            }

            positionConfigs.computeIfAbsent(config.position, k -> new ArrayList<>()).add(config);
        }

        EquipConfig.idConfigs = unmodifiable(idConfigs);
        EquipConfig.positionConfigs = unmodifiable(positionConfigs);

    }

}
