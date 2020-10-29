---
---道具
---代码自动生成，请勿手动修改
---

local Config = require("quan.config.Config")

---所有ItemConfig
local configs = {
    { id = 1, key = "item1", name = "道具1", type = 1, useEffect = { class = "UseEffect", aaa = 123 }, reward = { itemId = 3, itemNum = 10 }, list = { 11, 112, 322 }, set = { 465, 554, 655, 233 }, map = { [122] = 2, [322] = 22, [455] = 33 }, effectiveTime = 1564979635000, effectiveTime_ = "2019-08-05 12:33:55" },
    { id = 2, key = "item2", name = "道具2", type = 1, useEffect = { class = "UseEffect2", aaa = 456, bbb = 33 }, reward = { itemId = 2, itemNum = 2 }, list = { 56, 22, 21 }, set = { 244 }, map = { }, effectiveTime = 0, effectiveTime_ = "" },
    { id = 3, key = "", name = "道具3", type = 2, useEffect = nil, reward = { itemId = 2, itemNum = 11 }, list = { 44, 223, 342, 45 }, set = {  }, map = { [43] = 45 }, effectiveTime = 0, effectiveTime_ = "" },
}

---索引:ID
local idConfigs = {}

---索引:常量Key
local keyConfigs = {}

---索引:类型
local typeConfigs = {}

---加载配置，建立索引
local function loadConfigs()
    local EquipConfig = require("test.config.item.EquipConfig")
    for i, equipConfig in ipairs(EquipConfig.getConfigs()) do
        table.insert(configs, equipConfig)
    end

    for i, config in ipairs(configs) do
        Config.load(idConfigs, config, true, { "id" }, { config.id })
        Config.load(keyConfigs, config, true, { "key" }, { config.key })
        Config.load(typeConfigs, config, false, { "type" }, { config.type })
    end
end

loadConfigs()

---道具
local ItemConfig = {}

---
---获取所有ItemConfig
---@return list<ItemConfig>
function ItemConfig.getConfigs()
    return configs
end

---
---通过索引[id]获取ItemConfig
---@overload fun():map<id:int,ItemConfig>
---@param id int ID
---@return ItemConfig
function ItemConfig.getById(id)
    if (not id) then
        return idConfigs
    end
    return idConfigs[id]
end

---
---通过索引[key]获取ItemConfig
---@overload fun():map<key:string,ItemConfig>
---@param key string 常量Key
---@return ItemConfig
function ItemConfig.getByKey(key)
    if (not key) then
        return keyConfigs
    end
    return keyConfigs[key]
end

---
---通过索引[type]获取ItemConfig
---@overload fun():map<type:ItemType,list<ItemConfig>> 
---@param type ItemType 类型
---@return list<ItemConfig>
function ItemConfig.getByType(type)
    return typeConfigs[type] or table.empty()
end

return ItemConfig