package quan.config.quest;

import java.util.*;
import com.alibaba.fastjson.*;
import quan.config.*;

/**
* QuestTargetConfig<br/>
* 自动生成
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


    // 所有QuestTargetConfig
    private static volatile List<QuestTargetConfig> configs = new ArrayList<>();

    //ID
    private static volatile Map<Integer, QuestTargetConfig> idConfigs = new HashMap<>();

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
    public static List<String> load(List<QuestTargetConfig> configs) {
        Map<Integer, QuestTargetConfig> idConfigs = new HashMap<>();

        List<String> errors = new ArrayList<>();

        for (QuestTargetConfig config : configs) {
            load(idConfigs, errors, config, true, Arrays.asList("id"), config.id);
        }

        configs = Collections.unmodifiableList(configs);
        idConfigs = unmodifiableMap(idConfigs);

        QuestTargetConfig.configs = configs;
        QuestTargetConfig.idConfigs = idConfigs;

        return errors;
    }

}
