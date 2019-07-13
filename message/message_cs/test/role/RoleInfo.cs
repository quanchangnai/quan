using System;
using System.Collections.Generic;
using message_cs;
using Buffer = message_cs.Buffer;

namespace message_cs.test.role
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
		    foreach (var _list_Value in list) {
			    buffer.WriteInt(_list_Value);
		    }

		    buffer.WriteInt(set.Count);
		    foreach (var _set_Value in set) {
			    buffer.WriteInt(_set_Value);
		    }

		    buffer.WriteInt(map.Count);
		    foreach (var _map_Key in map.Keys) {
		        buffer.WriteInt(_map_Key);
			    buffer.WriteInt(map[_map_Key]);
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

		    var _list_Size = buffer.ReadInt();
		    for (var _index_ = 0; _index_ < _list_Size; _index_++) {
			    list.Add(buffer.ReadInt());
		    }

		    var _set_Size = buffer.ReadInt();
		    for (var _index_ = 0; _index_ < _set_Size; _index_++) {
			    set.Add(buffer.ReadInt());
		    }

		    var _map_Size = buffer.ReadInt();
		    for (var _index_ = 0; _index_ < _map_Size; _index_++) {
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