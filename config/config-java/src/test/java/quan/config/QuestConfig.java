package quan.config;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by quanchangnai on 2019/7/11.
 */
public class QuestConfig extends Config {

    private static Map<Integer, QuestConfig> id_index = new HashMap<>();//唯一索引

    private static Map<Integer, List<QuestConfig>> type_index = new HashMap<>();//普通索引

    private static Map<Integer, Map<Integer, QuestConfig>> composite1_index = new HashMap<>();//二级组合唯一索引

    private static Map<Integer, Map<Integer, List<QuestConfig>>> composite2_index = new HashMap<>();//二级组合普通索引

    private static Map<Integer, Map<Integer, Map<Integer, QuestConfig>>> composite3_index = new HashMap<>();//三级组合唯一索引

    private static Map<Integer, Map<Integer, Map<Integer, List<QuestConfig>>>> composite4_index = new HashMap<>();//三级组合普通索引


    public static QuestConfig getById(int id) {
        return id_index.get(id);
    }

    public static List<QuestConfig> getByType(int type) {
        List<QuestConfig> list = type_index.get(type);
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    public static Map<Integer, QuestConfig> getByComposite1(int a1) {
        Map<Integer, QuestConfig> map = composite1_index.get(a1);
        if (map == null) {
            map = new HashMap<>();
        }
        return map;
    }

    public static QuestConfig getByComposite1(int a1, int a2) {
        return getByComposite1(a1).get(a2);
    }

    public static Map<Integer, List<QuestConfig>> getByComposite2(int b1) {
        Map<Integer, List<QuestConfig>> map = composite2_index.get(b1);
        if (map == null) {
            map = new HashMap<>();
        }
        return map;
    }

    public static List<QuestConfig> getByComposite2(int b1, int b2) {
        List<QuestConfig> list = getByComposite2(b1).get(b2);
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    public static Map<Integer, Map<Integer, QuestConfig>> getByComposite3(int c1) {
        Map<Integer, Map<Integer, QuestConfig>> map = composite3_index.get(c1);
        if (map == null) {
            map = new HashMap<>();
        }
        return map;
    }

    public static Map<Integer, QuestConfig> getByComposite3(int c1, int c2) {
        Map<Integer, QuestConfig> map = getByComposite3(c1).get(c2);
        if (map == null) {
            map = new HashMap<>();
        }
        return map;
    }

    public static QuestConfig getByComposite3(int c1, int c2, int c3) {
        return getByComposite3(c1, c2).get(c3);
    }

    public static Map<Integer, Map<Integer, List<QuestConfig>>> getByComposite4(int d1) {
        Map<Integer, Map<Integer, List<QuestConfig>>> map = composite4_index.get(d1);
        if (map == null) {
            map = new HashMap<>();
        }
        return map;
    }

    public static Map<Integer, List<QuestConfig>> getByComposite4(int d1, int d2) {
        Map<Integer, List<QuestConfig>> map = getByComposite4(d1).get(d2);
        if (map == null) {
            map = new HashMap<>();
        }
        return map;
    }

    public static List<QuestConfig> getByComposite4(int d1, int d2, int d3) {
        List<QuestConfig> list = getByComposite4(d1, d2).get(d3);
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    static void index(List<QuestConfig> configs) {

        Map<Integer, QuestConfig> id_index = new HashMap<>();

        Map<Integer, List<QuestConfig>> type_index = new HashMap<>();

        Map<Integer, Map<Integer, QuestConfig>> composite1_index = new HashMap<>();

        Map<Integer, Map<Integer, List<QuestConfig>>> composite2_index = new HashMap<>();

        Map<Integer, Map<Integer, Map<Integer, QuestConfig>>> composite3_index = new HashMap<>();

        Map<Integer, Map<Integer, Map<Integer, List<QuestConfig>>>> composite4_index = new HashMap<>();


        for (QuestConfig config : configs) {

            id_index.put(config.id, config);

            type_index.computeIfAbsent(config.type, k -> new ArrayList<>()).add(config);

            composite1_index.computeIfAbsent(config.a1, k -> new HashMap<>()).put(config.a2, config);

            composite2_index.computeIfAbsent(config.b1, k -> new HashMap<>()).computeIfAbsent(config.b2, k -> new ArrayList<>()).add(config);

            composite3_index.computeIfAbsent(config.c1, k -> new HashMap<>()).computeIfAbsent(config.c2, k -> new HashMap<>()).put(config.c3, config);

            composite4_index.computeIfAbsent(config.d1, k -> new HashMap<>()).computeIfAbsent(config.d2, k -> new HashMap<>()).computeIfAbsent(config.d3, k -> new ArrayList<>()).add(config);

        }

        QuestConfig.id_index = id_index;

        QuestConfig.type_index = type_index;

        QuestConfig.composite1_index = composite1_index;

        QuestConfig.composite2_index = composite2_index;

        QuestConfig.composite3_index = composite3_index;

        QuestConfig.composite4_index = composite4_index;

    }


    private int id;

    private String name;

    private int type;

    private int a1;

    private int a2;

    private int b1;

    private int b2;

    private int c1;

    private int c2;

    private int c3;

    private int d1;

    private int d2;

    private int d3;

    private Reward reward;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getType() {
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

    public int getB2() {
        return b2;
    }

    public int getC1() {
        return c1;
    }

    public int getC2() {
        return c2;
    }

    public int getC3() {
        return c3;
    }

    public int getD1() {
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


    @Override
    protected void parse(JSONObject object) {
        id = object.getIntValue("id");
        name = object.getString("name");
        type = object.getIntValue("type");
        a1 = object.getIntValue("a1");
        a2 = object.getIntValue("a2");
        b1 = object.getIntValue("b1");
        b2 = object.getIntValue("b2");
        c1 = object.getIntValue("c1");
        c2 = object.getIntValue("c2");
        c3 = object.getIntValue("c3");
        d1 = object.getIntValue("d1");
        d2 = object.getIntValue("d2");
        d3 = object.getIntValue("d3");

        JSONObject $reward = object.getJSONObject("reward");
        if ($reward != null) {
            reward = new Reward();
            reward.parse($reward);
        }

    }


    @Override
    public String toString() {
        return "QuestConfig{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", a1=" + a1 +
                ", a2=" + a2 +
                ", b1=" + b1 +
                ", b2=" + b2 +
                ", c1=" + c1 +
                ", c2=" + c2 +
                ", c3=" + c3 +
                ", d1=" + d1 +
                ", d2=" + d2 +
                ", d3=" + d3 +
                ", reward=" + reward +
                '}';
    }
}
