using Newtonsoft.Json.Linq;
using Quan.Common.Utils;
using Quan.Config;

namespace Test.Config.Item
{
    /// <summary>
	/// 奖励<br/>
	/// 代码自动生成，请勿手动修改
	/// </summary>
    public class Reward : Bean
    {
        public readonly int itemId;

        public ItemConfig itemId_Ref => ItemConfig.GetById(itemId);

        public readonly int itemNum;


        public Reward(JObject json) : base(json)
        {
            itemId = json["itemId"]?.Value<int>() ?? default;
            itemNum = json["itemNum"]?.Value<int>() ?? default;
        }

        public static Reward Create(JObject json)
        {
            return new Reward(json);
        }

        public override string ToString()
        {
            return "Reward{" +
                   "itemId=" + itemId.ToString2() +
                   ",itemNum=" + itemNum.ToString2() +
                   '}';
        }
    }
}