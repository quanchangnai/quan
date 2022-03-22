---
---卡片
---代码自动生成，请勿手动修改
---

local Config = require("quan.config.Config")

---所有CardConfig
local configs = {
}

---索引:ID
local idConfigs = {}

---索引:类型
local typeConfigs = {}

---加载配置，建立索引
local function loadConfigs()
    for i, config in ipairs(configs) do
        Config.load(idConfigs, config, true, { "id" }, { config.id })
        Config.load(typeConfigs, config, false, { "type" }, { config.type })
    end
end

loadConfigs()

---卡片
local CardConfig = {}

---
---获取所有CardConfig
---@return list<CardConfig>
function CardConfig.getAll()
    return configs
end

---
---获取所有CardConfig
---@return map<id:int,CardConfig>
function CardConfig.getIdAll()
     return idConfigs
end

---
---通过索引[id]获取CardConfig
---@overload fun():map<id:int,CardConfig>
---@param id int ID
---@return CardConfig
function CardConfig.get(id)
    if (not id) then
        return idConfigs
    end
    return idConfigs[id]
end

---
---获取所有CardConfig
---@return map<type:int,list<CardConfig>> 
function CardConfig.getTypeAll()
     return typeConfigs
end

---
---通过索引[type]获取CardConfig
---@overload fun():map<type:int,list<CardConfig>> 
---@param type int 类型
---@return list<CardConfig>
function CardConfig.getByType(type)
    return typeConfigs[type] or table.empty()
end

return CardConfig