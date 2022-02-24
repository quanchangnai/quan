using Quan.Utils;
using Quan.Message;
using Buffer = Quan.Message.Buffer;
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

        public RoleType type { get; set; }

        public bool b { get; set; }

        public short s { get; set; }

        public int i { get; set; }

        public double d { get; set; }

        private byte[] _data = new byte[0];

        public byte[] data
        {
            get => _data;
            set => _data = value ?? throw new NullReferenceException();
        }

        public List<int> list { get; } = new List<int>();

        public HashSet<int> set { get; } = new HashSet<int>();

        public Dictionary<int, int> map { get; } = new Dictionary<int, int>();


        public override void Encode(Buffer buffer)
        {
            base.Encode(buffer);

            buffer.WriteInt(id);
            buffer.WriteString(name);
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

        public override void Decode(Buffer buffer)
        {
            base.Decode(buffer);

            id = buffer.ReadInt();
            name = buffer.ReadString();
            type = (RoleType) buffer.ReadInt();
            b = buffer.ReadBool();
            s = buffer.ReadShort();
            i = buffer.ReadInt();
            d = buffer.ReadDouble();
            data = buffer.ReadBytes();

            var listSize = buffer.ReadInt();
            for (var i = 0; i < listSize; i++)
            {
                list.Add(buffer.ReadInt());
            }

            var setSize = buffer.ReadInt();
            for (var i = 0; i < setSize; i++)
            {
                set.Add(buffer.ReadInt());
            }
        }

        public override string ToString()
        {
            return "RoleInfo{" +
                   "id=" + id.ToString2() +
                   ",name='" + name + '\'' +
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