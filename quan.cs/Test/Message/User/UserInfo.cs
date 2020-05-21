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
					"id=" + Id.ToString2() +
					",name='" + Name + '\'' +
					",level=" + Level.ToString2() +
					'}';
		}
    }
}