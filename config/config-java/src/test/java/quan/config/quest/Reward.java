package quan.config.quest;

import quan.config.*;
import java.util.*;
import com.alibaba.fastjson.*;

/**
* 奖励<br/>
* Created by 自动生成
*/
public class Reward extends Bean {

    
    private int itemId;

    
    private int itemNum;


    public int getItemId() {
        return itemId;
    }

    public int getItemNum() {
        return itemNum;
    }


    @Override
    protected void parse(JSONObject object) {
        itemId = object.getIntValue("itemId");
        itemNum = object.getIntValue("itemNum");
    }

    @Override
    public String toString() {
        return "Reward{" +
                "itemId=" + itemId +
                ",itemNum=" + itemNum +
                '}';

        }

}
