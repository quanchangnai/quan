using System;
using System.Collections.Generic;
using Quan.Common.Utils;
using Quan.Message;
using Buffer = Quan.Message.Buffer;

namespace Test.Message.Common
{
	/// <summary>
	/// 消息头<br/>
	/// 自动生成
	/// </summary>
    public abstract class MessageHeader : MessageBase
    {
        /// <summary>
		/// 消息序号
		/// </summary>
		public long Seq { get; set; }

        /// <summary>
		/// 错误码
		/// </summary>
		public int Error { get; set; }


		public override void Encode(Buffer buffer)
		{
	    	base.Encode(buffer);

		    buffer.WriteLong(Seq);
		    buffer.WriteInt(Error);
		}

		public override void Decode(Buffer buffer)
		{
	    	base.Decode(buffer);

		    Seq = buffer.ReadLong();
		    Error = buffer.ReadInt();
		}

		public override string ToString()
		{
			return "MessageHeader{" +
					"seq=" + Seq.ToString2() +
					",error=" + Error.ToString2() +
					'}';
		}
    }
}