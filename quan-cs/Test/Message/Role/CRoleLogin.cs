using System;
using System.Collections.Generic;
using Quan.Common;
using Quan.Message;
using Buffer = Quan.Message.Buffer;
using Test.Message.User;

namespace Test.Message.Role
{
	/// <summary>
	/// 角色登录<br/>
	/// 自动生成
	/// </summary>
    public class CRoleLogin : MessageBase
    {
		/// <summary>
		/// 消息ID
		/// </summary>
		public const int ID = 544233;

        /// <summary>
		/// 角色id
		/// </summary>
		public long RoleId { get; set; }

		private string _roleName = "";

        /// <summary>
		/// 角色名
		/// </summary>
		public string RoleName
		{
	    	get => _roleName;
	    	set => _roleName = value ?? throw new NullReferenceException();
		}

		private RoleInfo _roleInfo = new RoleInfo();

        /// <summary>
		/// 角色信息
		/// </summary>
		public RoleInfo RoleInfo
		{
	    	get => _roleInfo;
	    	set => _roleInfo = value ?? throw new NullReferenceException();
		}

        /// <summary>
		/// 角色信息
		/// </summary>
		public List<RoleInfo> RoleInfoList { get; } = new List<RoleInfo>();

        /// <summary>
		/// 角色信息
		/// </summary>
		public HashSet<RoleInfo> RoleInfoSet { get; } = new HashSet<RoleInfo>();

        /// <summary>
		/// 角色信息
		/// </summary>
		public Dictionary<long, RoleInfo> RoleInfoMap { get; } = new Dictionary<long, RoleInfo>();

        /// <summary>
		/// 用户信息
		/// </summary>
		private UserInfo UserInfo { get; set; }


        public CRoleLogin(): base(ID)
		{
		}

		public override MessageBase Create()
		{
			return new CRoleLogin();
		}

		public override void Encode(Buffer buffer)
		{
	    	base.Encode(buffer);

		    buffer.WriteLong(RoleId);
		    buffer.WriteString(RoleName);
		    RoleInfo.Encode(buffer);

		    buffer.WriteInt(RoleInfoList.Count);
		    foreach (var roleInfoListValue in RoleInfoList) {
				roleInfoListValue.Encode(buffer);
		    }

		    buffer.WriteInt(RoleInfoSet.Count);
		    foreach (var roleInfoSetValue in RoleInfoSet) {
				roleInfoSetValue.Encode(buffer);
		    }

		    buffer.WriteInt(RoleInfoMap.Count);
		    foreach (var roleInfoMapKey in RoleInfoMap.Keys) {
		        buffer.WriteLong(roleInfoMapKey);
			    RoleInfoMap[roleInfoMapKey].Encode(buffer);
		    }

		    buffer.WriteBool(UserInfo != null);
		    UserInfo?.Encode(buffer);
		}

		public override void Decode(Buffer buffer)
		{
	    	base.Decode(buffer);

		    RoleId = buffer.ReadLong();
		    RoleName = buffer.ReadString();
		    RoleInfo.Decode(buffer);

		    var roleInfoListSize = buffer.ReadInt();
		    for (var i = 0; i < roleInfoListSize; i++) {
			    var roleInfoListValue = new RoleInfo();
			  	roleInfoListValue.Decode(buffer);
			    RoleInfoList.Add(roleInfoListValue);
		    }

		    var roleInfoSetSize = buffer.ReadInt();
		    for (var i = 0; i < roleInfoSetSize; i++) {
			    var roleInfoSetValue = new RoleInfo();
			  	roleInfoSetValue.Decode(buffer);
			    RoleInfoSet.Add(roleInfoSetValue);
		    }

		    var roleInfoMapSize = buffer.ReadInt();
		    for (var i = 0; i < roleInfoMapSize; i++) {
			    var roleInfoMapKey = buffer.ReadLong();
			    var roleInfoMapValue = new RoleInfo();
				roleInfoMapValue.Decode(buffer);
			    RoleInfoMap.Add(roleInfoMapKey, roleInfoMapValue);
		    }

		    if (buffer.ReadBool()) {
		        if (UserInfo == null) {
		            UserInfo = new UserInfo();
		        }
		        UserInfo.Decode(buffer);
            }
		}

		public override string ToString()
		{
			return "CRoleLogin{" +
					"roleId=" + RoleId.ToString2() +
					",roleName='" + RoleName + '\'' +
					",roleInfo=" + RoleInfo.ToString2() +
					",roleInfoList=" + RoleInfoList.ToString2() +
					",roleInfoSet=" + RoleInfoSet.ToString2() +
					",roleInfoMap=" + RoleInfoMap.ToString2() +
					",userInfo=" + UserInfo.ToString2() +
					'}';
		}
    }
}