using System;
using System.Collections.Generic;
using quan.message;
using quan.message.user;

namespace quan.message.role
{
    public class SRoleLogin : Message
    {
		public long roleId{ get; set; }

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

		private UserInfo userInfo;


		public SRoleLogin(): base(222)
		{
		}

		public override Message create()
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
		    if (userInfo != null) {
		        userInfo.Encode(buffer);
		    }

		}

		public override void Decode(Buffer buffer)
		{
	    	base.Decode(buffer);

		    roleId = buffer.ReadLong();
		    roleName = buffer.ReadString();
		    roleInfo.Decode(buffer);

		    int _roleInfoList_Size = buffer.ReadInt();
		    for (int i = 0; i < _roleInfoList_Size; i++) {
			    RoleInfo _roleInfoList_Value = new RoleInfo();
			    _roleInfoList_Value.Decode(buffer);
			    roleInfoList.Add(_roleInfoList_Value);
		    }

		    int _roleInfoSet_Size = buffer.ReadInt();
		    for (int i = 0; i < _roleInfoSet_Size; i++) {
			    RoleInfo _roleInfoSet_Value = new RoleInfo();
			    _roleInfoSet_Value.Decode(buffer);
			    roleInfoSet.Add(_roleInfoSet_Value);
		    }

		    int _roleInfoMap_Size = buffer.ReadInt();
		    for (int i = 0; i < _roleInfoMap_Size; i++) {
			    long _roleInfoMap_Key = buffer.ReadLong();
			    RoleInfo _roleInfoMap_Value = new RoleInfo();
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
    }
}