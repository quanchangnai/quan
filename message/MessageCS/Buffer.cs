using System;
using System.IO;

namespace MessageCS
{
    /// <summary>
    /// 基于VarInt和ZigZag编码的缓冲区，字节顺序采用小端模式
    /// </summary>
    public class Buffer
    {
        /**
        * 字节缓冲区
        */
        private byte[] _bytes;

        //当前位置
        private int _position;

        //当前是在读数据还是在写数据，true：读，false：写
        private bool _reading;

        //结束位置，后面的是无效数据
        private int _end;

        public Buffer() : this(64)
        {
        }

        public Buffer(int capacity)
        {
            _bytes = new byte[capacity];
            _end = Capacity() - 1;
        }

        public Buffer(byte[] bytes)
        {
            _bytes = bytes;
            _position = bytes.Length - 1;
            _end = Capacity() - 1;
        }

        public int Capacity()
        {
            return _bytes.Length;
        }

        public void Reset()
        {
            _position = 0;
        }

        /// <summary>
        /// 当前可用的字节数组<br/>
        /// 当前正在读数据时：[0,end]<br/>
        /// 当前正在写数据时：[0,position)<br/>
        /// </summary>
        /// <returns></returns>
        public byte[] AvailableBytes()
        {
            var availableBytes = new byte[Available()];
            if (_reading)
            {
                Array.Copy(_bytes, _position, availableBytes, 0, availableBytes.Length);
            }
            else
            {
                Array.Copy(_bytes, 0, availableBytes, 0, availableBytes.Length);
            }

            return availableBytes;
        }

        /// <summary>
        /// 当前可用的字节数
        /// </summary>
        /// <returns></returns>
        public int Available()
        {
            if (_reading)
            {
                return _end + 1;
            }

            return _position;
        }

        /// <summary>
        /// 当前剩余可用的字节数组<br/>
        /// 当前正在读数据时：[position,end]<br/>
        /// 当前正在写数据时：[0,position)<br/>
        /// </summary>
        /// <returns></returns>
        public byte[] RemainingBytes()
        {
            var remainingBytes = new byte[Remaining()];
            if (_reading)
            {
                Array.Copy(_bytes, _position, remainingBytes, 0, remainingBytes.Length);
            }
            else
            {
                Array.Copy(_bytes, 0, remainingBytes, 0, remainingBytes.Length);
            }

            return remainingBytes;
        }

        /// <summary>
        /// 当前剩余可用的字节数
        /// </summary>
        /// <returns></returns>
        public int Remaining()
        {
            if (_reading)
            {
                return _end - _position + 1;
            }

            return _position;
        }

        /// <summary>
        /// 读取VarInt，bits只能是[8,16,32,64]中的一种
        /// </summary>
        /// <param name="bits"></param>
        /// <returns></returns>
        /// <exception cref="SystemException"></exception>
        /// <exception cref="IOException"></exception>
        private long ReadVarInt(int bits)
        {
            if (bits != 16 && bits != 32 && bits != 64)
            {
                throw new SystemException("参数bits限定取值范围[16,32,64],实际值：" + bits);
            }

            var position = _reading ? _position : 0;
            var shift = 0;
            long temp = 0;
            while (shift < bits)
            {
                var b = _bytes[position++];
                temp |= (b & 0b1111111L) << shift;
                if ((b & 0b10000000) == 0)
                {
                    if (!_reading)
                    {
                        _reading = true;
                        _end = _position - 1;
                    }

                    _position = position;
                    //ZigZag解码
                    return (temp >> 1) ^ -(temp & 1);
                }

                shift += 7;
            }

            throw new IOException("读取数据异常，编码错误");
        }

        public byte[] ReadBytes()
        {
            var length = ReadInt();
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
            var position = _reading ? _position : 0;
            var shift = 0;
            var temp = 0;
            while (shift < 32)
            {
                var b = _bytes[position++];
                temp |= (b & 0b11111111) << shift;
                shift += 8;
            }

            if (!_reading)
            {
                _reading = true;
                _end = _position - 1;
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

            return (float) ReadDouble(scale);
        }

        public double ReadDouble()
        {
            var position = _reading ? _position : 0;
            var shift = 0;
            long temp = 0;

            while (shift < 64)
            {
                byte b = _bytes[position++];
                temp |= (b & 0b11111111L) << shift;
                shift += 8;
            }

            if (!_reading)
            {
                _reading = true;
                _end = _position - 1;
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
            var capacity = Capacity();
            var position = _reading ? 0 : _position;
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

            if (!_reading)
            {
                _end = capacity - 1;
            }
        }

        private void WriteVarInt(long n)
        {
            CheckCapacity(10);
            //ZigZag编码
            n = (n << 1) ^ (n >> 63);

            var position = _reading ? 0 : _position;
            while (true)
            {
                if ((n & ~0b1111111) == 0)
                {
                    _bytes[position++] = (byte) (n & 0b1111111);
                    if (_reading)
                    {
                        _reading = false;
                        _end = Capacity() - 1;
                    }

                    _position = position;
                    return;
                }

                _bytes[position++] = (byte) (n & 0b1111111 | 0b10000000);
                n >>= 7;
            }
        }

        public void WriteBytes(byte[] bytes)
        {
            CheckCapacity(10 + bytes.Length);
            WriteInt(bytes.Length);
            Array.Copy(bytes, 0, _bytes, _position, bytes.Length);
            _position += bytes.Length;
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
            var position = _reading ? 0 : _position;
            var temp = BitConverter.ToInt32(BitConverter.GetBytes(n), 0);
            var shift = 0;
            while (shift < 32)
            {
                _bytes[position++] = (byte) (temp >> shift & 0b11111111);
                shift += 8;
            }

            if (_reading)
            {
                _reading = false;
                _end = Capacity() - 1;
            }

            _position = position;
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
            var position = _reading ? 0 : _position;
            var temp = BitConverter.DoubleToInt64Bits(n);
            var shift = 0;
            while (shift < 64)
            {
                _bytes[position++] = (byte) (temp >> shift & 0b11111111);
                shift += 8;
            }

            if (_reading)
            {
                _reading = false;
                _end = Capacity() - 1;
            }

            _position = position;
        }

        public void WriteDouble(double n, int scale)
        {
            if (scale < 0)
            {
                WriteDouble(n);
                return;
            }

            n = Math.Round(n, scale);
            var times = (int) Math.Pow(10, scale);
            var threshold = long.MaxValue / times;
            if (n >= -threshold && n <= threshold)
            {
                WriteLong((long) Math.Floor(n * times));
                return;
            }

            throw new SystemException("参数n超出了限定范围[" + -threshold + "," + threshold + "]，无法转换为指定精度的定点型数据");
        }

        public void WriteString(string s)
        {
            WriteBytes(System.Text.Encoding.UTF8.GetBytes(s));
        }
    }
}