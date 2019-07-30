package quan.config.item;

import java.util.*;
import com.alibaba.fastjson.*;
import quan.config.*;
import quan.common.ItemType;

/**
* Created by 自动生成
*/
@SuppressWarnings({"unchecked"})
public class WeaponConfig extends Config {

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

    //字段1
    private int w1;

    //字段2
    private int w2;

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

    /**
     * 字段1
     */
    public int getW1() {
        return w1;
    }

    /**
     * 字段2
     */
    public int getW2() {
        return w2;
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
        w1 = object.getIntValue("w1");
        w2 = object.getIntValue("w2");
    }

    @Override
    public String toString() {
        return "WeaponConfig{" +
                "id=" + id +
                ",name='" + name + '\'' +
                ",type=" + type +
                ",list=" + list +
                ",set=" + set +
                ",map=" + map +
                ",position=" + position +
                ",color=" + color +
                ",w1=" + w1 +
                ",w2=" + w2 +
                '}';

        }


    //ID
	private static Map<Integer, WeaponConfig> idConfigs = new HashMap<>();

    private static Map<Integer, Map<Integer, List<WeaponConfig>>> composite1Configs = new HashMap<>();


    public static Map<Integer, WeaponConfig> getIdConfigs() {
        return idConfigs;
    }

    public static WeaponConfig getById(int id) {
        return idConfigs.get(id);
    }

    public static Map<Integer, Map<Integer, List<WeaponConfig>>> getComposite1Configs() {
        return composite1Configs;
    }

    public static Map<Integer, List<WeaponConfig>> getByComposite1(int color) {
        return composite1Configs.getOrDefault(color, Collections.emptyMap());
    }

    public static List<WeaponConfig> getByComposite1(int color, int w1) {
        return getByComposite1(color).getOrDefault(w1, Collections.emptyList());
    }


    public static void index(List<WeaponConfig> configs) {
        Map<Integer, WeaponConfig> idConfigs = new HashMap<>();
        Map<Integer, Map<Integer, List<WeaponConfig>>> composite1Configs = new HashMap<>();

        for (WeaponConfig config : configs) {
            if (idConfigs.put(config.id, config) != null) {
                throw new RuntimeException("配置[WeaponConfig]的索引[id]:[" + config.id + "]有重复");
            }

            composite1Configs.computeIfAbsent(config.color, k -> new HashMap<>()).computeIfAbsent(config.w1, k -> new ArrayList<>()).add(config);
        }

        WeaponConfig.idConfigs = unmodifiable(idConfigs);
        WeaponConfig.composite1Configs = unmodifiable(composite1Configs);

    }

}
