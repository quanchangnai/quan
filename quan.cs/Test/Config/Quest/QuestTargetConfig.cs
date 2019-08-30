using System.Collections.Generic;
using System.Collections.Immutable;
using Newtonsoft.Json.Linq;
using Quan.Common;
using Quan.Config;

namespace Test.Config.Quest
{
    /// <summary>
	/// QuestTargetConfig<br/>
	/// 自动生成
	/// </summary>
    public class QuestTargetConfig : ConfigBase
    {
        /// <summary>
        /// ID
        /// </summary>
        public readonly int Id;

        /// <summary>
        /// 名字
        /// </summary>
        public readonly string Name;


        public QuestTargetConfig(JObject json) : base(json)
        {
            Id = json["id"]?.Value<int>() ?? default;
            Name = json["name"]?.Value<string>() ?? "";
        }

        protected override ConfigBase Create(JObject json)
        {
            return new QuestTargetConfig(json);
        }


        public override string ToString()
        {
            return "QuestTargetConfig{" +
                   "Id=" + Id.ToString2() +
                   ",Name='" + Name + '\'' +
                   '}';
        }


        // 所有QuestTargetConfig
        private static volatile IList<QuestTargetConfig> _configs = new List<QuestTargetConfig>();

        // ID
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
            _idConfigs.TryGetValue(id, out var result);
            return result;
        }


        public static void Load(IList<QuestTargetConfig> configs)
        {
            IDictionary<int, QuestTargetConfig> idConfigs = new Dictionary<int, QuestTargetConfig>();

            foreach (var config in configs)
            {
                Load(idConfigs, config, config.Id);
            }

            configs = configs.ToImmutableList();
            idConfigs = ToImmutableDictionary(idConfigs);

            _configs = configs;
            _idConfigs = idConfigs;
        }
    }
}