using System.Collections.Generic;
using System.Collections.Immutable;
using Newtonsoft.Json.Linq;
using Quan.Common.Utils;
using Quan.Config;
using System;

namespace Test.Config
{
    /// <summary>
	/// CardConfig<br/>
	/// 代码自动生成，请勿手动修改
	/// </summary>
    public class CardConfig : ConfigBase
    {
        /// <summary>
        /// ID
        /// </summary>
        public readonly int Id;

        /// <summary>
        /// 常量Key
        /// </summary>
        public readonly string Key;

        /// <summary>
        /// 名字
        /// </summary>
        public readonly string Name;

        /// <summary>
        /// 类型
        /// </summary>
        public readonly int Type;

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

        public readonly string EffectiveTime_;


        public CardConfig(JObject json) : base(json)
        {
            Id = json["id"]?.Value<int>() ?? default;
            Key = json["key"]?.Value<string>() ?? "";
            Name = json["name"]?.Value<string>() ?? "";
            Type = json["type"]?.Value<int>() ?? default;

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
            EffectiveTime_ = json["effectiveTime_"]?.Value<string>() ?? "";
        }

        protected override ConfigBase Create(JObject json)
        {
            return new CardConfig(json);
        }

        public override string ToString()
        {
            return "CardConfig{" +
                   "Id=" + Id.ToString2() +
                   ",Key='" + Key + '\'' +
                   ",Name='" + Name + '\'' +
                   ",Type=" + Type.ToString2() +
                   ",List=" + List.ToString2() +
                   ",Set=" + Set.ToString2() +
                   ",Map=" + Map.ToString2() +
                   ",EffectiveTime='" + EffectiveTime_ + '\'' +
                   '}';
        }

        // 所有CardConfig
        private static volatile IList<CardConfig> _configs = new List<CardConfig>();

        // 索引:ID
        private static volatile IDictionary<int, CardConfig> _idConfigs = new Dictionary<int, CardConfig>();

        // 索引:类型
        private static volatile IDictionary<int, IList<CardConfig>> _typeConfigs = new Dictionary<int, IList<CardConfig>>();

        public static IList<CardConfig> GetConfigs()
        {
            return _configs;
        }

        public static IDictionary<int, CardConfig> GetIdConfigs()
        {
            return _idConfigs;
        }

        public static CardConfig GetById(int id)
        {
            _idConfigs.TryGetValue(id, out var result);
            return result;
        }

        public static IDictionary<int, IList<CardConfig>> GetTypeConfigs()
        {
            return _typeConfigs;
        }

        public static IList<CardConfig> GetByType(int type)
        {
            _typeConfigs.TryGetValue(type, out var result);
            return result ?? ImmutableList<CardConfig>.Empty;
        }


        public static void Load(IList<CardConfig> configs)
        {
            IDictionary<int, CardConfig> idConfigs = new Dictionary<int, CardConfig>();
            IDictionary<int, IList<CardConfig>> typeConfigs = new Dictionary<int, IList<CardConfig>>();

            foreach (var config in configs)
            {
                Load(idConfigs, config, config.Id);
                Load(typeConfigs, config, config.Type);
            }

            configs = configs.ToImmutableList();
            idConfigs = ToImmutableDictionary(idConfigs);
            typeConfigs = ToImmutableDictionary(typeConfigs);

            _configs = configs;
            _idConfigs = idConfigs;
            _typeConfigs = typeConfigs;
        }
    }
}