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


    @Override
    protected void parse(JSONObject object) {
        id = object.getIntValue("id");
        name = object.getString("name");

        String $type = object.getString("type");
        if ($type != null) {
            type = ItemType.valueOf($type);
        }
    }

    @Override
    public String toString() {
        return "ItemConfig{" +
                "id=" + id +
                ",name='" + name + '\'' +
                ",type=" + ItemType.valueOf(type.getValue()) +
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
