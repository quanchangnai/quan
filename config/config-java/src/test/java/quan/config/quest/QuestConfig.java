package quan.config.quest;

import quan.config.*;
import java.util.*;
import com.alibaba.fastjson.*;

/**
* Created by 自动生成
*/
public class QuestConfig extends Config {

    //ID
	private static Map<Long, QuestConfig> idConfigs = new HashMap<>();

    //类型
    private static Map<QuestType, List<QuestConfig>> typeConfigs = new HashMap<>();

    private static Map<Integer, Map<Integer, QuestConfig>> composite1Configs = new HashMap<>();

    private static Map<Integer, Map<Boolean, List<QuestConfig>>> composite2Configs = new HashMap<>();

    private static Map<String, Map<Integer, Map<Integer, QuestConfig>>> composite3Configs = new HashMap<>();

    private static Map<String, Map<Integer, Map<Integer, List<QuestConfig>>>> composite4Configs = new HashMap<>();


    public static Map<Long, QuestConfig> getIdConfigs() {
        return idConfigs;
    }

    public static QuestConfig getById(long id) {
        return idConfigs.get(id);
    }

    public static Map<QuestType, List<QuestConfig>> getTypeConfigs() {
        return typeConfigs;
    }

    public static List<QuestConfig> getByType(QuestType type) {
        return typeConfigs.getOrDefault(type, Collections.emptyList());
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
        return getByComposite3(c1, c3).get(c3);
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


    static void index(List<QuestConfig> configs) {
        Map<Long, QuestConfig> idConfigs = new HashMap<>();
        Map<QuestType, List<QuestConfig>> typeConfigs = new HashMap<>();
        Map<Integer, Map<Integer, QuestConfig>> composite1Configs = new HashMap<>();
        Map<Integer, Map<Boolean, List<QuestConfig>>> composite2Configs = new HashMap<>();
        Map<String, Map<Integer, Map<Integer, QuestConfig>>> composite3Configs = new HashMap<>();
        Map<String, Map<Integer, Map<Integer, List<QuestConfig>>>> composite4Configs = new HashMap<>();

        for (QuestConfig config : configs) {
            if (idConfigs.put(config.id, config) != null) {
                throw new RuntimeException("配置[QuestConfig]的索引[id]:[" + config.id + "]有重复");
            }

            typeConfigs.computeIfAbsent(config.type, k -> new ArrayList<>()).add(config);

            if (composite1Configs.computeIfAbsent(config.a1, k -> new HashMap<>()).put(config.a2, config) != null) {
                throw new RuntimeException("配置[QuestConfig]的索引[[a1,[a2]:[" + config.a1 + "," + config.a2 + "]有重复");
            }

            composite2Configs.computeIfAbsent(config.b1, k -> new HashMap<>()).computeIfAbsent(config.b2, k -> new ArrayList<>()).add(config);

            if (composite3Configs.computeIfAbsent(config.c1, k -> new HashMap<>()).computeIfAbsent(config.c2, k -> new HashMap<>()).put(config.c3, config) != null) {
                throw new RuntimeException("配置[QuestConfig]的索引[c1,c2,c3]:[" + config.c1 + "," + config.c2 + "," + config.c3 + "]有重复");
            }

            composite4Configs.computeIfAbsent(config.d1, k -> new HashMap<>()).computeIfAbsent(config.d2, k -> new HashMap<>()).computeIfAbsent(config.d3, k -> new ArrayList<>()).add(config);
        }

        QuestConfig.idConfigs = unmodifiable(idConfigs);
        QuestConfig.typeConfigs = unmodifiable(typeConfigs);
        QuestConfig.composite1Configs = unmodifiable(composite1Configs);
        QuestConfig.composite2Configs = unmodifiable(composite2Configs);
        QuestConfig.composite3Configs = unmodifiable(composite3Configs);
        QuestConfig.composite4Configs = unmodifiable(composite4Configs);
    }


    //ID
    private long id;

    //名字
    private String name;

    //类型
    private QuestType type;

    //A1
    private int a1;

    //A2
    private int a2;

    //B1
    private int b1;

    //B2
    private boolean b2;

    //C1
    private String c1;

    //C2
    private int c2;

    //C3
    private int c3;

    //D1
    private String d1;

    //D2
    private int d2;

    //D3
    private int d3;

    //Reward
    private Reward reward;

    //S1
    private Set<Integer> s1 = new HashSet<>();

    //L1
    private List<Integer> l1 = new ArrayList<>();

    //M1
    private Map<Integer, Integer> m1 = new HashMap<>();


    /**
     * ID
     */
    public long getId() {
        return id;
    }

    /**
     * 名字
     */
    public String getName() {
        return name;
    }

    /**
     * 类型
     */
    public QuestType getType() {
        return type;
    }

    /**
     * A1
     */
    public int getA1() {
        return a1;
    }

    /**
     * A2
     */
    public int getA2() {
        return a2;
    }

    /**
     * B1
     */
    public int getB1() {
        return b1;
    }

    /**
     * B2
     */
    public boolean getB2() {
        return b2;
    }

    /**
     * C1
     */
    public String getC1() {
        return c1;
    }

    /**
     * C2
     */
    public int getC2() {
        return c2;
    }

    /**
     * C3
     */
    public int getC3() {
        return c3;
    }

    /**
     * D1
     */
    public String getD1() {
        return d1;
    }

    /**
     * D2
     */
    public int getD2() {
        return d2;
    }

    /**
     * D3
     */
    public int getD3() {
        return d3;
    }

    /**
     * Reward
     */
    public Reward getReward() {
        return reward;
    }

    /**
     * S1
     */
    public Set<Integer> getS1() {
        return s1;
    }

    /**
     * L1
     */
    public List<Integer> getL1() {
        return l1;
    }

    /**
     * M1
     */
    public Map<Integer, Integer> getM1() {
        return m1;
    }


    @Override
    protected void parse(JSONObject object) {
        id = object.getLongValue("id");
        name = object.getString("name");

        String $type = object.getString("type");
        if ($type != null) {
            type = QuestType.valueOf($type);
        }

        a1 = object.getIntValue("a1");
        a2 = object.getIntValue("a2");
        b1 = object.getIntValue("b1");
        b2 = object.getBooleanValue("b2");
        c1 = object.getString("c1");
        c2 = object.getIntValue("c2");
        c3 = object.getIntValue("c3");
        d1 = object.getString("d1");
        d2 = object.getIntValue("d2");
        d3 = object.getIntValue("d3");

        JSONObject $reward = object.getJSONObject("reward");
        if ($reward != null) {
            reward = new Reward();
            reward.parse($reward);
        }

        JSONArray $s1 = object.getJSONArray("s1");
        if ($s1 != null) {
            for (int i = 0; i < $s1.size(); i++) {
                s1.add($s1.getInteger(i));
            }
        }
        s1 = Collections.unmodifiableSet(s1);

        JSONArray $l1 = object.getJSONArray("l1");
        if ($l1 != null) {
            for (int i = 0; i < $l1.size(); i++) {
                l1.add($l1.getInteger(i));
            }
        }
        l1 = Collections.unmodifiableList(l1);

        JSONObject $m1 = object.getJSONObject("m1");
        if ($m1 != null) {
            for (String $m1$Key : $m1.keySet()) {
                m1.put(Integer.valueOf($m1$Key), $m1.getInteger($m1$Key));
            }
        }
        m1 = Collections.unmodifiableMap(m1);
    }

    @Override
    public String toString() {
        return "QuestConfig{" +
                "id=" + id +
                ",name='" + name + '\'' +
                ",type=" + QuestType.valueOf(type.getValue()) +
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
                ",reward=" + reward +
                ",s1=" + s1 +
                ",l1=" + l1 +
                ",m1=" + m1 +
                '}';

        }

}
