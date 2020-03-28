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
    }
}