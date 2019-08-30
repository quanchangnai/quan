package quan.config.item;

import java.util.*;
import com.alibaba.fastjson.*;
import quan.config.*;

/**
* 武器<br/>
* 自动生成
*/
public class WeaponConfig extends EquipConfig {

    //字段1
    protected final int w1;

    //字段2
    protected final int w2;

    //奖励List
    protected final List<Reward> rewardList;

    //奖励Set
    protected final Set<Reward> rewardSet;

    //奖励Map
    protected final Map<Integer, Reward> rewardMap;

    //List2
    protected final List<Integer> list2;


    public WeaponConfig(JSONObject json) {
        super(json);

        this.w1 = json.getIntValue("w1");
        this.w2 = json.getIntValue("w2");

        JSONArray $rewardList$1 = json.getJSONArray("rewardList");
        List<Reward> $rewardList$2 = new ArrayList<>();
        if ($rewardList$1 != null) {
            for (int i = 0; i < $rewardList$1.size(); i++) {
                Reward $rewardList$Value = new Reward($rewardList$1.getJSONObject(i));
                $rewardList$2.add($rewardList$Value);
            }
        }
        this.rewardList = Collections.unmodifiableList($rewardList$2);

        JSONArray $rewardSet$1 = json.getJSONArray("rewardSet");
        Set<Reward> $rewardSet$2 = new HashSet<>();
        if ($rewardSet$1 != null) {
            for (int i = 0; i < $rewardSet$1.size(); i++) {
                Reward $rewardSet$Value = new Reward($rewardSet$1.getJSONObject(i));
                $rewardSet$2.add($rewardSet$Value);
            }
        }
        this.rewardSet = Collections.unmodifiableSet($rewardSet$2);

        JSONObject $rewardMap$1 = json.getJSONObject("rewardMap");
        Map<Integer, Reward> $rewardMap$2 = new HashMap<>();
        if ($rewardMap$1 != null) {
            for (String $rewardMap$Key : $rewardMap$1.keySet()) {
                Reward $rewardMap$Value = new Reward($rewardMap$1.getJSONObject($rewardMap$Key));
                $rewardMap$2.put(Integer.valueOf($rewardMap$Key), $rewardMap$Value);
            }
        }
        this.rewardMap = Collections.unmodifiableMap($rewardMap$2);

        JSONArray $list2$1 = json.getJSONArray("list2");
        List<Integer> $list2$2 = new ArrayList<>();
        if ($list2$1 != null) {
            for (int i = 0; i < $list2$1.size(); i++) {
                $list2$2.add($list2$1.getInteger(i));
            }
        }
        this.list2 = Collections.unmodifiableList($list2$2);
    }

    /**
     * 字段1
     */
    public final int getW1() {
        return w1;
    }

    /**
     * 字段2
     */
    public final int getW2() {
        return w2;
    }

    /**
     * 奖励List
     */
    public final List<Reward> getRewardList() {
        return rewardList;
    }

    /**
     * 奖励Set
     */
    public final Set<Reward> getRewardSet() {
        return rewardSet;
    }

    /**
     * 奖励Map
     */
    public final Map<Integer, Reward> getRewardMap() {
        return rewardMap;
    }

    /**
     * List2
     */
    public final List<Integer> getList2() {
        return list2;
    }


    @Override
    protected WeaponConfig create(JSONObject json) {
        return new WeaponConfig(json);
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


    public static class self {

        private self() {
        }

        // 所有WeaponConfig
        private static volatile List<WeaponConfig> configs = new ArrayList<>();

        //ID
        private static volatile Map<Integer, WeaponConfig> idConfigs = new HashMap<>();

        //部位
        private static volatile Map<Integer, List<WeaponConfig>> positionConfigs = new HashMap<>();

        private static volatile Map<Integer, Map<Integer, List<WeaponConfig>>> composite1Configs = new HashMap<>();

        private static volatile Map<Integer, Map<Integer, WeaponConfig>> composite2Configs = new HashMap<>();

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


        @SuppressWarnings({"unchecked"})
        public static List<String> load(List<WeaponConfig> configs) {
            Map<Integer, WeaponConfig> idConfigs = new HashMap<>();
            Map<Integer, List<WeaponConfig>> positionConfigs = new HashMap<>();
            Map<Integer, Map<Integer, List<WeaponConfig>>> composite1Configs = new HashMap<>();
            Map<Integer, Map<Integer, WeaponConfig>> composite2Configs = new HashMap<>();

            List<String> errors = new ArrayList<>();

            for (WeaponConfig config : configs) {
                Config.load(idConfigs, errors, config, true, Arrays.asList("id"), config.id);
                Config.load(positionConfigs, errors, config, false, Arrays.asList("position"), config.position);
                Config.load(composite1Configs, errors, config, false, Arrays.asList("color", "w1"), config.color, config.w1);
                Config.load(composite2Configs, errors, config, true, Arrays.asList("w1", "w2"), config.w1, config.w2);
            }

            configs = Collections.unmodifiableList(configs);
            idConfigs = unmodifiableMap(idConfigs);
            positionConfigs = unmodifiableMap(positionConfigs);
            composite1Configs = unmodifiableMap(composite1Configs);
            composite2Configs = unmodifiableMap(composite2Configs);

            WeaponConfig.self.configs = configs;
            WeaponConfig.self.idConfigs = idConfigs;
            WeaponConfig.self.positionConfigs = positionConfigs;
            WeaponConfig.self.composite1Configs = composite1Configs;
            WeaponConfig.self.composite2Configs = composite2Configs;

            return errors;
        }

    }

}
