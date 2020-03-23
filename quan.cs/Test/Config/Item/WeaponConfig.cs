using System.Collections.Generic;
using System.Collections.Immutable;
using Newtonsoft.Json.Linq;
using Quan.Common;
using Quan.Config;

namespace Test.Config.Item
{
    /// <summary>
	/// 武器<br/>
	/// 自动生成
	/// </summary>
    public class WeaponConfig : EquipConfig
    {
        /// <summary>
        /// 字段1
        /// </summary>
        public readonly int W1;

        /// <summary>
        /// 字段2
        /// </summary>
        public readonly int W2;

        /// <summary>
        /// 奖励List
        /// </summary>
        public readonly IList<Reward> RewardList;

        /// <summary>
        /// 奖励Set
        /// </summary>
        public readonly ISet<Reward> RewardSet;

        /// <summary>
        /// 奖励Map
        /// </summary>
        public readonly IDictionary<int, Reward> RewardMap;

        /// <summary>
        /// List2
        /// </summary>
        public readonly IList<int> List2;


        public WeaponConfig(JObject json) : base(json)
        {
            W1 = json["w1"]?.Value<int>() ?? default;
            W2 = json["w2"]?.Value<int>() ?? default;

            var rewardList1 = json["rewardList"]?.Value<JArray>();
            var rewardList2 = ImmutableList<Reward>.Empty;
            if (rewardList1 != null)
            {
                foreach (var rewardListValue in rewardList1)
                {
                    rewardList2 =rewardList2.Add(Reward.Create(rewardListValue.Value<JObject>()));
                }
            }
            RewardList = rewardList2;

            var rewardSet1 = json["rewardSet"]?.Value<JArray>();
            var rewardSet2 = ImmutableHashSet<Reward>.Empty;
            if (rewardSet1 != null)
            {
                foreach (var rewardSetValue in rewardSet1)
                {
                    rewardSet2 =rewardSet2.Add(Reward.Create(rewardSetValue.Value<JObject>()));
                }
            }
            RewardSet = rewardSet2;

            var rewardMap1 = json["rewardMap"]?.Value<JObject>();
            var rewardMap2 = ImmutableDictionary<int, Reward>.Empty;
            if (rewardMap1 != null)
            {
                foreach (var rewardMapKeyValue in rewardMap1)
                {
                    rewardMap2 = rewardMap2.Add(int.Parse(rewardMapKeyValue.Key), Reward.Create(rewardMapKeyValue.Value.Value<JObject>()));
                }
            }
            RewardMap = rewardMap2;

            var list21 = json["list2"]?.Value<JArray>();
            var list22 = ImmutableList<int>.Empty;
            if (list21 != null)
            {
                foreach (var list2Value in list21)
                {
                    list22 =list22.Add(list2Value.Value<int>());
                }
            }
            List2 = list22;
        }

        protected override ConfigBase Create(JObject json)
        {
            return new WeaponConfig(json);
        }

        public override string ToString()
        {
            return "WeaponConfig{" +
                   "Id=" + Id.ToString2() +
                   ",Key='" + Key + '\'' +
                   ",Name='" + Name + '\'' +
                   ",Type=" + Type.ToString2() +
                   ",UseEffect=" + UseEffect.ToString2() +
                   ",Reward=" + Reward.ToString2() +
                   ",List=" + List.ToString2() +
                   ",Set=" + Set.ToString2() +
                   ",Map=" + Map.ToString2() +
                   ",EffectiveTime='" + EffectiveTime_ + '\'' +
                   ",Position=" + Position.ToString2() +
                   ",Color=" + Color.ToString2() +
                   ",W1=" + W1.ToString2() +
                   ",W2=" + W2.ToString2() +
                   ",RewardList=" + RewardList.ToString2() +
                   ",RewardSet=" + RewardSet.ToString2() +
                   ",RewardMap=" + RewardMap.ToString2() +
                   ",List2=" + List2.ToString2() +
                   '}';
        }

        // 所有WeaponConfig
        private static volatile IList<WeaponConfig> _configs = new List<WeaponConfig>();

        // 索引:ID
        private static volatile IDictionary<int, WeaponConfig> _idConfigs = new Dictionary<int, WeaponConfig>();

        // 索引:常量Key
        private static volatile IDictionary<string, WeaponConfig> _keyConfigs = new Dictionary<string, WeaponConfig>();

