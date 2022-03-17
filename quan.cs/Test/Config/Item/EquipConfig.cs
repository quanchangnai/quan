using Newtonsoft.Json.Linq;
using Quan.Utils;
using Quan.Config;
using System.Collections.Generic;
using System.Collections.Immutable;

namespace Test.Config.Item
{
    /// <summary>
	/// 装备1,装备2<br/>
	/// 代码自动生成，请勿手动修改
	/// </summary>
    public class EquipConfig : ItemConfig
    {
        /// <summary>
        /// 部位
        /// </summary>
        public readonly int position;

        /// <summary>
        /// 颜色
        /// </summary>
        public readonly int color;


        public EquipConfig(JObject json) : base(json)
        {
            position = json["position"]?.Value<int>() ?? default;
            color = json["color"]?.Value<int>() ?? default;
        }

        protected override ConfigBase Create(JObject json)
        {
            return new EquipConfig(json);
        }

        public override string ToString()
        {
            return "EquipConfig{" +
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
                   '}';
        }

        // 所有EquipConfig
        private static volatile IList<EquipConfig> _configs = new List<EquipConfig>();

        // 索引:部位
        private static volatile IDictionary<int, IList<EquipConfig>> _positionConfigs = new Dictionary<int, IList<EquipConfig>>();

        // 索引:ID
        private static volatile IDictionary<int, EquipConfig> _idConfigs = new Dictionary<int, EquipConfig>();

        // 索引:常量Key
        private static volatile IDictionary<string, EquipConfig> _keyConfigs = new Dictionary<string, EquipConfig>();

        // 索引:类型
        private static volatile IDictionary<ItemType, IList<EquipConfig>> _typeConfigs = new Dictionary<ItemType, IList<EquipConfig>>();

        public new static IList<EquipConfig> GetAll()
        {
            return _configs;
        }

        public static IDictionary<int, IList<EquipConfig>> GetPositionAll()
        {
            return _positionConfigs;
        }

        public static IList<EquipConfig> GetByPosition(int position)
        {
            _positionConfigs.TryGetValue(position, out var result);
            return result ?? ImmutableList<EquipConfig>.Empty;
        }

        public new static IDictionary<int, EquipConfig> GetIdAll()
        {
            return _idConfigs;
        }

        public new static EquipConfig Get(int id)
        {
            _idConfigs.TryGetValue(id, out var result);
            return result;
        }

        public new static IDictionary<string, EquipConfig> GetKeyAll()
        {
            return _keyConfigs;
        }

        public new static EquipConfig GetByKey(string key)
        {
            _keyConfigs.TryGetValue(key, out var result);
            return result;
        }

        public new static IDictionary<ItemType, IList<EquipConfig>> GetTypeAll()
        {
            return _typeConfigs;
        }

        public new static IList<EquipConfig> GetByType(ItemType type)
        {
            _typeConfigs.TryGetValue(type, out var result);
            return result ?? ImmutableList<EquipConfig>.Empty;
        }


        public static void Load(IList<EquipConfig> configs)
        {
            IDictionary<int, IList<EquipConfig>> positionConfigs = new Dictionary<int, IList<EquipConfig>>();
            IDictionary<int, EquipConfig> idConfigs = new Dictionary<int, EquipConfig>();
            IDictionary<string, EquipConfig> keyConfigs = new Dictionary<string, EquipConfig>();
            IDictionary<ItemType, IList<EquipConfig>> typeConfigs = new Dictionary<ItemType, IList<EquipConfig>>();

            foreach (var config in configs)
            {
                ConfigBase.Load(positionConfigs, config, config.position);
                ConfigBase.Load(idConfigs, config, config.id);
                ConfigBase.Load(keyConfigs, config, config.key);
                ConfigBase.Load(typeConfigs, config, config.type);
            }

            configs = configs.ToImmutableList();
            positionConfigs = ToImmutableDictionary(positionConfigs);
            idConfigs = ToImmutableDictionary(idConfigs);
            keyConfigs = ToImmutableDictionary(keyConfigs);
            typeConfigs = ToImmutableDictionary(typeConfigs);

            _configs = configs;
            _positionConfigs = positionConfigs;
            _idConfigs = idConfigs;
            _keyConfigs = keyConfigs;
            _typeConfigs = typeConfigs;
        }
    }
}