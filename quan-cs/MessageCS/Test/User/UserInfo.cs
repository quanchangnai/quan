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

		    buffer.WriteLong(Id);
		    buffer.WriteString(Name);
		    buffer.WriteInt(Level);
		}

		public override void Decode(Buffer buffer)
		{
	    	base.Decode(buffer);

		    Id = buffer.ReadLong();
		    Name = buffer.ReadString();
		    Level = buffer.ReadInt();
		}

		public override string ToString()
		{
			return "UserInfo{" +
					"id=" + Id +
					",name='" + Name + '\'' +
					",level=" + Level +
					'}';
		}
    }
}