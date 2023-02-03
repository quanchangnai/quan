using Newtonsoft.Json.Linq;
using Quan.Config;
using Quan.Utils;

namespace Test.Config.Item
{
    /// <summary>
	/// 使用效果<br/>
	/// 代码自动生成，请勿手动修改
	/// </summary>
    public class UseEffect : Bean
    {
        public readonly int aaa;


        public UseEffect(JObject json) : base(json)
        {
            aaa = json["aaa"]?.Value<int>() ?? default;
        }

        public static UseEffect Create(JObject json)
        {
            var clazz = json["class"].Value<string>() ?? "";
            switch (clazz) 
            {
                case "UseEffect4":
                case "item.UseEffect4":
                    return UseEffect4.Create(json);
                case "UseEffect3":
                case "item.UseEffect3":
                    return UseEffect3.Create(json);
                case "UseEffect2":
                case "item.UseEffect2":
                    return UseEffect2.Create(json);
                case "UseEffect":
                case "item.UseEffect":
                    return new UseEffect(json);
                default:
                    return null;   
            }
        }

        public override string ToString()
        {
            return "UseEffect{" +
                   "aaa=" + aaa.ToString2() +
                   '}';
        }
    }
}