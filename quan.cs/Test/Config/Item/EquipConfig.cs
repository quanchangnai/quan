using System.Collections.Generic;
using System.Collections.Immutable;
using Newtonsoft.Json.Linq;
using Quan.Common.Utils;
using Quan.Config;

namespace Test.Config.Item
{
    /// <summary>
	/// 装备1,装备2<br/>
	/// 自动生成
	/// </summary>
    public class EquipConfig : ItemConfig
    {
        /// <summary>
        /// 部位
        /// </summary>
        public readonly int Position;

        /// <summary>
        /// 颜色
        /// </summary>
        public readonly int Color;


        public EquipConfig(JObject json) : base(json)
        {
            Position = json["position"]?.Value<int>() ?? default;
            Color = json["color"]?.Value<int>() ?? default;
        }

        protected override ConfigBase Create(JObject json)
        {
            return new EquipConfig(json);
        }

        public override string ToString()
        {
            return "EquipConfig{" +
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
                   '}';
        }

        // 所有EquipConfig
        private static volatile IList<EquipConfig> _configs = new List<EquipConfig>();

        // 索引:ID
        private static volatile IDictionary<int, EquipConfig> _idConfigs = new Dictionary<int, EquipConfig>();

        // 索引:常量Key
        private static volatile IDictionary<string, EquipConfig> _keyConfigs = new Dictionary<string, EquipConfig>();

        // 索引:部位
        private static volatile IDictionary<int, IList<EquipConfig>> _positionConfigs = new Dictionary<int, IList<EquipConfig>>();

        public new static IList<EquipConfig> GetConfigs()
        {
            return _configs;
        }

        public new static IDictionary<int, EquipConfig> GetIdConfigs()
        {
            return _idConfigs;
        }

        public new static EquipConfig GetById(int id)
        {
            _idConfigs.TryGetValue(id, out var result);
            return result;
        }

        public new static IDictionary<string, EquipConfig> GetKeyConfigs()
        {
            return _keyConfigs;
        }

        public new static EquipConfig GetByKey(string key)
        {
            _keyConfigs.TryGetValue(key, out var result);
            return result;
        }

        public static IDictionary<int, IList<EquipConfig>> GetPositionConfigs()
        {
            return _positionConfigs;
        }

        public static IList<EquipConfig> GetByPosition(int position)
        {
            _positionConfigs.TryGetValue(position, out var result);
            return result ?? ImmutableList<EquipConfig>.Empty;
        }


        public static void Load(IList<EquipConfig> configs)
        {
            IDictionary<int, EquipConfig> idConfigs = new Dictionary<int, EquipConfig>();
            IDictionary<string, EquipConfig> keyConfigs = new Dictionary<string, EquipConfig>();
            IDictionary<int, IList<EquipConfig>> positionConfigs = new Dictionary<int, IList<EquipConfig>>();

            foreach (var config in configs)
            {
                ConfigBase.Load(idConfigs, config, config.Id);
                ConfigBase.Load(keyConfigs, config, config.Key);
                ConfigBase.Load(positionConfigs, config, config.Position);
            }

            configs = configs.ToImmutableList();
            idConfigs = ToImmutableDictionary(idConfigs);
            keyConfigs = ToImmutableDictionary(keyConfigs);
            positionConfigs = ToImmutableDictionary(positionConfigs);

            _configs = configs;
            _idConfigs = idConfigs;
            _keyConfigs = keyConfigs;
            _positionConfigs = positionConfigs;
        }
    }
}