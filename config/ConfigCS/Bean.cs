using Newtonsoft.Json.Linq;

namespace ConfigCS
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
    }
}