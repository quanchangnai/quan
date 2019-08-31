--
--- Generated by EmmyLua(https://github.com/EmmyLua)
--- Created by quanchangnai.
--- DateTime: 2019/8/30 17:04
--

local BuffConfigs = require("./BuffConfigs")
local Config = require("./Config")
local BuffConfig = require("./BuffConfig")

function testConfig()
    print("testConfig=======================")
    Config.test1()
    local config = Config.new()
    config.test1()
    config:test2()
end

function testBuffConfig()
    print("testBuffConfig=======================")
    BuffConfig.test1()
    local buffConfig = BuffConfig.new()
    buffConfig.test1()
    buffConfig:test2()
    buffConfig.test3()

    print("BuffConfig._name=" .. BuffConfig._name)
    print("buffConfig._name=" .. buffConfig._name)
    print("getmetatable(BuffConfig)._name=" .. getmetatable(BuffConfig)._name)
end

function testBuffConfigs()
    print("testBuffConfigs=======================")
    for id, config in pairs(BuffConfigs) do
        print("id:" .. id .. "，name:" .. config.name)
    end
end

testConfig()
print()
testBuffConfig()
--print()
--testBuffConfigs()
