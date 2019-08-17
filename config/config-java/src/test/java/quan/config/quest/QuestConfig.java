package quan.config.quest;

import java.util.*;
import com.alibaba.fastjson.*;
import quan.config.*;
import quan.config.item.Reward;

/**
* 任务<br/>
* Created by 自动生成
*/
@SuppressWarnings({"unchecked"})
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

        id = json.getIntValue("id");
        name = json.getOrDefault("name", "").toString();

        String $type = json.getString("type");
        if ($type != null) {
            type = QuestType.valueOf($type);
        } else {
            type = null;
        }

        target = json.getIntValue("target");

        JSONObject $reward = json.getJSONObject("reward");
        if ($reward != null) {
            reward = new Reward($reward);
        } else {
            reward = null;
        }

        a1 = json.getIntValue("a1");
        a2 = json.getIntValue("a2");
        b1 = json.getIntValue("b1");
        b2 = json.getBooleanValue("b2");
        c1 = json.getOrDefault("c1", "").toString();
        c2 = json.getIntValue("c2");
        c3 = json.getIntValue("c3");
        d1 = json.getOrDefault("d1", "").toString();
        d2 = json.getIntValue("d2");
        d3 = json.getIntValue("d3");

        JSONArray $s1$1 = json.getJSONArray("s1");
        Set<Integer> $s1$2 = new HashSet<>();
        if ($s1$1 != null) {
            for (int i = 0; i < $s1$1.size(); i++) {
                $s1$2.add($s1$1.getInteger(i));
            }
        }
        s1 = Collections.unmodifiableSet($s1$2);

        JSONArray $l1$1 = json.getJSONArray("l1");
        List<Integer> $l1$2 = new ArrayList<>();
        if ($l1$1 != null) {
            for (int i = 0; i < $l1$1.size(); i++) {
                $l1$2.add($l1$1.getInteger(i));
            }
        }
        l1 = Collections.unmodifiableList($l1$2);

        JSONObject $m1$1 = json.getJSONObject("m1");
        Map<Integer, Integer> $m1$2 = new HashMap();
        if ($m1$1 != null) {
            for (String $m1$Key : $m1$1.keySet()) {
                $m1$2.put(Integer.valueOf($m1$Key), $m1$1.getInteger($m1$Key));
            }
        }
        m1 = Collections.unmodifiableMap($m1$2);
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
    protected QuestConfig create(JSONObject json) {
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


    private volatile static List<QuestConfig> configs = new ArrayList<>();

    private volatile static Map<Integer, Map<Integer, QuestConfig>> composite1Configs = new HashMap<>();

    private volatile static Map<Integer, Map<Boolean, List<QuestConfig>>> composite2Configs = new HashMap<>();

    private volatile static Map<String, Map<Integer, Map<Integer, QuestConfig>>> composite3Configs = new HashMap<>();

    private volatile static Map<String, Map<Integer, Map<Integer, List<QuestConfig>>>> composite4Configs = new HashMap<>();

    //ID
    private volatile static Map<Integer, QuestConfig> idConfigs = new HashMap<>();

    //类型
    private volatile static Map<QuestType, List<QuestConfig>> typeConfigs = new HashMap<>();

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


    public static List<String> index(List<QuestConfig> configs) {
        Map<Integer, Map<Integer, QuestConfig>> composite1Configs = new HashMap<>();
        Map<Integer, Map<Boolean, List<QuestConfig>>> composite2Configs = new HashMap<>();
        Map<String, Map<Integer, Map<Integer, QuestConfig>>> composite3Configs = new HashMap<>();
        Map<String, Map<Integer, Map<Integer, List<QuestConfig>>>> composite4Configs = new HashMap<>();
        Map<Integer, QuestConfig> idConfigs = new HashMap<>();
        Map<QuestType, List<QuestConfig>> typeConfigs = new HashMap<>();

        List<String> errors = new ArrayList<>();
        QuestConfig oldConfig;

        for (QuestConfig config : configs) {
            oldConfig = composite1Configs.computeIfAbsent(config.a1, k -> new HashMap<>()).put(config.a2, config);
            if (oldConfig != null) {
                String repeatedConfigs = config.getClass().getSimpleName();
                if (oldConfig.getClass() != config.getClass()) {
                    repeatedConfigs += "," + oldConfig.getClass().getSimpleName();
                }
                errors.add(String.format("配置[%s]有重复数据[%s,%s = %s,%s]", repeatedConfigs, "a1", "a2", config.a1, config.a2));
            }

            composite2Configs.computeIfAbsent(config.b1, k -> new HashMap<>()).computeIfAbsent(config.b2, k -> new ArrayList<>()).add(config);

            oldConfig = composite3Configs.computeIfAbsent(config.c1, k -> new HashMap<>()).computeIfAbsent(config.c2, k -> new HashMap<>()).put(config.c3, config);
            if (oldConfig != null) {
                String repeatedConfigs = config.getClass().getSimpleName();
                if (oldConfig.getClass() != config.getClass()) {
                    repeatedConfigs += "," + oldConfig.getClass().getSimpleName();
                }
                errors.add(String.format("配置[%s]有重复数据[%s,%s,%s = %s,%s,%s]", repeatedConfigs, "c1", "c2", "c3", config.c1, config.c2, config.c3));
            }

            composite4Configs.computeIfAbsent(config.d1, k -> new HashMap<>()).computeIfAbsent(config.d2, k -> new HashMap<>()).computeIfAbsent(config.d3, k -> new ArrayList<>()).add(config);

            oldConfig = idConfigs.put(config.id, config);
            if (oldConfig != null) {
                String repeatedConfigs = config.getClass().getSimpleName();
                if (oldConfig.getClass() != config.getClass()) {
                    repeatedConfigs += "," + oldConfig.getClass().getSimpleName();
                }
                errors.add(String.format("配置[%s]有重复数据[%s = %s]", repeatedConfigs, "id", config.id));
            }

            typeConfigs.computeIfAbsent(config.type, k -> new ArrayList<>()).add(config);
        }

        QuestConfig.configs = Collections.unmodifiableList(configs);
        QuestConfig.composite1Configs = unmodifiableMap(composite1Configs);
        QuestConfig.composite2Configs = unmodifiableMap(composite2Configs);
        QuestConfig.composite3Configs = unmodifiableMap(composite3Configs);
        QuestConfig.composite4Configs = unmodifiableMap(composite4Configs);
        QuestConfig.idConfigs = unmodifiableMap(idConfigs);
        QuestConfig.typeConfigs = unmodifiableMap(typeConfigs);

        return errors;
    }

}
