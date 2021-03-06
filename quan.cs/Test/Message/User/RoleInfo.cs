using Quan.Utils;
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
		public long id { get; set; }

		private string _name = "";

        /// <summary>
		/// 角色名
		/// </summary>
		public string name
		{
	    	get => _name;
	    	set => _name = value ?? throw new NullReferenceException();
		}


		public override void Encode(Buffer buffer)
		{
	    	base.Encode(buffer);

		    buffer.WriteLong(id);
		    buffer.WriteString(name);
		}

		public override void Decode(Buffer buffer)
		{
	    	base.Decode(buffer);

		    id = buffer.ReadLong();
		    name = buffer.ReadString();
		}

		public override string ToString()
		{
			return "RoleInfo{" +
				   "id=" + id.ToString2() +
				   ",name='" + name + '\'' +
				   '}';
		}
    }
}