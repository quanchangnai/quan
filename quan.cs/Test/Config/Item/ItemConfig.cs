using Newtonsoft.Json.Linq;
using Quan.Utils;
using Quan.Config;
using System;
using System.Collections.Generic;
using System.Collections.Immutable;

namespace Test.Config.Item
{
    /// <summary>
	/// 道具<br/>
	/// 代码自动生成，请勿手动修改
	/// </summary>
    public class ItemConfig : ConfigBase
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
        public readonly ItemType type;

        /// <summary>
        /// 使用效果
        /// </summary>
        public readonly UseEffect useEffect;

        /// <summary>
        /// 奖励
        /// </summary>
        public readonly Reward reward;

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


        public ItemConfig(JObject json) : base(json)
        {
            id = json["id"]?.Value<int>() ?? default;
            key = json["key"]?.Value<string>() ?? "";
            name = json["name"]?.Value<string>() ?? "";
            type = (ItemType) (json["type"]?.Value<int>() ?? default);
            useEffect = json.ContainsKey("useEffect") ? UseEffect.Create(json["useEffect"].Value<JObject>()) : null;
            reward = json.ContainsKey("reward") ? Reward.Create(json["reward"].Value<JObject>()) : null;

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
            return new ItemConfig(json);
        }

        public override string ToString()
        {
            return "ItemConfig{" +
                   "id=" + id.ToString2() +
                   ",key='" + key + '\'' +
                   ",name='" + name + '\'' +
                   ",type=" + type.ToString2() +
                   ",useEffect=" + useEffect.ToString2() +
                   ",reward=" + reward.ToString2() +
                   ",list=" + list.ToString2() +
                   ",set=" + set.ToString2() +
                   ",map=" + map.ToString2() +
                   ",effectiveTime='" + effectiveTime_ + '\'' +
                   '}';
        }

        // 所有ItemConfig
        private static volatile IList<ItemConfig> _configs = new List<ItemConfig>();

        // 索引:ID
        private static volatile IDictionary<int, ItemConfig> _idConfigs = new Dictionary<int, ItemConfig>();

        // 索引:常量Key
        private static volatile IDictionary<string, ItemConfig> _keyConfigs = new Dictionary<string, ItemConfig>();

        // 索引:类型
        private static volatile IDictionary<ItemType, IList<ItemConfig>> _typeConfigs = new Dictionary<ItemType, IList<ItemConfig>>();

        public static IList<ItemConfig> GetAll()
        {
            return _configs;
        }

        public static IDictionary<int, ItemConfig> GetIdAll()
        {
            return _idConfigs;
        }

        public static ItemConfig Get(int id)
        {
            _idConfigs.TryGetValue(id, out var result);
            return result;
        }

        public static IDictionary<string, ItemConfig> GetKeyAll()
        {
            return _keyConfigs;
        }

        public static ItemConfig GetByKey(string key)
        {
            _keyConfigs.TryGetValue(key, out var result);
            return result;
        }

        public static IDictionary<ItemType, IList<ItemConfig>> GetTypeAll()
        {
            return _typeConfigs;
        }

        public static IList<ItemConfig> GetByType(ItemType type)
        {
            _typeConfigs.TryGetValue(type, out var result);
            return result ?? ImmutableList<ItemConfig>.Empty;
        }


        public static void Load(IList<ItemConfig> configs)
        {
            IDictionary<int, ItemConfig> idConfigs = new Dictionary<int, ItemConfig>();
            IDictionary<string, ItemConfig> keyConfigs = new Dictionary<string, ItemConfig>();
            IDictionary<ItemType, IList<ItemConfig>> typeConfigs = new Dictionary<ItemType, IList<ItemConfig>>();

            foreach (var config in configs)
            {
                Load(idConfigs, config, config.id);
                Load(keyConfigs, config, config.key);
                Load(typeConfigs, config, config.type);
            }

            configs = configs.ToImmutableList();
            idConfigs = ToImmutableDictionary(idConfigs);
            keyConfigs = ToImmutableDictionary(keyConfigs);
            typeConfigs = ToImmutableDictionary(typeConfigs);

            _configs = configs;
            _idConfigs = idConfigs;
            _keyConfigs = keyConfigs;
            _typeConfigs = typeConfigs;
        }
    }
}