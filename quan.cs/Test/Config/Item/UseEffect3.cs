using Newtonsoft.Json.Linq;
using Quan.Utils;

namespace Test.Config.Item
{
    /// <summary>
	/// 使用效果3<br/>
	/// 代码自动生成，请勿手动修改
	/// </summary>
    public class UseEffect3 : UseEffect
    {
        public readonly int ccc;


        public UseEffect3(JObject json) : base(json)
        {
            ccc = json["ccc"]?.Value<int>() ?? default;
        }

        public new static UseEffect3 Create(JObject json)
        {
            return new UseEffect3(json);
        }

        public override string ToString()
        {
            return "UseEffect3{" +
                   "aaa=" + aaa.ToString2() +
                   ",ccc=" + ccc.ToString2() +
                   '}';
        }
    }
}