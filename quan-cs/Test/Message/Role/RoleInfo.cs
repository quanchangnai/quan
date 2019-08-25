using System;
using System.Collections.Generic;
using Quan.Common;
using Quan.Message;
using Buffer = Quan.Message.Buffer;

namespace Test.Message.Role
{
	/// <summary>
	/// 角色信息<br/>
	/// 自动生成
	/// </summary>
    public class RoleInfo : Bean
    {
        /// <summary>
		/// 角色id
		/// </summary>
		public long Id { get; set; }

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

		public bool B { get; set; }

		public short S { get; set; }

		public int I { get; set; }

		public float F { get; set; }

		public double D { get; set; }

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
		    buffer.WriteString(RoleName);
			buffer.WriteInt((int)RoleType);
		    buffer.WriteBool(B);
		    buffer.WriteShort(S);
		    buffer.WriteInt(I);
		    buffer.WriteFloat(F);
		    buffer.WriteDouble(D);
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
		    RoleName = buffer.ReadString();
		    RoleType = (RoleType)buffer.ReadInt();
		    B = buffer.ReadBool();
		    S = buffer.ReadShort();
		    I = buffer.ReadInt();
		    F = buffer.ReadFloat();
		    D = buffer.ReadDouble();
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
					"id=" + Id.ToString2() +
					",roleName='" + RoleName + '\'' +
					",roleType=" + RoleType.ToString2() +
					",b=" + B.ToString2() +
					",s=" + S.ToString2() +
					",i=" + I.ToString2() +
					",f=" + F.ToString2() +
					",d=" + D.ToString2() +
					",data=" + Data.ToString2() +
					",list=" + List.ToString2() +
					",set=" + Set.ToString2() +
					",map=" + Map.ToString2() +
					'}';
		}
    }
}