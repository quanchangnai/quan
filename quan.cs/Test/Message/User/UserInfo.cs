using Quan.Message;
using Quan.Utils;
using System;
using System.Collections.Generic;

namespace Test.Message.User
{
    /// <summary>
    /// 用户信息<br/>
    /// 代码自动生成，请勿手动修改
    /// </summary>
    public class UserInfo : Bean
    {
        /// <summary>
        /// ID
        /// </summary>
        public int id { get; set; }

        private string _name = "";

        /// <summary>
        /// 名字
        /// </summary>
        public string name
        {
            get => _name;
            set => _name = value ?? throw new NullReferenceException();
        }

        /// <summary>
        /// 等级
        /// </summary>
        public int level { get; set; }

        /// <summary>
        /// 类型
        /// </summary>
        public UserType type { get; set; }

        /// <summary>
        /// 角色信息
        /// </summary>
        public Test.Message.Role.RoleInfo roleInfo1 { get; set; }

        private RoleInfo _roleInfo2 = new RoleInfo();

        /// <summary>
        /// 角色信息2
        /// </summary>
        public RoleInfo roleInfo2
        {
            get => _roleInfo2;
            set => _roleInfo2 = value ?? throw new NullReferenceException();
        }

        private RoleInfo _roleInfo3 = new RoleInfo();

        /// <summary>
        /// 角色信息2
        /// </summary>
        public RoleInfo roleInfo3
        {
            get => _roleInfo3;
            set => _roleInfo3 = value ?? throw new NullReferenceException();
        }

        /// <summary>
        /// 角色信息List
        /// </summary>
        public IList<Test.Message.Role.RoleInfo> roleList { get; } = new List<Test.Message.Role.RoleInfo>();

        /// <summary>
        /// 角色信息Set
        /// </summary>
        public ISet<Test.Message.Role.RoleInfo> roleSet { get; } = new HashSet<Test.Message.Role.RoleInfo>();

        /// <summary>
        /// 角色信息Map
        /// </summary>
        public IDictionary<int, Test.Message.Role.RoleInfo> roleMap { get; } = new Dictionary<int, Test.Message.Role.RoleInfo>();

        private byte[] _f11 = Array.Empty<byte>();

        public byte[] f11
        {
            get => _f11;
            set => _f11 = value ?? throw new NullReferenceException();
        }

        public bool f12 { get; set; }

        public bool f13 { get; set; }

        public short f14 { get; set; }

        public float f15 { get; set; }

        private float _f16;

        public float f16
        {
            get => _f16;
            set
            {
                CodedBuffer.ValidateScale(value, 2);
                _f16 = value;
            }
        }

        public double f17 { get; set; }

        private double _f18;

        public double f18
        {
            get => _f18;
            set
            {
                CodedBuffer.ValidateScale(value, 2);
                _f18 = value;
            }
        }

        public string alias { get; set; }


        public override void Encode(CodedBuffer buffer)
        {
            base.Encode(buffer);

            Validate();

            if (id != 0)
            {
                WriteTag(buffer, 4);
                buffer.WriteInt(id);
            }

            if (name.Length > 0)
            {
                WriteTag(buffer, 11);
                buffer.WriteString(name);
            }

            if (level != 0)
            {
                WriteTag(buffer, 12);
                buffer.WriteInt(level);
            }

            if (type != 0)
            {
                WriteTag(buffer, 16);
                buffer.WriteInt((int) type);
            }

            if (roleInfo1 != null)
            {
                WriteTag(buffer, 23);
                var roleInfo1Buffer = new CodedBuffer();
                roleInfo1.Encode(roleInfo1Buffer);
                buffer.WriteBuffer(roleInfo1Buffer);
            }

            WriteTag(buffer, 27);
            var roleInfo2Buffer = new CodedBuffer();
            roleInfo2.Encode(roleInfo2Buffer);
            buffer.WriteBuffer(roleInfo2Buffer);

            WriteTag(buffer, 31);
            var roleInfo3Buffer = new CodedBuffer();
            roleInfo3.Encode(roleInfo3Buffer);
            buffer.WriteBuffer(roleInfo3Buffer);

            if (roleList.Count > 0)
            {
                WriteTag(buffer, 35);
                var roleListBuffer = new CodedBuffer();
                roleListBuffer.WriteInt(roleList.Count);
                foreach (var roleListValue in roleList)
                {
                    roleListValue.Encode(roleListBuffer);
                }
                buffer.WriteBuffer(roleListBuffer);
            }

            if (roleSet.Count > 0)
            {
                WriteTag(buffer, 39);
                var roleSetBuffer = new CodedBuffer();
                roleSetBuffer.WriteInt(roleSet.Count);
                foreach (var roleSetValue in roleSet)
                {
                    roleSetValue.Encode(roleSetBuffer);
                }
                buffer.WriteBuffer(roleSetBuffer);
            }

            if (roleMap.Count > 0)
            {
                WriteTag(buffer, 43);
                var roleMapBuffer = new CodedBuffer();
                roleMapBuffer.WriteInt(roleMap.Count);
                foreach (var roleMapKey in roleMap.Keys)
                {
                    roleMapBuffer.WriteInt(roleMapKey);
                    roleMap[roleMapKey].Encode(roleMapBuffer);
                }
                buffer.WriteBuffer(roleMapBuffer);
            }

            if (f11.Length > 0)
            {
                WriteTag(buffer, 47);
                buffer.WriteBytes(f11);
            }

            if (f12)
            {
                WriteTag(buffer, 48);
                buffer.WriteBool(f12);
            }

            if (f13)
            {
                WriteTag(buffer, 52);
                buffer.WriteBool(f13);
            }

            if (f14 != 0)
            {
                WriteTag(buffer, 56);
                buffer.WriteShort(f14);
            }

            if (f15 != 0)
            {
                WriteTag(buffer, 61);
                buffer.WriteFloat(f15);
            }

            if (f16 != 0)
            {
                WriteTag(buffer, 64);
                buffer.WriteFloat(f16, 2);
            }

            if (f17 != 0)
            {
                WriteTag(buffer, 70);
                buffer.WriteDouble(f17);
            }

            if (f18 != 0)
            {
                WriteTag(buffer, 72);
                buffer.WriteDouble(f18, 2);
            }

            if (alias != null)
            {
                WriteTag(buffer, 79);
                buffer.WriteString(alias);
            }

            WriteTag(buffer,0);
        }

