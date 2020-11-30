using Quan.Common.Utils;
using Quan.Message;
using Buffer = Quan.Message.Buffer;

namespace Test.Message.Common
{
	/// <summary>
	/// 消息头<br/>
	/// 代码自动生成，请勿手动修改
	/// </summary>
    public abstract class MessageHeader : MessageBase
    {
        /// <summary>
		/// 消息序号
		/// </summary>
		public long seq { get; set; }

        /// <summary>
		/// 错误码
		/// </summary>
		public int error { get; set; }


		public override void Encode(Buffer buffer)
		{
	    	base.Encode(buffer);

		    buffer.WriteLong(seq);
		    buffer.WriteInt(error);
		}

		public override void Decode(Buffer buffer)
		{
	    	base.Decode(buffer);

		    seq = buffer.ReadLong();
		    error = buffer.ReadInt();
		}

		public override string ToString()
		{
			return "MessageHeader{" +
				   "seq=" + seq.ToString2() +
				   ",error=" + error.ToString2() +
				   '}';
		}
    }
}