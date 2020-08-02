using System.Collections.Generic;
using System.Collections.Immutable;
using Newtonsoft.Json.Linq;
using Quan.Common.Utils;
using Quan.Config;
using Test.Config.Item;

namespace Test.Config.Quest
{
    /// <summary>
	/// 任务<br/>
	/// 代码自动生成，请勿手动修改
	/// </summary>
    public class QuestConfig : ConfigBase
    {
        /// <summary>
        /// ID
        /// </summary>
        public readonly int Id;

        /// <summary>
        /// 名字
        /// </summary>
        public readonly string Name;

        /// <summary>
        /// 类型
        /// </summary>
        public readonly QuestType Type;

        /// <summary>
        /// 任务目标
        /// </summary>
        public readonly int Target;

        /// <summary>
        /// 奖励
        /// </summary>
        public readonly Reward Reward;

        /// <summary>
        /// A1
        /// </summary>
        public readonly int A1;

        /// <summary>
        /// A2
        /// </summary>
        public readonly int A2;

        /// <summary>
        /// B1
        /// </summary>
        public readonly int B1;

        /// <summary>
        /// B2
        /// </summary>
        public readonly bool B2;

        /// <summary>
        /// C1
        /// </summary>
        public readonly string C1;

        /// <summary>
        /// C2
        /// </summary>
        public readonly int C2;

        /// <summary>
        /// C3
        /// </summary>
        public readonly int C3;

        /// <summary>
        /// D1
        /// </summary>
        public readonly string D1;

        /// <summary>
        /// D2
        /// </summary>
        public readonly int D2;

        /// <summary>
        /// D3
        /// </summary>
        public readonly int D3;

        /// <summary>
        /// S1
        /// </summary>
        public readonly ISet<int> S1;

        /// <summary>
        /// L1
        /// </summary>
        public readonly IList<int> L1;

        /// <summary>
        /// M1
        /// </summary>
        public readonly IDictionary<int, int> M1;


        public QuestConfig(JObject json) : base(json)
        {
            Id = json["id"]?.Value<int>() ?? default;
            Name = json["name"]?.Value<string>() ?? "";
            Type = (QuestType) (json["type"]?.Value<int>() ?? default);
            Target = json["target"]?.Value<int>() ?? default;
            Reward = json.ContainsKey("reward") ? Reward.Create(json["reward"].Value<JObject>()) : null;
            A1 = json["a1"]?.Value<int>() ?? default;
            A2 = json["a2"]?.Value<int>() ?? default;
            B1 = json["b1"]?.Value<int>() ?? default;
            B2 = json["b2"]?.Value<bool>() ?? default;
            C1 = json["c1"]?.Value<string>() ?? "";
            C2 = json["c2"]?.Value<int>() ?? default;
            C3 = json["c3"]?.Value<int>() ?? default;
            D1 = json["d1"]?.Value<string>() ?? "";
            D2 = json["d2"]?.Value<int>() ?? default;
            D3 = json["d3"]?.Value<int>() ?? default;

            var s11 = json["s1"]?.Value<JArray>();
            var s12 = ImmutableHashSet<int>.Empty;
            if (s11 != null)
            {
                foreach (var s1Value in s11)
                {
                    s12 =s12.Add(s1Value.Value<int>());
                }
            }
            S1 = s12;

            var l11 = json["l1"]?.Value<JArray>();
            var l12 = ImmutableList<int>.Empty;
            if (l11 != null)
            {
                foreach (var l1Value in l11)
                {
                    l12 =l12.Add(l1Value.Value<int>());
                }
            }
            L1 = l12;

            var m11 = json["m1"]?.Value<JObject>();
            var m12 = ImmutableDictionary<int, int>.Empty;
            if (m11 != null)
            {
                foreach (var m1KeyValue in m11)
                {
                    m12 = m12.Add(int.Parse(m1KeyValue.Key), m1KeyValue.Value.Value<int>());
                }
            }
            M1 = m12;
        }

        protected override ConfigBase Create(JObject json)
        {
            return new QuestConfig(json);
        }

