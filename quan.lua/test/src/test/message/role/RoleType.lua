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

local meta = {
    __index = RoleType,
    __newindex = function()
        error("枚举不能修改")
    end
}

return setmetatable({}, meta)
