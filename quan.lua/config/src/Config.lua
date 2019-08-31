---
--- Generated by EmmyLua(https://github.com/EmmyLua)
--- Created by quanchangnai.
--- DateTime: 2019/8/30 21:04
---

local Config = { _name = "Config" }
local prototype = {}

Config.__index = Config
prototype.__index = prototype

setmetatable(prototype, Config)

function Config.new()
    local instance = {}
    setmetatable(instance, prototype)
    return instance
end

function Config.test1()
    print("类方法test1()")
end

function prototype:test2()
    print("实例方法test2()")
    print("self=" .. tostring(self))
end

return Config