using System;
using System.Collections.Generic;
using MessageCS;
using Buffer = MessageCS.Buffer;

namespace MessageCS.Test.Role
{
	/// <summary>
	/// 角色信息<br/>
	/// Created by 自动生成
	/// </summary>
    public class RoleInfo : Bean
    {
		public long roleId { get; set; }

		public bool bo { get; set; }

		public byte by { get; set; }

		public short s { get; set; }

		public int i { get; set; }

		public float f { get; set; }

		public double d { get; set; }

		private string _roleName = "";

		public string roleName
		{
	    	get => _roleName;
	    	set => _roleName = value ?? throw new NullReferenceException();
		}

		public RoleType roleType { get; set; }

		private byte[] _data = new byte[0];

		public byte[] data
		{
            get => _data;
            set => _data = value ?? throw new NullReferenceException();
		}

		public List<int> list { get; } = new List<int>();

		public HashSet<int> set { get; } = new HashSet<int>();

		public Dictionary<int, int> map { get; } = new Dictionary<int, int>();


		public RoleInfo()
		{
		}

		public override void Encode(Buffer buffer)
		{
	    	base.Encode(buffer);

		    buffer.WriteLong(roleId);
		    buffer.WriteBool(bo);
		    buffer.WriteByte(by);
		    buffer.WriteShort(s);
		    buffer.WriteInt(i);
		    buffer.WriteFloat(f);
		    buffer.WriteDouble(d);
		    buffer.WriteString(roleName);
			buffer.WriteInt((int)roleType);
		    buffer.WriteBytes(data);

		    buffer.WriteInt(list.Count);
		    foreach (var list_Value in list) {
			    buffer.WriteInt(list_Value);
		    }

		    buffer.WriteInt(set.Count);
		    foreach (var set_Value in set) {
			    buffer.WriteInt(set_Value);
		    }

		    buffer.WriteInt(map.Count);
		    foreach (var map_Key in map.Keys) {
		        buffer.WriteInt(map_Key);
			    buffer.WriteInt(map[map_Key]);
		    }

		}

		public override void Decode(Buffer buffer)
		{
	    	base.Decode(buffer);

		    roleId = buffer.ReadLong();
		    bo = buffer.ReadBool();
		    by = buffer.ReadByte();
		    s = buffer.ReadShort();
		    i = buffer.ReadInt();
		    f = buffer.ReadFloat();
		    d = buffer.ReadDouble();
		    roleName = buffer.ReadString();
		    roleType = (RoleType)buffer.ReadInt();
		    data = buffer.ReadBytes();

		    var list_Size = buffer.ReadInt();
		    for (var i = 0; i < list_Size; i++) {
			    list.Add(buffer.ReadInt());
		    }

		    var set_Size = buffer.ReadInt();
		    for (var i = 0; i < set_Size; i++) {
			    set.Add(buffer.ReadInt());
		    }

		    var map_Size = buffer.ReadInt();
		    for (var i = 0; i < map_Size; i++) {
			    map.Add(buffer.ReadInt(), buffer.ReadInt());
		    }

		}

		public override string ToString()
		{
			return "RoleInfo{" +
					"roleId=" + roleId +
					",bo=" + bo +
					",by=" + by +
					",s=" + s +
					",i=" + i +
					",f=" + f +
					",d=" + d +
					",roleName='" + roleName + '\'' +
					",roleType=" + roleType +
					",data=" + Convert.ToBase64String(data) +
					",list=" + ToString(list) +
					",set=" + ToString(set) +
					",map=" + ToString(map) +
					'}';
		}
    }
}