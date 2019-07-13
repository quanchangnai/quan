using System;
using System.Collections.Generic;
using quan.message;

namespace quan.message.user
{
    public class UserInfo : Bean
    {
		private string _name = "";

		public string name
		{
	    	get => _name;
	    	set => _name = value ?? throw new NullReferenceException();
		}

		public int level{ get; set; }

		public int experience{ get; set; }

		public int icon{ get; set; }

		public int power{ get; set; }

		public int modifyNameCount{ get; set; }

		private string _eventState = "";

		public string eventState
		{
	    	get => _eventState;
	    	set => _eventState = value ?? throw new NullReferenceException();
		}

		private string _functionState = "";

		public string functionState
		{
	    	get => _functionState;
	    	set => _functionState = value ?? throw new NullReferenceException();
		}

		public int lucky{ get; set; }

		public int currentState{ get; set; }

		public int buyPowerCount{ get; set; }


		public UserInfo()
		{
		}

		public override void Encode(Buffer buffer)
		{
	    	base.Encode(buffer);

		    buffer.WriteString(name);
		    buffer.WriteInt(level);
		    buffer.WriteInt(experience);
		    buffer.WriteInt(icon);
		    buffer.WriteInt(power);
		    buffer.WriteInt(modifyNameCount);
		    buffer.WriteString(eventState);
		    buffer.WriteString(functionState);
		    buffer.WriteInt(lucky);
		    buffer.WriteInt(currentState);
		    buffer.WriteInt(buyPowerCount);
		}

		public override void Decode(Buffer buffer)
		{
	    	base.Decode(buffer);

		    name = buffer.ReadString();
		    level = buffer.ReadInt();
		    experience = buffer.ReadInt();
		    icon = buffer.ReadInt();
		    power = buffer.ReadInt();
		    modifyNameCount = buffer.ReadInt();
		    eventState = buffer.ReadString();
		    functionState = buffer.ReadString();
		    lucky = buffer.ReadInt();
		    currentState = buffer.ReadInt();
		    buyPowerCount = buffer.ReadInt();
		}
    }
}