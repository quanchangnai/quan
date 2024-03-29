using System.IO;

namespace Quan.Message
{
    /// <summary>
    /// 基于VarInt和ZigZag编码的消息
    /// </summary>
    public abstract class MessageBase : Bean
    {
        public abstract int Id { get; }

        public abstract MessageBase Create();

        public override void Encode(CodedBuffer buffer)
        {
            buffer.WriteInt(Id);
        }

        public override void Decode(CodedBuffer buffer)
        {
            var id = buffer.ReadInt();
            if (id != Id)
            {
                throw new IOException($"消息ID不匹配,期望值[{Id}],实际值[{id}]");
            }
        }
    }
}