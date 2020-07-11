---
---常量
---@module ConstantConfig
---@author 自动生成
---

local Config = require("quan.config.Config")

---所有ConstantConfig
local configs = {
    { key = "constant1", itemId = 1, reward = nil, rewardList = {  }, comment = "常量1" },
    { key = "constant2", itemId = 2, reward = nil, rewardList = {  }, comment = "常量2" },
    { key = "constant2", itemId = 3, reward = nil, rewardList = {  }, comment = "常量3" },
    { key = "", itemId = 4, reward = nil, rewardList = {  }, comment = "常量4" },
}

---索引:常量Key
local keyConfigs = {}

---加载配置，建立索引
local function loadConfigs()
    for i, config in ipairs(configs) do
        Config.load(keyConfigs, config, true, { "key" }, { config.key })
    end
end

loadConfigs()

---常量
local ConstantConfig = {}

---
---获取所有ConstantConfig
---@return list<ConstantConfig>
function ConstantConfig.getConfigs()
    return configs
end

---
---通过索引[key]获取ConstantConfig
---@overload fun():map<key:string,ConstantConfig>
---@param key string 常量Key
---@return ConstantConfig
function ConstantConfig.getByKey(key)
    if (not key) then
        return keyConfigs
    end
    return keyConfigs[key]
end

return ConstantConfig