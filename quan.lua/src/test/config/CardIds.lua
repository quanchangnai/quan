local CardConfig = require("test.config.CardConfig")

---
---卡片ID
---@author 代码自动生成，请勿手动修改
local CardIds = {}

---
---卡片1
---@return int
function CardIds.card1()
    return CardConfig.getByKey("card1").id
end

---
---卡片2
---@return int
function CardIds.card2()
    return CardConfig.getByKey("card2").id
end

return CardIds