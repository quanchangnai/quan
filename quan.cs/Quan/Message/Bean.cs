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

        protected static void CheckRange(double value, double min, double max)
        {
            if (value < min || value > max)
            {
                throw new ArgumentException($"参数${value}不在范围(${min},${max})之中");
            }
        }
        
        protected static void CheckMin(double value, double min)
        {
            if (value < min)
            {
                throw new ArgumentException($"参数${value}不能小于${min}");
            }
        }
        
        protected static void CheckMax(double value,  double max)
        {
            if ( value > max)
            {
                throw new ArgumentException($"参数${value}不能大于${max}");
            }
        }
    }
}