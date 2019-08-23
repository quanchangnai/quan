using System;
using System.Diagnostics;
using System.IO;
using ConfigCS.Test.Item;

namespace ConfigCS.Test
{
    internal static class ConfigTest
    {
        public static void Main()
        {
            var jsonPath = "..\\..\\..\\..\\quan-java\\config\\json";
            var namespacePrefix = "ConfigCS.Test";
            var configLoader = new ConfigLoader {JsonPath = jsonPath, NamespacePrefix = namespacePrefix};
            configLoader.Load();

            foreach (var config in EquipConfig.GetIdConfigs().Values)
            {
                Console.WriteLine("{0} = {1} = {2}", config.Name, config.EffectiveTime, config.EffectiveTime_Str);
            }
        }
    }
}