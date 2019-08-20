using System;
using System.Collections.Generic;
using ConfigCS.Test.Item;
using Newtonsoft.Json.Linq;

namespace ConfigCS.Test
{
    internal class ConfigTest
    {
        public static void Main(string[] args)
        {
            JObject json =new JObject();
            var id=json["id"].Value<int>();
            Console.WriteLine("id:"+id);

        }
    }
}