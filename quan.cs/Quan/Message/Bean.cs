namespace Quan.Message
{
    public abstract class Bean
    {
        public byte[] Encode()
        {
            var buffer = new CodedBuffer();
            Encode(buffer);
            return buffer.RemainingBytes();
        }

        public virtual void Encode(CodedBuffer buffer)
        {
        }

        public void Decode(byte[] bytes)
        {
            var buffer = new CodedBuffer(bytes);
            Decode(buffer);
        }

        public virtual void Decode(CodedBuffer buffer)
        {
        }

        public void WriteTag(CodedBuffer buffer, int tag)
        {
            buffer.WriteByte((byte) tag);
        }

        public int ReadTag(CodedBuffer buffer)
        {
            return buffer.ReadByte();
        }

        protected static void SkipField(int tag, CodedBuffer buffer)
        {
            switch (tag & 0b11)
            {
                case 0:
                    buffer.ReadLong();
                    break;
                case 1:
                    buffer.ReadFloat();
                    break;
                case 2:
                    buffer.ReadDouble();
                    break;
                case 3:
                    buffer.SkipBytes();
                    break;
            }
        }
    }
}