using System;
using System.Collections.Generic;
using System.Collections.Immutable;
using Newtonsoft.Json.Linq;
using ConfigCS;

namespace ConfigCS.Test.Quest
{
    /// <summary>
	/// QuestTargetConfig<br/>
	/// Created by 自动生成
	/// </summary>
    public class QuestTargetConfig : Config
    {

        /// <summary>
        /// ID
        /// </summary>
        public readonly int Id;

        /// <summary>
        /// 名字
        /// </summary>
        public readonly string Name;


        public QuestTargetConfig(JObject json): base(json)
        {
            Id = json["id"]?.Value<int>()?? default;
            Name = json["name"]?.Value<string>()?? "";
        }

        protected override Config Create(JObject json) 
        {
            return new QuestTargetConfig(json);
        }


        public override string ToString()
        {
            return "QuestTargetConfig{" +
                   "Id=" + Id +
                   ",Name='" + Name + '\'' +
                   '}';
        }


        private static volatile IList<QuestTargetConfig> _configs = new List<QuestTargetConfig>();

        /// <summary>
        /// ID
        /// </summary>
        private static volatile IDictionary<int, QuestTargetConfig> _idConfigs = new Dictionary<int, QuestTargetConfig>();

        public static IList<QuestTargetConfig> GetConfigs() 
        {
            return _configs;
        }

        public static IDictionary<int, QuestTargetConfig> GetIdConfigs() 
        {
            return _idConfigs;
        }

        public static QuestTargetConfig GetById(int id)
        {
            return _idConfigs.ContainsKey(id) ? _idConfigs[id] : null;
        }


        public static void Index(List<QuestTargetConfig> configs)
        {
            var idConfigs = new Dictionary<int, QuestTargetConfig>();

            foreach (var config in configs)
            {
                idConfigs[config.Id] = config;

            }

            _configs = configs.ToImmutableList();
            _idConfigs = idConfigs.ToImmutableDictionary();

        }
    }
}