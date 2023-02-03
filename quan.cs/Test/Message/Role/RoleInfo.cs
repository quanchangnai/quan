using Quan.Message;
using Quan.Utils;
using System;
using System.Collections.Generic;

namespace Test.Message.Role
{
    /// <summary>
    /// 角色信息<br/>
    /// 代码自动生成，请勿手动修改
    /// </summary>
    public class RoleInfo : Bean
    {
        /// <summary>
        /// 角色id
        /// </summary>
        public int id { get; set; }

        private string _name = "";

        /// <summary>
        /// 角色名
        /// </summary>
        public string name
        {
            get => _name;
            set => _name = value ?? throw new NullReferenceException();
        }

        private string _alias;

        public string alias
        {
            get => _alias;
            set => _alias = value;
        }

        public RoleType type { get; set; }

        public bool b { get; set; }

        public short s { get; set; }

        private int _i;

        public int i
        {
            get => _i;
            set
            {
                CheckRange(value, 1, 20);
                _i = value;
            }
        }

        public double d { get; set; }

        private byte[] _data = Array.Empty<byte>();

        public byte[] data
        {
            get => _data;
            set => _data = value ?? throw new NullReferenceException();
        }

        public List<int> list { get; } = new List<int>();

        public HashSet<int> set { get; } = new HashSet<int>();

        public Dictionary<int, int> map { get; } = new Dictionary<int, int>();


        public override void Encode(CodedBuffer buffer)
        {
            base.Encode(buffer);

            buffer.WriteInt(id);
            buffer.WriteString(name);
            buffer.WriteString(alias);
            buffer.WriteInt((int) type);
            buffer.WriteBool(b);
            buffer.WriteShort(s);
            buffer.WriteInt(i);
            buffer.WriteDouble(d);
            buffer.WriteBytes(data);

            buffer.WriteInt(list.Count);
            foreach (var listValue in list)
            {
                buffer.WriteInt(listValue);
            }

            buffer.WriteInt(set.Count);
            foreach (var setValue in set)
            {
                buffer.WriteInt(setValue);
            }
        }

        public override void Decode(CodedBuffer buffer)
        {
            base.Decode(buffer);

            id = buffer.ReadInt();
            name = buffer.ReadString();
            alias = buffer.ReadString();
            type = (RoleType) buffer.ReadInt();
            b = buffer.ReadBool();
            s = buffer.ReadShort();
            i = buffer.ReadInt();
            d = buffer.ReadDouble();
            data = buffer.ReadBytes();

            var list_Size = buffer.ReadInt();
            for (var i = 0; i < list_Size; i++)
            {
                list.Add(buffer.ReadInt());
            }

            var set_Size = buffer.ReadInt();
            for (var i = 0; i < set_Size; i++)
            {
                set.Add(buffer.ReadInt());
            }
        }

        public override string ToString()
        {
            return "RoleInfo{" +
                   "id=" + id.ToString2() +
                   ",name='" + name + '\'' +
                   ",alias='" + alias + '\'' +
                   ",type=" + type.ToString2() +
                   ",b=" + b.ToString2() +
                   ",s=" + s.ToString2() +
                   ",i=" + i.ToString2() +
                   ",d=" + d.ToString2() +
                   ",data=" + data.ToString2() +
                   ",list=" + list.ToString2() +
                   ",set=" + set.ToString2() +
                   ",map=" + map.ToString2() +
                   '}';
        }
    }
}