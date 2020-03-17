---
---QuestTargetConfig
---@module QuestTargetConfig
---@author 自动生成
---

local Config = require("quan.config.Config")

---所有QuestTargetConfig
local configs = {
    { id = 1, name = "任务目标1" },
}

---索引:ID
local idConfigs = {}

---加载配置，建立索引
local function loadConfigs()
    for i, config in ipairs(configs) do
        Config.load(idConfigs, config, true, { "id" }, { config.id })
    end
end

loadConfigs()

---QuestTargetConfig
local QuestTargetConfig = {}

---
---获取所有QuestTargetConfig
---@return list<QuestTargetConfig>
function QuestTargetConfig.getConfigs()
    return configs
end

---
---通过索引[id]获取QuestTargetConfig
---@param id int ID
---  map<id int,QuestTargetConfig> | QuestTargetConfig
function QuestTargetConfig.getById(id)
    if (not id) then
        return idConfigs
    end
    return idConfigs[id]
end

return QuestTargetConfig