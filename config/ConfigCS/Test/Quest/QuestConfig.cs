using System;
using System.Collections.Generic;
using System.Collections.Immutable;
using Newtonsoft.Json.Linq;
using ConfigCS;
using ConfigCS.Test.Item;

namespace ConfigCS.Test.Quest
{
    /// <summary>
	/// 任务<br/>
	/// Created by 自动生成
	/// </summary>
    public class QuestConfig : Config
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


        public QuestConfig(JObject json): base(json)
        {
            Id = json["id"]?.Value<int>()?? default;
            Name = json["name"]?.Value<string>()?? "";
            Type = (QuestType) (json["type"]?.Value<int>()?? default);
            Target = json["target"]?.Value<int>()?? default;
            Reward = json.ContainsKey("reward") ? new Reward(json["reward"].Value<JObject>()) : null;
            A1 = json["a1"]?.Value<int>()?? default;
            A2 = json["a2"]?.Value<int>()?? default;
            B1 = json["b1"]?.Value<int>()?? default;
            B2 = json["b2"]?.Value<bool>()?? default;
            C1 = json["c1"]?.Value<string>()?? "";
            C2 = json["c2"]?.Value<int>()?? default;
            C3 = json["c3"]?.Value<int>()?? default;
            D1 = json["d1"]?.Value<string>()?? "";
            D2 = json["d2"]?.Value<int>()?? default;
            D3 = json["d3"]?.Value<int>()?? default;

            var s11 = json["s1"]?.Value<JArray>();
            var s12 = ImmutableHashSet<int>.Empty;
            if (s11 != null)
            {
                foreach (var s1Value in s11)
                {
                    s12.Add(s1Value.Value<int>());
                }
            }
            S1 = s12;

            var l11 = json["l1"]?.Value<JArray>();
            var l12 = ImmutableList<int>.Empty;
            if (l11 != null)
            {
                foreach (var l1Value in l11)
                {
                    l12.Add(l1Value.Value<int>());
                }
            }
            L1 = l12;

            var m11 = json["m1"]?.Value<JObject>();
            var m12 = ImmutableDictionary<int, int>.Empty;
            if (m11 != null)
            {
                foreach (var m1Prop in m11.Properties())
                {
                    m12.Add(int.Parse(m1Prop.Name), m1Prop.Value<int>());
                }
            }
            M1 = m12;
        }

        protected override Config Create(JObject json) 
        {
            return new QuestConfig(json);
        }


        public override string ToString()
        {
            return "QuestConfig{" +
                   "Id=" + Id +
                   ",Name='" + Name + '\'' +
                   ",Type=" + Type +
                   ",Target=" + Target +
                   ",Reward=" + Reward +
                   ",A1=" + A1 +
                   ",A2=" + A2 +
                   ",B1=" + B1 +
                   ",B2=" + B2 +
                   ",C1='" + C1 + '\'' +
                   ",C2=" + C2 +
                   ",C3=" + C3 +
                   ",D1='" + D1 + '\'' +
                   ",D2=" + D2 +
                   ",D3=" + D3 +
                   ",S1=" + S1 +
                   ",L1=" + L1 +
                   ",M1=" + M1 +
                   '}';
        }


        private static volatile IList<QuestConfig> _configs = new List<QuestConfig>();

        private static volatile IDictionary<int, IDictionary<int, QuestConfig>> _composite1Configs = new Dictionary<int, IDictionary<int, QuestConfig>>();

        private static volatile IDictionary<int, IDictionary<bool, IList<QuestConfig>>> _composite2Configs = new Dictionary<int, IDictionary<bool, IList<QuestConfig>>>();

        private static volatile IDictionary<string, IDictionary<int, IDictionary<int, QuestConfig>>> _composite3Configs = new Dictionary<string, IDictionary<int, IDictionary<int, QuestConfig>>>();

        private static volatile IDictionary<string, IDictionary<int, IDictionary<int, IList<QuestConfig>>>> _composite4Configs = new Dictionary<string, IDictionary<int, IDictionary<int, IList<QuestConfig>>>>();

        /// <summary>
        /// ID
        /// </summary>
        private static volatile IDictionary<int, QuestConfig> _idConfigs = new Dictionary<int, QuestConfig>();

