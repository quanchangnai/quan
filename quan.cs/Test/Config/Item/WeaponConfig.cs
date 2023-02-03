using Newtonsoft.Json.Linq;
using Quan.Config;
using Quan.Utils;
using System.Collections.Generic;
using System.Collections.Immutable;

namespace Test.Config.Item
{
    /// <summary>
	/// 道具/武器<br/>
	/// 代码自动生成，请勿手动修改
	/// </summary>
    public class WeaponConfig : EquipConfig
    {
        /// <summary>
        /// 字段1
        /// </summary>
        public readonly int w1;

        /// <summary>
        /// 字段2
        /// </summary>
        public readonly int w2;

        /// <summary>
        /// 奖励List
        /// </summary>
        public readonly IList<Reward> rewardList;

        /// <summary>
        /// 奖励Set
        /// </summary>
        public readonly ISet<Reward> rewardSet;

        /// <summary>
        /// 奖励Map
        /// </summary>
        public readonly IDictionary<int, Reward> rewardMap;

        /// <summary>
        /// List2
        /// </summary>
        public readonly IList<int> list2;


        public WeaponConfig(JObject json) : base(json)
        {
            w1 = json["w1"]?.Value<int>() ?? default;
            w2 = json["w2"]?.Value<int>() ?? default;

            var rewardList1 = json["rewardList"]?.Value<JArray>();
            var rewardList2 = ImmutableList<Reward>.Empty;
            if (rewardList1 != null)
            {
                foreach (var rewardListValue in rewardList1)
                {
                    rewardList2 =rewardList2.Add(Reward.Create(rewardListValue.Value<JObject>()));
                }
            }
            rewardList = rewardList2;

            var rewardSet1 = json["rewardSet"]?.Value<JArray>();
            var rewardSet2 = ImmutableHashSet<Reward>.Empty;
            if (rewardSet1 != null)
            {
                foreach (var rewardSetValue in rewardSet1)
                {
                    rewardSet2 =rewardSet2.Add(Reward.Create(rewardSetValue.Value<JObject>()));
                }
            }
            rewardSet = rewardSet2;

            var rewardMap1 = json["rewardMap"]?.Value<JObject>();
            var rewardMap2 = ImmutableDictionary<int, Reward>.Empty;
            if (rewardMap1 != null)
            {
                foreach (var rewardMapKeyValue in rewardMap1)
                {
                    rewardMap2 = rewardMap2.Add(int.Parse(rewardMapKeyValue.Key), Reward.Create(rewardMapKeyValue.Value.Value<JObject>()));
                }
            }
            rewardMap = rewardMap2;

            var list21 = json["list2"]?.Value<JArray>();
            var list22 = ImmutableList<int>.Empty;
            if (list21 != null)
            {
                foreach (var list2Value in list21)
                {
                    list22 =list22.Add(list2Value.Value<int>());
                }
            }
            list2 = list22;
        }

        protected override ConfigBase Create(JObject json)
        {
            return new WeaponConfig(json);
        }

        public override string ToString()
        {
            return "WeaponConfig{" +
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
                   ",position=" + position.ToString2() +
                   ",color=" + color.ToString2() +
                   ",w1=" + w1.ToString2() +
                   ",w2=" + w2.ToString2() +
                   ",rewardList=" + rewardList.ToString2() +
                   ",rewardSet=" + rewardSet.ToString2() +
                   ",rewardMap=" + rewardMap.ToString2() +
                   ",list2=" + list2.ToString2() +
                   '}';
        }

        // 所有WeaponConfig
        private static volatile IList<WeaponConfig> _configs = new List<WeaponConfig>();

        // 索引:ID
        private static volatile IDictionary<int, WeaponConfig> _idConfigs = new Dictionary<int, WeaponConfig>();

        // 索引:常量Key
        private static volatile IDictionary<string, WeaponConfig> _keyConfigs = new Dictionary<string, WeaponConfig>();

