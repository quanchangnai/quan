package quan.config.quest;

import java.util.*;
import com.alibaba.fastjson.*;
import quan.config.*;
import quan.config.item.Reward;
import quan.quest.QuestType;

/**
* Created by 自动生成
*/
@SuppressWarnings({"unchecked"})
public class QuestConfig extends Config {

    //ID
    protected long id;

    //名字
    protected String name;

    //类型
    protected QuestType type;

    //A1
    protected int a1;

    //A2
    protected int a2;

    //B1
    protected int b1;

    //B2
    protected boolean b2;

    //C1
    protected String c1;

    //C2
    protected int c2;

    //C3
    protected int c3;

    //D1
    protected String d1;

    //D2
    protected int d2;

    //D3
    protected int d3;

    //Reward
    protected Reward reward;

    //S1
    protected Set<Integer> s1 = new HashSet<>();

    //L1
    protected List<Integer> l1 = new ArrayList<>();

    //M1
    protected Map<Integer, Integer> m1 = new HashMap<>();

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
    public void parse(JSONObject object) {
        super.parse(object);

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
                ",type=" + type +
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

    @Override
    public QuestConfig create() {
        return new QuestConfig();
    }

    public static class get {
        
        private get() {
        }

        private static Map<Integer, Map<Integer, QuestConfig>> composite1Configs = new HashMap<>();
    
        private static Map<Integer, Map<Boolean, List<QuestConfig>>> composite2Configs = new HashMap<>();

        private static Map<String, Map<Integer, Map<Integer, QuestConfig>>> composite3Configs = new HashMap<>();

        private static Map<String, Map<Integer, Map<Integer, List<QuestConfig>>>> composite4Configs = new HashMap<>();

        //ID
	    private static Map<Long, QuestConfig> idConfigs = new HashMap<>();

        //类型
        private static Map<QuestType, List<QuestConfig>> typeConfigs = new HashMap<>();


        public static Map<Integer, Map<Integer, QuestConfig>> composite1Configs() {
            return composite1Configs;
        }

        public static Map<Integer, QuestConfig> byComposite1(int a1) {
            return composite1Configs.getOrDefault(a1, Collections.emptyMap());
        }

        public static QuestConfig byComposite1(int a1, int a2) {
            return byComposite1(a1).get(a2);
        }

        public static Map<Integer, Map<Boolean, List<QuestConfig>>> composite2Configs() {
            return composite2Configs;
        }

        public static Map<Boolean, List<QuestConfig>> byComposite2(int b1) {
            return composite2Configs.getOrDefault(b1, Collections.emptyMap());
        }

        public static List<QuestConfig> byComposite2(int b1, boolean b2) {
            return byComposite2(b1).getOrDefault(b2, Collections.emptyList());
        }

        public static Map<String, Map<Integer, Map<Integer, QuestConfig>>> composite3Configs() {
            return composite3Configs;
        }

        public static Map<Integer, Map<Integer, QuestConfig>> byComposite3(String c1) {
            return composite3Configs.getOrDefault(c1, Collections.emptyMap());
        }

        public static Map<Integer, QuestConfig> byComposite3(String c1, int c2) {
            return byComposite3(c1).getOrDefault(c2, Collections.emptyMap());
        }

        public static QuestConfig byComposite3(String c1, int c2, int c3) {
            return byComposite3(c1, c2).get(c3);
        }

        public static Map<String, Map<Integer, Map<Integer, List<QuestConfig>>>> composite4Configs() {
            return composite4Configs;
        }

        public static Map<Integer, Map<Integer, List<QuestConfig>>> byComposite4(String d1) {
            return composite4Configs.getOrDefault(d1, Collections.emptyMap());
        }

        public static Map<Integer, List<QuestConfig>> byComposite4(String d1, int d2) {
            return byComposite4(d1).getOrDefault(d2, Collections.emptyMap());
        }

        public static List<QuestConfig> byComposite4(String d1, int d2, int d3) {
            return byComposite4(d1, d2).getOrDefault(d3, Collections.emptyList());
        }

        public static Map<Long, QuestConfig> idConfigs() {
            return idConfigs;
        }

        public static QuestConfig byId(long id) {
            return idConfigs.get(id);
        }

        public static Map<QuestType, List<QuestConfig>> typeConfigs() {
            return typeConfigs;
        }

        public static List<QuestConfig> byType(QuestType type) {
            return typeConfigs.getOrDefault(type, Collections.emptyList());
        }


        public static void index(List<QuestConfig> configs) {
            Map<Integer, Map<Integer, QuestConfig>> composite1Configs = new HashMap<>();
            Map<Integer, Map<Boolean, List<QuestConfig>>> composite2Configs = new HashMap<>();
            Map<String, Map<Integer, Map<Integer, QuestConfig>>> composite3Configs = new HashMap<>();
            Map<String, Map<Integer, Map<Integer, List<QuestConfig>>>> composite4Configs = new HashMap<>();
            Map<Long, QuestConfig> idConfigs = new HashMap<>();
            Map<QuestType, List<QuestConfig>> typeConfigs = new HashMap<>();

            QuestConfig oldConfig;
            for (QuestConfig config : configs) {
                oldConfig = composite1Configs.computeIfAbsent(config.a1, k -> new HashMap<>()).put(config.a2, config);
                if (oldConfig != null) {
                    String repeatedConfigs = config.getClass().getSimpleName();
                    if (oldConfig.getClass() != config.getClass()) {
                        repeatedConfigs += "," + oldConfig.getClass().getSimpleName();
                    }
                    throw new ConfigException("配置[" + repeatedConfigs + "]有重复索引[a1,a2:" + config.a1 + "," + config.a2 + "]");
                }

                composite2Configs.computeIfAbsent(config.b1, k -> new HashMap<>()).computeIfAbsent(config.b2, k -> new ArrayList<>()).add(config);

                oldConfig = composite3Configs.computeIfAbsent(config.c1, k -> new HashMap<>()).computeIfAbsent(config.c2, k -> new HashMap<>()).put(config.c3, config);
                if (oldConfig != null) {
                    String repeatedConfigs = config.getClass().getSimpleName();
                    if (oldConfig.getClass() != config.getClass()) {
                        repeatedConfigs += "," + oldConfig.getClass().getSimpleName();
                    }
                    throw new ConfigException("配置[" + repeatedConfigs + "]有重复索引[c1,c2,c3:" + config.c1 + "," + config.c2 + "," + config.c3 + "]");
                }

                composite4Configs.computeIfAbsent(config.d1, k -> new HashMap<>()).computeIfAbsent(config.d2, k -> new HashMap<>()).computeIfAbsent(config.d3, k -> new ArrayList<>()).add(config);

                oldConfig = idConfigs.put(config.id, config);
                if (oldConfig != null) {
                    String repeatedConfigs = config.getClass().getSimpleName();
                    if (oldConfig.getClass() != config.getClass()) {
                        repeatedConfigs += "," + oldConfig.getClass().getSimpleName();
                    }
                    throw new ConfigException("配置[" + repeatedConfigs + "]有重复索引[id:" + config.id + "]");
                }

                typeConfigs.computeIfAbsent(config.type, k -> new ArrayList<>()).add(config);
            }

            get.composite1Configs = unmodifiable(composite1Configs);
            get.composite2Configs = unmodifiable(composite2Configs);
            get.composite3Configs = unmodifiable(composite3Configs);
            get.composite4Configs = unmodifiable(composite4Configs);
            get.idConfigs = unmodifiable(idConfigs);
            get.typeConfigs = unmodifiable(typeConfigs);

        }

    }

}
