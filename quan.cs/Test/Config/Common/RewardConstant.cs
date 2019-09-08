using System.Collections.Generic;
using Test.Config.Item;

namespace Test.Config.Common
{
    /// <summary>
    /// 自动生成
    /// </summary>
    public class RewardConstant 
    {
        public static IList<Reward> Constant1 => ConstantConfig.GetByKey("constant1").RewardList;

        public static IList<Reward> Constant2 => ConstantConfig.GetByKey("constant2").RewardList;

        public static IList<Reward> Constant3 => ConstantConfig.GetByKey("constant3").RewardList;
    }
}
