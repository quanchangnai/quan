using System;
using System.IO;

namespace quan.message
{
    /// <summary>
    /// 缓冲区，使用了VarInt和ZigZag编码
    /// </summary>
    public class Buffer
    {
        /**
        * 字节缓冲区
        */
        private byte[] bytes;

        //当前位置
        private int position;

        //当前是在读数据还是在写数据，true：读，false：写
        private bool reading;

        //结束位置，后面的是无效数据
        private int end;

        public Buffer() : this(64)
        {
        }

        public Buffer(int capacity)
        {
            bytes = new byte[capacity];
            end = Capacity() - 1;
        }

        public Buffer(byte[] bytes)
        {
            this.bytes = bytes;
            position = bytes.Length - 1;
            end = Capacity() - 1;
        }

        public int Capacity()
        {
            return bytes.Length;
        }

        public void Reset()
        {
            position = 0;
        }

        /// <summary>
        /// 当前可用的字节数组<br/>
        /// 当前正在读数据时：[0,end]<br/>
        /// 当前正在写数据时：[0,position)<br/>
        /// </summary>
        /// <returns></returns>
        public byte[] AvailableBytes()
        {
            byte[] availableBytes = new byte[Available()];
            if (reading)
            {
                Array.Copy(bytes, position, availableBytes, 0, availableBytes.Length);
            }
            else
            {
                Array.Copy(bytes, 0, availableBytes, 0, availableBytes.Length);
            }

            return availableBytes;
        }

        /// <summary>
        /// 当前可用的字节数
        /// </summary>
        /// <returns></returns>
        public int Available()
        {
            if (reading)
            {
                return end + 1;
            }
            else
            {
                return position;
            }
        }

        /// <summary>
        /// 当前剩余可用的字节数组<br/>
        /// 当前正在读数据时：[position,end]<br/>
        /// 当前正在写数据时：[0,position)<br/>
        /// </summary>
        /// <returns></returns>
        public byte[] RemainingBytes()
        {
            byte[] remainingBytes = new byte[Remaining()];
            if (reading)
            {
                Array.Copy(this.bytes, position, remainingBytes, 0, remainingBytes.Length);
            }
            else
            {
                Array.Copy(this.bytes, 0, remainingBytes, 0, remainingBytes.Length);
            }

            return remainingBytes;
        }

        /// <summary>
        /// 当前剩余可用的字节数
        /// </summary>
        /// <returns></returns>
        public int Remaining()
        {
            if (reading)
            {
                return end - position + 1;
            }
            else
            {
                return position;
            }
        }

        /// <summary>
        /// 读取VarInt，bits只能是[8,16,32,64]中的一种
        /// </summary>
        /// <param name="bits"></param>
        /// <returns></returns>
        /// <exception cref="SystemException"></exception>
        /// <exception cref="IOException"></exception>
        protected long ReadVarInt(int bits)
        {
            if (bits != 8 && bits != 16 && bits != 32 && bits != 64)
            {
                throw new SystemException("参数bits限定取值范围[8,16,32,64],实际值：" + bits);
            }

            int position = reading ? this.position : 0;
            int shift = 0;
            long temp = 0;
            while (shift < bits)
            {
                byte b = bytes[position++];
                temp |= (b & 0b1111111L) << shift;
                if ((b & 0b10000000) == 0)
                {
                    if (!reading)
                    {
                        reading = true;
                        end = this.position - 1;
                    }

                    this.position = position;
                    //ZigZag解码
                    return (temp >> 1) ^ -(temp & 1);
                }

                shift += 7;
            }

            throw new IOException("读取数据异常，编码错误");
        }

        public byte[] ReadBytes()
        {
            int length = ReadInt();
            byte[] bytes = new byte[length];
            Array.Copy(this.bytes, position, bytes, 0, length);
            position += length;
            return bytes;
        }

        public bool ReadBool()
        {
            return ReadVarInt(8) != 0;
        }

        public byte ReadByte()
        {
            return (byte) ReadVarInt(8);
        }

        public short ReadShort()
        {
            return (short) ReadVarInt(16);
        }

        public int ReadInt()
        {
            return (int) ReadVarInt(32);
        }

        public long ReadLong()
        {
            return ReadVarInt(64);
        }

        public float ReadFloat()
        {
            int position = reading ? this.position : 0;
            int shift = 0;
            int temp = 0;
            while (shift < 32)
            {
                byte b = bytes[position++];
                temp |= (b & 0b11111111) << shift;
                shift += 8;
            }

            if (!reading)
            {
                reading = true;
                end = this.position - 1;
            }

            this.position = position;

            return BitConverter.ToSingle(BitConverter.GetBytes(temp), 0);
        }

