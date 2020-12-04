package quan.config.quest;

import com.alibaba.fastjson.*;
import quan.config.*;
import java.util.*;
import quan.config.item.Reward;

/**
 * 任务<br/>
 * 代码自动生成，请勿手动修改
 */
public class QuestConfig extends Config {

    //ID
    protected final int id;

    //名字
    protected final String name;

    //类型
    protected final QuestType type;

    //任务目标
    protected final int target;

    //奖励
    protected final Reward reward;

    //A1
    protected final int a1;

    //A2
    protected final int a2;

    //B1
    protected final int b1;

    //B2
    protected final boolean b2;

    //C1
    protected final String c1;

    //C2
    protected final int c2;

    //C3
    protected final int c3;

    //D1
    protected final String d1;

    //D2
    protected final int d2;

    //D3
    protected final int d3;

    //S1
    protected final Set<Integer> s1;

    //L1
    protected final List<Integer> l1;

    //M1
    protected final Map<Integer, Integer> m1;


    public QuestConfig(JSONObject json) {
        super(json);

        this.id = json.getIntValue("id");
        this.name = json.getOrDefault("name", "").toString();
        this.type = QuestType.valueOf(json.getIntValue("type"));
        this.target = json.getIntValue("target");

        JSONObject reward = json.getJSONObject("reward");
        if (reward != null) {
            this.reward = Reward.create(reward);
        } else {
            this.reward = null;
        }

        this.a1 = json.getIntValue("a1");
        this.a2 = json.getIntValue("a2");
        this.b1 = json.getIntValue("b1");
        this.b2 = json.getBooleanValue("b2");
        this.c1 = json.getOrDefault("c1", "").toString();
        this.c2 = json.getIntValue("c2");
        this.c3 = json.getIntValue("c3");
        this.d1 = json.getOrDefault("d1", "").toString();
        this.d2 = json.getIntValue("d2");
        this.d3 = json.getIntValue("d3");

        JSONArray s1$1 = json.getJSONArray("s1");
        Set<Integer> s1$2 = new HashSet<>();
        if (s1$1 != null) {
            for (int i = 0; i < s1$1.size(); i++) {
                s1$2.add(s1$1.getInteger(i));
            }
        }
        this.s1 = Collections.unmodifiableSet(s1$2);

        JSONArray l1$1 = json.getJSONArray("l1");
        List<Integer> l1$2 = new ArrayList<>();
        if (l1$1 != null) {
            for (int i = 0; i < l1$1.size(); i++) {
                l1$2.add(l1$1.getInteger(i));
            }
        }
        this.l1 = Collections.unmodifiableList(l1$2);

        JSONObject m1$1 = json.getJSONObject("m1");
        Map<Integer, Integer> m1$2 = new HashMap<>();
        if (m1$1 != null) {
            for (String m1$Key : m1$1.keySet()) {
                m1$2.put(Integer.valueOf(m1$Key), m1$1.getInteger(m1$Key));
            }
        }
        this.m1 = Collections.unmodifiableMap(m1$2);
    }

