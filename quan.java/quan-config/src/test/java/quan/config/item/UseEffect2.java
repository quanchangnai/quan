package quan.config.item;

import com.alibaba.fastjson.*;

/**
 * 使用效果2<br/>
 * 代码自动生成，请勿手动修改
 */
public class UseEffect2 extends UseEffect {

    public final int bbb;


    public UseEffect2(JSONObject json) {
        super(json);

        this.bbb = json.getIntValue("bbb");
    }

    public static UseEffect2 create(JSONObject json) {
        return new UseEffect2(json);
    }

    @Override
    public String toString() {
        return "UseEffect2{" +
                "aaa=" + aaa +
                ",bbb=" + bbb +
                '}';

    }

}
