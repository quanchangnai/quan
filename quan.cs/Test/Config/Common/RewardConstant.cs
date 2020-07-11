using System.Collections.Generic;

namespace Test.Config.Common
{
    /// <summary>
    /// 自动生成
    /// </summary>
    public class RewardConstant
    {
        private readonly string _key;

        private RewardConstant(string key)
        {
            _key = key;
        }

        public IList<Test.Config.Item.Reward> Value => ConstantConfig.GetByKey(_key).RewardList;


        public static readonly RewardConstant Constant1 = new RewardConstant("constant1");

        public static readonly RewardConstant Constant2 = new RewardConstant("constant2");
    }
}