    /**
     * ID
     */
    public final int getId() {
        return id;
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
    public final QuestType getType() {
        return type;
    }

    /**
     * 任务目标
     */
    public final int getTarget() {
        return target;
    }

    public final QuestTargetConfig getTarget$Ref() {
        return QuestTargetConfig.getById(target);
    }

    /**
     * 奖励
     */
    public final Reward getReward() {
        return reward;
    }

    /**
     * A1
     */
    public final int getA1() {
        return a1;
    }

    /**
     * A2
     */
    public final int getA2() {
        return a2;
    }

    /**
     * B1
     */
    public final int getB1() {
        return b1;
    }

    /**
     * B2
     */
    public final boolean getB2() {
        return b2;
    }

    /**
     * C1
     */
    public final String getC1() {
        return c1;
    }

    /**
     * C2
     */
    public final int getC2() {
        return c2;
    }

    /**
     * C3
     */
    public final int getC3() {
        return c3;
    }

    /**
     * D1
     */
    public final String getD1() {
        return d1;
    }

    /**
     * D2
     */
    public final int getD2() {
        return d2;
    }

    /**
     * D3
     */
    public final int getD3() {
        return d3;
    }

    /**
     * S1
     */
    public final Set<Integer> getS1() {
        return s1;
    }

    /**
     * L1
     */
    public final List<Integer> getL1() {
        return l1;
    }

    /**
     * M1
     */
    public final Map<Integer, Integer> getM1() {
        return m1;
    }


    @Override
    public QuestConfig create(JSONObject json) {
        return new QuestConfig(json);
    }

    @Override
    public String toString() {
        return "QuestConfig{" +
                "id=" + id +
                ",name='" + name + '\'' +
                ",type=" + type +
                ",target=" + target +
                ",reward=" + reward +
                ",a1=" + a1 +
                ",a2=" + a2 +
                ",b1=" + b1 +
                ",b2=" + b2 +
                ",c1='" + c1 + '\'' +
                ",c2=" + c2 +
                ",c3=" + c3 +
                ",d1='" + d1 + '\'' +
                ",d2=" + d2 +
                ",d3=" + d3 +
                ",s1=" + s1 +
                ",l1=" + l1 +
                ",m1=" + m1 +
                '}';

    }


    //所有QuestConfig
    private static volatile List<QuestConfig> configs = new ArrayList<>();

    //索引:两字段唯一索引
    private static volatile Map<Integer, Map<Integer, QuestConfig>> composite1Configs = new HashMap<>();

    //索引:两字段普通索引
    private static volatile Map<Integer, Map<Boolean, List<QuestConfig>>> composite2Configs = new HashMap<>();

    //索引:三字段唯一索引
    private static volatile Map<String, Map<Integer, Map<Integer, QuestConfig>>> composite3Configs = new HashMap<>();

    //索引:三字段普通索引
    private static volatile Map<String, Map<Integer, Map<Integer, List<QuestConfig>>>> composite4Configs = new HashMap<>();

    //索引:ID
    private static volatile Map<Integer, QuestConfig> idConfigs = new HashMap<>();

    //索引:类型
    private static volatile Map<QuestType, List<QuestConfig>> typeConfigs = new HashMap<>();

    public static List<QuestConfig> getConfigs() {
        return configs;
    }

    public static Map<Integer, Map<Integer, QuestConfig>> getComposite1Configs() {
        return composite1Configs;
    }

    public static Map<Integer, QuestConfig> getByComposite1(int a1) {
        return composite1Configs.getOrDefault(a1, Collections.emptyMap());
    }

    public static QuestConfig getByComposite1(int a1, int a2) {
        return getByComposite1(a1).get(a2);
    }

    public static Map<Integer, Map<Boolean, List<QuestConfig>>> getComposite2Configs() {
        return composite2Configs;
    }

    public static Map<Boolean, List<QuestConfig>> getByComposite2(int b1) {
        return composite2Configs.getOrDefault(b1, Collections.emptyMap());
    }

    public static List<QuestConfig> getByComposite2(int b1, boolean b2) {
        return getByComposite2(b1).getOrDefault(b2, Collections.emptyList());
    }

    public static Map<String, Map<Integer, Map<Integer, QuestConfig>>> getComposite3Configs() {
        return composite3Configs;
    }

    public static Map<Integer, Map<Integer, QuestConfig>> getByComposite3(String c1) {
        return composite3Configs.getOrDefault(c1, Collections.emptyMap());
    }

    public static Map<Integer, QuestConfig> getByComposite3(String c1, int c2) {
        return getByComposite3(c1).getOrDefault(c2, Collections.emptyMap());
    }

    public static QuestConfig getByComposite3(String c1, int c2, int c3) {
        return getByComposite3(c1, c2).get(c3);
    }

    public static Map<String, Map<Integer, Map<Integer, List<QuestConfig>>>> getComposite4Configs() {
        return composite4Configs;
    }

    public static Map<Integer, Map<Integer, List<QuestConfig>>> getByComposite4(String d1) {
        return composite4Configs.getOrDefault(d1, Collections.emptyMap());
    }

    public static Map<Integer, List<QuestConfig>> getByComposite4(String d1, int d2) {
        return getByComposite4(d1).getOrDefault(d2, Collections.emptyMap());
    }

    public static List<QuestConfig> getByComposite4(String d1, int d2, int d3) {
        return getByComposite4(d1, d2).getOrDefault(d3, Collections.emptyList());
    }

    public static Map<Integer, QuestConfig> getIdConfigs() {
        return idConfigs;
    }

    public static QuestConfig getById(int id) {
        return idConfigs.get(id);
    }

    public static Map<QuestType, List<QuestConfig>> getTypeConfigs() {
        return typeConfigs;
    }

    public static List<QuestConfig> getByType(QuestType type) {
        return typeConfigs.getOrDefault(type, Collections.emptyList());
    }


    /**
     * 加载配置，建立索引
     * @param configs 所有配置
     * @return 错误信息
     */
    @SuppressWarnings({"unchecked"})
    public static List<String> load(List<QuestConfig> configs) {
        Map<Integer, Map<Integer, QuestConfig>> composite1Configs = new HashMap<>();
        Map<Integer, Map<Boolean, List<QuestConfig>>> composite2Configs = new HashMap<>();
        Map<String, Map<Integer, Map<Integer, QuestConfig>>> composite3Configs = new HashMap<>();
        Map<String, Map<Integer, Map<Integer, List<QuestConfig>>>> composite4Configs = new HashMap<>();
        Map<Integer, QuestConfig> idConfigs = new HashMap<>();
        Map<QuestType, List<QuestConfig>> typeConfigs = new HashMap<>();

        List<String> errors = new ArrayList<>();

        for (QuestConfig config : configs) {
            load(composite1Configs, errors, config, true, Arrays.asList("a1", "a2"), config.a1, config.a2);
            load(composite2Configs, errors, config, false, Arrays.asList("b1", "b2"), config.b1, config.b2);
            load(composite3Configs, errors, config, true, Arrays.asList("c1", "c2", "c3"), config.c1, config.c2, config.c3);
            load(composite4Configs, errors, config, false, Arrays.asList("d1", "d2", "d3"), config.d1, config.d2, config.d3);
            load(idConfigs, errors, config, true, Collections.singletonList("id"), config.id);
            load(typeConfigs, errors, config, false, Collections.singletonList("type"), config.type);
        }

        configs = Collections.unmodifiableList(configs);
        composite1Configs = unmodifiableMap(composite1Configs);
        composite2Configs = unmodifiableMap(composite2Configs);
        composite3Configs = unmodifiableMap(composite3Configs);
        composite4Configs = unmodifiableMap(composite4Configs);
        idConfigs = unmodifiableMap(idConfigs);
        typeConfigs = unmodifiableMap(typeConfigs);

        QuestConfig.configs = configs;
        QuestConfig.composite1Configs = composite1Configs;
        QuestConfig.composite2Configs = composite2Configs;
        QuestConfig.composite3Configs = composite3Configs;
        QuestConfig.composite4Configs = composite4Configs;
        QuestConfig.idConfigs = idConfigs;
        QuestConfig.typeConfigs = typeConfigs;

        return errors;
    }

}
