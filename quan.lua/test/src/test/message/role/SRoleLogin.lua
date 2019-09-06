local Buffer = require("quan.message.Buffer")
local Message = require("quan.message.Message")
local UserInfo = require("test.message.user.UserInfo")
local RoleInfo = require("test.message.role.RoleInfo")

---
---角色登录
---自动生成
---
local SRoleLogin = {
    ---类名
    class = "test.message.role.SRoleLogin",
    ---消息ID
    id = 763075
}

local function onUpdateProp(table, key, value)
    assert(not SRoleLogin[key], "不允许修改只读属性:" .. key)
    rawset(table, key, value)
end

---
---角色登录.构造
---@param args 参数列表可以为空
---
function SRoleLogin.new(args)
    assert(args == nil or type(args) == "table", "参数错误")
    args = args or {}

    local instance = {
        ---消息序号
        seq = args.seq or 0,
        ---角色id
        roleId = args.roleId or 0,
        ---角色名
        roleName = args.roleName or "",
        ---角色信息
        roleInfo = args.roleInfo or RoleInfo.new(),
        ---角色信息
        roleInfoList = args.roleInfoList or {},
        ---角色信息
        roleInfoSet = args.roleInfoSet or {},
        ---角色信息
        roleInfoMap = args.roleInfoMap or {},
        ---用户信息
        userInfo = args.userInfo,
    }

    instance = setmetatable(instance, { __index = SRoleLogin, __newindex = onUpdateProp })
    return instance
end

---
---角色登录.编码
---@param msg test.message.role.SRoleLogin 不能为空
---@param buffer quan.message.Buffer 可以为空
---@return quan.message.Buffer
---
function SRoleLogin.encode(msg, buffer)
    assert(msg ~= nil, "参数[msg]不能为空")
    buffer = Message.encode(msg, buffer)

    buffer:writeLong(msg.roleId)
    buffer:writeString(msg.roleName)
    RoleInfo.encode(msg.roleInfo, buffer)

    buffer:writeInt(#msg.roleInfoList)
    for i, value in ipairs(msg.roleInfoList) do
        RoleInfo.encode(value, buffer)
    end

    buffer:writeInt(#msg.roleInfoSet)
    for i, value in ipairs(msg.roleInfoSet) do
        RoleInfo.encode(value, buffer)
    end

    buffer:writeInt(table.size(msg.roleInfoMap))
    for key, value in pairs(msg.roleInfoMap) do
        buffer:writeLong(key)
        RoleInfo.encode(value, buffer)
    end

    buffer:writeBool(msg.userInfo ~= nil);
    if msg.userInfo ~= nil then
        UserInfo.encode(msg.userInfo, buffer)
    end

    return buffer
end

---
---角色登录.解码
---@param buffer quan.message.Buffer 不能为空
---@param msg test.message.role.SRoleLogin 可以为空
---@return test.message.role.SRoleLogin
---
function SRoleLogin.decode(buffer, msg)
    assert(buffer ~= nil, "参数[buffer]不能为空")
    msg = msg or SRoleLogin.new()
    Message.decode(buffer, msg)

    msg.roleId = buffer:readLong()
    msg.roleName = buffer:readString()
    msg.roleInfo = RoleInfo.decode(buffer, msg.roleInfo)

    for i = 1, buffer:readInt() do
        msg.roleInfoList[i] = RoleInfo.decode(buffer)
    end

    for i = 1, buffer:readInt() do
        msg.roleInfoSet[i] = RoleInfo.decode(buffer)
    end

    for i = 1, buffer:readInt() do
        msg.roleInfoMap[buffer:readLong()] = RoleInfo.decode(buffer)
    end

    if buffer:readBool() then
        msg.userInfo = UserInfo.decode(buffer)
    end

    return msg
end

SRoleLogin = table.readOnly(SRoleLogin)
return SRoleLogin