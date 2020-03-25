using Newtonsoft.Json.Linq;
using Quan.Common;
using Quan.Config;

namespace Test.Config.Item
{
    /// <summary>
	/// 使用效果<br/>
	/// 自动生成
	/// </summary>
    public class UseEffect : Bean
    {
        public readonly int Aaa;


        public UseEffect(JObject json) : base(json)
        {
            Aaa = json["aaa"]?.Value<int>() ?? default;
        }

        public static UseEffect Create(JObject json)
        {
            var clazz = json["class"].Value<string>() ?? "";
            switch (clazz) 
            {
                case "UseEffect2":
                    return UseEffect2.Create(json);
                case "":
                case "UseEffect":
                    return new UseEffect(json);
                default:
                    return null;   
            }
        }

        public override string ToString()
        {
            return "UseEffect{" +
                   "Aaa=" + Aaa.ToString2() +
                   '}';
        }
    }
}