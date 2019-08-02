package quan.config.quest;

import java.util.*;
import com.alibaba.fastjson.*;
import quan.config.*;

/**
* Created by 自动生成
*/
@SuppressWarnings({"unchecked"})
public class QuestTargetConfig extends Config {

    protected long id;

    protected String name;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    @Override
    public void parse(JSONObject object) {
        super.parse(object);

        id = object.getLongValue("id");
        name = object.getString("name");
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

    private static Map<Long, QuestTargetConfig> idConfigs = new HashMap<>();


    public static Map<Long, QuestTargetConfig> getIdConfigs() {
        return idConfigs;
    }

    public static QuestTargetConfig getById(long id) {
        return idConfigs.get(id);
    }


    public static List<String> index(List<QuestTargetConfig> configs) {
        Map<Long, QuestTargetConfig> _idConfigs = new HashMap<>();

        List<String> errors = new ArrayList<>();
        QuestTargetConfig oldConfig;

        for (QuestTargetConfig config : configs) {
            oldConfig = _idConfigs.put(config.id, config);
            if (oldConfig != null) {
                String repeatedConfigs = config.getClass().getSimpleName();
                if (oldConfig.getClass() != config.getClass()) {
                    repeatedConfigs += "," + oldConfig.getClass().getSimpleName();
                }
                errors.add(String.format("配置[%s]有重复数据[%s = %s]", repeatedConfigs, "id", config.id));
            }
        }

        idConfigs = unmodifiable(_idConfigs);

        return errors;
    }

}