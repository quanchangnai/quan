using Newtonsoft.Json.Linq;
using Quan.Common.Utils;

namespace Test.Config.Item
{
    /// <summary>
	/// 使用效果2<br/>
	/// 自动生成
	/// </summary>
    public class UseEffect2 : UseEffect
    {
        public readonly int Bbb;


        public UseEffect2(JObject json) : base(json)
        {
            Bbb = json["bbb"]?.Value<int>() ?? default;
        }

        public new static UseEffect2 Create(JObject json)
        {
            return new UseEffect2(json);
        }

        public override string ToString()
        {
            return "UseEffect2{" +
                   "Aaa=" + Aaa.ToString2() +
                   ",Bbb=" + Bbb.ToString2() +
                   '}';
        }
    }
}