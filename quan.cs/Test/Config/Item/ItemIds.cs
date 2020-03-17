namespace Test.Config.Item
{
    /// <summary>
    /// 道具ID<br/>
    /// 自动生成
    /// </summary>
    public class ItemIds 
    {
        /// <summary>
        /// 道具1
        /// </summary>
        public static int Item1 => ItemConfig.GetByKey("item1").Id;

        /// <summary>
        /// 道具2
        /// </summary>
        public static int Item2 => ItemConfig.GetByKey("item2").Id;
    }
}
