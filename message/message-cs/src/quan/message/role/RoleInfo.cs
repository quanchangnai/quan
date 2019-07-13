using System;
using System.Collections.Generic;
using quan.message;

namespace quan.message.role
{
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
		    data = buffer.ReadBytes();

		    int _list_Size = buffer.ReadInt();
		    for (int i = 0; i < _list_Size; i++) {
			    list.Add(buffer.ReadInt());
		    }

		    int _set_Size = buffer.ReadInt();
		    for (int i = 0; i < _set_Size; i++) {
			    set.Add(buffer.ReadInt());
		    }

		    int _map_Size = buffer.ReadInt();
		    for (int i = 0; i < _map_Size; i++) {
			    map.Add(buffer.ReadInt(), buffer.ReadInt());
		    }

		}
    }
}