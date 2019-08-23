using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;

namespace ConfigCS
{
    public class ConfigLoader
    {
        private string _jsonPath;

        public string JsonPath
        {
            set => _jsonPath = PathUtils.CurrentPlatPath(value);
        }

        public string NamespacePrefix { get; set; }

        private readonly Dictionary<string, ConfigReader> _readers = new Dictionary<string, ConfigReader>();

        //配置全类名:所有子孙配置[命名空间.类名]，包含自己
        private readonly Dictionary<string, HashSet<string>> _configDescendants = new Dictionary<string, HashSet<string>>();

        private readonly Dictionary<string, FileInfo> _configToJsonFiles = new Dictionary<string, FileInfo>();

        public void Load()
        {
            InitConfigDescendants();

            foreach (var configFullName in _configDescendants.Keys)
            {
                Load(configFullName, _configDescendants[configFullName]);
            }
        }

        private void InitConfigDescendants()
        {
            var jsonFiles = PathUtils.ListFiles(new DirectoryInfo(_jsonPath), "json");
            var configs = new List<Config>();

            foreach (var jsonFile in jsonFiles)
            {
                var configNameWithPackage = ToCapitalCamel(jsonFile.Name.Substring(0, jsonFile.Name.LastIndexOf(".")));
                var configFullName = NamespacePrefix == null ? configNameWithPackage : NamespacePrefix + "." + configNameWithPackage;

                if (!_configDescendants.ContainsKey(configFullName))
                {
                    _configDescendants[configFullName] = new HashSet<string>();
                }

                _configToJsonFiles[configNameWithPackage] = jsonFile;

                var reader = GetReader(configNameWithPackage);
                if (reader.Prototype != null)
                {
                    configs.Add(reader.Prototype);
                }
            }

            foreach (var config1 in configs)
            {
                foreach (var config2 in configs)
                {
                    if (!config1.GetType().IsInstanceOfType(config2))
                    {
                        continue;
                    }

                    var config2NameWithPackage = config2.GetType().FullName;
                    if (NamespacePrefix != null)
                    {
                        config2NameWithPackage = config2NameWithPackage.Substring(NamespacePrefix.Length + 1);
                    }

                    _configDescendants[config1.GetType().FullName].Add(config2NameWithPackage);
                }
            }
        }

        private ConfigReader GetReader(string configNameWithPackage)
        {
            _readers.TryGetValue(configNameWithPackage, out var configReader);
            if (configReader != null)
            {
                return configReader;
            }

            var configFullName = NamespacePrefix == null ? configNameWithPackage : NamespacePrefix + "." + configNameWithPackage;

            configReader = new ConfigReader(_configToJsonFiles[configNameWithPackage], configFullName);
            _readers[configNameWithPackage] = configReader;

            return configReader;
        }

        private void Load(string configFullName, HashSet<string> descendants)
        {
            var configName = configFullName.Substring(configFullName.LastIndexOf(".", StringComparison.Ordinal) + 1);
            var configType = Type.GetType(configFullName);
            if (configType == null)
            {
                Console.WriteLine("加载配置[{0}]出错，配置类[{1}]不存在", configName, configFullName);
                return;
            }

            var configs = Activator.CreateInstance(typeof(List<>).MakeGenericType(configType));
            var addMethod = configs.GetType().GetMethod("Add", new[] {configType});
            foreach (var configReader in descendants.Select(GetReader))
            {
                foreach (var config in configReader.ReadObjects())
                {
                    addMethod?.Invoke(configs, new object[] {config});
                }
            }

            try
            {
                var indexMethod = Type.GetType(configFullName)?.GetMethod("Index", new[] {typeof(IList<>).MakeGenericType(configType)});
                if (indexMethod == null)
                {
                    Console.WriteLine("加载配置[{0}]出错，配置类[{1}]没有索引方法", configName, configFullName);
                    return;
                }

                indexMethod.Invoke(null, new[] {configs});
            }
            catch (Exception e)
            {
                Console.WriteLine("加载配置[{0}]出错，调用配置类[{1}]的索引方法出错", configName, configFullName);
                Console.WriteLine(e);
            }
        }

        public static string ToCapitalCamel(string str)
        {
            var result = "";
            for (var i = 0; i < str.Length; i++)
            {
                var c = str[i].ToString();
                if (i == 0 || str[i - 1] == '.')
                {
                    c = c.ToUpper();
                }

                result += c;
            }

            return result;
        }
    }
}