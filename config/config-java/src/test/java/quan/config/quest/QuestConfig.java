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

    protected long id;

    protected String name;

    protected QuestType type;

    protected int a1;

    protected int a2;

    protected int b1;

    protected boolean b2;

    protected String c1;

    protected int c2;

    protected int c3;

    protected String d1;

    protected int d2;

    protected int d3;

    protected Reward reward;

    protected Set<Integer> s1 = new HashSet<>();

    protected List<Integer> l1 = new ArrayList<>();

    protected Map<Integer, Integer> m1 = new HashMap<>();

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public QuestType getType() {
        return type;
    }

    public int getA1() {
        return a1;
    }

    public int getA2() {
        return a2;
    }

    public int getB1() {
        return b1;
    }

    public boolean getB2() {
        return b2;
    }

    public String getC1() {
        return c1;
    }

    public int getC2() {
        return c2;
    }

    public int getC3() {
        return c3;
    }

    public String getD1() {
        return d1;
    }

    public int getD2() {
        return d2;
    }

    public int getD3() {
        return d3;
    }

    public Reward getReward() {
        return reward;
    }

    public Set<Integer> getS1() {
        return s1;
    }

    public List<Integer> getL1() {
        return l1;
    }

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

    private static Map<Integer, Map<Integer, QuestConfig>> composite1Configs = new HashMap<>();

    private static Map<Integer, Map<Boolean, List<QuestConfig>>> composite2Configs = new HashMap<>();

    private static Map<String, Map<Integer, Map<Integer, QuestConfig>>> composite3Configs = new HashMap<>();

    private static Map<String, Map<Integer, Map<Integer, List<QuestConfig>>>> composite4Configs = new HashMap<>();

    private static Map<Long, QuestConfig> idConfigs = new HashMap<>();

    private static Map<QuestType, List<QuestConfig>> typeConfigs = new HashMap<>();


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


    public static List<String> index(List<QuestConfig> configs) {
        Map<Integer, Map<Integer, QuestConfig>> _composite1Configs = new HashMap<>();
        Map<Integer, Map<Boolean, List<QuestConfig>>> _composite2Configs = new HashMap<>();
        Map<String, Map<Integer, Map<Integer, QuestConfig>>> _composite3Configs = new HashMap<>();
        Map<String, Map<Integer, Map<Integer, List<QuestConfig>>>> _composite4Configs = new HashMap<>();
        Map<Long, QuestConfig> _idConfigs = new HashMap<>();
        Map<QuestType, List<QuestConfig>> _typeConfigs = new HashMap<>();

        List<String> errors = new ArrayList<>();
        QuestConfig oldConfig;

        for (QuestConfig config : configs) {
            oldConfig = _composite1Configs.computeIfAbsent(config.a1, k -> new HashMap<>()).put(config.a2, config);
            if (oldConfig != null) {
                String repeatedConfigs = config.getClass().getSimpleName();
                if (oldConfig.getClass() != config.getClass()) {
                    repeatedConfigs += "," + oldConfig.getClass().getSimpleName();
                }
                errors.add(String.format("配置[%s]有重复数据[%s,%s = %s,%s]", repeatedConfigs, "a1", "a2", config.a1, config.a2));
            }

            _composite2Configs.computeIfAbsent(config.b1, k -> new HashMap<>()).computeIfAbsent(config.b2, k -> new ArrayList<>()).add(config);

            oldConfig = _composite3Configs.computeIfAbsent(config.c1, k -> new HashMap<>()).computeIfAbsent(config.c2, k -> new HashMap<>()).put(config.c3, config);
            if (oldConfig != null) {
                String repeatedConfigs = config.getClass().getSimpleName();
                if (oldConfig.getClass() != config.getClass()) {
                    repeatedConfigs += "," + oldConfig.getClass().getSimpleName();
                }
                errors.add(String.format("配置[%s]有重复数据[%s,%s,%s = %s,%s,%s]", repeatedConfigs, "c1", "c2", "c3", config.c1, config.c2, config.c3));
            }

            _composite4Configs.computeIfAbsent(config.d1, k -> new HashMap<>()).computeIfAbsent(config.d2, k -> new HashMap<>()).computeIfAbsent(config.d3, k -> new ArrayList<>()).add(config);

            oldConfig = _idConfigs.put(config.id, config);
            if (oldConfig != null) {
                String repeatedConfigs = config.getClass().getSimpleName();
                if (oldConfig.getClass() != config.getClass()) {
                    repeatedConfigs += "," + oldConfig.getClass().getSimpleName();
                }
                errors.add(String.format("配置[%s]有重复数据[%s = %s]", repeatedConfigs, "id", config.id));
            }

            _typeConfigs.computeIfAbsent(config.type, k -> new ArrayList<>()).add(config);
        }

        composite1Configs = unmodifiable(_composite1Configs);
        composite2Configs = unmodifiable(_composite2Configs);
        composite3Configs = unmodifiable(_composite3Configs);
        composite4Configs = unmodifiable(_composite4Configs);
        idConfigs = unmodifiable(_idConfigs);
        typeConfigs = unmodifiable(_typeConfigs);

        return errors;
    }

}
