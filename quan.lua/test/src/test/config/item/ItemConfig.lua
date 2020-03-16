local Config = require("quan.config.Config")

---所有ItemConfig
local configs = {
    { id = 1, key = "item1", name = "道具1", type = 1, reward = { itemId = 3, itemNum = 10 }, list = { 11, 112, 322 }, set = { 465, 554, 655, 233 }, map = { [122] = 2, [322] = 22, [455] = 33 }, effectiveTime = "2019.08.05 12.33.55" },    
    { id = 2, key = "item2", name = "道具2", type = 1, reward = { itemId = 2, itemNum = 2 }, list = { 56, 22, 21 }, set = { 244 }, map = { }, effectiveTime = "" },    
    { id = 2, key = "", name = "道具3", type = 2, reward = { itemId = 2, itemNum = 11 }, list = { 44, 223, 342, 45 }, set = {  }, map = { [43] = 45 }, effectiveTime = "" },    
}

---ID
local idConfigs = {}

---常量Key
local keyConfigs = {}

---加载配置，建立索引
local function loadConfigs()
    local EquipConfig = require("test.config.item.EquipConfig")
    for i, equipConfig in ipairs(EquipConfig.getConfigs()) do
        table.insert(configs, equipConfig)
    end

    for i, config in ipairs(configs) do
        Config.load(idConfigs, config, true, { "id" }, { config.id })
        Config.load(keyConfigs, config, true, { "key" }, { config.key })
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
---@param id int ID
---@return map<id int,ItemConfig> | ItemConfig
function ItemConfig.getById(id)
    if (not id) then
        return idConfigs
    end
    return idConfigs[id]
end

---
---通过索引[key]获取ItemConfig
---@param key string 常量Key
---@return map<key string,ItemConfig> | ItemConfig
function ItemConfig.getByKey(key)
    if (not key) then
        return keyConfigs
    end
    return keyConfigs[key]
end

return ItemConfig