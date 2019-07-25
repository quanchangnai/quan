using System;
using System.Collections.Generic;
using MessageCS;
using Buffer = MessageCS.Buffer;
using MessageCS.Test.User;

namespace MessageCS.Test.Role
{
	/// <summary>
	/// 角色登录<br/>
	/// Created by 自动生成
	/// </summary>
    public class SRoleLogin : Message
    {
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
		private UserInfo userInfo { get; set; }


		public SRoleLogin(): base(222)
		{
		}

		public override Message Create()
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
		    foreach (var roleInfoList_Value in roleInfoList) {
				roleInfoList_Value.Encode(buffer);
		    }

		    buffer.WriteInt(roleInfoSet.Count);
		    foreach (var roleInfoSet_Value in roleInfoSet) {
				roleInfoSet_Value.Encode(buffer);
		    }

		    buffer.WriteInt(roleInfoMap.Count);
		    foreach (var roleInfoMap_Key in roleInfoMap.Keys) {
		        buffer.WriteLong(roleInfoMap_Key);
			    roleInfoMap[roleInfoMap_Key].Encode(buffer);
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

		    var roleInfoList_Size = buffer.ReadInt();
		    for (var i = 0; i < roleInfoList_Size; i++) {
			    var roleInfoList_Value = new RoleInfo();
			  	roleInfoList_Value.Decode(buffer);
			    roleInfoList.Add(roleInfoList_Value);
		    }

		    var roleInfoSet_Size = buffer.ReadInt();
		    for (var i = 0; i < roleInfoSet_Size; i++) {
			    var roleInfoSet_Value = new RoleInfo();
			  	roleInfoSet_Value.Decode(buffer);
			    roleInfoSet.Add(roleInfoSet_Value);
		    }

		    var roleInfoMap_Size = buffer.ReadInt();
		    for (var i = 0; i < roleInfoMap_Size; i++) {
			    var roleInfoMap_Key = buffer.ReadLong();
			    var roleInfoMap_Value = new RoleInfo();
				roleInfoMap_Value.Decode(buffer);
			    roleInfoMap.Add(roleInfoMap_Key, roleInfoMap_Value);
		    }

		    if (buffer.ReadBool()) {
		        if (userInfo == null) {
		            userInfo = new UserInfo();
		        }
		        userInfo.Decode(buffer);
            }
		}

		public override string ToString()
		{
			return "SRoleLogin{" +
					"roleId=" + roleId +
					",roleName='" + roleName + '\'' +
					",roleInfo=" + roleInfo +
					",roleInfoList=" + ToString(roleInfoList) +
					",roleInfoSet=" + ToString(roleInfoSet) +
					",roleInfoMap=" + ToString(roleInfoMap) +
					",userInfo=" + userInfo +
					'}';
		}
    }
}