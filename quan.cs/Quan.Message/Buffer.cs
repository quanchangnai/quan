using System;
using System.IO;

namespace Quan.Message
{
    /// <summary>
    /// 基于VarInt和ZigZag编码的字节缓冲区，字节顺序采用小端模式
    /// </summary>
    public class Buffer
    {
        /**
        * 字节缓冲区
        */
        private byte[] _bytes;

        //下一个读或写的位置
        private int _position;

        //当前是在读数据还是在写数据
        public bool Reading { get; private set; }

        //结束位置，后面的是无效数据
        private int _end;

        public Buffer() : this(64)
        {
        }

        public Buffer(int capacity)
        {
            _bytes = new byte[capacity];
            _end = -1;
        }

        public Buffer(byte[] bytes)
        {
            _bytes = bytes;
            _position = bytes.Length - 1;
            _end = _position;
        }

        public int Capacity => _bytes.Length;

        public void Reset()
        {
            _position = 0;
            if (!Reading)
            {
                _end = _position - 1;
            }
        }


        /// <summary>
        /// 当前可用的字节数
        /// </summary>
        /// <returns></returns>
        public int Available => Reading ? _end + 1 : _position;

        /// <summary>
        /// 当前可用的字节数组<br/>
        /// 当前正在读数据时：[0,end]<br/>
        /// 当前正在写数据时：[0,position)<br/>
        /// </summary>
        /// <returns></returns>
        public byte[] AvailableBytes()
        {
            var availableBytes = new byte[Available];
            Array.Copy(_bytes, Reading ? _position : 0, availableBytes, 0, availableBytes.Length);
            return availableBytes;
        }

        /// <summary>
        /// 当前剩余可用的字节数
        /// </summary>
        /// <returns></returns>
        public int Remaining => Reading ? _end - _position + 1 : _position;

        /// <summary>
        /// 当前剩余可用的字节数组<br/>
        /// 当前正在读数据时：[position,end]<br/>
        /// 当前正在写数据时：[0,position)<br/>
        /// </summary>
        /// <returns></returns>
        public byte[] RemainingBytes()
        {
            var remainingBytes = new byte[Remaining];
            Array.Copy(_bytes, Reading ? _position : 0, remainingBytes, 0, remainingBytes.Length);
            return remainingBytes;
        }

        /// <summary>
        /// 读取VarInt
        /// </summary>
        /// <param name="bits">读取的最大比特位,只能是[8,16,32,64]中的一种</param>
        /// <returns></returns>
        /// <exception cref="ArgumentException"></exception>
        /// <exception cref="IOException"></exception>
        private long ReadVarInt(int bits)
        {
            if (bits != 16 && bits != 32 && bits != 64)
            {
                throw new ArgumentException("参数bits限定取值范围[16,32,64],实际值：" + bits);
            }

            var position = Reading ? _position : 0;
            Reading = true;
            var shift = 0;
            long temp = 0;

            while (shift < bits)
            {
                if (position >= _bytes.Length)
                {
                    break;
                }

                var b = _bytes[position++];
                temp |= (b & 0b1111111L) << shift;
                shift += 7;

                if ((b & 0b10000000) != 0)
                {
                    continue;
                }

                _position = position;
                //ZigZag解码
                return (temp >> 1) ^ -(temp & 1);
            }

            throw new IOException("读数据出错");
        }

        public byte[] ReadBytes()
        {
            var length = ReadInt();
            if (length > Remaining)
            {
                throw new IOException("读数据出错");
            }

            var bytes = new byte[length];
            Array.Copy(_bytes, _position, bytes, 0, length);
            _position += length;
            return bytes;
        }

        public bool ReadBool()
        {
            return ReadInt() != 0;
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
            var position = Reading ? _position : 0;
            Reading = true;
            var shift = 0;
            var temp = 0;

            while (shift < 32)
            {
                if (position >= _bytes.Length)
                {
                    throw new IOException("读数据出错");
                }

                var b = _bytes[position++];
                temp |= (b & 0b11111111) << shift;
                shift += 8;
            }

            _position = position;
            return BitConverter.ToSingle(BitConverter.GetBytes(temp), 0);
        }

