package quan.config.item;

import quan.config.*;
import java.util.*;
import com.alibaba.fastjson.*;

/**
* 道具<br/>
* Created by 自动生成
*/
public class ItemConfig extends Config {

    //ID
    private int id;

    //名字
    private String name;

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


    @Override
    protected void parse(JSONObject object) {
        id = object.getIntValue("id");
        name = object.getString("name");
    }

    @Override
    public String toString() {
        return "ItemConfig{" +
                "id=" + id +
                ",name='" + name + '\'' +
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


    static void index(List<ItemConfig> configs) {
        Map<Integer, ItemConfig> idConfigs = new HashMap<>();

        for (ItemConfig config : configs) {
            if (idConfigs.put(config.id, config) != null) {
                throw new RuntimeException("配置[ItemConfig]的索引[id]:[" + config.id + "]有重复");
            }
        }

        ItemConfig.idConfigs = unmodifiable(idConfigs);

    }

}
