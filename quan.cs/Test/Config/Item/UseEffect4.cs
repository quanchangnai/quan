using Newtonsoft.Json.Linq;
using Quan.Utils;
using System.Collections.Generic;

namespace Test.Config.Item
{
    /// <summary>
	/// 使用效果4<br/>
	/// 代码自动生成，请勿手动修改
	/// </summary>
    public class UseEffect4 : UseEffect
    {
        public readonly ItemType itemType;

        public IList<ItemConfig> itemType_Ref => ItemConfig.GetByType(itemType);


        public UseEffect4(JObject json) : base(json)
        {
            itemType = (ItemType) (json["itemType"]?.Value<int>() ?? default);
        }

        public new static UseEffect4 Create(JObject json)
        {
            return new UseEffect4(json);
        }

        public override string ToString()
        {
            return "UseEffect4{" +
                   "aaa=" + aaa.ToString2() +
                   ",itemType=" + itemType.ToString2() +
                   '}';
        }
    }
}