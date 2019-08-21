using System.Collections.Generic;
using System.Collections.Immutable;
using Newtonsoft.Json.Linq;

namespace ConfigCS
{
    public abstract class Config : Bean
    {
        protected Config(JObject json) : base(json)
        {
        }

        protected abstract Config Create(JObject json);


        protected static IDictionary<TKey, TValue> ToImmutableDictionary<TKey, TValue>(IDictionary<TKey, TValue> dictionary)
        {
            return dictionary.ToImmutableDictionary();
        }

        protected static IDictionary<TKey, IList<TValue>> ToImmutableDictionary<TKey, TValue>(IDictionary<TKey, IList<TValue>> dictionary)
        {
            var keys = new List<TKey>(dictionary.Keys);
            foreach (var key in keys)
            {
                dictionary[key] = dictionary[key].ToImmutableList();
            }

            return dictionary.ToImmutableDictionary();
        }

        protected static IDictionary<TKey1, IDictionary<TKey2, TValue>> ToImmutableDictionary<TKey1, TKey2, TValue>(IDictionary<TKey1, IDictionary<TKey2, TValue>> dictionary)
        {
            var keys = new List<TKey1>(dictionary.Keys);
            foreach (var key in keys)
            {
                dictionary[key] = dictionary[key].ToImmutableDictionary();
            }

            return dictionary.ToImmutableDictionary();
        }

        protected static IDictionary<TKey1, IDictionary<TKey2, IList<TValue>>> ToImmutableDictionary<TKey1, TKey2, TValue>(IDictionary<TKey1, IDictionary<TKey2, IList<TValue>>> dictionary)
        {
            var keys1 = new List<TKey1>(dictionary.Keys);
            foreach (var key1 in keys1)
            {
                var value1 = dictionary[key1];
                var keys2 = new List<TKey2>(value1.Keys);
                foreach (var key2 in keys2)
                {
                    value1[key2] = value1[key2].ToImmutableList();
                }

                dictionary[key1] = value1.ToImmutableDictionary();
            }

            return dictionary.ToImmutableDictionary();
        }

        protected static IDictionary<TKey1, IDictionary<TKey2, IDictionary<TKey3, TValue>>> ToImmutableDictionary<TKey1, TKey2, TKey3, TValue>(IDictionary<TKey1, IDictionary<TKey2, IDictionary<TKey3, TValue>>> dictionary)
        {
            var keys1 = new List<TKey1>(dictionary.Keys);
            foreach (var key1 in keys1)
            {
                var value1 = dictionary[key1];
                var keys2 = new List<TKey2>(value1.Keys);
                foreach (var key2 in keys2)
                {
                    value1[key2] = value1[key2].ToImmutableDictionary();
                }

                dictionary[key1] = value1.ToImmutableDictionary();
            }

            return dictionary.ToImmutableDictionary();
        }

        protected static IDictionary<TKey1, IDictionary<TKey2, IDictionary<TKey3, IList<TValue>>>> ToImmutableDictionary<TKey1, TKey2, TKey3, TValue>(IDictionary<TKey1, IDictionary<TKey2, IDictionary<TKey3, IList<TValue>>>> dictionary)
        {
            var keys1 = new List<TKey1>(dictionary.Keys);
            foreach (var key1 in keys1)
            {
                var value1 = dictionary[key1];
                var keys2 = new List<TKey2>(value1.Keys);
                foreach (var key2 in keys2)
                {
                    var value2 = value1[key2];
                    var keys3 = new List<TKey3>(value2.Keys);
                    foreach (var key3 in keys3)
                    {
                        value2[key3] = value2[key3].ToImmutableList();
                    }

                    value1[key2] = value2.ToImmutableDictionary();
                }

                dictionary[key1] = value1.ToImmutableDictionary();
            }

            return dictionary.ToImmutableDictionary();
        }
    }
}