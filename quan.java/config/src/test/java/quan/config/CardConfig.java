package quan.config;

import java.util.*;
import com.alibaba.fastjson.*;

/**
 * CardConfig<br/>
 * 代码自动生成，请勿手动修改
 */
public class CardConfig extends Config {

    //ID
    protected final int id;

    //常量Key
    protected final String key;

    //名字
    protected final String name;

    //类型
    protected final int type;

    //List
    protected final List<Integer> list;

    //Set
    protected final Set<Integer> set;

    //Map
    protected final Map<Integer, Integer> map;

    //生效时间
    protected final Date effectiveTime;

    //生效时间
    protected final String effectiveTime_;


    public CardConfig(JSONObject json) {
        super(json);

        this.id = json.getIntValue("id");
        this.key = json.getOrDefault("key", "").toString();
        this.name = json.getOrDefault("name", "").toString();
        this.type = json.getIntValue("type");

        JSONArray list$1 = json.getJSONArray("list");
        List<Integer> list$2 = new ArrayList<>();
        if (list$1 != null) {
            for (int i = 0; i < list$1.size(); i++) {
                list$2.add(list$1.getInteger(i));
            }
        }
        this.list = Collections.unmodifiableList(list$2);

        JSONArray set$1 = json.getJSONArray("set");
        Set<Integer> set$2 = new HashSet<>();
        if (set$1 != null) {
            for (int i = 0; i < set$1.size(); i++) {
                set$2.add(set$1.getInteger(i));
            }
        }
        this.set = Collections.unmodifiableSet(set$2);

        JSONObject map$1 = json.getJSONObject("map");
        Map<Integer, Integer> map$2 = new HashMap<>();
        if (map$1 != null) {
            for (String map$Key : map$1.keySet()) {
                map$2.put(Integer.valueOf(map$Key), map$1.getInteger(map$Key));
            }
        }
        this.map = Collections.unmodifiableMap(map$2);

        this.effectiveTime = json.getDate("effectiveTime");
        this.effectiveTime_ = json.getOrDefault("effectiveTime_", "").toString();
    }

    /**
     * ID
     */
    public final int getId() {
        return id;
    }

    /**
     * 常量Key
     */
    public final String getKey() {
        return key;
    }

    /**
     * 名字
     */
    public final String getName() {
        return name;
    }

    /**
     * 类型
     */
    public final int getType() {
        return type;
    }

    /**
     * List
     */
    public final List<Integer> getList() {
        return list;
    }

    /**
     * Set
     */
    public final Set<Integer> getSet() {
        return set;
    }

    /**
     * Map
     */
    public final Map<Integer, Integer> getMap() {
        return map;
    }

    /**
     * 生效时间
     */
    public final Date getEffectiveTime() {
        return effectiveTime;
    }

    /**
     * 生效时间
     */
    public final String getEffectiveTime_() {
        return effectiveTime_;
    }


    @Override
    public CardConfig create(JSONObject json) {
        return new CardConfig(json);
    }

    @Override
    public String toString() {
        return "CardConfig{" +
                "id=" + id +
                ",key='" + key + '\'' +
                ",name='" + name + '\'' +
                ",type=" + type +
                ",list=" + list +
                ",set=" + set +
                ",map=" + map +
                ",effectiveTime='" + effectiveTime_ + '\'' +
                '}';

    }


    //所有CardConfig
    private static volatile List<CardConfig> configs = new ArrayList<>();

    //索引:ID
    private static volatile Map<Integer, CardConfig> idConfigs = new HashMap<>();

    public static List<CardConfig> getConfigs() {
        return configs;
    }

    public static Map<Integer, CardConfig> getIdConfigs() {
        return idConfigs;
    }

    public static CardConfig getById(int id) {
        return idConfigs.get(id);
    }


    /**
     * 加载配置，建立索引
     * @param configs 所有配置
     * @return 错误信息
     */
    @SuppressWarnings({"unchecked"})
    public static List<String> load(List<CardConfig> configs) {
        Map<Integer, CardConfig> idConfigs = new HashMap<>();

        List<String> errors = new ArrayList<>();

        for (CardConfig config : configs) {
            load(idConfigs, errors, config, true, Collections.singletonList("id"), config.id);
        }

        configs = Collections.unmodifiableList(configs);
        idConfigs = unmodifiableMap(idConfigs);

        CardConfig.configs = configs;
        CardConfig.idConfigs = idConfigs;

        return errors;
    }

}
