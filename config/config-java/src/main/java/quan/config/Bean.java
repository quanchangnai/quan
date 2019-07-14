package quan.config;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by quanchangnai on 2019/7/11.
 */
public abstract class Bean {

    protected abstract void parse(JSONObject object);

}
