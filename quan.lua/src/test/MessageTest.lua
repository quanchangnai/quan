---
--- Generated by EmmyLua(https://github.com/EmmyLua)
--- Created by quanchangnai.
--- DateTime: 2019/8/30 17:36
---
package.path = package.path .. ";../../../src/?.lua"

local Buffer = require("quan.message.Buffer")
local Message = require("quan.message.Message")
local MessageRegistry = require("test.message.MessageRegistry")
local SRoleLogin = require("test.message.role.SRoleLogin")
local RoleInfo = require("test.message.role.RoleInfo")
local RoleType = require("test.message.role.RoleType")
local UserInfo = require("test.message.user.UserInfo")

print("MessageTest===========")

local function test1()
    print()
    print("test1===========")
    local str = ""
    for i = 1, 3 do
        str = str .. string.char(i)
    end
    print("str[2]", str[2])
    print("str", str, "str.len()", string.len(str))
    print(string.unpack("bbb", str))
    print(str:byte(1, string.len(str)))

    str = string.pack("bbb", 23, 43, 54)
    print(string.unpack("bbb", str))
end

local function testBuffer()
    print()
    print("testBuffer===========")
    local buffer = Buffer.new()
    buffer:writeBool(true)
    buffer:writeInt(70)
    buffer:writeInt(2423)
    buffer:writeFloat(13.43)
    buffer:writeDouble(4242.432)
    buffer:writeFloat(132.32434, 2)
    buffer:writeDouble(342254.653254, 2)
    buffer:writeString("搭顺风车")
    buffer:writeLong(12324)
    buffer:writeTag(255)

    print("buffer:size()", buffer:size())

    buffer = Buffer.new(buffer.bytes)

    print(buffer:readBool())
    print(buffer:readInt())
    buffer:reset()
    print(buffer:readBool())
    print(buffer:readInt())
    print(buffer:readInt())
    print(buffer:readFloat())
    print(buffer:readDouble())
    print(buffer:readFloat(2))
    print(buffer:readDouble(2))
    print(buffer:readString())
    print(buffer:readLong())
    print(buffer:readTag())

    print("=================")

    local file = io.open("../../.temp/message/buffer", "w")
    file:write(buffer.bytes)
    file:flush()

    buffer:reset()
    print(buffer:readBool())
    buffer:clear()
    buffer:writeInt(45)
    buffer:writeString("奋斗服务")
    print(buffer:readInt())
    buffer = Buffer.new(buffer:remainingBytes())
    print(buffer:readString())

    print(table.unpack({ 12, 45, 33 }))
end

local function testMessage1()
    print()
    print("testMessage1===========")
    local sRoleLogin1 = SRoleLogin.new({ roleId = 1111 })
    sRoleLogin1.roleName = "张三3333"

    local roleInfo = RoleInfo.new()
    roleInfo.id = 111
    roleInfo.type = RoleType.type2
    sRoleLogin1.roleInfo = roleInfo

    local roleInfo2 = RoleInfo.new({ id = 222, name = "bbb", type = RoleType.type2, set = { 2233 } })

    local roleInfoBuffer = roleInfo2:encode()
    local roleInfo3 = RoleInfo.decode(roleInfoBuffer)
    print("roleInfo3.name", roleInfo3.name)

    sRoleLogin1.roleInfoList[1] = roleInfo2
    sRoleLogin1.roleInfoList[2] = roleInfo2

    sRoleLogin1.roleInfoSet[1] = roleInfo2
    sRoleLogin1.roleInfoSet[2] = roleInfo2

    sRoleLogin1.roleInfoMap[roleInfo2.id] = roleInfo2

    sRoleLogin1.userInfo = UserInfo.new()

    sRoleLogin1.seq = 1000

    print("sRoleLogin1：", sRoleLogin1)

    local buffer = sRoleLogin1:encode()
    print("buffer:size()", buffer:size())

    local sRoleLogin2 = SRoleLogin.decode(buffer)

    --local sRoleLogin2 = MessageRegistry.create(buffer:readInt())
    --buffer:reset()
    --local sRoleLogin2 = sRoleLogin2.decode(buffer)

    print("sRoleLogin2：", sRoleLogin1)

    --local buffer = SRoleLogin.encode(sRoleLogin2)
    --local file1 = io.open("D:\\SRoleLogin", "w")
    --file1:write(buffer.bytes)
    --file1:flush()

    --local file2 = io.open("D:\\SRoleLogin", "r")
    --local bytes = file2:read("*a")
    --local sRoleLogin3 = SRoleLogin.decode(Buffer.new(bytes))

    print("SRoleLogin1", sRoleLogin1)
    print("SRoleLogin2", sRoleLogin2)
    print("SRoleLogin3", sRoleLogin3)

    print("SRoleLogin.id", SRoleLogin.id)
    print("SRoleLogin.class", SRoleLogin.class)
    print("sRoleLogin2.id", sRoleLogin2.id)
    print("sRoleLogin2.class", sRoleLogin2.class)
    print("sRoleLogin2.roleId", sRoleLogin2.roleId)
    print("sRoleLogin2.roleName", sRoleLogin2.roleName)

end

local function testMessage2()
    print()
    print("testMessage2===========")
    --SRoleLogin.id=1
    print("SRoleLogin.id", SRoleLogin.id)

    local sRoleLogin1 = SRoleLogin.new({ roleId = 1111 })

    --sRoleLogin1.id=1
    print("sRoleLogin1.id", sRoleLogin1.id)

    --sRoleLogin1.roleId = "1322"
    print("sRoleLogin1.roleId", sRoleLogin1.roleId)

    sRoleLogin1.userInfo = UserInfo.new()
    print("sRoleLogin1.userInfo", sRoleLogin1.userInfo)

end

--test1()
--testBuffer()
testMessage1()
--testMessage2()




