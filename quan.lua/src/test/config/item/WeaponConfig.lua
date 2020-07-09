---
---武器
---@module WeaponConfig
---@author 自动生成
---

local Config = require("quan.config.Config")

---所有WeaponConfig
local configs = {
    { id = 6, key = "", name = "武器1", type = 1, useEffect = { class = "item.UseEffect", aaa = 0 }, reward = { itemId = 5, itemNum = 0 }, list = { 1, 2, 342, 45 }, set = {  }, map = { [54] = 34 }, effectiveTime = 0, effectiveTime_ = "", position = 2, color = 4, w1 = 11, w2 = 43, rewardList = {  }, rewardSet = {  }, rewardMap = { }, list2 = { 21, 32 } },
    { id = 7, key = "", name = "武器3", type = 1, useEffect = { class = "item.UseEffect2", aaa = 0, bbb = 0 }, reward = { itemId = 6, itemNum = 12 }, list = { 44, 25, 342, 45 }, set = {  }, map = { [88] = 33 }, effectiveTime = 0, effectiveTime_ = "", position = 0, color = 4, w1 = 11, w2 = 43, rewardList = {  }, rewardSet = {  }, rewardMap = { }, list2 = { 41, 22 } },
    { id = 8, key = "", name = "武器4", type = 1, useEffect = nil, reward = { itemId = 7, itemNum = 13 }, list = { 44, 44, 0, 342, 45 }, set = {  }, map = { [22] = 32 }, effectiveTime = 0, effectiveTime_ = "", position = 2, color = 4, w1 = 11, w2 = 43, rewardList = {  }, rewardSet = {  }, rewardMap = { }, list2 = { 2, 324 } },
}

---索引:ID
local idConfigs = {}

---索引:常量Key
local keyConfigs = {}

---索引:部位
local positionConfigs = {}

local composite1Configs = {}

local composite2Configs = {}

---加载配置，建立索引
local function loadConfigs()
    for i, config in ipairs(configs) do
        Config.load(idConfigs, config, true, { "id" }, { config.id })
        Config.load(keyConfigs, config, true, { "key" }, { config.key })
        Config.load(positionConfigs, config, false, { "position" }, { config.position })
        Config.load(composite1Configs, config, false, { "color", "w1" }, { config.color, config.w1 })
        Config.load(composite2Configs, config, true, { "w1", "w2" }, { config.w1, config.w2 })
    end
end

loadConfigs()

---武器
local WeaponConfig = {}

---
---获取所有WeaponConfig
---@return list<WeaponConfig>
function WeaponConfig.getConfigs()
    return configs
end

---
---通过索引[id]获取WeaponConfig
---@overload fun():map<id:int,WeaponConfig>
---@param id int ID
---@return WeaponConfig
function WeaponConfig.getById(id)
    if (not id) then
        return idConfigs
    end
    return idConfigs[id]
end

---
---通过索引[key]获取WeaponConfig
---@overload fun():map<key:string,WeaponConfig>
---@param key string 常量Key
---@return WeaponConfig
function WeaponConfig.getByKey(key)
    if (not key) then
        return keyConfigs
    end
    return keyConfigs[key]
end

---
---通过索引[position]获取WeaponConfig
---@overload fun():map<position:int,list<WeaponConfig>> 
---@param position int 部位
---@return list<WeaponConfig>
function WeaponConfig.getByPosition(position)
    return positionConfigs[position] or table.empty()
end

---
---通过索引[composite1]获取WeaponConfig
---@overload fun():map<color:int,map<w1:int,list<WeaponConfig>>>
---@overload fun(color:int):map<w1:int,list<WeaponConfig>>
---@param color int 颜色
---@param w1 int 字段1
---@return list<WeaponConfig>
function WeaponConfig.getByComposite1(color, w1)
    if (not color) then
        return composite1Configs
    end

    local map = composite1Configs[color] or table.empty()
    if (not w1) then
        return map
    end
    return map[w1]
end

---
---通过索引[composite2]获取WeaponConfig
---@overload fun():map<w1:int,map<w2:int,WeaponConfig>>
---@overload fun(w1:int):map<w2:int,WeaponConfig>
---@param w1 int 字段1
---@param w2 int 字段2
---@return WeaponConfig
function WeaponConfig.getByComposite2(w1, w2)
    if (not w1) then
        return composite2Configs
    end

    local map = composite2Configs[w1] or table.empty()
    if (not w2) then
        return map
    end

    return map[w2]
end

return WeaponConfig