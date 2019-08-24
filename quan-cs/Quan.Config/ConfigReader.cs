using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Reflection;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;

namespace Quan.Config
{
    public class ConfigReader
    {
        private readonly FileInfo _jsonFile;

        private readonly string _configFullName;

        private readonly List<JObject> _jsons = new List<JObject>();

        private readonly List<ConfigBase> _configs = new List<ConfigBase>();

        /// <summary>
        /// 配置类所在的程序集，用于通过类名反射加载类
        /// </summary>
        private HashSet<string> _configAssemblies;

        public ConfigBase Prototype { get; private set; }


        public ConfigReader(FileInfo jsonFile, HashSet<string> configAssemblies, string configFullName)
        {
            _jsonFile = jsonFile;
            _configAssemblies = configAssemblies;
            _configFullName = configFullName;
            InitPrototype();
        }

        protected void InitPrototype()
        {
            Type configType = null;
            foreach (var assemblyName in _configAssemblies)
            {
                configType = Assembly.Load(assemblyName)?.GetType(_configFullName);
                if (configType != null)
                {
                    break;
                }
            }

            if (configType == null)
            {
                Console.WriteLine("初始化配置原型失败,配置类[{0}]不存在", _configFullName);
                return;
            }

            Prototype = Activator.CreateInstance(configType, new JObject()) as ConfigBase;
        }

        public List<JObject> ReadJsons()
        {
            if (!_jsons.Any())
            {
                Read();
            }

            return _jsons;
        }


        public List<ConfigBase> ReadObjects()
        {
            if (Prototype == null || _configs.Any())
            {
                return _configs;
            }

            ReadJsons();

            foreach (var json in _jsons)
            {
                _configs.Add(Prototype.Create(json));
            }

            return _configs;
        }

        protected void Read()
        {
            using (var jsonReader = new JsonTextReader(_jsonFile.OpenText()))
            {
                if (!(JToken.ReadFrom(jsonReader) is JArray array))
                {
                    return;
                }

                foreach (var token in array)
                {
                    _jsons.Add(token as JObject);
                }
            }
        }

        public void Clear()
        {
            _jsons.Clear();
            _configs.Clear();
        }
    }
}