        public float ReadFloat(int scale)
        {
            if (scale < 0)
            {
                return ReadFloat();
            }
            else
            {
                return (float) ReadDouble(scale);
            }
        }

        public double ReadDouble()
        {
            int position = reading ? this.position : 0;
            int shift = 0;
            long temp = 0;

            while (shift < 64)
            {
                byte b = bytes[position++];
                temp |= (b & 0b11111111L) << shift;
                shift += 8;
            }

            if (!reading)
            {
                reading = true;
                end = this.position - 1;
            }

            this.position = position;

            return BitConverter.Int64BitsToDouble(temp);
        }

        public double ReadDouble(int scale)
        {
            if (scale < 0)
            {
                return ReadDouble();
            }
            else
            {
                return ReadLong() / Math.Pow(10, scale);
            }
        }

        public string ReadString()
        {
            return System.Text.Encoding.UTF8.GetString(ReadBytes());
        }


        private void CheckCapacity(int minAddValue)
        {
            int capacity = Capacity();
            int position = reading ? 0 : this.position;
            if (position + minAddValue < capacity)
            {
                return;
            }

            int newCapacity = capacity;
            while (minAddValue > 0)
            {
                newCapacity += capacity;
                minAddValue -= capacity;
            }

            byte[] newBytes = new byte[newCapacity];
            Array.Copy(this.bytes, 0, newBytes, 0, capacity);
            bytes = newBytes;
            if (!reading)
            {
                end = capacity - 1;
            }
        }

        protected void WriteVarInt(long n)
        {
            CheckCapacity(10);
            //ZigZag编码
            n = (n << 1) ^ (n >> 63);

            int position = reading ? 0 : this.position;
            while (true)
            {
                if ((n & ~0b1111111) == 0)
                {
                    bytes[position++] = (byte) (n & 0b1111111);
                    if (reading)
                    {
                        reading = false;
                        end = Capacity() - 1;
                    }

                    this.position = position;
                    return;
                }
                else
                {
                    bytes[position++] = (byte) (n & 0b1111111 | 0b10000000);
                    n >>= 7;
                }
            }
        }

        public void WriteBytes(byte[] bytes)
        {
            CheckCapacity(10 + bytes.Length);
            WriteVarInt(bytes.Length);
            Array.Copy(bytes, 0, this.bytes, position, bytes.Length);
            position += bytes.Length;
        }

        public void WriteBool(bool b)
        {
            WriteVarInt(b ? 1 : 0);
        }

        public void WriteByte(byte n)
        {
            WriteVarInt(n);
        }

        public void WriteShort(short n)
        {
            WriteVarInt(n);
        }

        public void WriteInt(int n)
        {
            WriteVarInt(n);
        }

        public void WriteLong(long n)
        {
            WriteVarInt(n);
        }

        public void WriteFloat(float n)
        {
            CheckCapacity(4);
            int position = reading ? 0 : this.position;
            int temp = BitConverter.ToInt32(BitConverter.GetBytes(n), 0);
            int shift = 0;
            while (shift < 32)
            {
                bytes[position++] = (byte) (temp >> shift & 0b11111111);
                shift += 8;
            }

            if (reading)
            {
                reading = false;
                end = Capacity() - 1;
            }

            this.position = position;
        }

        public void WriteFloat(float n, int scale)
        {
            if (scale < 0)
            {
                WriteFloat(n);
            }
            else
            {
                WriteDouble(n, scale);
            }
        }

        public void WriteDouble(double n)
        {
            CheckCapacity(8);
            int position = reading ? 0 : this.position;
            long temp = BitConverter.DoubleToInt64Bits(n);
            int shift = 0;
            while (shift < 64)
            {
                bytes[position++] = (byte) (temp >> shift & 0b11111111);
                shift += 8;
            }

            if (reading)
            {
                reading = false;
                end = Capacity() - 1;
            }

            this.position = position;
        }

        public void WriteDouble(double n, int scale)
        {
            if (scale < 0)
            {
                WriteDouble(n);
            }
            else
            {
                n = Math.Round(n, scale);
                int times = (int) Math.Pow(10, scale);
                long threshold = long.MaxValue / times;
                if (n >= -threshold && n <= threshold)
                {
                    WriteLong((long) Math.Floor(n * times));
                }
                else
                {
                    throw new SystemException("参数n超出了限定范围[" + -threshold + "," + threshold + "]，无法转换为指定精度的定点型数据");
                }
            }
        }

        public void WriteString(string s)
        {
            WriteBytes(System.Text.Encoding.UTF8.GetBytes(s));
        }
    }
}