        /// <summary>
        /// 类型
        /// </summary>
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
            return _composite1Configs.ContainsKey(a1) ? _composite1Configs[a1] : ImmutableDictionary<int, QuestConfig>.Empty;
        }

        public static QuestConfig GetByComposite1(int a1, int a2)
        {
            var composite1Configs = GetByComposite1(a1);
            return composite1Configs.ContainsKey(a1) ? composite1Configs[a2] : null;
        }

        public static IDictionary<int, IDictionary<bool, IList<QuestConfig>>> GetComposite2Configs()
        {
            return _composite2Configs;
        }

        public static IDictionary<bool, IList<QuestConfig>> GetByComposite2(int b1)
        {
            return _composite2Configs.ContainsKey(b1) ? _composite2Configs[b1] : ImmutableDictionary<bool, IList<QuestConfig>>.Empty;
        }

        public static IList<QuestConfig> GetByComposite2(int b1, bool b2)
        {
            var composite2Configs = GetByComposite2(b1);
            return composite2Configs.ContainsKey(b2) ? composite2Configs[b2] : ImmutableList<QuestConfig>.Empty;
        }

        public static IDictionary<string, IDictionary<int, IDictionary<int, QuestConfig>>> GetComposite3Configs()
        {
            return _composite3Configs;
        }

        public static IDictionary<int, IDictionary<int, QuestConfig>> GetByComposite3(string c1)
        {
            return _composite3Configs.ContainsKey(c1) ? _composite3Configs[c1] : ImmutableDictionary<int, IDictionary<int, QuestConfig>>.Empty;
        }

        public static IDictionary<int, QuestConfig> GetByComposite3(string c1, int c2)
        {
            var composite3Configs = GetByComposite3(c1);
            return composite3Configs.ContainsKey(c2) ? composite3Configs[c2] : ImmutableDictionary<int, QuestConfig>.Empty;
        }

        public static QuestConfig GetByComposite3(string c1, int c2, int c3)
        {
            var composite3Configs = GetByComposite3(c1, c2);
            return composite3Configs.ContainsKey(c3) ? composite3Configs[c3] : null;
        }

        public static IDictionary<string, IDictionary<int, IDictionary<int, IList<QuestConfig>>>> GetComposite4Configs()
        {
            return _composite4Configs;
        }

        public static IDictionary<int, IDictionary<int, IList<QuestConfig>>> GetByComposite4(string d1)
        {
            return _composite4Configs.ContainsKey(d1) ? _composite4Configs[d1] : ImmutableDictionary<int, IDictionary<int, IList<QuestConfig>>>.Empty;
        }

        public static IDictionary<int, IList<QuestConfig>> GetByComposite4(string d1, int d2)
        {
            var composite4Configs = GetByComposite4(d1);
            return composite4Configs.ContainsKey(d2) ? composite4Configs[d2] : ImmutableDictionary<int, IList<QuestConfig>>.Empty;
        }

        public static IList<QuestConfig> GetByComposite4(string d1, int d2, int d3)
        {
            var composite4Configs = GetByComposite4(d1, d2);
            return composite4Configs.ContainsKey(d3) ? composite4Configs[d3] : ImmutableList<QuestConfig>.Empty;
        }

        public static IDictionary<int, QuestConfig> GetIdConfigs() 
        {
            return _idConfigs;
        }

        public static QuestConfig GetById(int id)
        {
            return _idConfigs.ContainsKey(id) ? _idConfigs[id] : null;
        }

        public static IDictionary<QuestType, IList<QuestConfig>> GetTypeConfigs() 
        {
            return _typeConfigs;
        }

        public static IList<QuestConfig> GetByType(QuestType type)
        {
            return _typeConfigs.ContainsKey(type) ? _typeConfigs[type] : ImmutableList<QuestConfig>.Empty;
        }


        public static void Index(List<QuestConfig> configs)
        {
            var composite1Configs = new Dictionary<int, IDictionary<int, QuestConfig>>();
            var composite2Configs = new Dictionary<int, IDictionary<bool, IList<QuestConfig>>>();
            var composite3Configs = new Dictionary<string, IDictionary<int, IDictionary<int, QuestConfig>>>();
            var composite4Configs = new Dictionary<string, IDictionary<int, IDictionary<int, IList<QuestConfig>>>>();
            var idConfigs = new Dictionary<int, QuestConfig>();
            var typeConfigs = new Dictionary<QuestType, IList<QuestConfig>>();

            foreach (var config in configs)
            {
                if (!composite1Configs.ContainsKey(config.A1))
                {
                    composite1Configs.Add(config.A1, new Dictionary<int, QuestConfig>());
                }

                composite1Configs[config.A1][config.A2] = config;


                if (!composite2Configs.ContainsKey(config.B1))
                {
                    composite2Configs.Add(config.B1, new Dictionary<bool, IList<QuestConfig>>());
                }

                if (!composite2Configs[config.B1].ContainsKey(config.B2))
                {
                    composite2Configs[config.B1][config.B2] = new List<QuestConfig>();
                }

                composite2Configs[config.B1][config.B2].Add(config);


                if (!composite3Configs.ContainsKey(config.C1))
                {
                    composite3Configs.Add(config.C1, new Dictionary<int, IDictionary<int, QuestConfig>>());
                }

                if (!composite3Configs[config.C1].ContainsKey(config.C3))
                {
                    composite3Configs[config.C1].Add(config.C2, new Dictionary<int, QuestConfig>());
                }

                composite3Configs[config.C1][config.C2][config.C3] = config;


                if (!composite4Configs.ContainsKey(config.D1))
                {
                    composite4Configs.Add(config.D1, new Dictionary<int, IDictionary<int, IList<QuestConfig>>>());
                }

                if (!composite4Configs[config.D1].ContainsKey(config.D2))
                {
                        composite4Configs[config.D1].Add(config.D2, new Dictionary<int, IList<QuestConfig>>());
                }

                if (!composite4Configs[config.D1][config.D2].ContainsKey(config.D3))
                {
                        composite4Configs[config.D1][config.D2].Add(config.D3, new List<QuestConfig>());
                }

                composite4Configs[config.D1][config.D2][config.D3].Add(config);


                idConfigs[config.Id] = config;


                if (!typeConfigs.ContainsKey(config.Type))
                {
                    typeConfigs.Add(config.Type, new List<QuestConfig>());
                }

                typeConfigs[config.Type].Add(config);

            }

            _configs = configs.ToImmutableList();
            _composite1Configs = composite1Configs.ToImmutableDictionary();
            _composite2Configs = composite2Configs.ToImmutableDictionary();
            _composite3Configs = composite3Configs.ToImmutableDictionary();
            _composite4Configs = composite4Configs.ToImmutableDictionary();
            _idConfigs = idConfigs.ToImmutableDictionary();
            _typeConfigs = typeConfigs.ToImmutableDictionary();

        }
    }
}