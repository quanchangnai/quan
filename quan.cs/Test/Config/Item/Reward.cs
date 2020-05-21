using Newtonsoft.Json.Linq;
using Quan.Common.Utils;
using Quan.Config;

namespace Test.Config.Item
{
    /// <summary>
	/// 奖励<br/>
	/// 自动生成
	/// </summary>
    public class Reward : Bean
    {
        public readonly int ItemId;

        public readonly int ItemNum;


        public Reward(JObject json) : base(json)
        {
            ItemId = json["itemId"]?.Value<int>() ?? default;
            ItemNum = json["itemNum"]?.Value<int>() ?? default;
        }

        public static Reward Create(JObject json)
        {
            return new Reward(json);
        }

        public override string ToString()
        {
            return "Reward{" +
                   "ItemId=" + ItemId.ToString2() +
                   ",ItemNum=" + ItemNum.ToString2() +
                   '}';
        }
    }
}