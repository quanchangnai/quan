local Buffer = require("quan.message.Buffer")
local Message = require("quan.message.Message")
local UserInfo = require("test.message.user.UserInfo")
local RoleInfo = require("test.message.role.RoleInfo")

---
---角色登录
---自动生成
---
local CRoleLogin = {
    ---类名
    class = "test.message.role.CRoleLogin",
    ---消息ID
    id = 544233
}

local function onSet(self, key, value)
    assert(not CRoleLogin[key], "不允许修改只读属性:" .. key)
    rawset(self, key, value)
end

local function toString(self)
    return "CRoleLogin{" ..
            "seq=" .. tostring(self.seq) ..
            ",error=" .. tostring(self.error) ..
            ",roleId=" .. tostring(self.roleId) ..
            ",roleName='" .. tostring(self.roleName) .. '\'' ..
            ",roleInfo=" .. tostring(self.roleInfo) ..
            ",roleInfoList=" .. Message.listToString(self.roleInfoList) ..
            ",roleInfoSet=" .. Message.setToString(self.roleInfoSet) ..
            ",roleInfoMap=" .. Message.mapToString(self.roleInfoMap) ..
            ",userInfo=" .. tostring(self.userInfo) ..
            '}';
end

---元表
local meta = { __index = CRoleLogin, __newindex = onSet, __tostring = toString }

---
---角色登录.构造
---@param args 参数列表可以为空
---
function CRoleLogin.new(args)
    assert(args == nil or type(args) == "table", "参数错误")
    args = args or {}

    local instance = {
        ---消息序号
        seq = args.seq or 0,
        ---错误码
        error = args.error or 0,
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

    instance = setmetatable(instance, meta)
    return instance
end

---
---角色登录.编码
---@return quan.message.Buffer
---
function CRoleLogin:encode()
    assert(type(self) == "table" and self.class == CRoleLogin.class, "参数[self]类型错误")
    local buffer = Message.encode(self)

    buffer:writeLong(self.seq)
    buffer:writeInt(self.error)
    buffer:writeLong(self.roleId)
    buffer:writeString(self.roleName)
    RoleInfo.encode(self.roleInfo, buffer)

    buffer:writeInt(#self.roleInfoList)
    for i, value in ipairs(self.roleInfoList) do
        RoleInfo.encode(value, buffer)
    end

    buffer:writeInt(#self.roleInfoSet)
    for i, value in ipairs(self.roleInfoSet) do
        RoleInfo.encode(value, buffer)
    end

    buffer:writeInt(table.size(self.roleInfoMap))
    for key, value in pairs(self.roleInfoMap) do
        buffer:writeLong(key)
        RoleInfo.encode(value, buffer)
    end

    buffer:writeBool(self.userInfo ~= nil);
    if self.userInfo ~= nil then
        UserInfo.encode(self.userInfo, buffer)
    end

    return buffer
end

---
---角色登录.解码
---@param buffer quan.message.Buffer 不能为空
---@return test.message.role.CRoleLogin
---
function CRoleLogin.decode(buffer)
    assert(type(buffer) == "table" and buffer.class == Buffer.class, "参数[buffer]类型错误")
    local self = CRoleLogin.new()
    Message.decode(buffer, self)
    self.seq = buffer:readLong()
    self.error = buffer:readInt()
    self.roleId = buffer:readLong()
    self.roleName = buffer:readString()
    self.roleInfo = RoleInfo.decode(buffer, self.roleInfo)

    for i = 1, buffer:readInt() do
        self.roleInfoList[i] = RoleInfo.decode(buffer)
    end

    for i = 1, buffer:readInt() do
        self.roleInfoSet[i] = RoleInfo.decode(buffer)
    end

    for i = 1, buffer:readInt() do
        self.roleInfoMap[buffer:readLong()] = RoleInfo.decode(buffer)
    end

    if buffer:readBool() then
        self.userInfo = UserInfo.decode(buffer)
    end

    return self
end

CRoleLogin = table.readOnly(CRoleLogin)
return CRoleLogin