using System;
using System.Collections.Generic;
using Quan.Common;
using Quan.Message;
using Buffer = Quan.Message.Buffer;

namespace Test.Message.Common
{
	/// <summary>
	/// 自动生成
	/// </summary>
    public class HeadedMessage : 
    {
		public long H1 { get; set; }

		private string _h2 = "";

		public string H2
		{
	    	get => _h2;
	    	set => _h2 = value ?? throw new NullReferenceException();
		}


		public override void Encode(Buffer buffer)
		{
	    	base.Encode(buffer);

		    buffer.WriteLong(H1);
		    buffer.WriteString(H2);
		}

		public override void Decode(Buffer buffer)
		{
	    	base.Decode(buffer);

		    H1 = buffer.ReadLong();
		    H2 = buffer.ReadString();
		}

		public override string ToString()
		{
			return "HeadedMessage{" +
					"h1=" + H1.ToString2() +
					",h2='" + H2 + '\'' +
					'}';
		}
    }
}