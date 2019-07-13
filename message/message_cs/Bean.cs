using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Policy;

namespace message_cs
{
    public abstract class Bean
    {
        public byte[] Encode()
        {
            var buffer = new Buffer();
            Encode(buffer);
            return buffer.RemainingBytes();
        }

        public virtual void Encode(Buffer buffer)
        {
        }

        public void Decode(byte[] bytes)
        {
            var buffer = new Buffer(bytes);
            Decode(buffer);
        }

        public virtual void Decode(Buffer buffer)
        {
        }

        public static string ToString<T>(List<T> list)
        {
            if (list == null)
            {
                return "null";
            }

            var result = "[";
            result += string.Join(", ", list.ToArray());
            result += "]";

            return result;
        }

        public static string ToString<T>(HashSet<T> set)
        {
            if (set == null)
            {
                return "null";
            }

            var result = "[";
            result += string.Join(", ", set.ToArray());
            result += "]";

            return result;
        }

        protected static string ToString<TK, TV>(Dictionary<TK, TV> dictionary)
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
                result += ", " + key + "=" + value;
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