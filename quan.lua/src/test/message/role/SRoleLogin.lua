---
---角色登录，哈希生成ID
---代码自动生成，请勿手动修改
---

local _Buffer = require("quan.message.Buffer")
local _Message = require("quan.message.Message")
local _MessageHeader = require("test.message.common.MessageHeader")
local RoleInfo = require("test.message.role.RoleInfo")
local UserInfo = require("test.message.user.UserInfo")

---
---角色登录，哈希生成ID
---
local SRoleLogin = {
    ---类名
    class = "test.message.role.SRoleLogin",
    ---消息ID
    id = 763075
}

local function onSet(self, key, value)
    assert(not SRoleLogin[key], "不允许修改只读属性:" .. key)
    rawset(self, key, value)
end

local function toString(self)
    return "SRoleLogin{" ..
            "seq=" .. tostring(self.seq) ..
            ",error=" .. tostring(self.error) ..
            ",roleId=" .. tostring(self.roleId) ..
            ",roleName='" .. tostring(self.roleName) .. '\'' ..
            ",roleInfo=" .. tostring(self.roleInfo) ..
            ",roleInfoList=" .. table.listToString(self.roleInfoList) ..
            ",roleInfoSet=" .. table.setToString(self.roleInfoSet) ..
            ",roleInfoMap=" .. table.mapToString(self.roleInfoMap) ..
            ",userInfo=" .. tostring(self.userInfo) ..
            '}';
end

---元表
local meta = { __index = SRoleLogin, __newindex = onSet, __tostring = toString }

---
---[角色登录，哈希生成ID].构造
---@param args 参数列表可以为空
---
function SRoleLogin.new(args)
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
---[角色登录，哈希生成ID].编码
---@return quan.message.Buffer
---
function SRoleLogin:encode()
    assert(type(self) == "table" and self.class == SRoleLogin.class, "参数[self]类型错误")

    local buffer = _Message.encode(self)
    _MessageHeader.encode(self, buffer)

    buffer:writeInt(self.seq)
    buffer:writeInt(self.error)
    buffer:writeInt(self.roleId)
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
        buffer:writeInt(key)
        RoleInfo.encode(value, buffer)
    end

    buffer:writeBool(self.userInfo ~= nil)
    if self.userInfo ~= nil then
        UserInfo.encode(self.userInfo, buffer)
    end

    return buffer
end

---
---[角色登录，哈希生成ID].解码
---@param buffer quan.message.Buffer 不能为空
---@return test.message.role.SRoleLogin
---
function SRoleLogin.decode(buffer)
    assert(type(buffer) == "table" and buffer.class == _Buffer.class, "参数[buffer]类型错误")

    local self = SRoleLogin.new()

    _Message.decode(buffer, self)
    _MessageHeader.decode(buffer, self)

    self.seq = buffer:readInt()
    self.error = buffer:readInt()
    self.roleId = buffer:readInt()
    self.roleName = buffer:readString()
    self.roleInfo = RoleInfo.decode(buffer, self.roleInfo)

    for i = 1, buffer:readInt() do
        self.roleInfoList[i] = RoleInfo.decode(buffer)
    end

    for i = 1, buffer:readInt() do
        self.roleInfoSet[i] = RoleInfo.decode(buffer)
    end

    for i = 1, buffer:readInt() do
        self.roleInfoMap[buffer:readInt()] = RoleInfo.decode(buffer)
    end

    if buffer:readBool() then
        self.userInfo = UserInfo.decode(buffer)
    end

    return self
end

SRoleLogin = table.readOnly(SRoleLogin)
return SRoleLogin