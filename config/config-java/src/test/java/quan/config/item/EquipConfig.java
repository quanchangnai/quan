package quan.config.item;

import java.util.*;
import com.alibaba.fastjson.*;
import quan.config.*;

/**
* Created by 自动生成
*/
@SuppressWarnings({"unchecked"})
public class EquipConfig extends ItemConfig {

    protected int position;

    protected int color;

    public int getPosition() {
        return position;
    }

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

    public static class self {

        private self() {
        }

        private static Map<Integer, EquipConfig> idConfigs = new HashMap<>();

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


        public static List<String> index(List<EquipConfig> configs) {
            Map<Integer, EquipConfig> _idConfigs = new HashMap<>();
            Map<Integer, List<EquipConfig>> _positionConfigs = new HashMap<>();

            List<String> errors = new ArrayList<>();
            EquipConfig oldConfig;

            for (EquipConfig config : configs) {
                oldConfig = _idConfigs.put(config.id, config);
                if (oldConfig != null) {
                    String repeatedConfigs = config.getClass().getSimpleName();
                    if (oldConfig.getClass() != config.getClass()) {
                        repeatedConfigs += "," + oldConfig.getClass().getSimpleName();
                    }
                    errors.add(String.format("配置[%s]有重复数据[%s = %s]", repeatedConfigs, "id", config.id));
                }

                _positionConfigs.computeIfAbsent(config.position, k -> new ArrayList<>()).add(config);
            }

            idConfigs = unmodifiable(_idConfigs);
            positionConfigs = unmodifiable(_positionConfigs);

            return errors;
        }

    }

}