        // 索引:部位
        private static volatile IDictionary<int, IList<WeaponConfig>> _positionConfigs = new Dictionary<int, IList<WeaponConfig>>();

        private static volatile IDictionary<int, IDictionary<int, IList<WeaponConfig>>> _composite1Configs = new Dictionary<int, IDictionary<int, IList<WeaponConfig>>>();

        private static volatile IDictionary<int, IDictionary<int, WeaponConfig>> _composite2Configs = new Dictionary<int, IDictionary<int, WeaponConfig>>();

        public new static IList<WeaponConfig> GetConfigs()
        {
            return _configs;
        }

        public new static IDictionary<int, WeaponConfig> GetIdConfigs()
        {
            return _idConfigs;
        }

        public new static WeaponConfig GetById(int id)
        {
            _idConfigs.TryGetValue(id, out var result);
            return result;
        }

        public new static IDictionary<string, WeaponConfig> GetKeyConfigs()
        {
            return _keyConfigs;
        }

        public new static WeaponConfig GetByKey(string key)
        {
            _keyConfigs.TryGetValue(key, out var result);
            return result;
        }

        public new static IDictionary<int, IList<WeaponConfig>> GetPositionConfigs()
        {
            return _positionConfigs;
        }

        public new static IList<WeaponConfig> GetByPosition(int position)
        {
            _positionConfigs.TryGetValue(position, out var result);
            return result ?? ImmutableList<WeaponConfig>.Empty;
        }

        public static IDictionary<int, IDictionary<int, IList<WeaponConfig>>> GetComposite1Configs()
        {
            return _composite1Configs;
        }

        public static IDictionary<int, IList<WeaponConfig>> GetByComposite1(int color)
        {
            _composite1Configs.TryGetValue(color, out var result);
            return result ?? ImmutableDictionary<int, IList<WeaponConfig>>.Empty;
        }

        public static IList<WeaponConfig> GetByComposite1(int color, int w1)
        {
            GetByComposite1(color).TryGetValue(w1, out var result);
            return result ?? ImmutableList<WeaponConfig>.Empty;
        }

        public static IDictionary<int, IDictionary<int, WeaponConfig>> GetComposite2Configs()
        {
            return _composite2Configs;
        }

        public static IDictionary<int, WeaponConfig> GetByComposite2(int w1)
        {
            _composite2Configs.TryGetValue(w1, out var result);
            return result ?? ImmutableDictionary<int, WeaponConfig>.Empty;
        }

        public static WeaponConfig GetByComposite2(int w1, int w2)
        {
            GetByComposite2(w1).TryGetValue(w2, out var result);
            return result;
        }


        public static void Load(IList<WeaponConfig> configs)
        {
            IDictionary<int, WeaponConfig> idConfigs = new Dictionary<int, WeaponConfig>();
            IDictionary<string, WeaponConfig> keyConfigs = new Dictionary<string, WeaponConfig>();
            IDictionary<int, IList<WeaponConfig>> positionConfigs = new Dictionary<int, IList<WeaponConfig>>();
            IDictionary<int, IDictionary<int, IList<WeaponConfig>>> composite1Configs = new Dictionary<int, IDictionary<int, IList<WeaponConfig>>>();
            IDictionary<int, IDictionary<int, WeaponConfig>> composite2Configs = new Dictionary<int, IDictionary<int, WeaponConfig>>();

            foreach (var config in configs)
            {
                ConfigBase.Load(idConfigs, config, config.Id);
                ConfigBase.Load(keyConfigs, config, config.Key);
                ConfigBase.Load(positionConfigs, config, config.Position);
                ConfigBase.Load(composite1Configs, config, config.Color, config.W1);
                ConfigBase.Load(composite2Configs, config, config.W1, config.W2);
            }

            configs = configs.ToImmutableList();
            idConfigs = ToImmutableDictionary(idConfigs);
            keyConfigs = ToImmutableDictionary(keyConfigs);
            positionConfigs = ToImmutableDictionary(positionConfigs);
            composite1Configs = ToImmutableDictionary(composite1Configs);
            composite2Configs = ToImmutableDictionary(composite2Configs);

            _configs = configs;
            _idConfigs = idConfigs;
            _keyConfigs = keyConfigs;
            _positionConfigs = positionConfigs;
            _composite1Configs = composite1Configs;
            _composite2Configs = composite2Configs;
        }
    }
}