using Newtonsoft.Json.Linq;
using Quan.Utils;
using Quan.Config;
using System;
using System.Collections.Generic;
using System.Collections.Immutable;

namespace Test.Config
{
    /// <summary>
	/// 卡片<br/>
	/// 代码自动生成，请勿手动修改
	/// </summary>
    public class CardConfig : ConfigBase
    {
        /// <summary>
        /// ID
        /// </summary>
        public readonly int id;

        /// <summary>
        /// 常量Key
        /// </summary>
        public readonly string key;

        /// <summary>
        /// 名字
        /// </summary>
        public readonly string name;

        /// <summary>
        /// 类型
        /// </summary>
        public readonly int type;

        /// <summary>
        /// List
        /// </summary>
        public readonly IList<int> list;

        /// <summary>
        /// Set
        /// </summary>
        public readonly ISet<int> set;

        /// <summary>
        /// Map
        /// </summary>
        public readonly IDictionary<int, int> map;

        /// <summary>
        /// 生效时间
        /// </summary>
        public readonly DateTime effectiveTime;

        public readonly string effectiveTime_;


        public CardConfig(JObject json) : base(json)
        {
            id = json["id"]?.Value<int>() ?? default;
            key = json["key"]?.Value<string>() ?? "";
            name = json["name"]?.Value<string>() ?? "";
            type = json["type"]?.Value<int>() ?? default;

            var list1 = json["list"]?.Value<JArray>();
            var list2 = ImmutableList<int>.Empty;
            if (list1 != null)
            {
                foreach (var listValue in list1)
                {
                    list2 =list2.Add(listValue.Value<int>());
                }
            }
            list = list2;

            var set1 = json["set"]?.Value<JArray>();
            var set2 = ImmutableHashSet<int>.Empty;
            if (set1 != null)
            {
                foreach (var setValue in set1)
                {
                    set2 =set2.Add(setValue.Value<int>());
                }
            }
            set = set2;

            var map1 = json["map"]?.Value<JObject>();
            var map2 = ImmutableDictionary<int, int>.Empty;
            if (map1 != null)
            {
                foreach (var mapKeyValue in map1)
                {
                    map2 = map2.Add(int.Parse(mapKeyValue.Key), mapKeyValue.Value.Value<int>());
                }
            }
            map = map2;

            effectiveTime = ToDateTime(json["effectiveTime"]?.Value<long>() ?? default);
            effectiveTime_ = json["effectiveTime_"]?.Value<string>() ?? "";
        }

        protected override ConfigBase Create(JObject json)
        {
            return new CardConfig(json);
        }

        public override string ToString()
        {
            return "CardConfig{" +
                   "id=" + id.ToString2() +
                   ",key='" + key + '\'' +
                   ",name='" + name + '\'' +
                   ",type=" + type.ToString2() +
                   ",list=" + list.ToString2() +
                   ",set=" + set.ToString2() +
                   ",map=" + map.ToString2() +
                   ",effectiveTime='" + effectiveTime_ + '\'' +
                   '}';
        }

        // 所有CardConfig
        private static volatile IList<CardConfig> _configs = new List<CardConfig>();

        // 索引:ID
        private static volatile IDictionary<int, CardConfig> _idConfigs = new Dictionary<int, CardConfig>();

        // 索引:类型
        private static volatile IDictionary<int, IList<CardConfig>> _typeConfigs = new Dictionary<int, IList<CardConfig>>();

        public static IList<CardConfig> GetAll()
        {
            return _configs;
        }

        public static IDictionary<int, CardConfig> GetIdAll()
        {
            return _idConfigs;
        }

        public static CardConfig Get(int id)
        {
            _idConfigs.TryGetValue(id, out var result);
            return result;
        }

        public static IDictionary<int, IList<CardConfig>> GetTypeAll()
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
                Load(idConfigs, config, config.id);
                Load(typeConfigs, config, config.type);
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