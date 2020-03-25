---
---装备1,装备2
---@module EquipConfig
---@author 自动生成
---

local Config = require("quan.config.Config")

---所有EquipConfig
local configs = {
    { id = 3, key = "", name = "装备1", type = 2, useEffect = nil, reward = { itemId = 2, itemNum = 11 }, list = { 44, 223, 342, 45 }, set = { 22, 23, 456 }, map = { [43] = 45 }, effectiveTime = 1565073325000, effectiveTime_ = "2019.08.06 14.35.25", position = 1, color = 2 },
    { id = 5, key = "", name = "装备2", type = 2, useEffect = nil, reward = { itemId = 6, itemNum = 11 }, list = { 44, 223, 342, 45 }, set = {  }, map = { [43] = 45 }, effectiveTime = 1565152435000, effectiveTime_ = "2019.08.07 12.33.55", position = 3, color = 2 },
}

---索引:ID
local idConfigs = {}

---索引:常量Key
local keyConfigs = {}

---索引:部位
local positionConfigs = {}

---加载配置，建立索引
local function loadConfigs()
    local WeaponConfig = require("test.config.item.WeaponConfig")
    for i, weaponConfig in ipairs(WeaponConfig.getConfigs()) do
        table.insert(configs, weaponConfig)
    end

    for i, config in ipairs(configs) do
        Config.load(idConfigs, config, true, { "id" }, { config.id })
        Config.load(keyConfigs, config, true, { "key" }, { config.key })
        Config.load(positionConfigs, config, false, { "position" }, { config.position })
    end
end

loadConfigs()

---装备1,装备2
local EquipConfig = {}

---
---获取所有EquipConfig
---@return list<EquipConfig>
function EquipConfig.getConfigs()
    return configs
end

---
---通过索引[id]获取EquipConfig
---@overload fun():map<id:int,EquipConfig>
---@param id int ID
---@return EquipConfig
function EquipConfig.getById(id)
    if (not id) then
        return idConfigs
    end
    return idConfigs[id]
end

---
---通过索引[key]获取EquipConfig
---@overload fun():map<key:string,EquipConfig>
---@param key string 常量Key
---@return EquipConfig
function EquipConfig.getByKey(key)
    if (not key) then
        return keyConfigs
    end
    return keyConfigs[key]
end

---
---通过索引[position]获取EquipConfig
---@overload fun():map<position:int,list<EquipConfig>> 
---@param position int 部位
---@return list<EquipConfig>
function EquipConfig.getByPosition(position)
    return positionConfigs[position] or table.empty()
end

return EquipConfig