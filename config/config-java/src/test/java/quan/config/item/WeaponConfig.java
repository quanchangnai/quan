package quan.config.item;

import java.util.*;
import com.alibaba.fastjson.*;
import quan.config.*;

/**
* Created by 自动生成
*/
@SuppressWarnings({"unchecked"})
public class WeaponConfig extends EquipConfig {

    //字段1
    protected int w1;

    //字段2
    protected int w2;

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
    public void parse(JSONObject object) {
        super.parse(object);

        w1 = object.getIntValue("w1");
        w2 = object.getIntValue("w2");
    }

    @Override
    public String toString() {
        return "WeaponConfig{" +
                "id=" + id +
                ",name='" + name + '\'' +
                ",type=" + type +
                ",reward=" + reward +
                ",list=" + list +
                ",set=" + set +
                ",map=" + map +
                ",position=" + position +
                ",color=" + color +
                ",w1=" + w1 +
                ",w2=" + w2 +
                '}';

    }

    @Override
    public WeaponConfig create() {
        return new WeaponConfig();
    }

    public static class self {
        
        private self() {
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
            Map<Integer, WeaponConfig> _idConfigs = new HashMap<>();
            Map<Integer, Map<Integer, List<WeaponConfig>>> _composite1Configs = new HashMap<>();

            WeaponConfig oldConfig;
            for (WeaponConfig config : configs) {
                oldConfig = _idConfigs.put(config.id, config);
                if (oldConfig != null) {
                    String repeatedConfigs = config.getClass().getSimpleName();
                    if (oldConfig.getClass() != config.getClass()) {
                        repeatedConfigs += "," + oldConfig.getClass().getSimpleName();
                    }
                    throw new ConfigException("配置[" + repeatedConfigs + "]有重复索引[id:" + config.id + "]");
                }

                _composite1Configs.computeIfAbsent(config.color, k -> new HashMap<>()).computeIfAbsent(config.w1, k -> new ArrayList<>()).add(config);
            }

            idConfigs = unmodifiable(_idConfigs);
            composite1Configs = unmodifiable(_composite1Configs);

        }

    }

}
