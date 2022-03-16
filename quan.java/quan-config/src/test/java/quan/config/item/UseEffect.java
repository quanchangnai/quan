package quan.config.item;

import com.alibaba.fastjson.*;
import quan.config.*;
import quan.config.loader.ConfigLoader;

/**
 * 使用效果<br/>
 * 代码自动生成，请勿手动修改
 */
public class UseEffect extends Bean {

    public final int aaa;


    public UseEffect(JSONObject json) {
        super(json);

        this.aaa = json.getIntValue("aaa");
    }

    public static UseEffect create(JSONObject json) {
        String clazz = json.getOrDefault("class", "").toString();
        switch (clazz) {
            case "UseEffect4":
                return UseEffect4.create(json);
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
