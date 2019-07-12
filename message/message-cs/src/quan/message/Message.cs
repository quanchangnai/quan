using System.IO;

namespace quan.message
{
    /// <summary>
    /// 基于VarInt和ZigZag编码的消息
    /// </summary>
    public abstract class Message : Bean
    {
        private int id;

        private long sn;


        protected Message(int id)
        {
            this.id = id;
        }

        public int Id => id;

        public long Sn
        {
            get => sn;
            set => sn = value;
        }

        public abstract Message create();


        public void encode(Buffer buffer)
        {
            buffer.WriteInt(id);
            buffer.WriteLong(sn);
        }

        public void decode(Buffer buffer)
        {
            int msgId = buffer.ReadInt();
            if (msgId != id)
            {
                throw new IOException("消息ID不匹配,目标值：" + id + "，实际值：" + msgId);
            }

            sn = buffer.ReadLong();
        }
    }
}