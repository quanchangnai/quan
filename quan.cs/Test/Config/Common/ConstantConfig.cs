using System.Collections.Generic;
using System.Collections.Immutable;
using Newtonsoft.Json.Linq;
using Quan.Common.Utils;
using Quan.Config;

namespace Test.Config.Common
{
    /// <summary>
	/// 常量<br/>
	/// 自动生成
	/// </summary>
    public class ConstantConfig : ConfigBase
    {
        /// <summary>
        /// 常量Key
        /// </summary>
        public readonly string Key;

        /// <summary>
        /// 道具ID
        /// </summary>
        public readonly int ItemId;

        /// <summary>
        /// 奖励
        /// </summary>
        public readonly Reward Reward;

        /// <summary>
        /// 奖励List
        /// </summary>
        public readonly IList<Test.Config.Item.Reward> RewardList;

        /// <summary>
        /// 备注
        /// </summary>
        public readonly string Comment;


        public ConstantConfig(JObject json) : base(json)
        {
            Key = json["key"]?.Value<string>() ?? "";
            ItemId = json["itemId"]?.Value<int>() ?? default;
            Reward = json.ContainsKey("reward") ? Reward.Create(json["reward"].Value<JObject>()) : null;

            var rewardList1 = json["rewardList"]?.Value<JArray>();
            var rewardList2 = ImmutableList<Test.Config.Item.Reward>.Empty;
            if (rewardList1 != null)
            {
                foreach (var rewardListValue in rewardList1)
                {
                    rewardList2 =rewardList2.Add(Test.Config.Item.Reward.Create(rewardListValue.Value<JObject>()));
                }
            }
            RewardList = rewardList2;

            Comment = json["comment"]?.Value<string>() ?? "";
        }

        protected override ConfigBase Create(JObject json)
        {
            return new ConstantConfig(json);
        }

        public override string ToString()
        {
            return "ConstantConfig{" +
                   "Key='" + Key + '\'' +
                   ",ItemId=" + ItemId.ToString2() +
                   ",Reward=" + Reward.ToString2() +
                   ",RewardList=" + RewardList.ToString2() +
                   ",Comment='" + Comment + '\'' +
                   '}';
        }

        // 所有ConstantConfig
        private static volatile IList<ConstantConfig> _configs = new List<ConstantConfig>();

        // 索引:常量Key
        private static volatile IDictionary<string, ConstantConfig> _keyConfigs = new Dictionary<string, ConstantConfig>();

        public static IList<ConstantConfig> GetConfigs()
        {
            return _configs;
        }

        public static IDictionary<string, ConstantConfig> GetKeyConfigs()
        {
            return _keyConfigs;
        }

        public static ConstantConfig GetByKey(string key)
        {
            _keyConfigs.TryGetValue(key, out var result);
            return result;
        }


        public static void Load(IList<ConstantConfig> configs)
        {
            IDictionary<string, ConstantConfig> keyConfigs = new Dictionary<string, ConstantConfig>();

            foreach (var config in configs)
            {
                Load(keyConfigs, config, config.Key);
            }

            configs = configs.ToImmutableList();
            keyConfigs = ToImmutableDictionary(keyConfigs);

            _configs = configs;
            _keyConfigs = keyConfigs;
        }
    }
}