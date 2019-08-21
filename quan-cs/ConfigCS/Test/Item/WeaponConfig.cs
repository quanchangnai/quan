using System;
using System.Collections.Generic;
using System.Collections.Immutable;
using Newtonsoft.Json.Linq;
using ConfigCS;

namespace ConfigCS.Test.Item
{
    /// <summary>
	/// 武器<br/>
	/// Created by 自动生成
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


        public WeaponConfig(JObject json): base(json)
        {
            W1 = json["w1"]?.Value<int>()?? default;
            W2 = json["w2"]?.Value<int>()?? default;

            var rewardList1 = json["rewardList"]?.Value<JArray>();
            var rewardList2 = ImmutableList<Reward>.Empty;
            if (rewardList1 != null)
            {
                foreach (var rewardListValue in rewardList1)
                {
                    rewardList2.Add(new Reward(rewardListValue.Value<JObject>()));
                }
            }
            RewardList = rewardList2;

            var rewardSet1 = json["rewardSet"]?.Value<JArray>();
            var rewardSet2 = ImmutableHashSet<Reward>.Empty;
            if (rewardSet1 != null)
            {
                foreach (var rewardSetValue in rewardSet1)
                {
                    rewardSet2.Add(new Reward(rewardSetValue.Value<JObject>()));
                }
            }
            RewardSet = rewardSet2;

            var rewardMap1 = json["rewardMap"]?.Value<JObject>();
            var rewardMap2 = ImmutableDictionary<int, Reward>.Empty;
            if (rewardMap1 != null)
            {
                foreach (var rewardMapProp in rewardMap1.Properties())
                {
                    rewardMap2.Add(int.Parse(rewardMapProp.Name), new Reward(rewardMapProp.Value<JObject>()));
                }
            }
            RewardMap = rewardMap2;

            var list21 = json["list2"]?.Value<JArray>();
            var list22 = ImmutableList<int>.Empty;
            if (list21 != null)
            {
                foreach (var list2Value in list21)
                {
                    list22.Add(list2Value.Value<int>());
                }
            }
            List2 = list22;
        }

        protected override Config Create(JObject json) 
        {
            return new WeaponConfig(json);
        }


        public override string ToString()
        {
            return "WeaponConfig{" +
                   "Id=" + Id +
                   ",Name='" + Name + '\'' +
                   ",Type=" + Type +
                   ",Reward=" + Reward +
                   ",List=" + List +
                   ",Set=" + Set +
                   ",Map=" + Map +
                   ",EffectiveTime='" + EffectiveTime_Str + '\'' +
                   ",Position=" + Position +
                   ",Color=" + Color +
                   ",W1=" + W1 +
                   ",W2=" + W2 +
                   ",RewardList=" + RewardList +
                   ",RewardSet=" + RewardSet +
                   ",RewardMap=" + RewardMap +
                   ",List2=" + List2 +
                   '}';
        }


        public new static class self 
        {
            private static volatile IList<WeaponConfig> _configs = new List<WeaponConfig>();

            /// <summary>
            /// ID
            /// </summary>
            private static volatile IDictionary<int, WeaponConfig> _idConfigs = new Dictionary<int, WeaponConfig>();

            /// <summary>
            /// 部位
            /// </summary>
            private static volatile IDictionary<int, IList<WeaponConfig>> _positionConfigs = new Dictionary<int, IList<WeaponConfig>>();

            private static volatile IDictionary<int, IDictionary<int, IList<WeaponConfig>>> _composite1Configs = new Dictionary<int, IDictionary<int, IList<WeaponConfig>>>();

            private static volatile IDictionary<int, IDictionary<int, WeaponConfig>> _composite2Configs = new Dictionary<int, IDictionary<int, WeaponConfig>>();

            public static IList<WeaponConfig> GetConfigs() 
            {
                return _configs;
            }

            public static IDictionary<int, WeaponConfig> GetIdConfigs() 
            {
                return _idConfigs;
            }

            public static WeaponConfig GetById(int id)
            {
                return _idConfigs.ContainsKey(id) ? _idConfigs[id] : null;
            }

            public static IDictionary<int, IList<WeaponConfig>> GetPositionConfigs() 
            {
                return _positionConfigs;
            }

            public static IList<WeaponConfig> GetByPosition(int position)
            {
                return _positionConfigs.ContainsKey(position) ? _positionConfigs[position] : ImmutableList<WeaponConfig>.Empty;
            }

            public static IDictionary<int, IDictionary<int, IList<WeaponConfig>>> GetComposite1Configs()
            {
                return _composite1Configs;
            }

            public static IDictionary<int, IList<WeaponConfig>> GetByComposite1(int color)
            {
                return _composite1Configs.ContainsKey(color) ? _composite1Configs[color] : ImmutableDictionary<int, IList<WeaponConfig>>.Empty;
            }

            public static IList<WeaponConfig> GetByComposite1(int color, int w1)
            {
                var composite1Configs = GetByComposite1(color);
                return composite1Configs.ContainsKey(w1) ? composite1Configs[w1] : ImmutableList<WeaponConfig>.Empty;
            }

            public static IDictionary<int, IDictionary<int, WeaponConfig>> GetComposite2Configs() 
            {
                return _composite2Configs;
            }

            public static IDictionary<int, WeaponConfig> GetByComposite2(int w1)
            {
                return _composite2Configs.ContainsKey(w1) ? _composite2Configs[w1] : ImmutableDictionary<int, WeaponConfig>.Empty;
            }

            public static WeaponConfig GetByComposite2(int w1, int w2)
            {
                var composite2Configs = GetByComposite2(w1);
                return composite2Configs.ContainsKey(w1) ? composite2Configs[w2] : null;
            }


            public static void Index(List<WeaponConfig> configs)
            {
                var idConfigs = new Dictionary<int, WeaponConfig>();
                var positionConfigs = new Dictionary<int, IList<WeaponConfig>>();
                var composite1Configs = new Dictionary<int, IDictionary<int, IList<WeaponConfig>>>();
                var composite2Configs = new Dictionary<int, IDictionary<int, WeaponConfig>>();

                foreach (var config in configs)
                {
                    idConfigs[config.Id] = config;


                    if (!positionConfigs.ContainsKey(config.Position))
                    {
                        positionConfigs.Add(config.Position, new List<WeaponConfig>());
                    }

                    positionConfigs[config.Position].Add(config);


                    if (!composite1Configs.ContainsKey(config.Color))
                    {
                        composite1Configs.Add(config.Color, new Dictionary<int, IList<WeaponConfig>>());
                    }

                    if (!composite1Configs[config.Color].ContainsKey(config.W1))
                    {
                        composite1Configs[config.Color][config.W1] = new List<WeaponConfig>();
                    }

                    composite1Configs[config.Color][config.W1].Add(config);


                    if (!composite2Configs.ContainsKey(config.W1))
                    {
                        composite2Configs.Add(config.W1, new Dictionary<int, WeaponConfig>());
                    }

                    composite2Configs[config.W1][config.W2] = config;

                }

                _configs = configs.ToImmutableList();
                _idConfigs = idConfigs.ToImmutableDictionary();
                _positionConfigs = positionConfigs.ToImmutableDictionary();
                _composite1Configs = composite1Configs.ToImmutableDictionary();
                _composite2Configs = composite2Configs.ToImmutableDictionary();

            }
        }
    }
}