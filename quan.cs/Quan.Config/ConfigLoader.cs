using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using Quan.Common;

namespace Quan.Config
{
    public class ConfigLoader
    {
        private string _jsonPath;

        public string JsonPath
        {
            set => _jsonPath = PathUtils.CurrentPlatPath(value);
        }

        public string NamespacePrefix { get; set; }

        public HashSet<string> ConfigAssemblies { get; } = new HashSet<string>();

        private readonly Dictionary<string, ConfigReader> _readers = new Dictionary<string, ConfigReader>();

        //配置类:所有子孙配置[命名空间.类名]，包含自己
        private readonly Dictionary<Type, HashSet<string>> _configDescendants = new Dictionary<Type, HashSet<string>>();

        private readonly Dictionary<string, FileInfo> _configToJsonFiles = new Dictionary<string, FileInfo>();

        public void Load()
        {
            InitConfigDescendants();

            foreach (var configType in _configDescendants.Keys)
            {
                Load(configType, _configDescendants[configType]);
            }
        }

        private void InitConfigDescendants()
        {
            var jsonFiles = new DirectoryInfo(_jsonPath).ListFiles("json");
            var configs = new List<ConfigBase>();

            foreach (var jsonFile in jsonFiles)
            {
                var configNameWithPackage = GetConfigNameWithPackage(jsonFile);
                _configToJsonFiles[configNameWithPackage] = jsonFile;

                var reader = GetReader(configNameWithPackage);
                if (reader.Prototype != null)
                {
                    configs.Add(reader.Prototype);
                    if (!_configDescendants.ContainsKey(reader.Prototype.GetType()))
                    {
                        _configDescendants[reader.Prototype.GetType()] = new HashSet<string>();
                    }
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

                    _configDescendants[config1.GetType()].Add(config2NameWithPackage);
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

            configReader = new ConfigReader(_configToJsonFiles[configNameWithPackage], ConfigAssemblies, configFullName);
            _readers[configNameWithPackage] = configReader;

            return configReader;
        }

        private void Load(Type configType, HashSet<string> descendants)
        {
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
                var loadMethod = configType.GetMethod("Load", new[] {typeof(IList<>).MakeGenericType(configType)});
                if (loadMethod == null)
                {
                    Console.WriteLine("加载配置出错，配置类[{0}]没有加载方法", configType.FullName);
                    return;
                }

                loadMethod.Invoke(null, new[] {configs});
            }
            catch (Exception e)
            {
                Console.WriteLine("加载配置出错，调用配置类[{0}]的索引方法出错", configType.FullName);
                Console.WriteLine(e);
            }
        }

        protected static string GetConfigNameWithPackage(FileInfo jsonFile)
        {
            var jsonName = jsonFile.Name.Substring(0, jsonFile.Name.LastIndexOf(".", StringComparison.Ordinal));
            var configNameWithPackage = "";
            for (var i = 0; i < jsonName.Length; i++)
            {
                var c = jsonName[i].ToString();
                if (i == 0 || jsonName[i - 1] == '.')
                {
                    c = c.ToUpper();
                }

                configNameWithPackage += c;
            }

            return configNameWithPackage;
        }
    }
}