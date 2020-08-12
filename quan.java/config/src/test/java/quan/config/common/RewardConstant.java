package quan.config.common;

import java.util.*;
import quan.config.item.Reward;

/**
 * 代码自动生成，请勿手动修改
 */
public enum RewardConstant {

    constant1,

    constant2;

    public List<Reward> value() {
        return ConstantConfig.getByKey(name()).getRewardList();
    }

}
