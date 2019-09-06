package quan.config.common;

import java.util.*;
import com.alibaba.fastjson.*;
import quan.config.*;

/**
* 错误码<br/>
* 自动生成
*/
public class ErrorCodeConfig extends Config {

    //ID
    protected final int id;

    //Key
    protected final String key;

    //文本
    protected final String text;


    public ErrorCodeConfig(JSONObject json) {
        super(json);

        this.id = json.getIntValue("id");
        this.key = json.getOrDefault("key", "").toString();
        this.text = json.getOrDefault("text", "").toString();
    }

    /**
     * ID
     */
    public final int getId() {
        return id;
    }

    /**
     * Key
     */
    public final String getKey() {
        return key;
    }

    /**
     * 文本
     */
    public final String getText() {
        return text;
    }


    @Override
    protected ErrorCodeConfig create(JSONObject json) {
        return new ErrorCodeConfig(json);
    }

    @Override
    public String toString() {
        return "ErrorCodeConfig{" +
                "id=" + id +
                ",key='" + key + '\'' +
                ",text='" + text + '\'' +
                '}';

    }


    // 所有ErrorCodeConfig
    private static volatile List<ErrorCodeConfig> configs = new ArrayList<>();

    //ID
    private static volatile Map<Integer, ErrorCodeConfig> idConfigs = new HashMap<>();

    //Key
    private static volatile Map<String, ErrorCodeConfig> keyConfigs = new HashMap<>();

    public static List<ErrorCodeConfig> getConfigs() {
        return configs;
    }

    public static Map<Integer, ErrorCodeConfig> getIdConfigs() {
        return idConfigs;
    }

    public static ErrorCodeConfig getById(int id) {
        return idConfigs.get(id);
    }

    public static Map<String, ErrorCodeConfig> getKeyConfigs() {
        return keyConfigs;
    }

    public static ErrorCodeConfig getByKey(String key) {
        return keyConfigs.get(key);
    }


    @SuppressWarnings({"unchecked"})
    public static List<String> load(List<ErrorCodeConfig> configs) {
        Map<Integer, ErrorCodeConfig> idConfigs = new HashMap<>();
        Map<String, ErrorCodeConfig> keyConfigs = new HashMap<>();

        List<String> errors = new ArrayList<>();

        for (ErrorCodeConfig config : configs) {
            load(idConfigs, errors, config, true, Arrays.asList("id"), config.id);
            load(keyConfigs, errors, config, true, Arrays.asList("key"), config.key);
        }

        configs = Collections.unmodifiableList(configs);
        idConfigs = unmodifiableMap(idConfigs);
        keyConfigs = unmodifiableMap(keyConfigs);

        ErrorCodeConfig.configs = configs;
        ErrorCodeConfig.idConfigs = idConfigs;
        ErrorCodeConfig.keyConfigs = keyConfigs;

        return errors;
    }

}
