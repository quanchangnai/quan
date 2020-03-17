package quan.config.common;

import java.util.*;
import com.alibaba.fastjson.*;
import quan.config.*;
import quan.config.item.Reward;

/**
 * 常量<br/>
 * 自动生成
 */
public class ConstantConfig extends Config {

    //常量Key
    protected final String key;

    //道具ID
    protected final int itemId;

    //奖励List
    protected final List<Reward> rewardList;

    //备注
    protected final String comment;


    public ConstantConfig(JSONObject json) {
        super(json);

        this.key = json.getOrDefault("key", "").toString();
        this.itemId = json.getIntValue("itemId");

        JSONArray $rewardList$1 = json.getJSONArray("rewardList");
        List<Reward> $rewardList$2 = new ArrayList<>();
        if ($rewardList$1 != null) {
            for (int i = 0; i < $rewardList$1.size(); i++) {
                Reward $rewardList$Value = new Reward($rewardList$1.getJSONObject(i));
                $rewardList$2.add($rewardList$Value);
            }
        }
        this.rewardList = Collections.unmodifiableList($rewardList$2);

        this.comment = json.getOrDefault("comment", "").toString();
    }

    /**
     * 常量Key
     */
    public final String getKey() {
        return key;
    }

    /**
     * 道具ID
     */
    public final int getItemId() {
        return itemId;
    }

    /**
     * 奖励List
     */
    public final List<Reward> getRewardList() {
        return rewardList;
    }

    /**
     * 备注
     */
    public final String getComment() {
        return comment;
    }


    @Override
    protected ConstantConfig create(JSONObject json) {
        return new ConstantConfig(json);
    }

    @Override
    public String toString() {
        return "ConstantConfig{" +
                "key='" + key + '\'' +
                ",itemId=" + itemId +
                ",rewardList=" + rewardList +
                ",comment='" + comment + '\'' +
                '}';

    }


    //所有ConstantConfig
    private static volatile List<ConstantConfig> configs = new ArrayList<>();

    //索引:常量Key
    private static volatile Map<String, ConstantConfig> keyConfigs = new HashMap<>();

    public static List<ConstantConfig> getConfigs() {
        return configs;
    }

    public static Map<String, ConstantConfig> getKeyConfigs() {
        return keyConfigs;
    }

    public static ConstantConfig getByKey(String key) {
        return keyConfigs.get(key);
    }


    /**
     * 加载配置，建立索引
     * @param configs 所有配置
     * @return 错误信息
     */
    @SuppressWarnings({"unchecked"})
    public static List<String> load(List<ConstantConfig> configs) {
        Map<String, ConstantConfig> keyConfigs = new HashMap<>();

        List<String> errors = new ArrayList<>();

        for (ConstantConfig config : configs) {
            if (!config.key.equals("")) {
                load(keyConfigs, errors, config, true, Collections.singletonList("key"), config.key);
            }
        }

        configs = Collections.unmodifiableList(configs);
        keyConfigs = unmodifiableMap(keyConfigs);

        ConstantConfig.configs = configs;
        ConstantConfig.keyConfigs = keyConfigs;

        return errors;
    }

}
