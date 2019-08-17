package quan.config.quest;

import java.util.*;
import com.alibaba.fastjson.*;
import quan.config.*;

/**
* QuestTargetConfig<br/>
* Created by 自动生成
*/
public class QuestTargetConfig extends Config {

    //ID
    protected final int id;

    //名字
    protected final String name;


    public QuestTargetConfig(JSONObject json) {
        super(json);

        this.id = json.getIntValue("id");
        this.name = json.getOrDefault("name", "").toString();
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


    @Override
    protected QuestTargetConfig create(JSONObject json) {
        return new QuestTargetConfig(json);
    }

    @Override
    public String toString() {
        return "QuestTargetConfig{" +
                "id=" + id +
                ",name='" + name + '\'' +
                '}';

    }


    private volatile static List<QuestTargetConfig> configs = new ArrayList<>();

    //ID
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


    @SuppressWarnings({"unchecked"})
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
