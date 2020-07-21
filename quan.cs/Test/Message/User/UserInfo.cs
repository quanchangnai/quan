using System;
using System.Collections.Generic;
using Quan.Common.Utils;
using Quan.Message;
using Buffer = Quan.Message.Buffer;

namespace Test.Message.User
{
	/// <summary>
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


		public override void Encode(Buffer buffer)
		{
	    	base.Encode(buffer);

        	buffer.WriteTag(4);
		    buffer.WriteLong(Id);

        	buffer.WriteTag(11);
		    buffer.WriteString(Name);

        	buffer.WriteTag(12);
		    buffer.WriteInt(Level);

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
				   '}';
		}
    }
}