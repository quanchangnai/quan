local ConstantConfig = require("test.config.common.ConstantConfig")

---
---@author 代码自动生成，请勿手动修改
local RewardConstant = {}

---
---@return list<Reward>
function RewardConstant.constant1()
    return ConstantConfig.getByKey("constant1").rewardList
end

---
---@return list<Reward>
function RewardConstant.constant2()
    return ConstantConfig.getByKey("constant2").rewardList
end

return RewardConstant