using System;

namespace quan.message.test
{
    internal class Test
    {
        static void Main(string[] args)
        {
            Buffer buffer = new Buffer(100);
            buffer.WriteBool(false);
            buffer.WriteInt(231);
            buffer.WriteFloat(424.4F);
            buffer.WriteDouble(23421.424D);
            buffer.WriteString("asddada");

            Console.WriteLine(buffer.ReadBool());
            Console.WriteLine(buffer.ReadInt());
            Console.WriteLine(buffer.ReadFloat());
            Console.WriteLine(buffer.ReadDouble());
            Console.WriteLine(buffer.ReadString());
        }
    }
}