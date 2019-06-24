package quan.database;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public abstract class Bean extends Node {

    public abstract JSONObject encode();

    public abstract void decode(JSONObject object);

}
