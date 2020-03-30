package quan.config.common;

import java.util.*;
import quan.config.item.Reward;

/**
 * 自动生成
 */
public class RewardConstant {

    public static List<Reward> constant1() {
        return ConstantConfig.getByKey("constant1").getRewardList();
    }

    public static List<Reward> constant2() {
        return ConstantConfig.getByKey("constant2").getRewardList();
    }

}
