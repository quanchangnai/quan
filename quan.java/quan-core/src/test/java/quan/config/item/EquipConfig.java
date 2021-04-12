package quan.config.item;

import com.alibaba.fastjson.*;
import quan.config.*;
import java.util.*;

/**
 * 装备1,装备2<br/>
 * 代码自动生成，请勿手动修改
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
    public EquipConfig create(JSONObject json) {
        return new EquipConfig(json);
    }

    @Override
    public String toString() {
        return "EquipConfig{" +
                "id=" + id +
                ",key='" + key + '\'' +
                ",name='" + name + '\'' +
                ",type=" + type +
                ",useEffect=" + useEffect +
                ",reward=" + reward +
                ",list=" + list +
                ",set=" + set +
                ",map=" + map +
                ",effectiveTime='" + effectiveTime_ + '\'' +
                ",position=" + position +
                ",color=" + color +
                '}';

    }


    public static class self {

        private self() {
        }

        //所有EquipConfig
        private static volatile List<EquipConfig> configs = new ArrayList<>();

        //索引:ID
        private static volatile Map<Integer, EquipConfig> idConfigs = new HashMap<>();

        //索引:常量Key
        private static volatile Map<String, EquipConfig> keyConfigs = new HashMap<>();

        //索引:类型
        private static volatile Map<ItemType, List<EquipConfig>> typeConfigs = new HashMap<>();

        //索引:部位
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

        public static Map<String, EquipConfig> getKeyConfigs() {
            return keyConfigs;
        }

        public static EquipConfig getByKey(String key) {
            return keyConfigs.get(key);
        }

        public static Map<ItemType, List<EquipConfig>> getTypeConfigs() {
            return typeConfigs;
        }

        public static List<EquipConfig> getByType(ItemType type) {
            return typeConfigs.getOrDefault(type, Collections.emptyList());
        }

        public static Map<Integer, List<EquipConfig>> getPositionConfigs() {
            return positionConfigs;
        }

        public static List<EquipConfig> getByPosition(int position) {
            return positionConfigs.getOrDefault(position, Collections.emptyList());
        }


        /**
         * 加载配置，建立索引
         * @param configs 所有配置
         * @return 错误信息
         */
        @SuppressWarnings({"unchecked"})
        public static List<String> load(List<EquipConfig> configs) {
            Map<Integer, EquipConfig> idConfigs = new HashMap<>();
            Map<String, EquipConfig> keyConfigs = new HashMap<>();
            Map<ItemType, List<EquipConfig>> typeConfigs = new HashMap<>();
            Map<Integer, List<EquipConfig>> positionConfigs = new HashMap<>();

            List<String> errors = new ArrayList<>();

            for (EquipConfig config : configs) {
                Config.load(idConfigs, errors, config, true, Collections.singletonList("id"), config.id);
                if (!config.key.equals("")) {
                    Config.load(keyConfigs, errors, config, true, Collections.singletonList("key"), config.key);
                }
                Config.load(typeConfigs, errors, config, false, Collections.singletonList("type"), config.type);
                Config.load(positionConfigs, errors, config, false, Collections.singletonList("position"), config.position);
            }

            configs = Collections.unmodifiableList(configs);
            idConfigs = unmodifiableMap(idConfigs);
            keyConfigs = unmodifiableMap(keyConfigs);
            typeConfigs = unmodifiableMap(typeConfigs);
            positionConfigs = unmodifiableMap(positionConfigs);

            EquipConfig.self.configs = configs;
            EquipConfig.self.idConfigs = idConfigs;
            EquipConfig.self.keyConfigs = keyConfigs;
            EquipConfig.self.typeConfigs = typeConfigs;
            EquipConfig.self.positionConfigs = positionConfigs;

            return errors;
        }

    }

}
