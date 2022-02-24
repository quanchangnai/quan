require("quan.table")

---
---用户类型
---代码自动生成，请勿手动修改
---
local UserType = {
    --- 用户类型1
    type1 = 1,
    --- 用户类型2
    type2 = 2
}

UserType = table.readOnly(UserType)
return UserType