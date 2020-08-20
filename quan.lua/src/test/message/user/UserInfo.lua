---
---用户信息
---代码自动生成，请勿手动修改
---

local _Buffer = require("quan.message.Buffer")
local _Message = require("quan.message.Message")
local test_message_role_RoleInfo = require("test.message.role.RoleInfo")
local RoleInfo = require("test.message.user.RoleInfo")

---
---用户信息
---
local UserInfo = {
    ---类名
    class = "test.message.user.UserInfo",
}

local function onSet(self, key, value)
    assert(not UserInfo[key], "不允许修改只读属性:" .. key)
    rawset(self, key, value)
end

local function toString(self)
    return "UserInfo{" ..
            "id=" .. tostring(self.id) ..
            ",name='" .. tostring(self.name) .. '\'' ..
            ",level=" .. tostring(self.level) ..
            ",roleInfo1=" .. tostring(self.roleInfo1) ..
            ",roleInfo2=" .. tostring(self.roleInfo2) ..
            ",roleInfo3=" .. tostring(self.roleInfo3) ..
            ",roleList=" .. table.listToString(self.roleList) ..
            ",roleSet=" .. table.setToString(self.roleSet) ..
            ",roleMap=" .. table.mapToString(self.roleMap) ..
            '}';
end

---元表
local meta = { __index = UserInfo, __newindex = onSet, __tostring = toString }

---
---[用户信息].构造
---@param args 参数列表可以为空
---
function UserInfo.new(args)
    assert(args == nil or type(args) == "table", "参数错误")
    args = args or {}

    local instance = {
        ---ID
        id = args.id or 0,
        ---名字
        name = args.name or "",
        ---等级
        level = args.level or 0,
        ---角色信息
        roleInfo1 = args.roleInfo1,
        ---角色信息2
        roleInfo2 = args.roleInfo2 or RoleInfo.new(),
        ---角色信息2
        roleInfo3 = args.roleInfo3 or RoleInfo.new(),
        ---角色信息List
        roleList = args.roleList or {},
        ---角色信息Set
        roleSet = args.roleSet or {},
        ---角色信息Map
        roleMap = args.roleMap or {},
    }

    instance = setmetatable(instance, meta)
    return instance
end

---
---[用户信息].编码
---@param buffer quan.message.Buffer 可以为空
---@return quan.message.Buffer
---
function UserInfo:encode(buffer)
    assert(type(self) == "table" and self.class == UserInfo.class, "参数[self]类型错误")
    assert(buffer == nil or type(buffer) == "table" and buffer.class == _Buffer.class, "参数[buffer]类型错误")

    buffer = buffer or _Buffer.new()

    buffer:writeTag(4)
    buffer:writeLong(self.id)

    buffer:writeTag(11)
    buffer:writeString(self.name)

    buffer:writeTag(12)
    buffer:writeInt(self.level)

    buffer:writeTag(19)
    local roleInfo1Buffer = _Buffer.new()
    roleInfo1Buffer:writeBool(self.roleInfo1 ~= nil)
    if self.roleInfo1 ~= nil then
        test_message_role_RoleInfo.encode(self.roleInfo1, roleInfo1Buffer)
    end
    buffer:writeBuffer(roleInfo1Buffer)

    buffer:writeTag(23)
    local roleInfo2Buffer = _Buffer.new()
    RoleInfo.encode(self.roleInfo2, roleInfo2Buffer)
    buffer:writeBuffer(roleInfo2Buffer)

    buffer:writeTag(27)
    local roleInfo3Buffer = _Buffer.new()
    RoleInfo.encode(self.roleInfo3, roleInfo3Buffer)
    buffer:writeBuffer(roleInfo3Buffer)

    buffer:writeTag(31)
    local roleListBuffer = _Buffer.new()
    roleListBuffer:writeInt(#self.roleList)
    for i, value in ipairs(self.roleList) do
        test_message_role_RoleInfo.encode(value, roleListBuffer)
    end
    buffer:writeBuffer(roleListBuffer)

    buffer:writeTag(35)
    local roleSetBuffer = _Buffer.new()
    roleSetBuffer:writeInt(#self.roleSet)
    for i, value in ipairs(self.roleSet) do
        test_message_role_RoleInfo.encode(value, roleSetBuffer)
    end
    buffer:writeBuffer(roleSetBuffer)

    buffer:writeTag(39)
    local roleMapBuffer = _Buffer.new()
    roleMapBuffer:writeInt(table.size(self.roleMap))
    for key, value in pairs(self.roleMap) do
        roleMapBuffer:writeLong(key)
        test.message.role.RoleInfo.encode(value, roleMapBuffer)
    end
    buffer:writeBuffer(roleMapBuffer)

    buffer:writeTag(0);

    return buffer
end

---
---[用户信息].解码
---@param buffer quan.message.Buffer 不能为空
---@param self test.message.user.UserInfo 可以为空
---@return test.message.user.UserInfo
---
function UserInfo.decode(buffer, self)
    assert(type(buffer) == "table" and buffer.class == _Buffer.class, "参数[buffer]类型错误")
    assert(self == nil or type(self) == "table" and self.class == UserInfo.class, "参数[self]类型错误")

    self = self or UserInfo.new()

    while true do
        local tag = buffer:readTag()
        if tag == 0 then
            break
        elseif tag == 4 then
            self.id = buffer:readLong()
        elseif tag == 11 then
            self.name = buffer:readString()
        elseif tag == 12 then
            self.level = buffer:readInt()
        elseif tag == 19 then
            buffer:readInt()
            if buffer:readBool() then
                self.roleInfo1 = test_message_role_RoleInfo.decode(buffer)
            end
        elseif tag == 23 then
            buffer:readInt()
            self.roleInfo2 = RoleInfo.decode(buffer, self.roleInfo2)
        elseif tag == 27 then
            buffer:readInt()
            self.roleInfo3 = RoleInfo.decode(buffer, self.roleInfo3)
        elseif tag == 31 then
            buffer:readInt()
            for i = 1, buffer:readInt() do
                self.roleList[i] = test_message_role_RoleInfo.decode(buffer)
            end
        elseif tag == 35 then
            buffer:readInt()
            for i = 1, buffer:readInt() do
                self.roleSet[i] = test_message_role_RoleInfo.decode(buffer)
            end
        elseif tag == 39 then
            buffer:readInt()
            for i = 1, buffer:readInt() do
                self.roleMap[buffer:readLong()] = test_message_role_RoleInfo.decode(buffer)
            end
        else
            _Message.skipField(tag, buffer)
        end
    end

    return self
end

UserInfo = table.readOnly(UserInfo)
return UserInfo