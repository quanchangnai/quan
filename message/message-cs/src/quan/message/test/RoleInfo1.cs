using System;

namespace quan.message.test
{
    public class RoleInfo1 : Bean
    {
        public long roleId { get; set; } = 0L; //角色id

        private string _roleName = ""; //角色名

        public string roleName
        {
            get => _roleName;
            set => _roleName = value ?? throw new NullReferenceException();
        }


        public override void Encode(Buffer buffer)
        {
            base.Encode(buffer);
            buffer.WriteLong(roleId);
            buffer.WriteString(roleName);
        }

        public override void Decode(Buffer buffer)
        {
            base.Decode(buffer);
            roleId = buffer.ReadLong();
            roleName = buffer.ReadString();
        }

        public override string ToString()
        {
            return $"{nameof(roleId)}: {roleId}, {nameof(roleName)}: {roleName}";
        }
    }
}