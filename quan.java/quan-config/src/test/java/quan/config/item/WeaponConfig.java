package quan.config.item;

import com.alibaba.fastjson.*;
import java.util.*;
import quan.config.*;
import quan.config.load.ConfigLoader;

/**
 * 道具/武器<br/>
 * 代码自动生成，请勿手动修改
 */
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

        this.w1 = json.getIntValue(Field.W1);
        this.w2 = json.getIntValue(Field.W2);

        JSONArray rewardList$1 = json.getJSONArray(Field.REWARD_LIST);
        List<Reward> rewardList$2 = new ArrayList<>();
        if (rewardList$1 != null) {
            for (int i = 0; i < rewardList$1.size(); i++) {
                Reward rewardList$Value = Reward.create(rewardList$1.getJSONObject(i));
                rewardList$2.add(rewardList$Value);
            }
        }
        this.rewardList = Collections.unmodifiableList(rewardList$2);

        JSONArray rewardSet$1 = json.getJSONArray(Field.REWARD_SET);
        Set<Reward> rewardSet$2 = new HashSet<>();
        if (rewardSet$1 != null) {
            for (int i = 0; i < rewardSet$1.size(); i++) {
                Reward rewardSet$Value = Reward.create(rewardSet$1.getJSONObject(i));
                rewardSet$2.add(rewardSet$Value);
            }
        }
        this.rewardSet = Collections.unmodifiableSet(rewardSet$2);

        JSONObject rewardMap$1 = json.getJSONObject(Field.REWARD_MAP);
        Map<Integer, Reward> rewardMap$2 = new HashMap<>();
        if (rewardMap$1 != null) {
            for (String rewardMap$Key : rewardMap$1.keySet()) {
                Reward rewardMap$Value = Reward.create(rewardMap$1.getJSONObject(rewardMap$Key));
                rewardMap$2.put(Integer.valueOf(rewardMap$Key), rewardMap$Value);
            }
        }
        this.rewardMap = Collections.unmodifiableMap(rewardMap$2);

        JSONArray list2$1 = json.getJSONArray(Field.LIST2);
        List<Integer> list2$2 = new ArrayList<>();
        if (list2$1 != null) {
            for (int i = 0; i < list2$1.size(); i++) {
                list2$2.add(list2$1.getInteger(i));
            }
        }
        this.list2 = Collections.unmodifiableList(list2$2);
    }

    @Override
    public WeaponConfig create(JSONObject json) {
        return new WeaponConfig(json);
    }

    @Override
    public String toString() {
        return "WeaponConfig{" +
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
                ",w1=" + w1 +
                ",w2=" + w2 +
                ",rewardList=" + rewardList +
                ",rewardSet=" + rewardSet +
                ",rewardMap=" + rewardMap +
                ",list2=" + list2 +
                '}';

    }

    public static class Field extends EquipConfig.Field{

        /**
         * 字段1
         */
        public static final String W1 = "w1";

        /**
         * 字段2
         */
        public static final String W2 = "w2";

        /**
         * 奖励List
         */
        public static final String REWARD_LIST = "rewardList";

        /**
         * 奖励Set
         */
        public static final String REWARD_SET = "rewardSet";

        /**
         * 奖励Map
         */
        public static final String REWARD_MAP = "rewardMap";

        /**
         * List2
         */
        public static final String LIST2 = "list2";

    }


    public final static class self {

        private self() {
        }

        //所有WeaponConfig
        private static volatile List<WeaponConfig> _configs = Collections.emptyList();

        //索引:ID
        private static volatile Map<Integer, WeaponConfig> _idConfigs = Collections.emptyMap();

        //索引:常量Key
        private static volatile Map<String, WeaponConfig> _keyConfigs = Collections.emptyMap();

        //索引:类型
        private static volatile Map<ItemType, List<WeaponConfig>> _typeConfigs = Collections.emptyMap();

        //索引:部位
        private static volatile Map<Integer, List<WeaponConfig>> _positionConfigs = Collections.emptyMap();

        private static volatile Map<Integer, Map<Integer, List<WeaponConfig>>> _composite1Configs = Collections.emptyMap();

        private static volatile Map<Integer, Map<Integer, WeaponConfig>> _composite2Configs = Collections.emptyMap();

        public static List<WeaponConfig> getAll() {
            return _configs;
        }

        public static Map<Integer, WeaponConfig> getIdAll() {
            return _idConfigs;
        }

        public static WeaponConfig get(int id) {
            return _idConfigs.get(id);
        }

        public static Map<String, WeaponConfig> getKeyAll() {
            return _keyConfigs;
        }

        public static WeaponConfig getByKey(String key) {
            return _keyConfigs.get(key);
        }

        public static Map<ItemType, List<WeaponConfig>> getTypeAll() {
            return _typeConfigs;
        }

        public static List<WeaponConfig> getByType(ItemType type) {
            return _typeConfigs.getOrDefault(type, Collections.emptyList());
        }

        public static Map<Integer, List<WeaponConfig>> getPositionAll() {
            return _positionConfigs;
        }

        public static List<WeaponConfig> getByPosition(int position) {
            return _positionConfigs.getOrDefault(position, Collections.emptyList());
        }

        public static Map<Integer, Map<Integer, List<WeaponConfig>>> getComposite1All() {
            return _composite1Configs;
        }

        public static Map<Integer, List<WeaponConfig>> getByComposite1(int color) {
            return _composite1Configs.getOrDefault(color, Collections.emptyMap());
        }

        public static List<WeaponConfig> getByComposite1(int color, int w1) {
            return getByComposite1(color).getOrDefault(w1, Collections.emptyList());
        }

        public static Map<Integer, Map<Integer, WeaponConfig>> getComposite2All() {
            return _composite2Configs;
        }

        public static Map<Integer, WeaponConfig> getByComposite2(int w1) {
            return _composite2Configs.getOrDefault(w1, Collections.emptyMap());
        }

        public static WeaponConfig getByComposite2(int w1, int w2) {
            return getByComposite2(w1).get(w2);
        }


        @SuppressWarnings({"unchecked"})
        private static List<String> load(List<WeaponConfig> configs) {
            Map<Integer, WeaponConfig> idConfigs = new HashMap<>();
            Map<String, WeaponConfig> keyConfigs = new HashMap<>();
            Map<ItemType, List<WeaponConfig>> typeConfigs = new HashMap<>();
            Map<Integer, List<WeaponConfig>> positionConfigs = new HashMap<>();
            Map<Integer, Map<Integer, List<WeaponConfig>>> composite1Configs = new HashMap<>();
            Map<Integer, Map<Integer, WeaponConfig>> composite2Configs = new HashMap<>();

            List<String> errors = new ArrayList<>();

            for (WeaponConfig config : configs) {
                Config.load(idConfigs, errors, config, true, "id", config.id);
                if (!config.key.isEmpty()) {
                    Config.load(keyConfigs, errors, config, true, "key", config.key);
                }
                Config.load(typeConfigs, errors, config, false, "type", config.type);
                Config.load(positionConfigs, errors, config, false, "position", config.position);
                Config.load(composite1Configs, errors, config, false, "color,w1", config.color, config.w1);
                Config.load(composite2Configs, errors, config, true, "w1,w2", config.w1, config.w2);
            }

            configs = Collections.unmodifiableList(configs);
            idConfigs = unmodifiableMap(idConfigs);
            keyConfigs = unmodifiableMap(keyConfigs);
            typeConfigs = unmodifiableMap(typeConfigs);
            positionConfigs = unmodifiableMap(positionConfigs);
            composite1Configs = unmodifiableMap(composite1Configs);
            composite2Configs = unmodifiableMap(composite2Configs);

            WeaponConfig.self._configs = configs;
            WeaponConfig.self._idConfigs = idConfigs;
            WeaponConfig.self._keyConfigs = keyConfigs;
            WeaponConfig.self._typeConfigs = typeConfigs;
            WeaponConfig.self._positionConfigs = positionConfigs;
            WeaponConfig.self._composite1Configs = composite1Configs;
            WeaponConfig.self._composite2Configs = composite2Configs;

            return errors;
        }

    }

    static {
        ConfigLoader.registerLoadFunction(WeaponConfig.class, WeaponConfig.self::load);
    }

}
