---
---WeaponConfig
---武器
---自动生成
---

local Config = require("quan.config.Config")

---所有WeaponConfig
local configs = {
    { id = 6, key = "", name = "武器1", type = 1, reward = { itemId = 5, itemNum = 0 }, list = { 1, 2, 342, 45 }, set = {  }, map = { [54] = 34 }, effectiveTime = "", position = 2, color = 4, w1 = 11, w2 = 43, rewardList = { { itemId = 12, itemNum = 22 }, { itemId = 32, itemNum = 56 }, { itemId = 23, itemNum = 56 } }, rewardSet = { { itemId = 433, itemNum = 51 }, { itemId = 433, itemNum = 52 } }, rewardMap = { [2] = { itemId = 12, itemNum = 22 }, [21] = { itemId = 32, itemNum = 56 }, [54] = { itemId = 23, itemNum = 56 } }, list2 = { 21, 32 } },
    { id = 0, key = "", name = "武器3", type = 1, reward = { itemId = 6, itemNum = 12 }, list = { 44, 25, 342, 45 }, set = {  }, map = { [88] = 33 }, effectiveTime = "", position = 0, color = 4, w1 = 11, w2 = 43, rewardList = { { itemId = 12, itemNum = 22 }, { itemId = 32, itemNum = 56 }, { itemId = 23, itemNum = 56 } }, rewardSet = { { itemId = 23, itemNum = 56 }, { itemId = 23, itemNum = 55 } }, rewardMap = { [22] = { itemId = 12, itemNum = 22 }, [21] = { itemId = 32, itemNum = 56 }, [65] = { itemId = 23, itemNum = 56 } }, list2 = { 41, 22 } },
    { id = 0, key = "", name = "武器4", type = 1, reward = { itemId = 7, itemNum = 13 }, list = { 44, 44, 0, 342, 45 }, set = {  }, map = { [22] = 32 }, effectiveTime = "", position = 2, color = 4, w1 = 11, w2 = 43, rewardList = { { itemId = 42, itemNum = 25 }, { itemId = 32, itemNum = 54 }, { itemId = 63, itemNum = 56 } }, rewardSet = { { itemId = 23, itemNum = 56 }, { itemId = 23, itemNum = 53 } }, rewardMap = { [3] = { itemId = 23, itemNum = 56 }, [4] = { itemId = 12, itemNum = 22 }, [6] = { itemId = 32, itemNum = 56 } }, list2 = { 2, 324 } },
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
---@param id int ID
---@return map<id int,WeaponConfig> | WeaponConfig
function WeaponConfig.getById(id)
    if (not id) then
        return idConfigs
    end
    return idConfigs[id]
end

---
---通过索引[key]获取WeaponConfig
---@param key string 常量Key
---@return map<key string,WeaponConfig> | WeaponConfig
function WeaponConfig.getByKey(key)
    if (not key) then
        return keyConfigs
    end
    return keyConfigs[key]
end

---
---通过索引[position]获取WeaponConfig
---@param position int 部位
---@return map<position int,list<WeaponConfig>> | list<WeaponConfig>
function WeaponConfig.getByPosition(position)
    return positionConfigs[position] or {}
end

---
---通过索引[composite1]获取WeaponConfig
---@param color int 颜色
---@param w1 int 字段1
---@return map<color int,map<w1 int,list<WeaponConfig>>> | map<w1 int,list<WeaponConfig>> | list<WeaponConfig>
function WeaponConfig.getByComposite1(color, w1)
    if (not color) then
        return composite1Configs
    end

    local map = composite1Configs[color] or {}
    if (not w1) then
        return map
    end
    return map[w1]
end

---
---通过索引[composite2]获取WeaponConfig
---@param w1 int 字段1
---@param w2 int 字段2
---@return map<w1 int,map<w2 int,WeaponConfig>> | map<w2 int,WeaponConfig> | WeaponConfig
function WeaponConfig.getByComposite2(w1, w2)
    if (not w1) then
        return composite2Configs
    end

    local map = composite2Configs[w1] or {}
    if (not w2) then
        return map
    end

    return map[w2]
end

return WeaponConfig