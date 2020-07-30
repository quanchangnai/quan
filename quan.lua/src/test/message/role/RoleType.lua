require("quan.table")

---
---角色类型
---自动生成，请勿修改
---
local RoleType = {
    --- 角色类型1
    type1 = 1,
    --- 角色类型2
    type2 = 2
}

RoleType = table.readOnly(RoleType)
return RoleType