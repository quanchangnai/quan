using System;
using System.Collections.Generic;
using message_cs;
using Buffer = message_cs.Buffer;
using message_cs.test.user;

namespace message_cs.test.role
{
	/// <summary>
	/// 角色登录<br/>
	/// Created by 自动生成
	/// </summary>
    public class SRoleLogin : Message
    {
		public long roleId { get; set; }

		private string _roleName = "";

		public string roleName
		{
	    	get => _roleName;
	    	set => _roleName = value ?? throw new NullReferenceException();
		}

		private RoleInfo _roleInfo = new RoleInfo();

		public RoleInfo roleInfo
		{
	    	get => _roleInfo;
	    	set => _roleInfo = value ?? throw new NullReferenceException();
		}

		public List<RoleInfo> roleInfoList { get; } = new List<RoleInfo>();

		public HashSet<RoleInfo> roleInfoSet { get; } = new HashSet<RoleInfo>();

		public Dictionary<long, RoleInfo> roleInfoMap { get; } = new Dictionary<long, RoleInfo>();

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
		    foreach (var _roleInfoList_Value in roleInfoList) {
			    _roleInfoList_Value.Encode(buffer);
		    }

		    buffer.WriteInt(roleInfoSet.Count);
		    foreach (var _roleInfoSet_Value in roleInfoSet) {
			    _roleInfoSet_Value.Encode(buffer);
		    }

		    buffer.WriteInt(roleInfoMap.Count);
		    foreach (var _roleInfoMap_Key in roleInfoMap.Keys) {
		        buffer.WriteLong(_roleInfoMap_Key);
			    roleInfoMap[_roleInfoMap_Key].Encode(buffer);
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

		    var _roleInfoList_Size = buffer.ReadInt();
		    for (var _index_ = 0; _index_ < _roleInfoList_Size; _index_++) {
			    var _roleInfoList_Value = new RoleInfo();
			    _roleInfoList_Value.Decode(buffer);
			    roleInfoList.Add(_roleInfoList_Value);
		    }

		    var _roleInfoSet_Size = buffer.ReadInt();
		    for (var _index_ = 0; _index_ < _roleInfoSet_Size; _index_++) {
			    var _roleInfoSet_Value = new RoleInfo();
			    _roleInfoSet_Value.Decode(buffer);
			    roleInfoSet.Add(_roleInfoSet_Value);
		    }

		    var _roleInfoMap_Size = buffer.ReadInt();
		    for (var _index_ = 0; _index_ < _roleInfoMap_Size; _index_++) {
			    var _roleInfoMap_Key = buffer.ReadLong();
			    var _roleInfoMap_Value = new RoleInfo();
			    _roleInfoMap_Value.Decode(buffer);
			    roleInfoMap.Add(_roleInfoMap_Key, _roleInfoMap_Value);
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