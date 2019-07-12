using System;

namespace quan.message.test
{
    public class RoleInfo : Bean
    {
        public long roleId { get; set; } = 0L; //角色id

        private string _roleName = ""; //角色名

        public string roleName
        {
            get => _roleName;
            set => _roleName = value ?? throw new NullReferenceException();
        }


        public override void encode(Buffer buffer)
        {
            base.encode(buffer);
            buffer.WriteLong(roleId);
            buffer.WriteString(roleName);
        }

        public override void decode(Buffer buffer)
        {
            base.decode(buffer);
            roleId = buffer.ReadLong();
            roleName = buffer.ReadString();
        }

        public override string ToString()
        {
            return $"{nameof(roleId)}: {roleId}, {nameof(roleName)}: {roleName}";
        }
    }
}