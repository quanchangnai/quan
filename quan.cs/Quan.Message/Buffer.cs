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

        //下一个读的位置
        private int _readPos;

        //下一个写的位置
        private int _writePos;

        public Buffer() : this(128)
        {
        }

        public Buffer(int capacity)
        {
            _bytes = new byte[capacity];
        }

        public Buffer(byte[] bytes)
        {
            _bytes = bytes;
            _writePos = bytes.Length;
        }

        public int Capacity => _bytes.Length;

        /// <summary>
        ///重置读位置到0
        /// </summary>
        public void Reset()
        {
            _readPos = 0;
        }

        /// <summary>
        /// 清除数据，读写位置都置为0
        /// </summary>
        public void Clear()
        {
            _readPos = 0;
            _writePos = 0;
        }


        /// <summary>
        /// 当前剩余可读的字节数
        /// </summary>
        /// <returns></returns>
        public int ReadableCount => _writePos - _readPos;

        /// <summary>
        /// 读取当前剩余的字节数组<br/>
        /// </summary>
        /// <returns></returns>
        public byte[] RemainingBytes()
        {
            var remainingBytes = new byte[ReadableCount];
            Array.Copy(_bytes, _readPos, remainingBytes, 0, remainingBytes.Length);
            _readPos += remainingBytes.Length;
            return remainingBytes;
        }

        /// <summary>
        /// 丢弃已经读过的数据
        /// </summary>
        public void DiscardReadBytes()
        {
            var newBytes = new byte[Capacity - _readPos];
            Array.Copy(_bytes, _readPos, newBytes, 0, newBytes.Length);
            _bytes = newBytes;
            _writePos -= _readPos;
            _readPos = 0;
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
            var shift = 0;
            long temp = 0;

            while (shift < bits)
            {
                if (ReadableCount < 1)
                {
                    break;
                }

                var b = _bytes[_readPos++];
                temp |= (b & 0b1111111L) << shift;
                shift += 7;

                if ((b & 0b10000000) != 0)
                {
                    continue;
                }

                //ZigZag解码
                return (temp >> 1) ^ -(temp & 1);
            }

            throw new IOException("读数据出错");
        }

        public byte[] ReadBytes()
        {
            var length = ReadInt();
            if (length > ReadableCount)
            {
                throw new IOException("读数据出错");
            }

            var bytes = new byte[length];
            Array.Copy(_bytes, _readPos, bytes, 0, length);
            _readPos += length;
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
            var shift = 0;
            var temp = 0;

            while (shift < 32)
            {
                if (ReadableCount < 1)
                {
                    throw new IOException("读数据出错");
                }

                var b = _bytes[_readPos++];
                temp |= (b & 0b11111111) << shift;
                shift += 8;
            }

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
            var shift = 0;
            long temp = 0;

            while (shift < 64)
            {
                if (ReadableCount < 1)
                {
                    throw new IOException("读数据出错");
                }

                var b = _bytes[_readPos++];
                temp |= (b & 0b11111111L) << shift;
                shift += 8;
            }

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


        private void OnWrite(int writeCount)
        {
            var capacity = Capacity;
            if (_writePos + writeCount < capacity)
            {
                return;
            }

            var newCapacity = capacity;
            while (writeCount > 0)
            {
                newCapacity += capacity;
                writeCount -= capacity;
            }

            var newBytes = new byte[newCapacity];
            Array.Copy(_bytes, 0, newBytes, 0, capacity);
            _bytes = newBytes;
        }

        private void WriteVarInt(long n)
        {
            OnWrite(10);

            //ZigZag编码
            n = (n << 1) ^ (n >> 63);

            while (true)
            {
                if ((n & ~0b1111111) == 0)
                {
                    _bytes[_writePos++] = (byte) (n & 0b1111111);
                    return;
                }

                _bytes[_writePos++] = (byte) (n & 0b1111111 | 0b10000000);
                n >>= 7;
            }
        }

        public void WriteBytes(byte[] bytes)
        {
            OnWrite(10 + bytes.Length);
            WriteInt(bytes.Length);
            Array.Copy(bytes, 0, _bytes, _writePos, bytes.Length);
            _writePos += bytes.Length;
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
            OnWrite(4);

            var temp = BitConverter.ToInt32(BitConverter.GetBytes(n), 0);
            var shift = 0;

            while (shift < 32)
            {
                _bytes[_writePos++] = (byte) (temp >> shift & 0b11111111);
                shift += 8;
            }
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
            OnWrite(8);

            var temp = BitConverter.DoubleToInt64Bits(n);
            var shift = 0;

            while (shift < 64)
            {
                _bytes[_writePos++] = (byte) (temp >> shift & 0b11111111);
                shift += 8;
            }
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