        public override string ToString()
        {
            return "QuestConfig{" +
                   "Id=" + Id.ToString2() +
                   ",Name='" + Name + '\'' +
                   ",Type=" + Type.ToString2() +
                   ",Target=" + Target.ToString2() +
                   ",Reward=" + Reward.ToString2() +
                   ",A1=" + A1.ToString2() +
                   ",A2=" + A2.ToString2() +
                   ",B1=" + B1.ToString2() +
                   ",B2=" + B2.ToString2() +
                   ",C1='" + C1 + '\'' +
                   ",C2=" + C2.ToString2() +
                   ",C3=" + C3.ToString2() +
                   ",D1='" + D1 + '\'' +
                   ",D2=" + D2.ToString2() +
                   ",D3=" + D3.ToString2() +
                   ",S1=" + S1.ToString2() +
                   ",L1=" + L1.ToString2() +
                   ",M1=" + M1.ToString2() +
                   '}';
        }

        // 所有QuestConfig
        private static volatile IList<QuestConfig> _configs = new List<QuestConfig>();

        // 索引:两字段唯一索引
        private static volatile IDictionary<int, IDictionary<int, QuestConfig>> _composite1Configs = new Dictionary<int, IDictionary<int, QuestConfig>>();

        // 索引:两字段普通索引
        private static volatile IDictionary<int, IDictionary<bool, IList<QuestConfig>>> _composite2Configs = new Dictionary<int, IDictionary<bool, IList<QuestConfig>>>();

        // 索引:三字段唯一索引
        private static volatile IDictionary<string, IDictionary<int, IDictionary<int, QuestConfig>>> _composite3Configs = new Dictionary<string, IDictionary<int, IDictionary<int, QuestConfig>>>();

        // 索引:三字段普通索引
        private static volatile IDictionary<string, IDictionary<int, IDictionary<int, IList<QuestConfig>>>> _composite4Configs = new Dictionary<string, IDictionary<int, IDictionary<int, IList<QuestConfig>>>>();

        // 索引:ID
        private static volatile IDictionary<int, QuestConfig> _idConfigs = new Dictionary<int, QuestConfig>();

        // 索引:类型
        private static volatile IDictionary<QuestType, IList<QuestConfig>> _typeConfigs = new Dictionary<QuestType, IList<QuestConfig>>();

        public static IList<QuestConfig> GetConfigs()
        {
            return _configs;
        }

        public static IDictionary<int, IDictionary<int, QuestConfig>> GetComposite1Configs()
        {
            return _composite1Configs;
        }

        public static IDictionary<int, QuestConfig> GetByComposite1(int a1)
        {
            _composite1Configs.TryGetValue(a1, out var result);
            return result ?? ImmutableDictionary<int, QuestConfig>.Empty;
        }

        public static QuestConfig GetByComposite1(int a1, int a2)
        {
            GetByComposite1(a1).TryGetValue(a2, out var result);
            return result;
        }

        public static IDictionary<int, IDictionary<bool, IList<QuestConfig>>> GetComposite2Configs()
        {
            return _composite2Configs;
        }

        public static IDictionary<bool, IList<QuestConfig>> GetByComposite2(int b1)
        {
            _composite2Configs.TryGetValue(b1, out var result);
            return result ?? ImmutableDictionary<bool, IList<QuestConfig>>.Empty;
        }

        public static IList<QuestConfig> GetByComposite2(int b1, bool b2)
        {
            GetByComposite2(b1).TryGetValue(b2, out var result);
            return result ?? ImmutableList<QuestConfig>.Empty;
        }

        public static IDictionary<string, IDictionary<int, IDictionary<int, QuestConfig>>> GetComposite3Configs()
        {
            return _composite3Configs;
        }

        public static IDictionary<int, IDictionary<int, QuestConfig>> GetByComposite3(string c1)
        {
            _composite3Configs.TryGetValue(c1, out var result);
            return result ?? ImmutableDictionary<int, IDictionary<int, QuestConfig>>.Empty;
        }

