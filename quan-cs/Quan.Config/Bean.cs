using System;
using Newtonsoft.Json.Linq;

namespace Quan.Config
{
    public class Bean
    {
        private readonly JObject _json;

        public Bean(JObject json)
        {
            _json = json;
        }

        public string ToJson()
        {
            return _json.ToString();
        }

        protected static DateTime ToDateTime(long time)
        {
            if (time <= 0)
            {
                return new DateTime();
            }

            var dateTime = TimeZone.CurrentTimeZone.ToLocalTime(new DateTime(1970, 1, 1));
            return dateTime.AddMilliseconds(time);
        }
    }
}