using System;
using System.Collections.Generic;
using System.Collections.Immutable;
using Newtonsoft.Json.Linq;
using ConfigCS;

namespace ConfigCS.Test.Item
{
    /// <summary>
	/// 装备1,装备2<br/>
	/// Created by 自动生成
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


        public EquipConfig(JObject json): base(json)
        {
            Position = json["position"]?.Value<int>()?? default;
            Color = json["color"]?.Value<int>()?? default;
        }

        protected override Config Create(JObject json) 
        {
            return new EquipConfig(json);
        }


        public override string ToString()
        {
            return "EquipConfig{" +
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
                   '}';
        }


        public static class self 
        {
            private static volatile IList<EquipConfig> _configs = new List<EquipConfig>();

            /// <summary>
            /// ID
            /// </summary>
            private static volatile IDictionary<int, EquipConfig> _idConfigs = new Dictionary<int, EquipConfig>();

            /// <summary>
            /// 部位
            /// </summary>
            private static volatile IDictionary<int, IList<EquipConfig>> _positionConfigs = new Dictionary<int, IList<EquipConfig>>();

            public static IList<EquipConfig> GetConfigs() 
            {
                return _configs;
            }

            public static IDictionary<int, EquipConfig> GetIdConfigs() 
            {
                return _idConfigs;
            }

            public static EquipConfig GetById(int id)
            {
                _idConfigs.TryGetValue(id, out var result);
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


            public static void Index(IList<EquipConfig> configs)
            {
                IDictionary<int, EquipConfig> idConfigs = new Dictionary<int, EquipConfig>();
                IDictionary<int, IList<EquipConfig>> positionConfigs = new Dictionary<int, IList<EquipConfig>>();

                foreach (var config in configs)
                {
                    idConfigs[config.Id] = config;

                    if (!positionConfigs.ContainsKey(config.Position)) positionConfigs[config.Position] = new List<EquipConfig>();
                    positionConfigs[config.Position].Add(config);
                }

                configs = configs.ToImmutableList();
                idConfigs = ToImmutableDictionary(idConfigs);
                positionConfigs = ToImmutableDictionary(positionConfigs);

                _configs = configs;
                _idConfigs = idConfigs;
                _positionConfigs = positionConfigs;
            }
        }
    }
}