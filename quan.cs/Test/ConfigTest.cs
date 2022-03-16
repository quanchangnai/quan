using System;
using System.Reflection;
using NUnit.Framework;
using Quan.Config;
using Test.Config.Item;
using Test.Config.Quest;

namespace Test
{
    [TestFixture]
    public class ConfigTest
    {
        [Test]
        public void Test()
        {
            const string jsonPath = "..\\..\\..\\..\\quan.java\\quan-config\\json";
            const string namespacePrefix = "Test.Config";
            var assemblyName = Assembly.GetExecutingAssembly().FullName;

            var configLoader = new ConfigLoader {JsonPath = jsonPath, NamespacePrefix = namespacePrefix, ConfigAssemblies = {assemblyName}};
            configLoader.Load();

            foreach (var config in ItemConfig.GetIdConfigs().Values)
            {
                Console.WriteLine(config);
            }

            EquipConfig.GetByPosition(1);
            QuestConfig.Get(1);
        }
    }
}