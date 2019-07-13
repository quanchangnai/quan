using System;
using System.Collections.Generic;

namespace quan.message.test
{
    public class SRoleLogin1 : Message
    {
        //角色id
        public long roleId { get; set; }

        //角色名
        private string _roleName = "";

        public string roleName
        {
            get => _roleName;
            set => _roleName = value ?? throw new NullReferenceException();
        }

        //角色信息
        private RoleInfo1 _roleInfo = new RoleInfo1();

        public RoleInfo1 roleInfo
        {
            get => _roleInfo;
            set => _roleInfo = value ?? throw new NullReferenceException();
        }

        //角色信息2
        public RoleInfo1 roleInfo2 { get; set; }

        public List<RoleInfo1> roleInfoList { get; } = new List<RoleInfo1>(); //角色信息

        public HashSet<RoleInfo1> roleInfoSet { get; } = new HashSet<RoleInfo1>(); //角色信息

        public Dictionary<long, RoleInfo1> roleInfoMap { get; } = new Dictionary<long, RoleInfo1>(); //角色信息

        public SRoleLogin1() : base(111)
        {
        }

        public override Message create()
        {
            return new SRoleLogin1();
        }


        public override void Encode(Buffer buffer)
        {
            base.Encode(buffer);
            buffer.WriteLong(roleId);
            buffer.WriteString(roleName);

            roleInfo.Encode(buffer);

            buffer.WriteBool(roleInfo2 != null);
            roleInfo2?.Encode(buffer);

            buffer.WriteInt(roleInfoList.Count);
            foreach (var _roleInfoList_Value in roleInfoList)
            {
                _roleInfoList_Value.Encode(buffer);
            }

            buffer.WriteInt(roleInfoSet.Count);
            foreach (var _roleInfoSet_Value in roleInfoSet)
            {
                _roleInfoSet_Value.Encode(buffer);
            }

            buffer.WriteInt(roleInfoMap.Count);
            foreach (var _roleInfoMap_Key in roleInfoMap.Keys)
            {
                buffer.WriteLong(_roleInfoMap_Key);
                roleInfoMap[_roleInfoMap_Key].Encode(buffer);
            }
        }

        public override void Decode(Buffer buffer)
        {
            base.Decode(buffer);
            roleId = buffer.ReadLong();
            roleName = buffer.ReadString();
            roleInfo.Decode(buffer);

            if (buffer.ReadBool())
            {
                if (roleInfo2 == null)
                {
                    roleInfo2 = new RoleInfo1();
                }

                roleInfo2.Decode(buffer);
            }

            int _roleInfoList_Size = buffer.ReadInt();
            for (int i = 0; i < _roleInfoList_Size; i++)
            {
                RoleInfo1 _roleInfoList_Value = new RoleInfo1();
                _roleInfoList_Value.Decode(buffer);
                roleInfoList.Add(_roleInfoList_Value);
            }

            int _roleInfoSet_Size = buffer.ReadInt();
            for (int i = 0; i < _roleInfoSet_Size; i++)
            {
                RoleInfo1 _roleInfoSet_Value = new RoleInfo1();
                _roleInfoSet_Value.Decode(buffer);
                roleInfoSet.Add(_roleInfoSet_Value);
            }

            int _roleInfoMap_Size = buffer.ReadInt();
            for (int i = 0; i < _roleInfoMap_Size; i++)
            {
                long _roleInfoMap_Key = buffer.ReadLong();
                RoleInfo1 _roleInfoMap_Value = new RoleInfo1();
                _roleInfoMap_Value.Decode(buffer);
                roleInfoMap.Add(_roleInfoMap_Key, _roleInfoMap_Value);
            }
        }

        public override string ToString()
        {
            return
                $"{nameof(roleId)}: {roleId}, {nameof(roleName)}: {roleName}, {nameof(roleInfo)}: {roleInfo}, {nameof(roleInfo2)}: {roleInfo2}, {nameof(roleInfoList)}: {roleInfoList}, {nameof(roleInfoSet)}: {roleInfoSet}, {nameof(roleInfoMap)}: {roleInfoMap}";
        }
    }
}