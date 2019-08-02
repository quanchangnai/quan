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
        /// <summary>
		/// 角色id
		/// </summary>
		public long Id { get; set; }

		public bool B { get; set; }

		public short S { get; set; }

		public int I { get; set; }

		public float F { get; set; }

		public double D { get; set; }

		private string _roleName = "";

        /// <summary>
		/// 角色名
		/// </summary>
		public string RoleName
		{
	    	get => _roleName;
	    	set => _roleName = value ?? throw new NullReferenceException();
		}

		public RoleType RoleType { get; set; }

		private byte[] _data = new byte[0];

		public byte[] Data
		{
            get => _data;
            set => _data = value ?? throw new NullReferenceException();
		}

		public List<int> List { get; } = new List<int>();

		public HashSet<int> Set { get; } = new HashSet<int>();

		public Dictionary<int, int> Map { get; } = new Dictionary<int, int>();



		public override void Encode(Buffer buffer)
		{
	    	base.Encode(buffer);

		    buffer.WriteLong(Id);
		    buffer.WriteBool(B);
		    buffer.WriteShort(S);
		    buffer.WriteInt(I);
		    buffer.WriteFloat(F);
		    buffer.WriteDouble(D);
		    buffer.WriteString(RoleName);
			buffer.WriteInt((int)RoleType);
		    buffer.WriteBytes(Data);

		    buffer.WriteInt(List.Count);
		    foreach (var listValue in List) {
			    buffer.WriteInt(listValue);
		    }

		    buffer.WriteInt(Set.Count);
		    foreach (var setValue in Set) {
			    buffer.WriteInt(setValue);
		    }

		    buffer.WriteInt(Map.Count);
		    foreach (var mapKey in Map.Keys) {
		        buffer.WriteInt(mapKey);
			    buffer.WriteInt(Map[mapKey]);
		    }
		}

		public override void Decode(Buffer buffer)
		{
	    	base.Decode(buffer);

		    Id = buffer.ReadLong();
		    B = buffer.ReadBool();
		    S = buffer.ReadShort();
		    I = buffer.ReadInt();
		    F = buffer.ReadFloat();
		    D = buffer.ReadDouble();
		    RoleName = buffer.ReadString();
		    RoleType = (RoleType)buffer.ReadInt();
		    Data = buffer.ReadBytes();

		    var listSize = buffer.ReadInt();
		    for (var i = 0; i < listSize; i++) {
			    List.Add(buffer.ReadInt());
		    }

		    var setSize = buffer.ReadInt();
		    for (var i = 0; i < setSize; i++) {
			    Set.Add(buffer.ReadInt());
		    }

		    var mapSize = buffer.ReadInt();
		    for (var i = 0; i < mapSize; i++) {
			    Map.Add(buffer.ReadInt(), buffer.ReadInt());
		    }
		}

		public override string ToString()
		{
			return "RoleInfo{" +
					"id=" + Id +
					",b=" + B +
					",s=" + S +
					",i=" + I +
					",f=" + F +
					",d=" + D +
					",roleName='" + RoleName + '\'' +
					",roleType=" + RoleType +
					",data=" + Convert.ToBase64String(Data) +
					",list=" + ToString(List) +
					",set=" + ToString(Set) +
					",map=" + ToString(Map) +
					'}';
		}
    }
}