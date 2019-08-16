package quan.config;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.Objects;

/**
 * Created by quanchangnai on 2019/7/11.
 */
public abstract class Bean {

    private JSONObject json;

    public String toJson(SerializerFeature... features) {
        if (json != null) {
            return json.toString(features);
        } else {
            return new JSONObject().toString(features);
        }
    }

    public Bean(JSONObject json) {
        Objects.requireNonNull(json, "参数[json]不能为空");
        this.json = json;
    }

}
