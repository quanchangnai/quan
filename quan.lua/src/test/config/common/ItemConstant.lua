local ConstantConfig = require("test.config.common.ConstantConfig")

---
---@author 代码自动生成，请勿手动修改
local ItemConstant = {}

---
---常量1
---@return int
function ItemConstant.constant1()
    return ConstantConfig.getByKey("constant1").itemId
end

---
---常量3
---@return int
function ItemConstant.constant2()
    return ConstantConfig.getByKey("constant2").itemId
end

return ItemConstant