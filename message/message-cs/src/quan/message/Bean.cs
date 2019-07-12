namespace quan.message
{
    public abstract class Bean
    {
        public byte[] encode()
        {
            Buffer buffer = new Buffer();
            encode(buffer);
            return buffer.RemainingBytes();
        }

        public virtual void encode(Buffer buffer)
        {
        }

        public void decode(byte[] bytes)
        {
            Buffer buffer = new Buffer(bytes);
            decode(buffer);
        }

        public virtual void decode(Buffer buffer)
        {
        }
    }
}