        // 索引:类型
        private static volatile IDictionary<ItemType, IList<WeaponConfig>> _typeConfigs = new Dictionary<ItemType, IList<WeaponConfig>>();

        // 索引:部位
        private static volatile IDictionary<int, IList<WeaponConfig>> _positionConfigs = new Dictionary<int, IList<WeaponConfig>>();

        private static volatile IDictionary<int, IDictionary<int, IList<WeaponConfig>>> _composite1Configs = new Dictionary<int, IDictionary<int, IList<WeaponConfig>>>();

        private static volatile IDictionary<int, IDictionary<int, WeaponConfig>> _composite2Configs = new Dictionary<int, IDictionary<int, WeaponConfig>>();

        public new static IList<WeaponConfig> GetAll()
        {
            return _configs;
        }

        public new static IDictionary<int, WeaponConfig> GetIdAll()
        {
            return _idConfigs;
        }

        public new static WeaponConfig Get(int id)
        {
            _idConfigs.TryGetValue(id, out var result);
            return result;
        }

        public new static IDictionary<string, WeaponConfig> GetKeyAll()
        {
            return _keyConfigs;
        }

        public new static WeaponConfig GetByKey(string key)
        {
            _keyConfigs.TryGetValue(key, out var result);
            return result;
        }

        public new static IDictionary<ItemType, IList<WeaponConfig>> GetTypeAll()
        {
            return _typeConfigs;
        }

        public new static IList<WeaponConfig> GetByType(ItemType type)
        {
            _typeConfigs.TryGetValue(type, out var result);
            return result ?? ImmutableList<WeaponConfig>.Empty;
        }

        public new static IDictionary<int, IList<WeaponConfig>> GetPositionAll()
        {
            return _positionConfigs;
        }

        public new static IList<WeaponConfig> GetByPosition(int position)
        {
            _positionConfigs.TryGetValue(position, out var result);
            return result ?? ImmutableList<WeaponConfig>.Empty;
        }

        public static IDictionary<int, IDictionary<int, IList<WeaponConfig>>> GetComposite1All()
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

        public static IDictionary<int, IDictionary<int, WeaponConfig>> GetComposite2All()
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
            IDictionary<ItemType, IList<WeaponConfig>> typeConfigs = new Dictionary<ItemType, IList<WeaponConfig>>();
            IDictionary<int, IList<WeaponConfig>> positionConfigs = new Dictionary<int, IList<WeaponConfig>>();
            IDictionary<int, IDictionary<int, IList<WeaponConfig>>> composite1Configs = new Dictionary<int, IDictionary<int, IList<WeaponConfig>>>();
            IDictionary<int, IDictionary<int, WeaponConfig>> composite2Configs = new Dictionary<int, IDictionary<int, WeaponConfig>>();

            foreach (var config in configs)
            {
                ConfigBase.Load(idConfigs, config, config.id);
                ConfigBase.Load(keyConfigs, config, config.key);
                ConfigBase.Load(typeConfigs, config, config.type);
                ConfigBase.Load(positionConfigs, config, config.position);
                ConfigBase.Load(composite1Configs, config, config.color, config.w1);
                ConfigBase.Load(composite2Configs, config, config.w1, config.w2);
            }

            configs = configs.ToImmutableList();
            idConfigs = ToImmutableDictionary(idConfigs);
            keyConfigs = ToImmutableDictionary(keyConfigs);
            typeConfigs = ToImmutableDictionary(typeConfigs);
            positionConfigs = ToImmutableDictionary(positionConfigs);
            composite1Configs = ToImmutableDictionary(composite1Configs);
            composite2Configs = ToImmutableDictionary(composite2Configs);

            _configs = configs;
            _idConfigs = idConfigs;
            _keyConfigs = keyConfigs;
            _typeConfigs = typeConfigs;
            _positionConfigs = positionConfigs;
            _composite1Configs = composite1Configs;
            _composite2Configs = composite2Configs;
        }
    }
}