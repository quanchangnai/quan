using System;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Reflection;
using System.Threading;
using NUnit.Framework;
using Quan.Message;
using Test.Message.Role;
using RoleInfo = Test.Message.Role.RoleInfo;
using Test.Message.User;

namespace Test
{
    [TestFixture]
    public class MessageTest
    {
        [Test]
        public void Test1()
        {
            Console.WriteLine("Test1====================");

            var buffer = new CodedBuffer();
            buffer.WriteBool(true);
            buffer.WriteInt(70);
            buffer.WriteInt(2423);
            buffer.WriteFloat(13.43F);
            buffer.WriteDouble(4242.432);
            buffer.WriteFloat(132.32434F, 2);
            buffer.WriteDouble(342254.653254, 2);
            buffer.WriteString("搭顺风车");
            buffer.WriteLong(long.MaxValue);
            buffer.WriteLong(long.MinValue);
            buffer.WriteTag(253);

            Console.WriteLine("buffer.ReadableCount:{0}", buffer.ReadableCount);

            // var fileStream = File.Open("D:\\buffer", FileMode.Open);
            // var bytes = new byte[fileStream.Length];
            // fileStream.Read(bytes, 0, bytes.Length);
            // Console.WriteLine($"bytes.Length={bytes.Length}");
            // buffer = new Buffer(bytes);

            Console.WriteLine(buffer.ReadBool());
            Console.WriteLine(buffer.ReadInt());
            Console.WriteLine(buffer.ReadInt());
            Console.WriteLine(buffer.ReadFloat());
            Console.WriteLine(buffer.ReadDouble());
            Console.WriteLine(buffer.ReadFloat(2));
            Console.WriteLine(buffer.ReadDouble(2));
            Console.WriteLine(buffer.ReadString());
            Console.WriteLine(buffer.ReadLong());
            Console.WriteLine(buffer.ReadLong());
            Console.WriteLine(buffer.ReadTag());
        }

        [Test]
        public void Test2()
        {
            Console.WriteLine("Test2====================");

            var sRoleLogin1 = new SRoleLogin { roleId = 1111, roleName = "张三1111" };

            var roleInfo1 = new RoleInfo { id = 111, name = "aaa", type = RoleType.type1, i = 5 };

            sRoleLogin1.roleInfo = roleInfo1;

            var roleInfo2 = new RoleInfo { id = 222, name = "bbb", type = RoleType.type2, i = 10 };
            roleInfo2.set.Add(2213);

            sRoleLogin1.roleInfoList.Add(roleInfo2);
            sRoleLogin1.roleInfoList.Add(roleInfo2);
            sRoleLogin1.roleInfoSet.Add(roleInfo2);
            sRoleLogin1.roleInfoSet.Add(roleInfo2);
            sRoleLogin1.roleInfoMap.Add(roleInfo2.id, roleInfo2);

            Console.WriteLine("sRoleLogin1:" + sRoleLogin1);

            var encodedBytes = sRoleLogin1.Encode();
            Console.WriteLine("encodedBytes.Length:{0}", encodedBytes.Length);

            // var fileStream1 = File.Open("D:\\SRoleLogin", FileMode.OpenOrCreate);
            // encodedBytes = sRoleLogin1.Encode();
            // fileStream1.Write(encodedBytes, 0, encodedBytes.Length);
            // fileStream1.Close();

            var fileStream2 = File.Open("D:\\SRoleLogin", FileMode.Open);
            encodedBytes = new byte[fileStream2.Length];
            fileStream2.Read(encodedBytes, 0, encodedBytes.Length);
            Console.WriteLine("fileStream.Length:{0}", fileStream2.Length);

            var sRoleLogin2 = new SRoleLogin();
            sRoleLogin2.Decode(encodedBytes);
            Console.WriteLine("encodedBytes.Length:{0}", encodedBytes.Length);

            Console.WriteLine("sRoleLogin2:" + sRoleLogin2);
        }

        [Test]
        public void Test3()
        {
            Console.WriteLine("Test3====================");
            var messageFactory = new MessageRegistry();
            messageFactory.Register(Assembly.GetExecutingAssembly().FullName);
            Console.WriteLine(messageFactory.Create(544233));
        }

        [Test]
        public void Test4()
        {
            Console.WriteLine("Test4====================");
            var fileStream = File.Open("D:\\UserInfo", FileMode.Open);
            var bytes = new byte[fileStream.Length];
            fileStream.Read(bytes, 0, bytes.Length);
            Console.WriteLine($"bytes.Length={bytes.Length}");
            var userInfo = new UserInfo();
            userInfo.Decode(bytes);
            Console.WriteLine($"userInfo:{userInfo}");
        }

        [Ignore("socket")]
        [Test]
        public void Test5()
        {
            Console.WriteLine("Test4====================");

            var sRoleLogin1 = new SRoleLogin { roleId = 1111, roleName = "张三1111" };

            var roleInfo = new RoleInfo { id = 1312, name = "李四1123123" };

            sRoleLogin1.roleInfo = roleInfo;


            var encodedBytes = sRoleLogin1.Encode();

            var lengthBytes = BitConverter.GetBytes(IPAddress.HostToNetworkOrder(encodedBytes.Length));

            var sendBytes = new byte[encodedBytes.Length + lengthBytes.Length];
            Array.Copy(lengthBytes, sendBytes, lengthBytes.Length);
            Array.Copy(encodedBytes, 0, sendBytes, lengthBytes.Length, encodedBytes.Length);

            var ip = IPAddress.Parse("127.0.0.1");
            const int port = 9898;

            var socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);

            socket.Connect(new IPEndPoint(ip, port));

            while (true)
            {
                Thread.Sleep(3000);
                socket.Send(sendBytes);
            }


//            socket.Close();
        }
    }
}