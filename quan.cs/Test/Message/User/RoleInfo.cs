using Quan.Common.Utils;
using Quan.Message;
using Buffer = Quan.Message.Buffer;
using System;

namespace Test.Message.User
{
	/// <summary>
	/// 角色信息2<br/>
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


		public override void Encode(Buffer buffer)
		{
	    	base.Encode(buffer);

		    buffer.WriteLong(Id);
		    buffer.WriteString(Name);
		}

		public override void Decode(Buffer buffer)
		{
	    	base.Decode(buffer);

		    Id = buffer.ReadLong();
		    Name = buffer.ReadString();
		}

		public override string ToString()
		{
			return "RoleInfo{" +
				   "id=" + Id.ToString2() +
				   ",name='" + Name + '\'' +
				   '}';
		}
    }
}