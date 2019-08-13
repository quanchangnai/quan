package quan.config.quest;

import java.util.*;
import com.alibaba.fastjson.*;
import quan.config.*;

/**
* Created by 自动生成
*/
@SuppressWarnings({"unchecked"})
public class QuestTargetConfig extends Config {

    protected int id;

    protected String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    @Override
    public void parse(JSONObject object) {
        super.parse(object);

        id = object.getIntValue("id");
        name = object.getOrDefault("name", "").toString();
    }

    @Override
    public String toString() {
        return "QuestTargetConfig{" +
                "id=" + id +
                ",name='" + name + '\'' +
                '}';

    }

    @Override
    public QuestTargetConfig create() {
        return new QuestTargetConfig();
    }

    private volatile static List<QuestTargetConfig> configs = new ArrayList<>();

    private volatile static Map<Integer, QuestTargetConfig> idConfigs = new HashMap<>();


    public static List<QuestTargetConfig> getConfigs() {
        return configs;
    }

    public static Map<Integer, QuestTargetConfig> getIdConfigs() {
        return idConfigs;
    }

    public static QuestTargetConfig getById(int id) {
        return idConfigs.get(id);
    }


    public static List<String> index(List<QuestTargetConfig> configs) {
        Map<Integer, QuestTargetConfig> idConfigs = new HashMap<>();

        List<String> errors = new ArrayList<>();
        QuestTargetConfig oldConfig;

        for (QuestTargetConfig config : configs) {
            oldConfig = idConfigs.put(config.id, config);
            if (oldConfig != null) {
                String repeatedConfigs = config.getClass().getSimpleName();
                if (oldConfig.getClass() != config.getClass()) {
                    repeatedConfigs += "," + oldConfig.getClass().getSimpleName();
                }
                errors.add(String.format("配置[%s]有重复数据[%s = %s]", repeatedConfigs, "id", config.id));
            }
        }

        QuestTargetConfig.configs = Collections.unmodifiableList(configs);
        QuestTargetConfig.idConfigs = unmodifiableMap(idConfigs);

        return errors;
    }

}
