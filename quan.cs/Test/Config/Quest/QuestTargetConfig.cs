using Newtonsoft.Json.Linq;
using Quan.Utils;
using Quan.Config;
using System;
using System.Collections.Generic;
using System.Collections.Immutable;

namespace Test.Config.Quest
{
    /// <summary>
	/// QuestTargetConfig<br/>
	/// 代码自动生成，请勿手动修改
	/// </summary>
    public class QuestTargetConfig : ConfigBase
    {
        /// <summary>
        /// ID
        /// </summary>
        public readonly int id;

        /// <summary>
        /// 名字
        /// </summary>
        public readonly string name;

        /// <summary>
        /// 中午
        /// </summary>
        public readonly DateTime noon;

        public readonly string noon_;


        public QuestTargetConfig(JObject json) : base(json)
        {
            id = json["id"]?.Value<int>() ?? default;
            name = json["name"]?.Value<string>() ?? "";
            noon = ToDateTime(json["noon"]?.Value<long>() ?? default);
            noon_ = json["noon_"]?.Value<string>() ?? "";
        }

        protected override ConfigBase Create(JObject json)
        {
            return new QuestTargetConfig(json);
        }

        public override string ToString()
        {
            return "QuestTargetConfig{" +
                   "id=" + id.ToString2() +
                   ",name='" + name + '\'' +
                   ",noon='" + noon_ + '\'' +
                   '}';
        }

        // 所有QuestTargetConfig
        private static volatile IList<QuestTargetConfig> _configs = new List<QuestTargetConfig>();

        // 索引:ID
        private static volatile IDictionary<int, QuestTargetConfig> _idConfigs = new Dictionary<int, QuestTargetConfig>();

        public static IList<QuestTargetConfig> GetConfigs()
        {
            return _configs;
        }

        public static IDictionary<int, QuestTargetConfig> GetIdConfigs()
        {
            return _idConfigs;
        }

        public static QuestTargetConfig Get(int id)
        {
            _idConfigs.TryGetValue(id, out var result);
            return result;
        }


        public static void Load(IList<QuestTargetConfig> configs)
        {
            IDictionary<int, QuestTargetConfig> idConfigs = new Dictionary<int, QuestTargetConfig>();

            foreach (var config in configs)
            {
                Load(idConfigs, config, config.id);
            }

            configs = configs.ToImmutableList();
            idConfigs = ToImmutableDictionary(idConfigs);

            _configs = configs;
            _idConfigs = idConfigs;
        }
    }
}