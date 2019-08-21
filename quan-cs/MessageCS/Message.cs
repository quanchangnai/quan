using System.IO;

namespace MessageCS
{
    /// <summary>
    /// 基于VarInt和ZigZag编码的消息
    /// </summary>
    public abstract class Message : Bean
    {
        public int Id { get; }

        public long Sn { get; set; }


        protected Message(int id)
        {
            Id = id;
        }

        public abstract Message Create();


        public override void Encode(Buffer buffer)
        {
            buffer.WriteInt(Id);
            buffer.WriteLong(Sn);
        }

        public override void Decode(Buffer buffer)
        {
            var id = buffer.ReadInt();
            if (id != Id)
            {
                throw new IOException("消息ID不匹配,目标值：" + Id + "，实际值：" + id);
            }

            Sn = buffer.ReadLong();
        }
    }
}