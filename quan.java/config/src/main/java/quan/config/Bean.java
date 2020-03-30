package quan.config;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.Objects;

/**
 * Created by quanchangnai on 2019/7/11.
 */
public abstract class Bean {

    private final JSONObject json;

    public Bean(JSONObject json) {
        Objects.requireNonNull(json, "参数[json]不能为空");
        this.json = json;
    }

    public String toJson(SerializerFeature... features) {
        return json.toString(features);
    }

}
