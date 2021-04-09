using System.Collections.Generic;
using System.Collections.Immutable;
using Newtonsoft.Json.Linq;

namespace Quan.Config
{
    public abstract class ConfigBase : Bean
    {
        protected ConfigBase(JObject json) : base(json)
        {
        }

        protected internal abstract ConfigBase Create(JObject json);


        protected static void Load<TKey, TValue>(IDictionary<TKey, TValue> configs, TValue config, TKey key)
        {
            configs[key] = config;
        }

        protected static void Load<TKey, TValue>(IDictionary<TKey, IList<TValue>> configs, TValue config, TKey key)
        {
            configs.TryGetValue(key, out var list);
            if (list == null)
            {
                list = new List<TValue>();
                configs[key] = list;
            }

            list.Add(config);
        }

        protected static void Load<TKey1, TKey2, TValue>(IDictionary<TKey1, IDictionary<TKey2, TValue>> configs, TValue config, TKey1 key1, TKey2 key2)
        {
            configs.TryGetValue(key1, out var dict);
            if (dict == null)
            {
                dict = new Dictionary<TKey2, TValue>();
                configs[key1] = dict;
            }

            Load(dict, config, key2);
        }

        protected static void Load<TKey1, TKey2, TValue>(IDictionary<TKey1, IDictionary<TKey2, IList<TValue>>> configs, TValue config, TKey1 key1, TKey2 key2)
        {
            configs.TryGetValue(key1, out var dict);
            if (dict == null)
            {
                dict = new Dictionary<TKey2, IList<TValue>>();
                configs[key1] = dict;
            }

            Load(dict, config, key2);
        }

        protected static void Load<TKey1, TKey2, TKey3, TValue>(IDictionary<TKey1, IDictionary<TKey2, IDictionary<TKey3, TValue>>> configs, TValue config, TKey1 key1, TKey2 key2, TKey3 key3)
        {
            configs.TryGetValue(key1, out var dict);
            if (dict == null)
            {
                dict = new Dictionary<TKey2, IDictionary<TKey3, TValue>>();
                configs[key1] = dict;
            }

            Load(dict, config, key2, key3);
        }

        protected static void Load<TKey1, TKey2, TKey3, TValue>(IDictionary<TKey1, IDictionary<TKey2, IDictionary<TKey3, IList<TValue>>>> configs, TValue config, TKey1 key1, TKey2 key2, TKey3 key3)
        {
            configs.TryGetValue(key1, out var dict);
            if (dict == null)
            {
                dict = new Dictionary<TKey2, IDictionary<TKey3, IList<TValue>>>();
                configs[key1] = dict;
            }

            Load(dict, config, key2, key3);
        }

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
                dictionary[key1] = ToImmutableDictionary(dictionary[key1]);
            }

            return dictionary.ToImmutableDictionary();
        }

        protected static IDictionary<TKey1, IDictionary<TKey2, IDictionary<TKey3, TValue>>> ToImmutableDictionary<TKey1, TKey2, TKey3, TValue>(IDictionary<TKey1, IDictionary<TKey2, IDictionary<TKey3, TValue>>> dictionary)
        {
            var keys1 = new List<TKey1>(dictionary.Keys);
            foreach (var key1 in keys1)
            {
                dictionary[key1] = ToImmutableDictionary(dictionary[key1]);
            }

            return dictionary.ToImmutableDictionary();
        }

        protected static IDictionary<TKey1, IDictionary<TKey2, IDictionary<TKey3, IList<TValue>>>> ToImmutableDictionary<TKey1, TKey2, TKey3, TValue>(IDictionary<TKey1, IDictionary<TKey2, IDictionary<TKey3, IList<TValue>>>> dictionary)
        {
            var keys1 = new List<TKey1>(dictionary.Keys);
            foreach (var key1 in keys1)
            {
                dictionary[key1] = ToImmutableDictionary(dictionary[key1]);
            }

            return dictionary.ToImmutableDictionary();
        }
    }
}