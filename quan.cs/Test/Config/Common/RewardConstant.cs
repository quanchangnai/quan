using System.Collections.Generic;

namespace Test.Config.Common
{
    /// <summary>
    /// 代码自动生成，请勿手动修改
    /// </summary>
    public class RewardConstant
    {
        private readonly string _key;

        private RewardConstant(string key)
        {
            _key = key;
        }

        public IList<Test.Config.Item.Reward> Value => ConstantConfig.GetByKey(_key).rewardList;


        public static readonly RewardConstant constant1 = new RewardConstant("constant1");

        public static readonly RewardConstant constant2 = new RewardConstant("constant2");
    }
}