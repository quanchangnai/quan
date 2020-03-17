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

    //中午
    protected final Date noon;

    //中午
    protected final String noon$Str;


    public QuestTargetConfig(JSONObject json) {
        super(json);

        this.id = json.getIntValue("id");
        this.noon = json.getDate("noon");
        this.noon$Str = json.getOrDefault("noon$Str", "").toString();
    }

    /**
     * ID
     */
    public final int getId() {
        return id;
    }

    /**
     * 中午
     */
    public final Date getNoon() {
        return noon;
    }

    /**
     * 中午
     */
    public final String getNoon$Str() {
        return noon$Str;
    }


    @Override
    protected QuestTargetConfig create(JSONObject json) {
        return new QuestTargetConfig(json);
    }

    @Override
    public String toString() {
        return "QuestTargetConfig{" +
                "id=" + id +
                ",noon='" + noon$Str + '\'' +
                '}';

    }


    //所有QuestTargetConfig
    private static volatile List<QuestTargetConfig> configs = new ArrayList<>();

    //索引:ID
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


    /**
     * 加载配置，建立索引
     * @param configs 所有配置
     * @return 错误信息
     */
    @SuppressWarnings({"unchecked"})
    public static List<String> load(List<QuestTargetConfig> configs) {
        Map<Integer, QuestTargetConfig> idConfigs = new HashMap<>();

        List<String> errors = new ArrayList<>();

        for (QuestTargetConfig config : configs) {
            load(idConfigs, errors, config, true, Collections.singletonList("id"), config.id);
        }

        configs = Collections.unmodifiableList(configs);
        idConfigs = unmodifiableMap(idConfigs);

        QuestTargetConfig.configs = configs;
        QuestTargetConfig.idConfigs = idConfigs;

        return errors;
    }

}
