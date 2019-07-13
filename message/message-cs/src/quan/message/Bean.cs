namespace quan.message
{
    public abstract class Bean
    {
        public byte[] Encode()
        {
            Buffer buffer = new Buffer();
            Encode(buffer);
            return buffer.RemainingBytes();
        }

        public virtual void Encode(Buffer buffer)
        {
        }

        public void Decode(byte[] bytes)
        {
            Buffer buffer = new Buffer(bytes);
            Decode(buffer);
        }

        public virtual void Decode(Buffer buffer)
        {
        }
    }
}