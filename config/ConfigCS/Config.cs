using Newtonsoft.Json.Linq;

namespace ConfigCS
{
    public abstract class Config : Bean
    {
        public Config(JObject json) : base(json)
        {
        }

        protected abstract Config Create(JObject json);
    }
}