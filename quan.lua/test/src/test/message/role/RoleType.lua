require("quan.message.Message")

---
---角色类型
---自动生成
---
local RoleType = {
    --- 角色类型1
    type1 = 1,
    --- 角色类型2
    type2 = 2
}

RoleType = table.readOnly(RoleType)
return RoleType