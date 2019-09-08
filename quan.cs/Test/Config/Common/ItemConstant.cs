namespace Test.Config.Common
{
    /// <summary>
    /// 自动生成
    /// </summary>
    public class ItemConstant 
    {
        /// <summary>
        /// 常量1
        /// </summary>
        public static int Constant1 => ConstantConfig.GetByKey("constant1").ItemId;

        /// <summary>
        /// 常量2
        /// </summary>
        public static int Constant2 => ConstantConfig.GetByKey("constant2").ItemId;

        /// <summary>
        /// 常量3
        /// </summary>
        public static int Constant3 => ConstantConfig.GetByKey("constant3").ItemId;
    }
}
