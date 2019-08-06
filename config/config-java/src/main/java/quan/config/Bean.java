package quan.config;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.Objects;

/**
 * Created by quanchangnai on 2019/7/11.
 */
public abstract class Bean {

    protected JSONObject json = new JSONObject();

    public String toJson(SerializerFeature... features) {
        return json.toString(features);
    }

    public void parse(JSONObject object) {
        Objects.requireNonNull(object, "参数[object]不能为空");
        json = object;
    }

}
