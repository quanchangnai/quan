using Newtonsoft.Json.Linq;
using Quan.Utils;

namespace Test.Config.Item
{
    /// <summary>
	/// 使用效果2<br/>
	/// 代码自动生成，请勿手动修改
	/// </summary>
    public class UseEffect2 : UseEffect
    {
        public readonly int bbb;


        public UseEffect2(JObject json) : base(json)
        {
            bbb = json["bbb"]?.Value<int>() ?? default;
        }

        public new static UseEffect2 Create(JObject json)
        {
            return new UseEffect2(json);
        }

        public override string ToString()
        {
            return "UseEffect2{" +
                   "aaa=" + aaa.ToString2() +
                   ",bbb=" + bbb.ToString2() +
                   '}';
        }
    }
}