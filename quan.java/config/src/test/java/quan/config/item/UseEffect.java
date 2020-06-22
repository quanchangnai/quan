package quan.config.item;

import com.alibaba.fastjson.*;
import quan.config.*;

/**
 * 使用效果<br/>
 * 自动生成
 */
public class UseEffect extends Bean {

    protected final int aaa;


    public UseEffect(JSONObject json) {
        super(json);

        this.aaa = json.getIntValue("aaa");
    }

    public final int getAaa() {
        return aaa;
    }


    public static UseEffect create(JSONObject json) {
        String clazz = json.getOrDefault("class", "").toString();
        switch (clazz) {
            case "UseEffect3":
                return UseEffect3.create(json);
            case "UseEffect2":
                return UseEffect2.create(json);
            case "UseEffect":
                return new UseEffect(json);
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        return "UseEffect{" +
                "aaa=" + aaa +
                '}';

    }

}
