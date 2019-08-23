using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;

namespace ConfigCS
{
    public class ConfigReader
    {
        private readonly FileInfo _jsonFile;

        private readonly string _configFullName;

        private readonly List<JObject> _jsons = new List<JObject>();

        private readonly List<Config> _configs = new List<Config>();

        public Config Prototype { get; private set; }


        public ConfigReader(FileInfo jsonFile, string configFullName)
        {
            _jsonFile = jsonFile;
            _configFullName = configFullName;
            InitPrototype();
        }

        protected void InitPrototype()
        {
            var type = Type.GetType(_configFullName);
            if (type == null)
            {
                Console.WriteLine("配置类[{0}]不存在", _configFullName);
                return;
            }

            Prototype = Activator.CreateInstance(type, new JObject()) as Config;
        }

        public List<JObject> ReadJsons()
        {
            if (!_jsons.Any())
            {
                Read();
            }

            return _jsons;
        }


        public List<Config> ReadObjects()
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