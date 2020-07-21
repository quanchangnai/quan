namespace Quan.Message
{
    public abstract class Bean
    {
        public byte[] Encode()
        {
            var buffer = new Buffer();
            Encode(buffer);
            return buffer.RemainingBytes();
        }

        public virtual void Encode(Buffer buffer)
        {
        }

        public void Decode(byte[] bytes)
        {
            var buffer = new Buffer(bytes);
            Decode(buffer);
        }

        public virtual void Decode(Buffer buffer)
        {
        }

        protected void SkipField(int tag, Buffer buffer)
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