package quan.config.item;

import java.util.*;
import com.alibaba.fastjson.*;
import quan.config.*;

/**
* Created by 自动生成
*/
@SuppressWarnings({"unchecked"})
public class WeaponConfig extends EquipConfig {

    protected int w1;

    protected int w2;

    protected List<Reward> rewardList = new ArrayList<>();

    protected Map<Integer, Reward> rewardMap = new HashMap<>();

    protected List<Integer> list2 = new ArrayList<>();

    public int getW1() {
        return w1;
    }

    public int getW2() {
        return w2;
    }

    public List<Reward> getRewardList() {
        return rewardList;
    }

    public Map<Integer, Reward> getRewardMap() {
        return rewardMap;
    }

    public List<Integer> getList2() {
        return list2;
    }


    @Override
    public void parse(JSONObject object) {
        super.parse(object);

        w1 = object.getIntValue("w1");
        w2 = object.getIntValue("w2");

        JSONArray $rewardList = object.getJSONArray("rewardList");
        if ($rewardList != null) {
            for (int i = 0; i < $rewardList.size(); i++) {
                Reward $rewardList$Value = new Reward();
                $rewardList$Value.parse($rewardList.getJSONObject(i));
                rewardList.add($rewardList$Value);
            }
        }
        rewardList = Collections.unmodifiableList(rewardList);

        JSONObject $rewardMap = object.getJSONObject("rewardMap");
        if ($rewardMap != null) {
            for (String $rewardMap$Key : $rewardMap.keySet()) {
                Reward $rewardMap$Value = new Reward();
                $rewardMap$Value.parse($rewardMap.getJSONObject($rewardMap$Key));
                rewardMap.put(Integer.valueOf($rewardMap$Key), $rewardMap$Value);
            }
        }
        rewardMap = Collections.unmodifiableMap(rewardMap);

        JSONArray $list2 = object.getJSONArray("list2");
        if ($list2 != null) {
            for (int i = 0; i < $list2.size(); i++) {
                list2.add($list2.getInteger(i));
            }
        }
        list2 = Collections.unmodifiableList(list2);
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
                ",rewardMap=" + rewardMap +
                ",list2=" + list2 +
                '}';

    }

    @Override
    public WeaponConfig create() {
        return new WeaponConfig();
    }

    public static class self {

        private self() {
        }

        private volatile static Map<Integer, WeaponConfig> idConfigs = new HashMap<>();

        private volatile static Map<Integer, List<WeaponConfig>> positionConfigs = new HashMap<>();

        private volatile static Map<Integer, Map<Integer, List<WeaponConfig>>> composite1Configs = new HashMap<>();

        private volatile static Map<Integer, Map<Integer, WeaponConfig>> composite2Configs = new HashMap<>();


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
            Map<Integer, WeaponConfig> _idConfigs = new HashMap<>();
            Map<Integer, List<WeaponConfig>> _positionConfigs = new HashMap<>();
            Map<Integer, Map<Integer, List<WeaponConfig>>> _composite1Configs = new HashMap<>();
            Map<Integer, Map<Integer, WeaponConfig>> _composite2Configs = new HashMap<>();

            List<String> errors = new ArrayList<>();
            WeaponConfig oldConfig;

            for (WeaponConfig config : configs) {
                oldConfig = _idConfigs.put(config.id, config);
                if (oldConfig != null) {
                    String repeatedConfigs = config.getClass().getSimpleName();
                    if (oldConfig.getClass() != config.getClass()) {
                        repeatedConfigs += "," + oldConfig.getClass().getSimpleName();
                    }
                    errors.add(String.format("配置[%s]有重复数据[%s = %s]", repeatedConfigs, "id", config.id));
                }

                _positionConfigs.computeIfAbsent(config.position, k -> new ArrayList<>()).add(config);

                _composite1Configs.computeIfAbsent(config.color, k -> new HashMap<>()).computeIfAbsent(config.w1, k -> new ArrayList<>()).add(config);

                oldConfig = _composite2Configs.computeIfAbsent(config.w1, k -> new HashMap<>()).put(config.w2, config);
                if (oldConfig != null) {
                    String repeatedConfigs = config.getClass().getSimpleName();
                    if (oldConfig.getClass() != config.getClass()) {
                        repeatedConfigs += "," + oldConfig.getClass().getSimpleName();
                    }
                    errors.add(String.format("配置[%s]有重复数据[%s,%s = %s,%s]", repeatedConfigs, "w1", "w2", config.w1, config.w2));
                }
            }

            idConfigs = unmodifiable(_idConfigs);
            positionConfigs = unmodifiable(_positionConfigs);
            composite1Configs = unmodifiable(_composite1Configs);
            composite2Configs = unmodifiable(_composite2Configs);

            return errors;
        }

    }

}
