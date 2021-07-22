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
    { id = 10, key = "", name = "道具10", type = 1, useEffect = { class = "UseEffect", aaa = 123 }, reward = { itemId = 3, itemNum = 10 }, list = { 11, 112, 322 }, set = { 465, 554, 655, 233 }, map = { [122] = 2, [322] = 22, [455] = 33 }, effectiveTime = 1564979635000, effectiveTime_ = "2019-08-05 12:33:55" },
    { id = 11, key = "", name = "道具11", type = 1, useEffect = { class = "UseEffect", aaa = 123 }, reward = { itemId = 3, itemNum = 10 }, list = { 11, 112, 322 }, set = { 465, 554, 655, 233 }, map = { [122] = 2, [322] = 22, [455] = 33 }, effectiveTime = 1564979635000, effectiveTime_ = "2019-08-05 12:33:55" },
    { id = 12, key = "", name = "道具12", type = 1, useEffect = { class = "UseEffect", aaa = 123 }, reward = { itemId = 3, itemNum = 10 }, list = { 11, 112, 322 }, set = { 465, 554, 655, 233 }, map = { [122] = 2, [322] = 22, [455] = 33 }, effectiveTime = 1564979635000, effectiveTime_ = "2019-08-05 12:33:55" },
    { id = 13, key = "", name = "道具13", type = 1, useEffect = { class = "UseEffect", aaa = 123 }, reward = { itemId = 3, itemNum = 10 }, list = { 11, 112, 322 }, set = { 465, 554, 655, 233 }, map = { [122] = 2, [322] = 22, [455] = 33 }, effectiveTime = 1564979635000, effectiveTime_ = "2019-08-05 12:33:55" },
    { id = 14, key = "", name = "道具14", type = 1, useEffect = { class = "UseEffect", aaa = 123 }, reward = { itemId = 3, itemNum = 10 }, list = { 11, 112, 322 }, set = { 465, 554, 655, 233 }, map = { [122] = 2, [322] = 22, [455] = 33 }, effectiveTime = 1564979635000, effectiveTime_ = "2019-08-05 12:33:55" },
    { id = 15, key = "", name = "道具15", type = 1, useEffect = { class = "UseEffect", aaa = 123 }, reward = { itemId = 3, itemNum = 10 }, list = { 11, 112, 322 }, set = { 465, 554, 655, 233 }, map = { [122] = 2, [322] = 22, [455] = 33 }, effectiveTime = 1564979635000, effectiveTime_ = "2019-08-05 12:33:55" },
    { id = 16, key = "", name = "道具16", type = 1, useEffect = { class = "UseEffect", aaa = 123 }, reward = { itemId = 3, itemNum = 10 }, list = { 11, 112, 322 }, set = { 465, 554, 655, 233 }, map = { [122] = 2, [322] = 22, [455] = 33 }, effectiveTime = 1564979635000, effectiveTime_ = "2019-08-05 12:33:55" },
    { id = 17, key = "", name = "道具17", type = 1, useEffect = { class = "UseEffect", aaa = 123 }, reward = { itemId = 3, itemNum = 10 }, list = { 11, 112, 322 }, set = { 465, 554, 655, 233 }, map = { [122] = 2, [322] = 22, [455] = 33 }, effectiveTime = 1564979635000, effectiveTime_ = "2019-08-05 12:33:55" },
    { id = 18, key = "", name = "道具18", type = 1, useEffect = { class = "UseEffect", aaa = 123 }, reward = { itemId = 3, itemNum = 10 }, list = { 11, 112, 322 }, set = { 465, 554, 655, 233 }, map = { [122] = 2, [322] = 22, [455] = 33 }, effectiveTime = 1564979635000, effectiveTime_ = "2019-08-05 12:33:55" },
    { id = 19, key = "", name = "道具19", type = 1, useEffect = { class = "UseEffect", aaa = 123 }, reward = { itemId = 3, itemNum = 10 }, list = { 11, 112, 322 }, set = { 465, 554, 655, 233 }, map = { [122] = 2, [322] = 22, [455] = 33 }, effectiveTime = 1564979635000, effectiveTime_ = "2019-08-05 12:33:55" },
    { id = 20, key = "", name = "道具20", type = 1, useEffect = { class = "UseEffect", aaa = 123 }, reward = { itemId = 3, itemNum = 10 }, list = { 11, 112, 322 }, set = { 465, 554, 655, 233 }, map = { [122] = 2, [322] = 22, [455] = 33 }, effectiveTime = 1564979635000, effectiveTime_ = "2019-08-05 12:33:55" },
    { id = 21, key = "", name = "道具21", type = 1, useEffect = { class = "UseEffect", aaa = 123 }, reward = { itemId = 3, itemNum = 10 }, list = { 11, 112, 322 }, set = { 465, 554, 655, 233 }, map = { [122] = 2, [322] = 22, [455] = 33 }, effectiveTime = 1564979635000, effectiveTime_ = "2019-08-05 12:33:55" },
    { id = 22, key = "", name = "道具22", type = 1, useEffect = { class = "UseEffect", aaa = 123 }, reward = { itemId = 3, itemNum = 10 }, list = { 11, 112, 322 }, set = { 465, 554, 655, 233 }, map = { [122] = 2, [322] = 22, [455] = 33 }, effectiveTime = 1564979635000, effectiveTime_ = "2019-08-05 12:33:55" },
    { id = 23, key = "", name = "道具23", type = 1, useEffect = { class = "UseEffect", aaa = 123 }, reward = { itemId = 3, itemNum = 10 }, list = { 11, 112, 322 }, set = { 465, 554, 655, 233 }, map = { [122] = 2, [322] = 22, [455] = 33 }, effectiveTime = 1564979635000, effectiveTime_ = "2019-08-05 12:33:55" },
    { id = 24, key = "", name = "道具24", type = 1, useEffect = { class = "UseEffect", aaa = 123 }, reward = { itemId = 3, itemNum = 10 }, list = { 11, 112, 322 }, set = { 465, 554, 655, 233 }, map = { [122] = 2, [322] = 22, [455] = 33 }, effectiveTime = 1564979635000, effectiveTime_ = "2019-08-05 12:33:55" },
    { id = 25, key = "", name = "道具25", type = 1, useEffect = { class = "UseEffect", aaa = 123 }, reward = { itemId = 3, itemNum = 10 }, list = { 11, 112, 322 }, set = { 465, 554, 655, 233 }, map = { [122] = 2, [322] = 22, [455] = 33 }, effectiveTime = 1564979635000, effectiveTime_ = "2019-08-05 12:33:55" },
    { id = 26, key = "", name = "道具26", type = 1, useEffect = { class = "UseEffect", aaa = 123 }, reward = { itemId = 3, itemNum = 10 }, list = { 11, 112, 322 }, set = { 465, 554, 655, 233 }, map = { [122] = 2, [322] = 22, [455] = 33 }, effectiveTime = 1564979635000, effectiveTime_ = "2019-08-05 12:33:55" },
    { id = 27, key = "", name = "道具27", type = 1, useEffect = { class = "UseEffect", aaa = 123 }, reward = { itemId = 3, itemNum = 10 }, list = { 11, 112, 322 }, set = { 465, 554, 655, 233 }, map = { [122] = 2, [322] = 22, [455] = 33 }, effectiveTime = 1564979635000, effectiveTime_ = "2019-08-05 12:33:55" },
    { id = 28, key = "", name = "道具28", type = 1, useEffect = { class = "UseEffect", aaa = 123 }, reward = { itemId = 3, itemNum = 10 }, list = { 11, 112, 322 }, set = { 465, 554, 655, 233 }, map = { [122] = 2, [322] = 22, [455] = 33 }, effectiveTime = 1564979635000, effectiveTime_ = "2019-08-05 12:33:55" },
    { id = 29, key = "", name = "道具29", type = 1, useEffect = { class = "UseEffect", aaa = 123 }, reward = { itemId = 3, itemNum = 10 }, list = { 11, 112, 322 }, set = { 465, 554, 655, 233 }, map = { [122] = 2, [322] = 22, [455] = 33 }, effectiveTime = 1564979635000, effectiveTime_ = "2019-08-05 12:33:55" },
    { id = 30, key = "", name = "道具30", type = 1, useEffect = { class = "UseEffect", aaa = 123 }, reward = { itemId = 3, itemNum = 10 }, list = { 11, 112, 322 }, set = { 465, 554, 655, 233 }, map = { [122] = 2, [322] = 22, [455] = 33 }, effectiveTime = 1564979635000, effectiveTime_ = "2019-08-05 12:33:55" },
    { id = 31, key = "", name = "道具31", type = 1, useEffect = { class = "UseEffect", aaa = 123 }, reward = { itemId = 3, itemNum = 10 }, list = { 11, 112, 322 }, set = { 465, 554, 655, 233 }, map = { [122] = 2, [322] = 22, [455] = 33 }, effectiveTime = 1564979635000, effectiveTime_ = "2019-08-05 12:33:55" },
    { id = 32, key = "", name = "道具32", type = 1, useEffect = { class = "UseEffect", aaa = 123 }, reward = { itemId = 3, itemNum = 10 }, list = { 11, 112, 322 }, set = { 465, 554, 655, 233 }, map = { [122] = 2, [322] = 22, [455] = 33 }, effectiveTime = 1564979635000, effectiveTime_ = "2019-08-05 12:33:55" },
    { id = 33, key = "", name = "道具33", type = 1, useEffect = { class = "UseEffect", aaa = 123 }, reward = { itemId = 3, itemNum = 10 }, list = { 11, 112, 322 }, set = { 465, 554, 655, 233 }, map = { [122] = 2, [322] = 22, [455] = 33 }, effectiveTime = 1564979635000, effectiveTime_ = "2019-08-05 12:33:55" },
    { id = 34, key = "", name = "道具34", type = 1, useEffect = { class = "UseEffect", aaa = 123 }, reward = { itemId = 3, itemNum = 10 }, list = { 11, 112, 322 }, set = { 465, 554, 655, 233 }, map = { [122] = 2, [322] = 22, [455] = 33 }, effectiveTime = 1564979635000, effectiveTime_ = "2019-08-05 12:33:55" },
    { id = 35, key = "", name = "道具35", type = 1, useEffect = { class = "UseEffect", aaa = 123 }, reward = { itemId = 3, itemNum = 10 }, list = { 11, 112, 322 }, set = { 465, 554, 655, 233 }, map = { [122] = 2, [322] = 22, [455] = 33 }, effectiveTime = 1564979635000, effectiveTime_ = "2019-08-05 12:33:55" },
    { id = 36, key = "", name = "道具36", type = 1, useEffect = { class = "UseEffect", aaa = 123 }, reward = { itemId = 3, itemNum = 10 }, list = { 11, 112, 322 }, set = { 465, 554, 655, 233 }, map = { [122] = 2, [322] = 22, [455] = 33 }, effectiveTime = 1564979635000, effectiveTime_ = "2019-08-05 12:33:55" },
    { id = 37, key = "", name = "道具37", type = 1, useEffect = { class = "UseEffect", aaa = 123 }, reward = { itemId = 3, itemNum = 10 }, list = { 11, 112, 322 }, set = { 465, 554, 655, 233 }, map = { [122] = 2, [322] = 22, [455] = 33 }, effectiveTime = 1564979635000, effectiveTime_ = "2019-08-05 12:33:55" },
    { id = 38, key = "", name = "道具38", type = 1, useEffect = { class = "UseEffect", aaa = 123 }, reward = { itemId = 3, itemNum = 10 }, list = { 11, 112, 322 }, set = { 465, 554, 655, 233 }, map = { [122] = 2, [322] = 22, [455] = 33 }, effectiveTime = 1564979635000, effectiveTime_ = "2019-08-05 12:33:55" },
    { id = 39, key = "", name = "道具39", type = 1, useEffect = { class = "UseEffect", aaa = 123 }, reward = { itemId = 3, itemNum = 10 }, list = { 11, 112, 322 }, set = { 465, 554, 655, 233 }, map = { [122] = 2, [322] = 22, [455] = 33 }, effectiveTime = 1564979635000, effectiveTime_ = "2019-08-05 12:33:55" },
    { id = 40, key = "", name = "道具40", type = 1, useEffect = { class = "UseEffect", aaa = 123 }, reward = { itemId = 3, itemNum = 10 }, list = { 11, 112, 322 }, set = { 465, 554, 655, 233 }, map = { [122] = 2, [322] = 22, [455] = 33 }, effectiveTime = 1564979635000, effectiveTime_ = "2019-08-05 12:33:55" },
    { id = 41, key = "", name = "道具41", type = 1, useEffect = { class = "UseEffect", aaa = 123 }, reward = { itemId = 3, itemNum = 10 }, list = { 11, 112, 322 }, set = { 465, 554, 655, 233 }, map = { [122] = 2, [322] = 22, [455] = 33 }, effectiveTime = 1564979635000, effectiveTime_ = "2019-08-05 12:33:55" },
    { id = 42, key = "", name = "道具42", type = 1, useEffect = { class = "UseEffect", aaa = 123 }, reward = { itemId = 3, itemNum = 10 }, list = { 11, 112, 322 }, set = { 465, 554, 655, 233 }, map = { [122] = 2, [322] = 22, [455] = 33 }, effectiveTime = 1564979635000, effectiveTime_ = "2019-08-05 12:33:55" },
    { id = 43, key = "", name = "道具43", type = 1, useEffect = { class = "UseEffect", aaa = 123 }, reward = { itemId = 3, itemNum = 10 }, list = { 11, 112, 322 }, set = { 465, 554, 655, 233 }, map = { [122] = 2, [322] = 22, [455] = 33 }, effectiveTime = 1564979635000, effectiveTime_ = "2019-08-05 12:33:55" },
    { id = 44, key = "", name = "道具44", type = 1, useEffect = { class = "UseEffect", aaa = 123 }, reward = { itemId = 3, itemNum = 10 }, list = { 11, 112, 322 }, set = { 465, 554, 655, 233 }, map = { [122] = 2, [322] = 22, [455] = 33 }, effectiveTime = 1564979635000, effectiveTime_ = "2019-08-05 12:33:55" },
    { id = 45, key = "", name = "道具45", type = 1, useEffect = { class = "UseEffect", aaa = 123 }, reward = { itemId = 3, itemNum = 10 }, list = { 11, 112, 322 }, set = { 465, 554, 655, 233 }, map = { [122] = 2, [322] = 22, [455] = 33 }, effectiveTime = 1564979635000, effectiveTime_ = "2019-08-05 12:33:55" },
    { id = 46, key = "", name = "道具46", type = 1, useEffect = { class = "UseEffect", aaa = 123 }, reward = { itemId = 3, itemNum = 10 }, list = { 11, 112, 322 }, set = { 465, 554, 655, 233 }, map = { [122] = 2, [322] = 22, [455] = 33 }, effectiveTime = 1564979635000, effectiveTime_ = "2019-08-05 12:33:55" },
    { id = 47, key = "", name = "道具47", type = 1, useEffect = { class = "UseEffect", aaa = 123 }, reward = { itemId = 3, itemNum = 10 }, list = { 11, 112, 322 }, set = { 465, 554, 655, 233 }, map = { [122] = 2, [322] = 22, [455] = 33 }, effectiveTime = 1564979635000, effectiveTime_ = "2019-08-05 12:33:55" },
    { id = 48, key = "", name = "道具48", type = 1, useEffect = { class = "UseEffect", aaa = 123 }, reward = { itemId = 3, itemNum = 10 }, list = { 11, 112, 322 }, set = { 465, 554, 655, 233 }, map = { [122] = 2, [322] = 22, [455] = 33 }, effectiveTime = 1564979635000, effectiveTime_ = "2019-08-05 12:33:55" },
    { id = 49, key = "", name = "道具49", type = 1, useEffect = { class = "UseEffect", aaa = 123 }, reward = { itemId = 3, itemNum = 10 }, list = { 11, 112, 322 }, set = { 465, 554, 655, 233 }, map = { [122] = 2, [322] = 22, [455] = 33 }, effectiveTime = 1564979635000, effectiveTime_ = "2019-08-05 12:33:55" },
    { id = 50, key = "", name = "道具50", type = 1, useEffect = { class = "UseEffect", aaa = 123 }, reward = { itemId = 3, itemNum = 10 }, list = { 11, 112, 322 }, set = { 465, 554, 655, 233 }, map = { [122] = 2, [322] = 22, [455] = 33 }, effectiveTime = 1564979635000, effectiveTime_ = "2019-08-05 12:33:55" },
    { id = 51, key = "", name = "道具51", type = 1, useEffect = { class = "UseEffect", aaa = 123 }, reward = { itemId = 3, itemNum = 10 }, list = { 11, 112, 322 }, set = { 465, 554, 655, 233 }, map = { [122] = 2, [322] = 22, [455] = 33 }, effectiveTime = 1564979635000, effectiveTime_ = "2019-08-05 12:33:55" },
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