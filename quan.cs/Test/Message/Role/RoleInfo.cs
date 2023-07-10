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

        public string alias { get; set; }

        public RoleType type { get; set; }

        public bool b { get; set; }

        public short s { get; set; }

        private int _i;

        public int i
        {
            get => _i;
            set
            {
                ValidateRange(value, 1, 20);
                _i = value;
            }
        }

        public double d { get; set; }

        private byte[] _bb1 = Array.Empty<byte>();

        public byte[] bb1
        {
            get => _bb1;
            set => _bb1 = value ?? throw new NullReferenceException();
        }

        public byte[] bb2 { get; set; }

        public IList<int> list { get; } = new List<int>();

        public ISet<int> set { get; } = new HashSet<int>();

        public IDictionary<int, int> map { get; } = new Dictionary<int, int>();


        public override void Encode(CodedBuffer buffer)
        {
            base.Encode(buffer);

            Validate();

            buffer.WriteInt(id);
            buffer.WriteString(name);

            buffer.WriteBool(alias != null);
            if (alias != null) 
            {
                buffer.WriteString(alias);
            }

            buffer.WriteInt((int) type);
            buffer.WriteBool(b);
            buffer.WriteShort(s);
            buffer.WriteInt(i);
            buffer.WriteDouble(d);
            buffer.WriteBytes(bb1);

            buffer.WriteBool(bb2 != null);
            if (bb2 != null) 
            {
                buffer.WriteBytes(bb2);
            }

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

            if (buffer.ReadBool()) 
            {
                alias = buffer.ReadString();
            }

            type = (RoleType) buffer.ReadInt();
            b = buffer.ReadBool();
            s = buffer.ReadShort();
            i = buffer.ReadInt();
            d = buffer.ReadDouble();
            bb1 = buffer.ReadBytes();

            if (buffer.ReadBool()) 
            {
                bb2 = buffer.ReadBytes();
            }

            var list_Size = buffer.ReadInt();
            for (var list_i = 0; list_i < list_Size; list_i++)
            {
                list.Add(buffer.ReadInt());
            }

            var set_Size = buffer.ReadInt();
            for (var set_i = 0; set_i < set_Size; set_i++)
            {
                set.Add(buffer.ReadInt());
            }

            Validate();
        }

        public override void Validate()
        {
            base.Validate();

            ValidateNull(name, "字段[name]");
            ValidateRange(i, 1, 20, "字段[i]");
            ValidateNull(bb1, "字段[bb1]");
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
                   ",bb1=" + bb1.ToString2() +
                   ",bb2=" + bb2.ToString2() +
                   ",list=" + list.ToString2() +
                   ",set=" + set.ToString2() +
                   ",map=" + map.ToString2() +
                   '}';
        }
    }
}