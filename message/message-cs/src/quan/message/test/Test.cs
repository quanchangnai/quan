using System;

namespace quan.message.test
{
    internal class Test
    {
        static void Main(string[] args)
        {
            test2();
        }

        static void test1()
        {
            Buffer buffer = new Buffer(100);
            buffer.WriteBool(false);
            buffer.WriteInt(231);
            buffer.WriteFloat(424.4F);
            buffer.WriteDouble(23421.424D);
            buffer.WriteString("asddada");

            Console.WriteLine("test1====================");
            Console.WriteLine(buffer.ReadBool());
            Console.WriteLine(buffer.ReadInt());
            Console.WriteLine(buffer.ReadFloat());
            Console.WriteLine(buffer.ReadDouble());
            Console.WriteLine(buffer.ReadString());
        }

        static void test2()
        {
            SRoleLogin sRoleLogin1 = new SRoleLogin();
            sRoleLogin1.sn = 31312L;
            sRoleLogin1.roleId = 111;
            sRoleLogin1.roleName = "aaa";

            RoleInfo roleInfo1 = new RoleInfo();
            roleInfo1.roleId = 222;
            roleInfo1.roleName = "bbb";

            sRoleLogin1.roleInfo = roleInfo1;

            RoleInfo roleInfo2 = new RoleInfo();
            roleInfo2.roleId = 222;
            roleInfo2.roleName = "bbb";

            sRoleLogin1.roleInfoList.Add(roleInfo2);
            sRoleLogin1.roleInfoSet.Add(roleInfo2);
            sRoleLogin1.roleInfoMap.Add(roleInfo2.roleId, roleInfo2);

            byte[] bytes = sRoleLogin1.encode();

            SRoleLogin sRoleLogin2 = new SRoleLogin();
            sRoleLogin2.decode(bytes);


            Console.WriteLine("sRoleLogin1:" + sRoleLogin1);
            Console.WriteLine("sRoleLogin2" + sRoleLogin2);
        }
    }
}