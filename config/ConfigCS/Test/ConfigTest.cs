using System;
using ConfigCS.Test.Item;
using ConfigCS.Test.Quest;
using Newtonsoft.Json.Linq;

namespace ConfigCS.Test
{
    internal static class ConfigTest
    {
        public static void Main()
        {
            var questConfig = new QuestConfig(new JObject());

            Console.WriteLine("questConfig.ToString():" + questConfig);
            Console.WriteLine("questConfig.ToJson():" + questConfig.ToJson());
            Console.WriteLine("QuestConfig.GetConfigs:" + QuestConfig.GetConfigs());
            
            Console.WriteLine("itemConfig.ToString():" + new ItemConfig(new JObject()));
        }
    }
}