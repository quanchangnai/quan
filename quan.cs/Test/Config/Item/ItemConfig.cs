using System.Collections.Generic;
using System.Collections.Immutable;
using Newtonsoft.Json.Linq;
using Quan.Common;
using Quan.Config;
using System;

namespace Test.Config.Item
{
    /// <summary>
	/// 道具<br/>
	/// 自动生成
	/// </summary>
    public class ItemConfig : ConfigBase
    {
        /// <summary>
        /// ID
        /// </summary>
        public readonly int Id;

        /// <summary>
        /// 名字
        /// </summary>
        public readonly string Name;

        /// <summary>
        /// 类型
        /// </summary>
        public readonly ItemType Type;

        /// <summary>
        /// 奖励
        /// </summary>
        public readonly Reward Reward;

        /// <summary>
        /// List
        /// </summary>
        public readonly IList<int> List;

        /// <summary>
        /// Set
        /// </summary>
        public readonly ISet<int> Set;

        /// <summary>
        /// Map
        /// </summary>
        public readonly IDictionary<int, int> Map;

        /// <summary>
        /// 生效时间
        /// </summary>
        public readonly DateTime EffectiveTime;

        public readonly string EffectiveTime_Str;


        public ItemConfig(JObject json) : base(json)
        {
            Id = json["id"]?.Value<int>() ?? default;
            Name = json["name"]?.Value<string>() ?? "";
            Type = (ItemType) (json["type"]?.Value<int>() ?? default);
            Reward = json.ContainsKey("reward") ? new Reward(json["reward"].Value<JObject>()) : null;

            var list1 = json["list"]?.Value<JArray>();
            var list2 = ImmutableList<int>.Empty;
            if (list1 != null)
            {
                foreach (var listValue in list1)
                {
                    list2 =list2.Add(listValue.Value<int>());
                }
            }
            List = list2;

            var set1 = json["set"]?.Value<JArray>();
            var set2 = ImmutableHashSet<int>.Empty;
            if (set1 != null)
            {
                foreach (var setValue in set1)
                {
                    set2 =set2.Add(setValue.Value<int>());
                }
            }
            Set = set2;

            var map1 = json["map"]?.Value<JObject>();
            var map2 = ImmutableDictionary<int, int>.Empty;
            if (map1 != null)
            {
                foreach (var mapKeyValue in map1)
                {
                    map2 = map2.Add(int.Parse(mapKeyValue.Key), mapKeyValue.Value.Value<int>());
                }
            }
            Map = map2;

            EffectiveTime = ToDateTime(json["effectiveTime"]?.Value<long>() ?? default);
            EffectiveTime_Str = json["effectiveTime$Str"]?.Value<string>() ?? "";
        }

        protected override ConfigBase Create(JObject json)
        {
            return new ItemConfig(json);
        }


        public override string ToString()
        {
            return "ItemConfig{" +
                   "Id=" + Id.ToString2() +
                   ",Name='" + Name + '\'' +
                   ",Type=" + Type.ToString2() +
                   ",Reward=" + Reward.ToString2() +
                   ",List=" + List.ToString2() +
                   ",Set=" + Set.ToString2() +
                   ",Map=" + Map.ToString2() +
                   ",EffectiveTime=" + EffectiveTime.ToString2() +
                   '}';
        }


        // 所有ItemConfig
        private static volatile IList<ItemConfig> _configs = new List<ItemConfig>();

        // ID
        private static volatile IDictionary<int, ItemConfig> _idConfigs = new Dictionary<int, ItemConfig>();

        public static IList<ItemConfig> GetConfigs()
        {
            return _configs;
        }

        public static IDictionary<int, ItemConfig> GetIdConfigs()
        {
            return _idConfigs;
        }

        public static ItemConfig GetById(int id)
        {
            _idConfigs.TryGetValue(id, out var result);
            return result;
        }


        public static void Index(IList<ItemConfig> configs)
        {
            IDictionary<int, ItemConfig> idConfigs = new Dictionary<int, ItemConfig>();

            foreach (var config in configs)
            {
                Index(idConfigs, config, config.Id);
            }

            configs = configs.ToImmutableList();
            idConfigs = ToImmutableDictionary(idConfigs);

            _configs = configs;
            _idConfigs = idConfigs;
        }
    }
}