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
                return _idConfigs.ContainsKey(id) ? _idConfigs[id] : null;
            }

            public static IDictionary<int, IList<EquipConfig>> GetPositionConfigs() 
            {
                return _positionConfigs;
            }

            public static IList<EquipConfig> GetByPosition(int position)
            {
                return _positionConfigs.ContainsKey(position) ? _positionConfigs[position] : ImmutableList<EquipConfig>.Empty;
            }


            public static void Index(List<EquipConfig> configs)
            {
                var idConfigs = new Dictionary<int, EquipConfig>();
                var positionConfigs = new Dictionary<int, IList<EquipConfig>>();

                foreach (var config in configs)
                {
                    idConfigs[config.Id] = config;


                    if (!positionConfigs.ContainsKey(config.Position))
                    {
                        positionConfigs.Add(config.Position, new List<EquipConfig>());
                    }

                    positionConfigs[config.Position].Add(config);

                }

                _configs = configs.ToImmutableList();
                _idConfigs = idConfigs.ToImmutableDictionary();
                _positionConfigs = positionConfigs.ToImmutableDictionary();

            }
        }
    }
}