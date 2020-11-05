---
---CardConfig
---代码自动生成，请勿手动修改
---

local Config = require("quan.config.Config")

---所有CardConfig
local configs = {
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

---CardConfig
local CardConfig = {}

---
---获取所有CardConfig
---@return list<CardConfig>
function CardConfig.getConfigs()
    return configs
end

---
---通过索引[id]获取CardConfig
---@overload fun():map<id:int,CardConfig>
---@param id int ID
---@return CardConfig
function CardConfig.getById(id)
    if (not id) then
        return idConfigs
    end
    return idConfigs[id]
end

return CardConfig