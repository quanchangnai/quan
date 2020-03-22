--
--- Generated by EmmyLua(https://github.com/EmmyLua)
--- Created by quanchangnai.
--- DateTime: 2019/8/30 17:04
--

package.path = package.path .. ";../../../src/?.lua"

local Config = require("quan.config.Config")
local ItemConfig = require("test.config.item.ItemConfig")
local ItemIds = require("test.config.item.ItemIds")
local QuestConfig = require("test.config.quest.QuestConfig")

function testConfig()
    print("testConfig=======================")
    print()

    print("ItemConfig=====")
    for i, v in ipairs(ItemConfig.getConfigs()) do
        print(v.id, v.name)
    end
    print()

    print("ItemIds=====")
    print("ItemIds.item1=" .. ItemIds.item1())
    print()

    print("QuestConfig=====")
    print("QuestConfig.getByComposite3(111,222,333)", QuestConfig.getByComposite3("111", 222, 333).name)
end

testConfig()

