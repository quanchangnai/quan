using System.Collections.Generic;

namespace Quan.Message
{
    public abstract class Bean
    {
        public byte[] Encode()
        {
            var buffer = new Buffer();
            Encode(buffer);
            return buffer.AvailableBytes();
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

        protected static string ToString<T>(ICollection<T> collection)
        {
            if (collection == null)
            {
                return "null";
            }

            var result = "[";

            var i = 0;
            foreach (var item in collection)
            {
                result += ReferenceEquals(item, collection) ? "(this)" : item.ToString();

                if (i < collection.Count - 1)
                {
                    result += ", ";
                }

                i++;
            }

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
                result += ReferenceEquals(key, dictionary) ? "(this)" : key.ToString();
                result += "=" + value;
                result += ReferenceEquals(value, dictionary) ? "(this)" : value.ToString();

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