        public static IDictionary<int, QuestConfig> GetByComposite3(string c1, int c2)
        {
            GetByComposite3(c1).TryGetValue(c2, out var result);
            return result ?? ImmutableDictionary<int, QuestConfig>.Empty;
        }

        public  static QuestConfig GetByComposite3(string c1, int c2, int c3)
        {
            GetByComposite3(c1, c2).TryGetValue(c3, out var result);
            return result;
        }

        public static IDictionary<string, IDictionary<int, IDictionary<int, IList<QuestConfig>>>> GetComposite4Configs()
        {
            return _composite4Configs;
        }

        public static IDictionary<int, IDictionary<int, IList<QuestConfig>>> GetByComposite4(string d1)
        {
            _composite4Configs.TryGetValue(d1, out var result);
            return result ?? ImmutableDictionary<int, IDictionary<int, IList<QuestConfig>>>.Empty;
        }

        public static IDictionary<int, IList<QuestConfig>> GetByComposite4(string d1, int d2)
        {
            GetByComposite4(d1).TryGetValue(d2, out var result);
            return result ?? ImmutableDictionary<int, IList<QuestConfig>>.Empty;
        }

        public static IList<QuestConfig> GetByComposite4(string d1, int d2, int d3)
        {
            GetByComposite4(d1, d2).TryGetValue(d3, out var result);
            return result ?? ImmutableList<QuestConfig>.Empty;
        }

        public static IDictionary<int, QuestConfig> GetIdConfigs()
        {
            return _idConfigs;
        }

        public static QuestConfig GetById(int id)
        {
            _idConfigs.TryGetValue(id, out var result);
            return result;
        }

        public static IDictionary<QuestType, IList<QuestConfig>> GetTypeConfigs()
        {
            return _typeConfigs;
        }

        public static IList<QuestConfig> GetByType(QuestType type)
        {
            _typeConfigs.TryGetValue(type, out var result);
            return result ?? ImmutableList<QuestConfig>.Empty;
        }


        public static void Load(IList<QuestConfig> configs)
        {
            IDictionary<int, IDictionary<int, QuestConfig>> composite1Configs = new Dictionary<int, IDictionary<int, QuestConfig>>();
            IDictionary<int, IDictionary<bool, IList<QuestConfig>>> composite2Configs = new Dictionary<int, IDictionary<bool, IList<QuestConfig>>>();
            IDictionary<string, IDictionary<int, IDictionary<int, QuestConfig>>> composite3Configs = new Dictionary<string, IDictionary<int, IDictionary<int, QuestConfig>>>();
            IDictionary<string, IDictionary<int, IDictionary<int, IList<QuestConfig>>>> composite4Configs = new Dictionary<string, IDictionary<int, IDictionary<int, IList<QuestConfig>>>>();
            IDictionary<int, QuestConfig> idConfigs = new Dictionary<int, QuestConfig>();
            IDictionary<QuestType, IList<QuestConfig>> typeConfigs = new Dictionary<QuestType, IList<QuestConfig>>();

            foreach (var config in configs)
            {
                Load(composite1Configs, config, config.A1, config.A2);
                Load(composite2Configs, config, config.B1, config.B2);
                Load(composite3Configs, config, config.C1, config.C2, config.C3);
                Load(composite4Configs, config, config.D1, config.D2, config.D3);
                Load(idConfigs, config, config.Id);
                Load(typeConfigs, config, config.Type);
            }

            configs = configs.ToImmutableList();
            composite1Configs = ToImmutableDictionary(composite1Configs);
            composite2Configs = ToImmutableDictionary(composite2Configs);
            composite3Configs = ToImmutableDictionary(composite3Configs);
            composite4Configs = ToImmutableDictionary(composite4Configs);
            idConfigs = ToImmutableDictionary(idConfigs);
            typeConfigs = ToImmutableDictionary(typeConfigs);

            _configs = configs;
            _composite1Configs = composite1Configs;
            _composite2Configs = composite2Configs;
            _composite3Configs = composite3Configs;
            _composite4Configs = composite4Configs;
            _idConfigs = idConfigs;
            _typeConfigs = typeConfigs;
        }
    }
}