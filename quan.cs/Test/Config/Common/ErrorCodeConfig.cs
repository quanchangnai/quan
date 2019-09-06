using System.Collections.Generic;
using System.Collections.Immutable;
using Newtonsoft.Json.Linq;
using Quan.Common;
using Quan.Config;

namespace Test.Config.Common
{
    /// <summary>
	/// 错误码<br/>
	/// 自动生成
	/// </summary>
    public class ErrorCodeConfig : ConfigBase
    {
        /// <summary>
        /// ID
        /// </summary>
        public readonly int Id;

        /// <summary>
        /// Key
        /// </summary>
        public readonly string Key;

        /// <summary>
        /// 文本
        /// </summary>
        public readonly string Text;


        public ErrorCodeConfig(JObject json) : base(json)
        {
            Id = json["id"]?.Value<int>() ?? default;
            Key = json["key"]?.Value<string>() ?? "";
            Text = json["text"]?.Value<string>() ?? "";
        }

        protected override ConfigBase Create(JObject json)
        {
            return new ErrorCodeConfig(json);
        }


        public override string ToString()
        {
            return "ErrorCodeConfig{" +
                   "Id=" + Id.ToString2() +
                   ",Key='" + Key + '\'' +
                   ",Text='" + Text + '\'' +
                   '}';
        }


        // 所有ErrorCodeConfig
        private static volatile IList<ErrorCodeConfig> _configs = new List<ErrorCodeConfig>();

        // ID
        private static volatile IDictionary<int, ErrorCodeConfig> _idConfigs = new Dictionary<int, ErrorCodeConfig>();

        // Key
        private static volatile IDictionary<string, ErrorCodeConfig> _keyConfigs = new Dictionary<string, ErrorCodeConfig>();

        public static IList<ErrorCodeConfig> GetConfigs()
        {
            return _configs;
        }

        public static IDictionary<int, ErrorCodeConfig> GetIdConfigs()
        {
            return _idConfigs;
        }

        public static ErrorCodeConfig GetById(int id)
        {
            _idConfigs.TryGetValue(id, out var result);
            return result;
        }

        public static IDictionary<string, ErrorCodeConfig> GetKeyConfigs()
        {
            return _keyConfigs;
        }

        public static ErrorCodeConfig GetByKey(string key)
        {
            _keyConfigs.TryGetValue(key, out var result);
            return result;
        }


        public static void Load(IList<ErrorCodeConfig> configs)
        {
            IDictionary<int, ErrorCodeConfig> idConfigs = new Dictionary<int, ErrorCodeConfig>();
            IDictionary<string, ErrorCodeConfig> keyConfigs = new Dictionary<string, ErrorCodeConfig>();

            foreach (var config in configs)
            {
                Load(idConfigs, config, config.Id);
                Load(keyConfigs, config, config.Key);
            }

            configs = configs.ToImmutableList();
            idConfigs = ToImmutableDictionary(idConfigs);
            keyConfigs = ToImmutableDictionary(keyConfigs);

            _configs = configs;
            _idConfigs = idConfigs;
            _keyConfigs = keyConfigs;
        }
    }
}