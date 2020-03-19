using System;
using System.IO;
using System.Reflection;
using Quan.Config;
using Test.Config.Item;
using Test.Config.Quest;

namespace Test
{
    public class ConfigTest
    {
        public static void Test()
        {
            const string jsonPath = "..\\..\\..\\..\\quan.java\\config\\json";
            const string namespacePrefix = "Test.Config";
            var assemblyName = Assembly.GetExecutingAssembly().FullName;

            var configLoader = new ConfigLoader {JsonPath = jsonPath, NamespacePrefix = namespacePrefix, ConfigAssemblies = {assemblyName}};
            configLoader.Load();

            foreach (var config in ItemConfig.GetIdConfigs().Values)
            {
                Console.WriteLine(config);
            }

            EquipConfig.GetByPosition(1);
            QuestConfig.GetById(1);
        }
    }
}