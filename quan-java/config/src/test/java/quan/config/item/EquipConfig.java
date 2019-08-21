package quan.config.item;

import java.util.*;
import com.alibaba.fastjson.*;
import quan.config.*;

/**
* 装备1,装备2<br/>
* Created by 自动生成
*/
public class EquipConfig extends ItemConfig {

    //部位
    protected final int position;

    //颜色
    protected final int color;


    public EquipConfig(JSONObject json) {
        super(json);

        this.position = json.getIntValue("position");
        this.color = json.getIntValue("color");
    }

    /**
     * 部位
     */
    public final int getPosition() {
        return position;
    }

    /**
     * 颜色
     */
    public final int getColor() {
        return color;
    }


    @Override
    protected EquipConfig create(JSONObject json) {
        return new EquipConfig(json);
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
                ",effectiveTime='" + effectiveTime$Str + '\'' +
                ",position=" + position +
                ",color=" + color +
                '}';

    }


    public static class self {

        private self() {
        }

        private static volatile List<EquipConfig> configs = new ArrayList<>();

        //ID
        private static volatile Map<Integer, EquipConfig> idConfigs = new HashMap<>();

        //部位
        private static volatile Map<Integer, List<EquipConfig>> positionConfigs = new HashMap<>();

        public static List<EquipConfig> getConfigs() {
            return configs;
        }

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


        @SuppressWarnings({"unchecked"})
        public static List<String> index(List<EquipConfig> configs) {
            Map<Integer, EquipConfig> idConfigs = new HashMap<>();
            Map<Integer, List<EquipConfig>> positionConfigs = new HashMap<>();

            List<String> errors = new ArrayList<>();
            EquipConfig oldConfig;

            for (EquipConfig config : configs) {
                oldConfig = idConfigs.put(config.id, config);
                if (oldConfig != null) {
                    String repeatedConfigs = config.getClass().getSimpleName();
                    if (oldConfig.getClass() != config.getClass()) {
                        repeatedConfigs += "," + oldConfig.getClass().getSimpleName();
                    }
                    errors.add(String.format("配置[%s]有重复数据[%s = %s]", repeatedConfigs, "id", config.id));
                }

                positionConfigs.computeIfAbsent(config.position, k -> new ArrayList<>()).add(config);
            }

            configs = Collections.unmodifiableList(configs);
            idConfigs = unmodifiableMap(idConfigs);
            positionConfigs = unmodifiableMap(positionConfigs);

            EquipConfig.self.configs = configs;
            EquipConfig.self.idConfigs = idConfigs;
            EquipConfig.self.positionConfigs = positionConfigs;

            return errors;
        }

    }

}