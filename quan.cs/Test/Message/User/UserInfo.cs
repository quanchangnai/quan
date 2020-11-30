using Quan.Common.Utils;
using Quan.Message;
using Buffer = Quan.Message.Buffer;
using System;
using System.Collections.Generic;

namespace Test.Message.User
{
	/// <summary>
	/// 用户信息<br/>
	/// 代码自动生成，请勿手动修改
	/// </summary>
    public class UserInfo : Bean
    {
        /// <summary>
		/// ID
		/// </summary>
		public long id { get; set; }

		private string _name = "";

        /// <summary>
		/// 名字
		/// </summary>
		public string name
		{
	    	get => _name;
	    	set => _name = value ?? throw new NullReferenceException();
		}

        /// <summary>
		/// 等级
		/// </summary>
		public int level { get; set; }

        /// <summary>
		/// 角色信息
		/// </summary>
		public Test.Message.Role.RoleInfo roleInfo1 { get; set; }

		private RoleInfo _roleInfo2 = new RoleInfo();

        /// <summary>
		/// 角色信息2
		/// </summary>
		public RoleInfo roleInfo2
		{
	    	get => _roleInfo2;
	    	set => _roleInfo2 = value ?? throw new NullReferenceException();
		}

		private RoleInfo _roleInfo3 = new RoleInfo();

        /// <summary>
		/// 角色信息2
		/// </summary>
		public RoleInfo roleInfo3
		{
	    	get => _roleInfo3;
	    	set => _roleInfo3 = value ?? throw new NullReferenceException();
		}

        /// <summary>
		/// 角色信息List
		/// </summary>
		public List<Test.Message.Role.RoleInfo> roleList { get; } = new List<Test.Message.Role.RoleInfo>();

        /// <summary>
		/// 角色信息Set
		/// </summary>
		public HashSet<Test.Message.Role.RoleInfo> roleSet { get; } = new HashSet<Test.Message.Role.RoleInfo>();

        /// <summary>
		/// 角色信息Map
		/// </summary>
		public Dictionary<long, Test.Message.Role.RoleInfo> roleMap { get; } = new Dictionary<long, Test.Message.Role.RoleInfo>();


		public override void Encode(Buffer buffer)
		{
	    	base.Encode(buffer);

        	buffer.WriteTag(4);
		    buffer.WriteLong(id);

        	buffer.WriteTag(11);
		    buffer.WriteString(name);

        	buffer.WriteTag(12);
		    buffer.WriteInt(level);

        	buffer.WriteTag(19);
			var roleInfo1Buffer = new Buffer();
			roleInfo1Buffer.WriteBool(roleInfo1 != null);
		    roleInfo1?.Encode(roleInfo1Buffer);
			buffer.WriteBuffer(roleInfo1Buffer);

        	buffer.WriteTag(23);
        	var roleInfo2Buffer = new Buffer();
        	roleInfo2.Encode(roleInfo2Buffer);
        	buffer.WriteBuffer(roleInfo2Buffer);

        	buffer.WriteTag(27);
        	var roleInfo3Buffer = new Buffer();
        	roleInfo3.Encode(roleInfo3Buffer);
        	buffer.WriteBuffer(roleInfo3Buffer);

        	buffer.WriteTag(31);
			var roleListBuffer = new Buffer();
			roleListBuffer.WriteInt(roleList.Count);
		    foreach (var roleListValue in roleList) {
				roleListValue.Encode(roleListBuffer);
		    }
			buffer.WriteBuffer(roleListBuffer);

        	buffer.WriteTag(35);
			var roleSetBuffer = new Buffer();
			roleSetBuffer.WriteInt(roleSet.Count);
		    foreach (var roleSetValue in roleSet) {
				roleSetValue.Encode(roleSetBuffer);
		    }
			buffer.WriteBuffer(roleSetBuffer);

        	buffer.WriteTag(39);
			var roleMapBuffer = new Buffer();
			roleMapBuffer.WriteInt(roleMap.Count);
		    foreach (var roleMapKey in roleMap.Keys) {
		        roleMapBuffer.WriteLong(roleMapKey);
			    roleMap[roleMapKey].Encode(roleMapBuffer);
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
                    	id = buffer.ReadLong();
                    	break;
                	case 11:
                    	name = buffer.ReadString();
                    	break;
                	case 12:
                    	level = buffer.ReadInt();
                    	break;
                	case 19:
                    	buffer.ReadInt();
                    	if (buffer.ReadBool()) 
						{
		        			if (roleInfo1 == null)
							{
		            			roleInfo1 = new Test.Message.Role.RoleInfo();
		        			}
		        			roleInfo1.Decode(buffer);
            			}
                    	break;
                	case 23:
                    	buffer.ReadInt();
                    	roleInfo2.Decode(buffer);
                    	break;
                	case 27:
                    	buffer.ReadInt();
                    	roleInfo3.Decode(buffer);
                    	break;
                	case 31:
                    	buffer.ReadInt();
                    	var roleListSize = buffer.ReadInt();
		    			for (var i = 0; i < roleListSize; i++) 
						{
			    			var roleListValue = new Test.Message.Role.RoleInfo();
			  				roleListValue.Decode(buffer);
			    			roleList.Add(roleListValue);
		    			}
                    	break;
                	case 35:
                    	buffer.ReadInt();
                    	var roleSetSize = buffer.ReadInt();
		    			for (var i = 0; i < roleSetSize; i++) 
						{
			    			var roleSetValue = new Test.Message.Role.RoleInfo();
			  				roleSetValue.Decode(buffer);
			    			roleSet.Add(roleSetValue);
		    			}
                    	break;
                	case 39:
                    	buffer.ReadInt();
                    	var roleMapSize = buffer.ReadInt();
		    			for (var i = 0; i < roleMapSize; i++) 
						{
			    			var roleMapKey = buffer.ReadLong();
			    			var roleMapValue = new Test.Message.Role.RoleInfo();
							roleMapValue.Decode(buffer);
			    			roleMap.Add(roleMapKey, roleMapValue);
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
				   "id=" + id.ToString2() +
				   ",name='" + name + '\'' +
				   ",level=" + level.ToString2() +
				   ",roleInfo1=" + roleInfo1.ToString2() +
				   ",roleInfo2=" + roleInfo2.ToString2() +
				   ",roleInfo3=" + roleInfo3.ToString2() +
				   ",roleList=" + roleList.ToString2() +
				   ",roleSet=" + roleSet.ToString2() +
				   ",roleMap=" + roleMap.ToString2() +
				   '}';
		}
    }
}