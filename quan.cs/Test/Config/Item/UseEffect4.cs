using Newtonsoft.Json.Linq;
using Quan.Common.Utils;

namespace Test.Config.Item
{
    /// <summary>
	/// 使用效果4<br/>
	/// 代码自动生成，请勿手动修改
	/// </summary>
    public class UseEffect4 : UseEffect
    {
        public readonly ItemType ItemType;


        public UseEffect4(JObject json) : base(json)
        {
            ItemType = (ItemType) (json["itemType"]?.Value<int>() ?? default);
        }

        public new static UseEffect4 Create(JObject json)
        {
            return new UseEffect4(json);
        }

        public override string ToString()
        {
            return "UseEffect4{" +
                   "Aaa=" + Aaa.ToString2() +
                   ",ItemType=" + ItemType.ToString2() +
                   '}';
        }
    }
}