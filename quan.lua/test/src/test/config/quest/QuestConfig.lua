---
---QuestConfig
---任务
---自动生成
---

local Config = require("quan.config.Config")

---所有QuestConfig
local configs = {
    { id = 1, name = "任务1", type = 1, target = 1, reward = { }, a1 = 1, a2 = 2, b1 = 11, b2 = false, c1 = "111", c2 = 222, c3 = 333, d1 = "1111", d2 = 2222, d3 = 333, s1 = {  }, l1 = {  }, m1 = {  } },
}

---索引:两字段唯一索引
local composite1Configs = {}

---索引:两字段普通索引
local composite2Configs = {}

---索引:三字段唯一索引
local composite3Configs = {}

---索引:三字段普通索引
local composite4Configs = {}

---索引:ID
local idConfigs = {}

---索引:类型
local typeConfigs = {}

---加载配置，建立索引
local function loadConfigs()
    for i, config in ipairs(configs) do
        Config.load(composite1Configs, config, true, { "a1", "a2" }, { config.a1, config.a2 })
        Config.load(composite2Configs, config, false, { "b1", "b2" }, { config.b1, config.b2 })
        Config.load(composite3Configs, config, true, { "c1", "c2", "c3" }, { config.c1, config.c2, config.c3 })
        Config.load(composite4Configs, config, false, { "d1", "d2", "d3" }, { config.d1, config.d2, config.d3 })
        Config.load(idConfigs, config, true, { "id" }, { config.id })
        Config.load(typeConfigs, config, false, { "type" }, { config.type })
    end
end

loadConfigs()

---任务
local QuestConfig = {}

---
---获取所有QuestConfig
---@return list<QuestConfig>
function QuestConfig.getConfigs()
    return configs
end

---
---通过索引[composite1]获取QuestConfig
---@param a1 int A1
---@param a2 int A2
---@return map<a1 int,map<a2 int,QuestConfig>> | map<a2 int,QuestConfig> | QuestConfig
function QuestConfig.getByComposite1(a1, a2)
    if (not a1) then
        return composite1Configs
    end

    local map = composite1Configs[a1] or {}
    if (not a2) then
        return map
    end

    return map[a2]
end

---
---通过索引[composite2]获取QuestConfig
---@param b1 int B1
---@param b2 bool B2
---@return map<b1 int,map<b2 bool,list<QuestConfig>>> | map<b2 bool,list<QuestConfig>> | list<QuestConfig>
function QuestConfig.getByComposite2(b1, b2)
    if (not b1) then
        return composite2Configs
    end

    local map = composite2Configs[b1] or {}
    if (not b2) then
        return map
    end
    return map[b2]
end

---
---通过索引[composite3]获取QuestConfig
---@param c1 string C1
---@param c2 int C2
---@param c3 int C3
---@return  map<c1 string,map<c2 int,map<c3 int,QuestConfig>>> | map<c2 int,map<c3 int,QuestConfig>> | map<c3 int,QuestConfig> | QuestConfig
function QuestConfig.getByComposite3(c1, c2, c3)
    if (not c1) then
        return composite3Configs
    end

    local map1 = composite3Configs[c1] or {}
    if (not c2) then
        return map1
    end

    local map2 = map1[c2] or {}
    if (not c3) then
        return map2
    end

    return map2[c3]
end

---
---通过索引[composite4]获取QuestConfig
---@param d1 string D1
---@param d2 int D2
---@param d3 int D3
---@return map<d1 string,map<d2 int,map<d3 int,list<QuestConfig>>>> | map<d2 int,map<d3 int,list<QuestConfig>>> | map<d3 int,list<QuestConfig>> | list<QuestConfig>
function QuestConfig.getByComposite4(d1, d2, d3)
    if (not d1) then
        return composite4Configs
    end

    local map1 = composite4Configs[d1] or {}
    if (not d2) then
        return map1
    end

    local map2 = map1[d2] or {}
    if (not d3) then
        return map2
    end

    return map2[d3]
end

---
---通过索引[id]获取QuestConfig
---@param id int ID
---@return map<id int,QuestConfig> | QuestConfig
function QuestConfig.getById(id)
    if (not id) then
        return idConfigs
    end
    return idConfigs[id]
end

---
---通过索引[type]获取QuestConfig
---@param type QuestType 类型
---@return map<type QuestType,list<QuestConfig>> | list<QuestConfig>
function QuestConfig.getByType(type)
    return typeConfigs[type] or {}
end

return QuestConfig