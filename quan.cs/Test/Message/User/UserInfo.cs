using System;
using System.Collections.Generic;
using Quan.Common.Utils;
using Quan.Message;
using Buffer = Quan.Message.Buffer;
using Test.Message.Role;

namespace Test.Message.User
{
	/// <summary>
	/// 用户信息<br/>
	/// 自动生成
	/// </summary>
    public class UserInfo : Bean
    {
        /// <summary>
		/// ID
		/// </summary>
		public long Id { get; set; }

		private string _name = "";

        /// <summary>
		/// 名字
		/// </summary>
		public string Name
		{
	    	get => _name;
	    	set => _name = value ?? throw new NullReferenceException();
		}

        /// <summary>
		/// 等级
		/// </summary>
		public int Level { get; set; }

        /// <summary>
		/// 角色信息
		/// </summary>
		public RoleInfo RoleInfo1 { get; set; }

		private RoleInfo _roleInfo2 = new RoleInfo();

        /// <summary>
		/// 角色信息2
		/// </summary>
		public RoleInfo RoleInfo2
		{
	    	get => _roleInfo2;
	    	set => _roleInfo2 = value ?? throw new NullReferenceException();
		}

        /// <summary>
		/// 角色信息List
		/// </summary>
		public List<RoleInfo> RoleList { get; } = new List<RoleInfo>();

        /// <summary>
		/// 角色信息Set
		/// </summary>
		public HashSet<RoleInfo> RoleSet { get; } = new HashSet<RoleInfo>();

        /// <summary>
		/// 角色信息Map
		/// </summary>
		public Dictionary<long, RoleInfo> RoleMap { get; } = new Dictionary<long, RoleInfo>();


		public override void Encode(Buffer buffer)
		{
	    	base.Encode(buffer);

        	buffer.WriteTag(4);
		    buffer.WriteLong(Id);

        	buffer.WriteTag(11);
		    buffer.WriteString(Name);

        	buffer.WriteTag(12);
		    buffer.WriteInt(Level);

        	buffer.WriteTag(19);
			var roleInfo1Buffer = new Buffer();
			roleInfo1Buffer.WriteBool(RoleInfo1 != null);
		    RoleInfo1?.Encode(roleInfo1Buffer);
			buffer.WriteBuffer(roleInfo1Buffer);

        	buffer.WriteTag(23);
        	var roleInfo2Buffer = new Buffer();
        	RoleInfo2.Encode(roleInfo2Buffer);
        	buffer.WriteBuffer(roleInfo2Buffer);

        	buffer.WriteTag(27);
			var roleListBuffer = new Buffer();
			roleListBuffer.WriteInt(RoleList.Count);
		    foreach (var roleListValue in RoleList) {
				roleListValue.Encode(roleListBuffer);
		    }
			buffer.WriteBuffer(roleListBuffer);

        	buffer.WriteTag(31);
			var roleSetBuffer = new Buffer();
			roleSetBuffer.WriteInt(RoleSet.Count);
		    foreach (var roleSetValue in RoleSet) {
				roleSetValue.Encode(roleSetBuffer);
		    }
			buffer.WriteBuffer(roleSetBuffer);

        	buffer.WriteTag(35);
			var roleMapBuffer = new Buffer();
			roleMapBuffer.WriteInt(RoleMap.Count);
		    foreach (var roleMapKey in RoleMap.Keys) {
		        roleMapBuffer.WriteLong(roleMapKey);
			    RoleMap[roleMapKey].Encode(roleMapBuffer);
		    }
			buffer.WriteBuffer(roleMapBuffer);

        	buffer.WriteTag(0);
		}

		public override void Decode(Buffer buffer)
		{
	    	base.Decode(buffer);

			for (var tag = buffer.ReadTag(); tag != 0; tag = buffer.ReadTag()) 
			{
            	switch (tag) 
				{
                	case 4:
                    	Id = buffer.ReadLong();
                    	break;
                	case 11:
                    	Name = buffer.ReadString();
                    	break;
                	case 12:
                    	Level = buffer.ReadInt();
                    	break;
                	case 19:
                    	buffer.ReadInt();
                    	if (buffer.ReadBool()) 
						{
		        			if (RoleInfo1 == null) 
							{
		            			RoleInfo1 = new RoleInfo();
		        			}
		        			RoleInfo1.Decode(buffer);
            			}
                    	break;
                	case 23:
                    	buffer.ReadInt();
                    	RoleInfo2.Decode(buffer);
                    	break;
                	case 27:
                    	buffer.ReadInt();
                    	var roleListSize = buffer.ReadInt();
		    			for (var i = 0; i < roleListSize; i++) 
						{
			    			var roleListValue = new RoleInfo();
			  				roleListValue.Decode(buffer);
			    			RoleList.Add(roleListValue);
		    			}
                    	break;
                	case 31:
                    	buffer.ReadInt();
                    	var roleSetSize = buffer.ReadInt();
		    			for (var i = 0; i < roleSetSize; i++) 
						{
			    			var roleSetValue = new RoleInfo();
			  				roleSetValue.Decode(buffer);
			    			RoleSet.Add(roleSetValue);
		    			}
                    	break;
                	case 35:
                    	buffer.ReadInt();
                    	var roleMapSize = buffer.ReadInt();
		    			for (var i = 0; i < roleMapSize; i++) 
						{
			    			var roleMapKey = buffer.ReadLong();
			    			var roleMapValue = new RoleInfo();
							roleMapValue.Decode(buffer);
			    			RoleMap.Add(roleMapKey, roleMapValue);
						}
                    	break;
                	default:
                   	 	SkipField(tag, buffer);
						break;
            	}
        	}
		}

		public override string ToString()
		{
			return "UserInfo{" +
				   "id=" + Id.ToString2() +
				   ",name='" + Name + '\'' +
				   ",level=" + Level.ToString2() +
				   ",roleInfo1=" + RoleInfo1.ToString2() +
				   ",roleInfo2=" + RoleInfo2.ToString2() +
				   ",roleList=" + RoleList.ToString2() +
				   ",roleSet=" + RoleSet.ToString2() +
				   ",roleMap=" + RoleMap.ToString2() +
				   '}';
		}
    }
}