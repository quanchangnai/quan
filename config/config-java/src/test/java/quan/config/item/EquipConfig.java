package quan.config.item;

import java.util.*;
import com.alibaba.fastjson.*;
import quan.config.*;

/**
* Created by 自动生成
*/
@SuppressWarnings({"unchecked"})
public class EquipConfig extends ItemConfig {

    //部位
    protected int position;

    //颜色
    protected int color;

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
    public void parse(JSONObject object) {
        super.parse(object);

        position = object.getIntValue("position");
        color = object.getIntValue("color");
    }

    @Override
    public String toString() {
        return "EquipConfig{" +
                "id=" + id +
                ",name='" + name + '\'' +
                ",type=" + type +
                ",reward=" + reward +
                ",list=" + list +
                ",set=" + set +
                ",map=" + map +
                ",position=" + position +
                ",color=" + color +
                '}';

    }

    @Override
    public EquipConfig create() {
        return new EquipConfig();
    }

    public static class get {
        
        private get() {
        }

        //ID
	    private static Map<Integer, EquipConfig> idConfigs = new HashMap<>();

        //部位
        private static Map<Integer, List<EquipConfig>> positionConfigs = new HashMap<>();


        public static Map<Integer, EquipConfig> idConfigs() {
            return idConfigs;
        }

        public static EquipConfig byId(int id) {
            return idConfigs.get(id);
        }

        public static Map<Integer, List<EquipConfig>> positionConfigs() {
            return positionConfigs;
        }

        public static List<EquipConfig> byPosition(int position) {
            return positionConfigs.getOrDefault(position, Collections.emptyList());
        }


        public static void index(List<EquipConfig> configs) {
            Map<Integer, EquipConfig> idConfigs = new HashMap<>();
            Map<Integer, List<EquipConfig>> positionConfigs = new HashMap<>();

            EquipConfig oldConfig;
            for (EquipConfig config : configs) {
                oldConfig = idConfigs.put(config.id, config);
                if (oldConfig != null) {
                    String repeatedConfigs = config.getClass().getSimpleName();
                    if (oldConfig.getClass() != config.getClass()) {
                        repeatedConfigs += "," + oldConfig.getClass().getSimpleName();
                    }
                    throw new ConfigException("配置[" + repeatedConfigs + "]有重复索引[id:" + config.id + "]");
                }

                positionConfigs.computeIfAbsent(config.position, k -> new ArrayList<>()).add(config);
            }

            get.idConfigs = unmodifiable(idConfigs);
            get.positionConfigs = unmodifiable(positionConfigs);

        }

    }

}
