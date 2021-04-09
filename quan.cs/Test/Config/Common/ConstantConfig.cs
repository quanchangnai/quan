using Newtonsoft.Json.Linq;
using Quan.Utils;
using Quan.Config;
using System.Collections.Generic;
using System.Collections.Immutable;
using Test.Config.Item;

namespace Test.Config.Common
{
    /// <summary>
	/// 常量<br/>
	/// 代码自动生成，请勿手动修改
	/// </summary>
    public class ConstantConfig : ConfigBase
    {
        /// <summary>
        /// 常量Key
        /// </summary>
        public readonly string key;

        /// <summary>
        /// 道具ID
        /// </summary>
        public readonly int itemId;

        public ItemConfig itemId_Ref => ItemConfig.GetById(itemId);

        /// <summary>
        /// 奖励
        /// </summary>
        public readonly Reward reward;

        /// <summary>
        /// 奖励List
        /// </summary>
        public readonly IList<Test.Config.Item.Reward> rewardList;

        /// <summary>
        /// 备注
        /// </summary>
        public readonly string comment;


        public ConstantConfig(JObject json) : base(json)
        {
            key = json["key"]?.Value<string>() ?? "";
            itemId = json["itemId"]?.Value<int>() ?? default;
            reward = json.ContainsKey("reward") ? Reward.Create(json["reward"].Value<JObject>()) : null;

            var rewardList1 = json["rewardList"]?.Value<JArray>();
            var rewardList2 = ImmutableList<Test.Config.Item.Reward>.Empty;
            if (rewardList1 != null)
            {
                foreach (var rewardListValue in rewardList1)
                {
                    rewardList2 =rewardList2.Add(Test.Config.Item.Reward.Create(rewardListValue.Value<JObject>()));
                }
            }
            rewardList = rewardList2;

            comment = json["comment"]?.Value<string>() ?? "";
        }

        protected override ConfigBase Create(JObject json)
        {
            return new ConstantConfig(json);
        }

        public override string ToString()
        {
            return "ConstantConfig{" +
                   "key='" + key + '\'' +
                   ",itemId=" + itemId.ToString2() +
                   ",reward=" + reward.ToString2() +
                   ",rewardList=" + rewardList.ToString2() +
                   ",comment='" + comment + '\'' +
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
                Load(keyConfigs, config, config.key);
            }

            configs = configs.ToImmutableList();
            keyConfigs = ToImmutableDictionary(keyConfigs);

            _configs = configs;
            _keyConfigs = keyConfigs;
        }
    }
}