        public float ReadFloat(int scale)
        {
            if (scale < 0)
            {
                return ReadFloat();
            }

            return (float) (ReadLong() / Math.Pow(10, scale));
        }

        public double ReadDouble()
        {
            var position = Reading ? _position : 0;
            Reading = true;
            var shift = 0;
            long temp = 0;

            while (shift < 64)
            {
                if (position >= _bytes.Length)
                {
                    throw new IOException("读数据出错");
                }

                var b = _bytes[position++];
                temp |= (b & 0b11111111L) << shift;
                shift += 8;
            }

            _position = position;
            return BitConverter.Int64BitsToDouble(temp);
        }

        public double ReadDouble(int scale)
        {
            if (scale < 0)
            {
                return ReadDouble();
            }

            return ReadLong() / Math.Pow(10, scale);
        }

        public string ReadString()
        {
            return System.Text.Encoding.UTF8.GetString(ReadBytes());
        }


        private void CheckCapacity(int minAddValue)
        {
            var capacity = Capacity;
            var position = Reading ? 0 : _position;
            if (position + minAddValue < capacity)
            {
                return;
            }

            var newCapacity = capacity;
            while (minAddValue > 0)
            {
                newCapacity += capacity;
                minAddValue -= capacity;
            }

            var newBytes = new byte[newCapacity];
            Array.Copy(_bytes, 0, newBytes, 0, capacity);
            _bytes = newBytes;
        }

        private void WriteVarInt(long n)
        {
            CheckCapacity(10);

            var position = Reading ? 0 : _position;
            var end = _end;
            Reading = false;
            //ZigZag编码
            n = (n << 1) ^ (n >> 63);

            while (true)
            {
                if ((n & ~0b1111111) == 0)
                {
                    _bytes[position++] = (byte) (n & 0b1111111);
                    _position = position;
                    _end = ++end;
                    return;
                }

                _bytes[position++] = (byte) (n & 0b1111111 | 0b10000000);
                n >>= 7;
                end++;
            }
        }

        public void WriteBytes(byte[] bytes)
        {
            CheckCapacity(10 + bytes.Length);
            WriteInt(bytes.Length);
            Array.Copy(bytes, 0, _bytes, _position, bytes.Length);
            _position += bytes.Length;
            _end += bytes.Length;
        }

        public void WriteBool(bool b)
        {
            WriteInt(b ? 1 : 0);
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

            var position = Reading ? 0 : _position;
            var end = _end;
            Reading = false;

            var temp = BitConverter.ToInt32(BitConverter.GetBytes(n), 0);
            var shift = 0;

            while (shift < 32)
            {
                _bytes[position++] = (byte) (temp >> shift & 0b11111111);
                shift += 8;
                end++;
            }

            _position = position;
            _end = end;
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

            var position = Reading ? 0 : _position;
            var end = _end;
            Reading = false;

            var temp = BitConverter.DoubleToInt64Bits(n);
            var shift = 0;

            while (shift < 64)
            {
                _bytes[position++] = (byte) (temp >> shift & 0b11111111);
                shift += 8;
                end++;
            }

            _position = position;
            _end = end;
        }

        public static long CheckScale(double n, int scale, bool encode)
        {
            var times = (int) Math.Pow(10, scale);
            var threshold = long.MaxValue / times;
            if (n >= -threshold && n <= threshold)
            {
                return (long) Math.Floor(n * times);
            }

            var error = $"参数[{n}]超出了限定范围[{-threshold},{threshold}],无法转换为指定精度[{scale}]的定点型数据";
            if (encode)
            {
                throw new IOException(error);
            }

            throw new ArgumentException(error);
        }

        public void WriteDouble(double n, int scale)
        {
            if (scale < 0)
            {
                WriteDouble(n);
            }
            else
            {
                WriteLong(CheckScale(n, scale, true));
            }
        }

        public void WriteString(string s)
        {
            WriteBytes(System.Text.Encoding.UTF8.GetBytes(s));
        }
    }
}