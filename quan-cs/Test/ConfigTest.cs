using System;
using System.IO;
using System.Reflection;
using Quan.Config;
using Test.Config.Item;

namespace Test
{
    public class ConfigTest
    {
        public static void Test()
        {
            var jsonPath = "..\\..\\..\\..\\quan-java\\config\\json";
            var namespacePrefix = "Test.Config";
            var assemblyName = Assembly.GetExecutingAssembly().FullName;

            var configLoader = new ConfigLoader {JsonPath = jsonPath, NamespacePrefix = namespacePrefix, ConfigAssemblies = {assemblyName}};
            configLoader.Load();

            foreach (var config in EquipConfig.GetIdConfigs().Values)
            {
                Console.WriteLine(config);
            }

//            EquipConfig.GetIdConfigs();
        }
    }
}