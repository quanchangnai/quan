using System;

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

        public virtual void Validate()
        {
        }

        public void WriteTag(CodedBuffer buffer, int tag)
        {
            buffer.WriteByte((byte)tag);
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

        protected static double ValidateRange(double value, double min, double max, string name = "参数")
        {
            if (value < min || value > max)
                throw new ArgumentException($"{name}({value})不在范围({min},{max})之中");
            return value;
        }

        protected static double ValidateMin(double value, double min, string name = "参数")
        {
            if (value < min)
                throw new ArgumentException($"{name}({value})不能小于({min})");
            return value;
        }

        protected static double ValidateMax(double value, double max, string name = "参数")
        {
            if (value > max)
                throw new ArgumentException($"{name}({value})不能大于({max})");
            return value;
        }

        protected static object ValidateNull(object value, string name = "参数")
        {
            if (value == null)
                throw new ArgumentException($"{name}不能为空");
            return value;
        }
    }
}