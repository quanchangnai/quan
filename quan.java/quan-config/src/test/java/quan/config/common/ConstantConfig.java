package quan.config.common;

import com.alibaba.fastjson.*;
import java.util.*;
import quan.config.*;
import quan.config.item.ItemConfig;
import quan.config.load.ConfigLoader;

/**
 * 常量<br/>
 * 代码自动生成，请勿手动修改
 */
public class ConstantConfig extends Config {

    /**
     * 常量Key
     */
    public final String key;

    /**
     * 道具ID
     */
    public final int itemId;

    /**
     * 道具ID
     */
    public final ItemConfig itemIdRef() {
        return ItemConfig.get(itemId);
    }

    /**
     * 奖励
     */
    public final Reward reward;

    /**
     * 奖励List
     */
    public final List<quan.config.item.Reward> rewardList;

    /**
     * 备注
     */
    public final String comment;


    public ConstantConfig(JSONObject json) {
        super(json);

        this.key = json.getOrDefault(Field.KEY, "").toString();
        this.itemId = json.getIntValue(Field.ITEM_ID);

        JSONObject reward = json.getJSONObject(Field.REWARD);
        if (reward != null) {
            this.reward = Reward.create(reward);
        } else {
            this.reward = null;
        }

        JSONArray rewardList$1 = json.getJSONArray(Field.REWARD_LIST);
        List<quan.config.item.Reward> rewardList$2 = new ArrayList<>();
        if (rewardList$1 != null) {
            for (int i = 0; i < rewardList$1.size(); i++) {
                quan.config.item.Reward rewardList$Value = quan.config.item.Reward.create(rewardList$1.getJSONObject(i));
                rewardList$2.add(rewardList$Value);
            }
        }
        this.rewardList = Collections.unmodifiableList(rewardList$2);

        this.comment = json.getOrDefault(Field.COMMENT, "").toString();
    }

    @Override
    public ConstantConfig create(JSONObject json) {
        return new ConstantConfig(json);
    }

    @Override
    public String toString() {
        return "ConstantConfig{" +
                "key='" + key + '\'' +
                ",itemId=" + itemId +
                ",reward=" + reward +
                ",rewardList=" + rewardList +
                ",comment='" + comment + '\'' +
                '}';

    }

    public static class Field {

        /**
         * 常量Key
         */
        public static final String KEY = "key";

        /**
         * 道具ID
         */
        public static final String ITEM_ID = "itemId";

        /**
         * 奖励
         */
        public static final String REWARD = "reward";

        /**
         * 奖励List
         */
        public static final String REWARD_LIST = "rewardList";

        /**
         * 备注
         */
        public static final String COMMENT = "comment";

    }


    //所有ConstantConfig
    private static volatile List<ConstantConfig> _configs = Collections.emptyList();

    //索引:常量Key
    private static volatile Map<String, ConstantConfig> _keyConfigs = Collections.emptyMap();

    public static List<ConstantConfig> getAll() {
        return _configs;
    }

    public static Map<String, ConstantConfig> getKeyAll() {
        return _keyConfigs;
    }

    public static ConstantConfig getByKey(String key) {
        return _keyConfigs.get(key);
    }


    @SuppressWarnings({"unchecked"})
    private static List<String> load(List<ConstantConfig> configs) {
        Map<String, ConstantConfig> keyConfigs = new HashMap<>();

        List<String> errors = new ArrayList<>();

        for (ConstantConfig config : configs) {
            if (!config.key.isEmpty()) {
                load(keyConfigs, errors, config, true, "key", config.key);
            }
        }

        configs = Collections.unmodifiableList(configs);
        keyConfigs = unmodifiableMap(keyConfigs);

        ConstantConfig._configs = configs;
        ConstantConfig._keyConfigs = keyConfigs;

        return errors;
    }

    static {
        ConfigLoader.registerLoadFunction(ConstantConfig.class, ConstantConfig::load);
    }

}
