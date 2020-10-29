package quan.config.item;

import com.alibaba.fastjson.*;

/**
 * 使用效果4<br/>
 * 代码自动生成，请勿手动修改
 */
public class UseEffect4 extends UseEffect {

    protected final ItemType itemType;


    public UseEffect4(JSONObject json) {
        super(json);

        int itemType = json.getIntValue("itemType");
        this.itemType = itemType > 0 ? ItemType.valueOf(itemType) : null;
    }

    public final ItemType getItemType() {
        return itemType;
    }


    public static UseEffect4 create(JSONObject json) {
        return new UseEffect4(json);
    }

    @Override
    public String toString() {
        return "UseEffect4{" +
                "aaa=" + aaa +
                ",itemType=" + itemType +
                '}';

    }

}
