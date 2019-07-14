using System;
using MessageCS;
using MessageCS.Test.Role;
using MessageCS.Test.User;

namespace MessageCS.Test
{
    internal class Test
    {
        static void Main(string[] args)
        {
            Test1();
            Test2();
            Test3();
            Console.ReadLine();
        }

        static void Test1()
        {
            Console.WriteLine("Test1====================");

            var buffer = new Buffer(100);
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

        static void Test2()
        {
            Console.WriteLine("Test2====================");

            var sRoleLogin1 = new SRoleLogin {sn = 31312L, roleId = 111, roleName = "aaa"};

            var roleInfo1 = new RoleInfo {roleId = 222, roleName = "bbb", roleType = RoleType.type1};

            sRoleLogin1.roleInfo = roleInfo1;

            var roleInfo2 = new RoleInfo {roleId = 222, roleName = "bbb", roleType = RoleType.type2};

            sRoleLogin1.roleInfoList.Add(roleInfo2);
            sRoleLogin1.roleInfoSet.Add(roleInfo2);
            sRoleLogin1.roleInfoMap.Add(roleInfo2.roleId, roleInfo2);

            var bytes = sRoleLogin1.Encode();

            var sRoleLogin2 = new SRoleLogin();
            sRoleLogin2.Decode(bytes);


            Console.WriteLine("sRoleLogin1:" + sRoleLogin1);
            Console.WriteLine("sRoleLogin2" + sRoleLogin2);
        }

        static void Test3()
        {
            Console.WriteLine("Test3====================");

            var userInfo = new UserInfo() {id = 1, name = "abc", level = 123};

            Console.WriteLine("userInfo:" + userInfo);
        }
    }
}