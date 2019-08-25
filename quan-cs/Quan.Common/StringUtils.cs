using System;
using System.Collections.Generic;

namespace Quan.Common
{
    public static class StringUtils
    {
        public static string ToString2(this object obj)
        {
            return obj.ToString();
        }

        public static string ToString2(this byte[] bytes)
        {
            return Convert.ToBase64String(bytes);
        }

        public static string ToString2<T>(this ICollection<T> collection)
        {
            if (collection == null)
            {
                return "null";
            }

            var result = "[";

            var i = 0;
            foreach (var item in collection)
            {
                result += ReferenceEquals(item, collection) ? "(this)" : item.ToString2();

                if (i < collection.Count - 1)
                {
                    result += ", ";
                }

                i++;
            }

            result += "]";

            return result;
        }

        public static string ToString2<TK, TV>(this Dictionary<TK, TV> dictionary)
        {
            if (dictionary == null)
            {
                return "null";
            }

            var result = "{";

            var i = 0;
            foreach (var key in dictionary.Keys)
            {
                var value = dictionary[key];
                result += ReferenceEquals(key, dictionary) ? "(this)" : key.ToString2();
                result += "=" + value.ToString2();
                result += ReferenceEquals(value, dictionary) ? "(this)" : value.ToString2();

                if (i < dictionary.Count - 1)
                {
                    result += ", ";
                }

                i++;
            }

            result += "}";

            return result;
        }
    }
}