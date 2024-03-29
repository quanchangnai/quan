using Quan.Message;
using Quan.Utils;
using System;
using System.Collections.Generic;
using Test.Message.User;

namespace Test.Message.Role
{
    /// <summary>
    /// 角色登录，哈希生成ID<br/>
    /// 代码自动生成，请勿手动修改
    /// </summary>
    public class SRoleLogin : MessageBase
    {
        /// <summary>
        /// 消息ID
        /// </summary>
        public override int Id => 763075;

        /// <summary>
        /// 角色id
        /// </summary>
        public int roleId { get; set; }

        private string _roleName = "";

        /// <summary>
        /// 角色名
        /// </summary>
        public string roleName
        {
            get => _roleName;
            set => _roleName = value ?? throw new NullReferenceException();
        }

        private RoleInfo _roleInfo = new RoleInfo();

        /// <summary>
        /// 角色信息
        /// </summary>
        public RoleInfo roleInfo
        {
            get => _roleInfo;
            set => _roleInfo = value ?? throw new NullReferenceException();
        }

        /// <summary>
        /// 角色信息
        /// </summary>
        public IList<RoleInfo> roleInfoList { get; } = new List<RoleInfo>();

        /// <summary>
        /// 角色信息
        /// </summary>
        public ISet<RoleInfo> roleInfoSet { get; } = new HashSet<RoleInfo>();

        /// <summary>
        /// 角色信息
        /// </summary>
        public IDictionary<int, RoleInfo> roleInfoMap { get; } = new Dictionary<int, RoleInfo>();

        /// <summary>
        /// 用户信息
        /// </summary>
        public UserInfo userInfo { get; set; }


        public override MessageBase Create()
        {
            return new SRoleLogin();
        }

        public override void Encode(CodedBuffer buffer)
        {
            base.Encode(buffer);

            Validate();

            buffer.WriteInt(roleId);
            buffer.WriteString(roleName);
            roleInfo.Encode(buffer);

            buffer.WriteInt(roleInfoList.Count);
            foreach (var roleInfoListValue in roleInfoList)
            {
                roleInfoListValue.Encode(buffer);
            }

            buffer.WriteInt(roleInfoSet.Count);
            foreach (var roleInfoSetValue in roleInfoSet)
            {
                roleInfoSetValue.Encode(buffer);
            }

            buffer.WriteInt(roleInfoMap.Count);
            foreach (var roleInfoMapKey in roleInfoMap.Keys)
            {
                buffer.WriteInt(roleInfoMapKey);
                roleInfoMap[roleInfoMapKey].Encode(buffer);
            }

            buffer.WriteBool(userInfo != null);
            if (userInfo != null) 
            {
                userInfo.Encode(buffer);
            }
        }

        public override void Decode(CodedBuffer buffer)
        {
            base.Decode(buffer);

            roleId = buffer.ReadInt();
            roleName = buffer.ReadString();
            roleInfo.Decode(buffer);

            var roleInfoList_Size = buffer.ReadInt();
            for (var roleInfoList_i = 0; roleInfoList_i < roleInfoList_Size; roleInfoList_i++)
            {
                var roleInfoListValue = new RoleInfo();
                roleInfoListValue.Decode(buffer);
                roleInfoList.Add(roleInfoListValue);
            }

            var roleInfoSet_Size = buffer.ReadInt();
            for (var roleInfoSet_i = 0; roleInfoSet_i < roleInfoSet_Size; roleInfoSet_i++)
            {
                var roleInfoSetValue = new RoleInfo();
                roleInfoSetValue.Decode(buffer);
                roleInfoSet.Add(roleInfoSetValue);
            }

            var roleInfoMap_Size = buffer.ReadInt();
            for (var roleInfoMap_i = 0; roleInfoMap_i < roleInfoMap_Size; roleInfoMap_i++)
            {
                var roleInfoMapKey = buffer.ReadInt();
                var roleInfoMapValue = new RoleInfo();
                roleInfoMapValue.Decode(buffer);
                roleInfoMap.Add(roleInfoMapKey, roleInfoMapValue);
            }

            if (buffer.ReadBool()) 
            {
                userInfo = userInfo ?? new UserInfo();
                userInfo.Decode(buffer);
            }

            Validate();
        }

        public override void Validate()
        {
            base.Validate();

            ValidateNull(roleName, "字段[roleName]");
            ValidateNull(roleInfo, "字段[roleInfo]");
        }

        public override string ToString()
        {
            return "SRoleLogin{" +
                   "_id=" + Id +
                   ",roleId=" + roleId.ToString2() +
                   ",roleName='" + roleName + '\'' +
                   ",roleInfo=" + roleInfo.ToString2() +
                   ",roleInfoList=" + roleInfoList.ToString2() +
                   ",roleInfoSet=" + roleInfoSet.ToString2() +
                   ",roleInfoMap=" + roleInfoMap.ToString2() +
                   ",userInfo=" + userInfo.ToString2() +
                   '}';
        }
    }
}