        public override void Decode(CodedBuffer buffer)
        {
            base.Decode(buffer);

            for (var tag = ReadTag(buffer); tag != 0; tag = ReadTag(buffer))
            {
                switch (tag)
                {
                    case 4:
                        id = buffer.ReadInt();
                        break;
                    case 11:
                        name = buffer.ReadString();
                        break;
                    case 12:
                        level = buffer.ReadInt();
                        break;
                    case 16:
                        type = (UserType) buffer.ReadInt();
                        break;
                    case 23:
                        buffer.ReadInt();
                        roleInfo1 = roleInfo1 ?? new Test.Message.Role.RoleInfo();
                        roleInfo1.Decode(buffer);
                        break;
                    case 27:
                        buffer.ReadInt();
                        roleInfo2.Decode(buffer);
                        break;
                    case 31:
                        buffer.ReadInt();
                        roleInfo3.Decode(buffer);
                        break;
                    case 35:
                        buffer.ReadInt();
                        var roleList_Size = buffer.ReadInt();
                        for (var roleList_i = 0; roleList_i < roleList_Size; roleList_i++) 
                        {
                            var roleListValue = new Test.Message.Role.RoleInfo();
                            roleListValue.Decode(buffer);
                            roleList.Add(roleListValue);
                        }
                        break;
                    case 39:
                        buffer.ReadInt();
                        var roleSet_Size = buffer.ReadInt();
                        for (var roleSet_i = 0; roleSet_i < roleSet_Size; roleSet_i++) 
                        {
                            var roleSetValue = new Test.Message.Role.RoleInfo();
                            roleSetValue.Decode(buffer);
                            roleSet.Add(roleSetValue);
                        }
                        break;
                    case 43:
                        buffer.ReadInt();
                        var roleMap_Size = buffer.ReadInt();
                        for (var roleMap_i = 0; roleMap_i < roleMap_Size; roleMap_i++)
                        {
                            var roleMapKey = buffer.ReadInt();
                            var roleMapValue = new Test.Message.Role.RoleInfo();
                            roleMapValue.Decode(buffer);
                            roleMap.Add(roleMapKey, roleMapValue);
                        }
                        break;
                    case 47:
                        f11 = buffer.ReadBytes();
                        break;
                    case 48:
                        f12 = buffer.ReadBool();
                        break;
                    case 52:
                        f13 = buffer.ReadBool();
                        break;
                    case 56:
                        f14 = buffer.ReadShort();
                        break;
                    case 61:
                        f15 = buffer.ReadFloat();
                        break;
                    case 64:
                        f16 = buffer.ReadFloat(2);
                        break;
                    case 70:
                        f17 = buffer.ReadDouble();
                        break;
                    case 72:
                        f18 = buffer.ReadDouble(2);
                        break;
                    case 79:
                        alias = buffer.ReadString();
                        break;
                    default:
                        SkipField(tag, buffer);
                        break;
                }
            }

            Validate();
        }

        public override void Validate()
        {
            base.Validate();

            ValidateNull(name, "字段[name]");
            ValidateNull(roleInfo2, "字段[roleInfo2]");
            ValidateNull(roleInfo3, "字段[roleInfo3]");
            ValidateNull(f11, "字段[f11]");
            CodedBuffer.ValidateScale(f16, 2, "字段[f16]");
            CodedBuffer.ValidateScale(f18, 2, "字段[f18]");
        }

        public override string ToString()
        {
            return "UserInfo{" +
                   "id=" + id.ToString2() +
                   ",name='" + name + '\'' +
                   ",level=" + level.ToString2() +
                   ",type=" + type.ToString2() +
                   ",roleInfo1=" + roleInfo1.ToString2() +
                   ",roleInfo2=" + roleInfo2.ToString2() +
                   ",roleInfo3=" + roleInfo3.ToString2() +
                   ",roleList=" + roleList.ToString2() +
                   ",roleSet=" + roleSet.ToString2() +
                   ",roleMap=" + roleMap.ToString2() +
                   ",f11=" + f11.ToString2() +
                   ",f12=" + f12.ToString2() +
                   ",f13=" + f13.ToString2() +
                   ",f14=" + f14.ToString2() +
                   ",f15=" + f15.ToString2() +
                   ",f16=" + f16.ToString2() +
                   ",f17=" + f17.ToString2() +
                   ",f18=" + f18.ToString2() +
                   ",alias='" + alias + '\'' +
                   '}';
        }
    }
}