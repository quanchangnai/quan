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

    //类型
    private ItemType type;

    //名字
    private String name;

    //ID
    private int id;

    //部位
    private int position;

    /**
     * 类型
     */
    public ItemType getType() {
        return type;
    }

    /**
     * 名字
     */
    public String getName() {
        return name;
    }

    /**
     * ID
     */
    public int getId() {
        return id;
    }

    /**
     * 部位
     */
    public int getPosition() {
        return position;
    }


    @Override
    protected void parse(JSONObject object) {
        String $type = object.getString("type");
        if ($type != null) {
            type = ItemType.valueOf($type);
        }

        name = object.getString("name");
        id = object.getIntValue("id");
        position = object.getIntValue("position");
    }

    @Override
    public String toString() {
        return "EquipConfig{" +
                "type=" + ItemType.valueOf(type.getValue()) +
                ",name='" + name + '\'' +
                ",id=" + id +
                ",position=" + position +
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
