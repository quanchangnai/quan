package quan.config.item;

import java.util.*;
import com.alibaba.fastjson.*;
import quan.config.*;

/**
* 武器<br/>
* Created by 自动生成
*/
@SuppressWarnings({"unchecked"})
public class WeaponConfig extends EquipConfig {

    /**
     * 字段1
     */
    public final int w1;

    /**
     * 字段2
     */
    public final int w2;

    /**
     * 奖励List
     */
    public final List<Reward> rewardList;

    /**
     * 奖励Set
     */
    public final Set<Reward> rewardSet;

    /**
     * 奖励Map
     */
    public final Map<Integer, Reward> rewardMap;

    /**
     * List2
     */
    public final List<Integer> list2;


    public WeaponConfig(JSONObject json) {
        super(json);

        w1 = json.getIntValue("w1");
        w2 = json.getIntValue("w2");

        JSONArray $rewardList$1 = json.getJSONArray("rewardList");
        List<Reward> $rewardList$2 = new ArrayList<>();
        if ($rewardList$1 != null) {
            for (int i = 0; i < $rewardList$1.size(); i++) {
                Reward $rewardList$Value = new Reward($rewardList$1.getJSONObject(i));
                $rewardList$2.add($rewardList$Value);
            }
        }
        rewardList = Collections.unmodifiableList($rewardList$2);

        JSONArray $rewardSet$1 = json.getJSONArray("rewardSet");
        Set<Reward> $rewardSet$2 = new HashSet<>();
        if ($rewardSet$1 != null) {
            for (int i = 0; i < $rewardSet$1.size(); i++) {
                Reward $rewardSet$Value = new Reward($rewardSet$1.getJSONObject(i));
                $rewardSet$2.add($rewardSet$Value);
            }
        }
        rewardSet = Collections.unmodifiableSet($rewardSet$2);

        JSONObject $rewardMap$1 = json.getJSONObject("rewardMap");
        Map<Integer, Reward> $rewardMap$2 = new HashMap();
        if ($rewardMap$1 != null) {
            for (String $rewardMap$Key : $rewardMap$1.keySet()) {
                Reward $rewardMap$Value = new Reward($rewardMap$1.getJSONObject($rewardMap$Key));
                $rewardMap$2.put(Integer.valueOf($rewardMap$Key), $rewardMap$Value);
            }
        }
        rewardMap = Collections.unmodifiableMap($rewardMap$2);

        JSONArray $list2$1 = json.getJSONArray("list2");
        List<Integer> $list2$2 = new ArrayList<>();
        if ($list2$1 != null) {
            for (int i = 0; i < $list2$1.size(); i++) {
                $list2$2.add($list2$1.getInteger(i));
            }
        }
        list2 = Collections.unmodifiableList($list2$2);
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
                ",effectiveTime='" + effectiveTime$Str + '\'' +
                ",position=" + position +
                ",color=" + color +
                ",w1=" + w1 +
                ",w2=" + w2 +
                ",rewardList=" + rewardList +
                ",rewardSet=" + rewardSet +
                ",rewardMap=" + rewardMap +
                ",list2=" + list2 +
                '}';

    }

    @Override
    protected WeaponConfig create(JSONObject json) {
        return new WeaponConfig(json);
    }

    public static class self {

        private self() {
        }

        private volatile static List<WeaponConfig> configs = new ArrayList<>();

        //ID
        private volatile static Map<Integer, WeaponConfig> idConfigs = new HashMap<>();

        //部位
        private volatile static Map<Integer, List<WeaponConfig>> positionConfigs = new HashMap<>();

        private volatile static Map<Integer, Map<Integer, List<WeaponConfig>>> composite1Configs = new HashMap<>();

        private volatile static Map<Integer, Map<Integer, WeaponConfig>> composite2Configs = new HashMap<>();


        public static List<WeaponConfig> getConfigs() {
            return configs;
        }

        public static Map<Integer, WeaponConfig> getIdConfigs() {
            return idConfigs;
        }

        public static WeaponConfig getById(int id) {
            return idConfigs.get(id);
        }

        public static Map<Integer, List<WeaponConfig>> getPositionConfigs() {
            return positionConfigs;
        }

        public static List<WeaponConfig> getByPosition(int position) {
            return positionConfigs.getOrDefault(position, Collections.emptyList());
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

        public static Map<Integer, Map<Integer, WeaponConfig>> getComposite2Configs() {
            return composite2Configs;
        }

        public static Map<Integer, WeaponConfig> getByComposite2(int w1) {
            return composite2Configs.getOrDefault(w1, Collections.emptyMap());
        }

        public static WeaponConfig getByComposite2(int w1, int w2) {
            return getByComposite2(w1).get(w2);
        }


        public static List<String> index(List<WeaponConfig> configs) {
            Map<Integer, WeaponConfig> idConfigs = new HashMap<>();
            Map<Integer, List<WeaponConfig>> positionConfigs = new HashMap<>();
            Map<Integer, Map<Integer, List<WeaponConfig>>> composite1Configs = new HashMap<>();
            Map<Integer, Map<Integer, WeaponConfig>> composite2Configs = new HashMap<>();

            List<String> errors = new ArrayList<>();
            WeaponConfig oldConfig;

            for (WeaponConfig config : configs) {
                oldConfig = idConfigs.put(config.id, config);
                if (oldConfig != null) {
                    String repeatedConfigs = config.getClass().getSimpleName();
                    if (oldConfig.getClass() != config.getClass()) {
                        repeatedConfigs += "," + oldConfig.getClass().getSimpleName();
                    }
                    errors.add(String.format("配置[%s]有重复数据[%s = %s]", repeatedConfigs, "id", config.id));
                }

                positionConfigs.computeIfAbsent(config.position, k -> new ArrayList<>()).add(config);

                composite1Configs.computeIfAbsent(config.color, k -> new HashMap<>()).computeIfAbsent(config.w1, k -> new ArrayList<>()).add(config);

                oldConfig = composite2Configs.computeIfAbsent(config.w1, k -> new HashMap<>()).put(config.w2, config);
                if (oldConfig != null) {
                    String repeatedConfigs = config.getClass().getSimpleName();
                    if (oldConfig.getClass() != config.getClass()) {
                        repeatedConfigs += "," + oldConfig.getClass().getSimpleName();
                    }
                    errors.add(String.format("配置[%s]有重复数据[%s,%s = %s,%s]", repeatedConfigs, "w1", "w2", config.w1, config.w2));
                }
            }

            WeaponConfig.self.configs = Collections.unmodifiableList(configs);
            WeaponConfig.self.idConfigs = unmodifiableMap(idConfigs);
            WeaponConfig.self.positionConfigs = unmodifiableMap(positionConfigs);
            WeaponConfig.self.composite1Configs = unmodifiableMap(composite1Configs);
            WeaponConfig.self.composite2Configs = unmodifiableMap(composite2Configs);

            return errors;
        }

    }

}
