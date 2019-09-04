using System.IO;

namespace Quan.Message
{
    /// <summary>
    /// 基于VarInt和ZigZag编码的消息
    /// </summary>
    public abstract class MessageBase : Bean
    {
        public abstract int Id { get; }

        public long Seq { get; set; }

        public abstract MessageBase Create();

        public override void Encode(Buffer buffer)
        {
            buffer.WriteInt(Id);
            buffer.WriteLong(Seq);
        }

        public override void Decode(Buffer buffer)
        {
            if (buffer.Reading)
            {
                buffer.Reset();
            }

            var id = buffer.ReadInt();
            if (id != Id)
            {
                throw new IOException($"消息ID不匹配,期望值[{Id}],实际值[{id}]");
            }

            Seq = buffer.ReadLong();
        }
    }
}