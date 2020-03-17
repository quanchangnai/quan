local ItemConfig = require("test.config.item.ItemConfig")

---
---道具ID
---@author 自动生成
local ItemIds = {}

---
---道具1
---@return int
function ItemIds.item1()
    return ItemConfig.getByKey("item1").id
end

---
---道具2
---@return int
function ItemIds.item2()
    return ItemConfig.getByKey("item2").id
end

return ItemIds