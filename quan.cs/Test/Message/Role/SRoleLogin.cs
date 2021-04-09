using Quan.Utils;
using Quan.Message;
using Buffer = Quan.Message.Buffer;
using System;
using System.Collections.Generic;
using Test.Message.Common;
using Test.Message.User;

namespace Test.Message.Role
{
	/// <summary>
	/// 角色登录，哈希生成ID<br/>
	/// 代码自动生成，请勿手动修改
	/// </summary>
    public class SRoleLogin : MessageHeader
    {
		/// <summary>
		/// 消息ID
		/// </summary>
		public override int Id => 763075;

        /// <summary>
		/// 角色id
		/// </summary>
		public long roleId { get; set; }

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
		public List<RoleInfo> roleInfoList { get; } = new List<RoleInfo>();

        /// <summary>
		/// 角色信息
		/// </summary>
		public HashSet<RoleInfo> roleInfoSet { get; } = new HashSet<RoleInfo>();

        /// <summary>
		/// 角色信息
		/// </summary>
		public Dictionary<long, RoleInfo> roleInfoMap { get; } = new Dictionary<long, RoleInfo>();

        /// <summary>
		/// 用户信息
		/// </summary>
		public UserInfo userInfo { get; set; }


		public override MessageBase Create()
		{
			return new SRoleLogin();
		}

		public override void Encode(Buffer buffer)
		{
	    	base.Encode(buffer);

		    buffer.WriteLong(roleId);
		    buffer.WriteString(roleName);
		    roleInfo.Encode(buffer);

		    buffer.WriteInt(roleInfoList.Count);
		    foreach (var roleInfoListValue in roleInfoList) {
				roleInfoListValue.Encode(buffer);
		    }

		    buffer.WriteInt(roleInfoSet.Count);
		    foreach (var roleInfoSetValue in roleInfoSet) {
				roleInfoSetValue.Encode(buffer);
		    }

		    buffer.WriteInt(roleInfoMap.Count);
		    foreach (var roleInfoMapKey in roleInfoMap.Keys) {
		        buffer.WriteLong(roleInfoMapKey);
			    roleInfoMap[roleInfoMapKey].Encode(buffer);
		    }

		    buffer.WriteBool(userInfo != null);
		    userInfo?.Encode(buffer);
		}

		public override void Decode(Buffer buffer)
		{
	    	base.Decode(buffer);

		    roleId = buffer.ReadLong();
		    roleName = buffer.ReadString();
		    roleInfo.Decode(buffer);

		    var roleInfoListSize = buffer.ReadInt();
		    for (var i = 0; i < roleInfoListSize; i++) 
			{
			    var roleInfoListValue = new RoleInfo();
			  	roleInfoListValue.Decode(buffer);
			    roleInfoList.Add(roleInfoListValue);
		    }

		    var roleInfoSetSize = buffer.ReadInt();
		    for (var i = 0; i < roleInfoSetSize; i++) 
			{
			    var roleInfoSetValue = new RoleInfo();
			  	roleInfoSetValue.Decode(buffer);
			    roleInfoSet.Add(roleInfoSetValue);
		    }

		    var roleInfoMapSize = buffer.ReadInt();
		    for (var i = 0; i < roleInfoMapSize; i++) 
			{
			    var roleInfoMapKey = buffer.ReadLong();
			    var roleInfoMapValue = new RoleInfo();
				roleInfoMapValue.Decode(buffer);
			    roleInfoMap.Add(roleInfoMapKey, roleInfoMapValue);
		    }

		    if (buffer.ReadBool()) {
		        if (userInfo == null)
				{
		            userInfo = new UserInfo();
		        }
		        userInfo.Decode(buffer);
            }
		}

		public override string ToString()
		{
			return "SRoleLogin{" +
				   "seq=" + seq.ToString2() +
				   ",error=" + error.ToString2() +
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