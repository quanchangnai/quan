using System;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using MessageCS;
using MessageCS.Test.Role;
using MessageCS.Test.User;

namespace MessageCS.Test
{
    internal class MessageTest
    {
        static void Main(string[] args)
        {
            Test1();
            Test2();
//            Test3();
//            Console.ReadLine();
        }

        private static void Test1()
        {
            Console.WriteLine("Test1====================");

            var buffer = new Buffer(100);
            buffer.WriteBool(false);
            buffer.WriteInt(231);
            buffer.WriteFloat(424.4F);
            buffer.WriteDouble(23421.424D);
            buffer.WriteString("张三1111");

            Console.WriteLine("buffer.Available:" + buffer.Available());

            Console.WriteLine(buffer.ReadBool());
            Console.WriteLine(buffer.ReadInt());
            Console.WriteLine(buffer.ReadFloat());
            Console.WriteLine(buffer.ReadDouble());
            Console.WriteLine(buffer.ReadString());
        }

        private static void Test2()
        {
            Console.WriteLine("Test2====================");

            var sRoleLogin1 = new SRoleLogin {roleId = 1111, roleName = "张三1111"};

            var roleInfo1 = new RoleInfo {id = 111, roleName = "aaa", roleType = RoleType.type1};

            sRoleLogin1.roleInfo = roleInfo1;

            var roleInfo2 = new RoleInfo {id = 222, roleName = "bbb", roleType = RoleType.type2};
            roleInfo2.set.Add(2213);

            sRoleLogin1.roleInfoList.Add(roleInfo2);
            sRoleLogin1.roleInfoSet.Add(roleInfo2);
            sRoleLogin1.roleInfoMap.Add(roleInfo2.id, roleInfo2);

            Console.WriteLine("sRoleLogin1:" + sRoleLogin1);

            var encodedBytes = sRoleLogin1.Encode();

            Console.WriteLine("encodedBytes.Length:{0}", encodedBytes.Length);

            var sRoleLogin2 = new SRoleLogin();
            sRoleLogin2.Decode(encodedBytes);

            Console.WriteLine("sRoleLogin2" + sRoleLogin2);
        }

        private static void Test3()
        {
            Console.WriteLine("Test3====================");

            var sRoleLogin1 = new SRoleLogin {roleId = 1111, roleName = "张三1111"};

            var roleInfo = new RoleInfo{id = 1312, roleName = "李四1123123", f = 343.4532F, d = 4242.54453D};

            sRoleLogin1.roleInfo = roleInfo;


            var encodedBytes = sRoleLogin1.Encode();

            var lengthBytes = BitConverter.GetBytes(IPAddress.HostToNetworkOrder(encodedBytes.Length));

            var sendBytes = new byte[encodedBytes.Length + lengthBytes.Length];
            Array.Copy(lengthBytes, sendBytes, lengthBytes.Length);
            Array.Copy(encodedBytes, 0, sendBytes, lengthBytes.Length, encodedBytes.Length);

            var ip = IPAddress.Parse("127.0.0.1");
            var port = 9898;

            var socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);

            socket.Connect(new IPEndPoint(ip, port));

            while (true)
            {
                Thread.Sleep(3000);
                socket.Send(sendBytes);

            }


            socket.Close();
        }
    }
}