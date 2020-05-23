package quan.config.common;

import java.util.*;
import quan.config.item.Reward;

/**
 * 自动生成
 */
public enum RewardConstant {

    constant1,

    constant2;

    public List<Reward> value() {
        return ConstantConfig.getByKey(name()).getRewardList();
    }

}
