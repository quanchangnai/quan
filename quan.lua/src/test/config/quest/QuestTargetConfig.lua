---
---QuestTargetConfig
---代码自动生成，请勿手动修改
---

local Config = require("quan.config.Config")

---所有QuestTargetConfig
local configs = {
    { id = 1, noon = 0, noon_ = "" },
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
---@overload fun():map<id:int,QuestTargetConfig>
---@param id int ID
---@return QuestTargetConfig
function QuestTargetConfig.get(id)
    if (not id) then
        return idConfigs
    end
    return idConfigs[id]
end

return QuestTargetConfig