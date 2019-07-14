using System.IO;

namespace MessageCS
{
    /// <summary>
    /// 基于VarInt和ZigZag编码的消息
    /// </summary>
    public abstract class Message : Bean
    {
        public int id { get; }

        public long sn { get; set; }


        protected Message(int id)
        {
            this.id = id;
        }

        public abstract Message Create();


        public override void Encode(Buffer buffer)
        {
            buffer.WriteInt(id);
            buffer.WriteLong(sn);
        }

        public override void Decode(Buffer buffer)
        {
            var msgId = buffer.ReadInt();
            if (msgId != id)
            {
                throw new IOException("消息ID不匹配,目标值：" + id + "，实际值：" + msgId);
            }

            sn = buffer.ReadLong();
        }
    }
}