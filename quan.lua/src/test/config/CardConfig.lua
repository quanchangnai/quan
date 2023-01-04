---
---卡片
---代码自动生成，请勿手动修改
---

local Config = require("quan.config.Config")

---所有CardConfig
local configs = {
    { id = 1, key = "card1", name = "卡片1", type = 1, list = { 11 }, set = { 465, 554, 655 }, map = { [122] = 2, [322] = 22, [455] = 33 }, effectiveTime = 1564979635000, effectiveTime_ = "2019-08-05 12:33:55" },
    { id = 2, key = "card2", name = "卡片2", type = 1, list = { 56, 22 }, set = { 244 }, map = { }, effectiveTime = 0, effectiveTime_ = "" },
    { id = 3, key = "", name = "卡片3", type = 2, list = { 44, 223 }, set = {  }, map = { [43] = 45 }, effectiveTime = 0, effectiveTime_ = "" },
}

---索引:ID
local idConfigs = {}

---索引:类型
local typeConfigs = {}

local keyConfigs = {}

---加载配置，建立索引
local function loadConfigs()
    for i, config in ipairs(configs) do
        Config.load(idConfigs, config, true, { "id" }, { config.id })
        Config.load(typeConfigs, config, false, { "type" }, { config.type })
        Config.load(keyConfigs, config, true, { "key" }, { config.key })
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
---@return map<type:CardType,list<CardConfig>> 
function CardConfig.getTypeAll()
     return typeConfigs
end

---
---通过索引[type]获取CardConfig
---@overload fun():map<type:CardType,list<CardConfig>> 
---@param type CardType 类型
---@return list<CardConfig>
function CardConfig.getByType(type)
    return typeConfigs[type] or table.empty()
end

---
---获取所有CardConfig
---@return map<key:string,CardConfig>
function CardConfig.getKeyAll()
     return keyConfigs
end

---
---通过索引[key]获取CardConfig
---@overload fun():map<key:string,CardConfig>
---@param key string 常量Key
---@return CardConfig
function CardConfig.getByKey(key)
    if (not key) then
        return keyConfigs
    end
    return keyConfigs[key]
end

return CardConfig