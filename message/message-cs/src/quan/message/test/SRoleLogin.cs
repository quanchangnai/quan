using System;
using System.Collections.Generic;

namespace quan.message.test
{
    public class SRoleLogin : Message
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
        private RoleInfo _roleInfo = new RoleInfo();

        public RoleInfo roleInfo
        {
            get => _roleInfo;
            set => _roleInfo = value ?? throw new NullReferenceException();
        }

        //角色信息2
        public RoleInfo roleInfo2 { get; set; }

        public List<RoleInfo> roleInfoList { get; } = new List<RoleInfo>(); //角色信息

        public HashSet<RoleInfo> roleInfoSet { get; } = new HashSet<RoleInfo>(); //角色信息

        public Dictionary<long, RoleInfo> roleInfoMap { get; } = new Dictionary<long, RoleInfo>(); //角色信息

        public SRoleLogin() : base(111)
        {
        }

        public override Message create()
        {
            return new SRoleLogin();
        }


        public override void encode(Buffer buffer)
        {
            base.encode(buffer);
            buffer.WriteLong(roleId);
            buffer.WriteString(roleName);

            roleInfo.encode(buffer);

            buffer.WriteBool(roleInfo2 != null);
            roleInfo2?.encode(buffer);

            buffer.WriteInt(roleInfoList.Count);
            foreach (var _roleInfoList_Value in roleInfoList)
            {
                _roleInfoList_Value.encode(buffer);
            }

            buffer.WriteInt(roleInfoSet.Count);
            foreach (var _roleInfoSet_Value in roleInfoSet)
            {
                _roleInfoSet_Value.encode(buffer);
            }

            buffer.WriteInt(roleInfoMap.Count);
            foreach (var _roleInfoMap_Key in roleInfoMap.Keys)
            {
                buffer.WriteLong(_roleInfoMap_Key);
                roleInfoMap[_roleInfoMap_Key].encode(buffer);
            }
        }

        public override void decode(Buffer buffer)
        {
            base.decode(buffer);
            roleId = buffer.ReadLong();
            roleName = buffer.ReadString();
            roleInfo.decode(buffer);

            if (buffer.ReadBool())
            {
                if (roleInfo2 == null)
                {
                    roleInfo2 = new RoleInfo();
                }

                roleInfo2.decode(buffer);
            }

            int _roleInfoList_Size = buffer.ReadInt();
            for (int i = 0; i < _roleInfoList_Size; i++)
            {
                RoleInfo _roleInfoList_Value = new RoleInfo();
                _roleInfoList_Value.decode(buffer);
                roleInfoList.Add(_roleInfoList_Value);
            }

            int _roleInfoSet_Size = buffer.ReadInt();
            for (int i = 0; i < _roleInfoSet_Size; i++)
            {
                RoleInfo _roleInfoSet_Value = new RoleInfo();
                _roleInfoSet_Value.decode(buffer);
                roleInfoSet.Add(_roleInfoSet_Value);
            }

            int _roleInfoMap_Size = buffer.ReadInt();
            for (int i = 0; i < _roleInfoMap_Size; i++)
            {
                long _roleInfoMap_Key = buffer.ReadLong();
                RoleInfo _roleInfoMap_Value = new RoleInfo();
                _roleInfoMap_Value.decode(buffer);
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