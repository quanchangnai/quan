local ConstantConfig = require("test.config.common.ConstantConfig")

---
---@author 自动生成
local RewardConstant = {}

---
---@return list<item.Reward>
function RewardConstant.constant1()
    return ConstantConfig.getByKey("constant1").rewardList
end

---
---@return list<item.Reward>
function RewardConstant.constant2()
    return ConstantConfig.getByKey("constant2").rewardList
end

return RewardConstant