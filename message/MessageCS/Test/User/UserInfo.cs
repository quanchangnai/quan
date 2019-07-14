using System;
using System.Collections.Generic;
using MessageCS;
using Buffer = MessageCS.Buffer;

namespace MessageCS.Test.User
{
	/// <summary>
	/// Created by 自动生成
	/// </summary>
    public class UserInfo : Bean
    {
		public long id { get; set; }

		private string _name = "";

		public string name
		{
	    	get => _name;
	    	set => _name = value ?? throw new NullReferenceException();
		}

		public int level { get; set; }


		public UserInfo()
		{
		}

		public override void Encode(Buffer buffer)
		{
	    	base.Encode(buffer);

		    buffer.WriteLong(id);
		    buffer.WriteString(name);
		    buffer.WriteInt(level);
		}

		public override void Decode(Buffer buffer)
		{
	    	base.Decode(buffer);

		    id = buffer.ReadLong();
		    name = buffer.ReadString();
		    level = buffer.ReadInt();
		}

		public override string ToString()
		{
			return "UserInfo{" +
					"id=" + id +
					",name='" + name + '\'' +
					",level=" + level +
					'}';
		}
    }
}