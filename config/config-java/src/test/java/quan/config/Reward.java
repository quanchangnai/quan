package quan.config;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by quanchangnai on 2019/7/11.
 */
public class Reward extends Bean {

    private int itemId;

    private int itemNum;


    @Override
    protected void parse(JSONObject object) {
        itemId = object.getIntValue("itemId");
        itemNum = object.getIntValue("itemNum");
    }

    public int getItemId() {
        return itemId;
    }

    public int getItemNum() {
        return itemNum;
    }

    @Override
    public String toString() {
        return "Reward{" +
                "itemId=" + itemId +
                ", itemNum=" + itemNum +
                '}';
    }
}
