using System;
using System.Collections.Generic;
using Quan.Common.Utils;
using Quan.Message;
using Buffer = Quan.Message.Buffer;

namespace Test.Message.Role
{
	/// <summary>
	/// 角色信息<br/>
	/// 代码自动生成，请勿手动修改
	/// </summary>
    public class RoleInfo : Bean
    {
        /// <summary>
		/// 角色id
		/// </summary>
		public long Id { get; set; }

		private string _name = "";

        /// <summary>
		/// 角色名
		/// </summary>
		public string Name
		{
	    	get => _name;
	    	set => _name = value ?? throw new NullReferenceException();
		}

		public RoleType Type { get; set; }

		public bool B { get; set; }

		public short S { get; set; }

		public int I { get; set; }

        private float _f;

        public float F
        {
            get => _f;
            set
            {
                Buffer.CheckScale(value, 2);
                _f = value;
            }
        }

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
		    buffer.WriteString(Name);
			buffer.WriteInt((int)Type);
		    buffer.WriteBool(B);
		    buffer.WriteShort(S);
		    buffer.WriteInt(I);
			buffer.WriteFloat(F, 2);
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
		}

		public override void Decode(Buffer buffer)
		{
	    	base.Decode(buffer);

		    Id = buffer.ReadLong();
		    Name = buffer.ReadString();
		    Type = (RoleType)buffer.ReadInt();
		    B = buffer.ReadBool();
		    S = buffer.ReadShort();
		    I = buffer.ReadInt();
			F = buffer.ReadFloat(2);
			D = buffer.ReadDouble();
		    Data = buffer.ReadBytes();

		    var listSize = buffer.ReadInt();
		    for (var i = 0; i < listSize; i++) 
			{
			    List.Add(buffer.ReadInt());
		    }

		    var setSize = buffer.ReadInt();
		    for (var i = 0; i < setSize; i++) 
			{
			    Set.Add(buffer.ReadInt());
		    }
		}

		public override string ToString()
		{
			return "RoleInfo{" +
				   "id=" + Id.ToString2() +
				   ",name='" + Name + '\'' +
				   ",type=" + Type.ToString2() +
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