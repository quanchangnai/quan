using System;
using System.Collections.Generic;

namespace Quan.Utils
{
    public static class StringUtils
    {
        public static string ToString2(this object obj)
        {
            return obj == null ? "null" : obj.ToString();
        }

        public static string ToString2(this byte[] bytes)
        {
            return bytes == null ? "null" : Convert.ToBase64String(bytes);
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
                result += item.ToString2();

                if (i < collection.Count - 1)
                {
                    result += ", ";
                }

                i++;
            }

            result += "]";

            return result;
        }

        public static string ToString2<TK, TV>(this IDictionary<TK, TV> dictionary)
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
                result += key.ToString2() + "=" + value.ToString2();
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