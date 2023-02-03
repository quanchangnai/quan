---
---用户信息
---代码自动生成，请勿手动修改
---

local _CodedBuffer = require("quan.message.CodedBuffer")
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
            ",type=" .. tostring(self.type) ..
            ",roleInfo1=" .. tostring(self.roleInfo1) ..
            ",roleInfo2=" .. tostring(self.roleInfo2) ..
            ",roleInfo3=" .. tostring(self.roleInfo3) ..
            ",roleList=" .. table.toString(self.roleList) ..
            ",roleSet=" .. table.toString(self.roleSet) ..
            ",roleMap=" .. table.toString(self.roleMap) ..
            ",f11=" .. tostring(self.f11) ..
            ",f12=" .. tostring(self.f12) ..
            ",f13=" .. tostring(self.f13) ..
            ",f14=" .. tostring(self.f14) ..
            ",f15=" .. tostring(self.f15) ..
            ",f16=" .. tostring(self.f16) ..
            ",f17=" .. tostring(self.f17) ..
            ",f18=" .. tostring(self.f18) ..
            ",alias='" .. tostring(self.alias) .. '\'' ..
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
        ---类型
        type = args.type or 0,
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
        f11 = args.f11 or "",
        f12 = args.f12 or false,
        f13 = args.f13 or false,
        f14 = args.f14 or 0,
        f15 = args.f15 or 0.0,
        f16 = args.f16 or 0.0,
        f17 = args.f17 or 0.0,
        f18 = args.f18 or 0.0,
        alias = args.alias,
    }

    instance = setmetatable(instance, meta)
    return instance
end

setmetatable(UserInfo, { __call = UserInfo.new })

---
---[用户信息].编码
---@param buffer quan.message.CodedBuffer 可以为空
---@return quan.message.CodedBuffer
---
function UserInfo:encode(buffer)
    assert(type(self) == "table" and self.class == UserInfo.class, "参数[self]类型错误")
    assert(buffer == nil or type(buffer) == "table" and buffer.class == _CodedBuffer.class, "参数[buffer]类型错误")

    buffer = buffer or _CodedBuffer.new()

    if self.id ~= 0 then
        _Message.writeTag(buffer, 4)
        buffer:writeInt(self.id)
    end

    if #self.name > 0 then
        _Message.writeTag(buffer, 11)
        buffer:writeString(self.name)
    end

    if self.level ~= 0 then
        _Message.writeTag(buffer, 12)
        buffer:writeInt(self.level)
    end

    if self.type ~= nil and self.type ~= 0 then
        _Message.writeTag(buffer, 16)
        buffer:writeInt(self.type)
    end

    if self.roleInfo1 ~= nil then
        _Message.writeTag(buffer, 23)
        local roleInfo1Buffer = _CodedBuffer.new()
        test_message_role_RoleInfo.encode(self.roleInfo1, roleInfo1Buffer)
        buffer:writeBuffer(roleInfo1Buffer)
    end

    _Message.writeTag(buffer, 27)
    local roleInfo2Buffer = _CodedBuffer.new()
    RoleInfo.encode(self.roleInfo2, roleInfo2Buffer)
    buffer:writeBuffer(roleInfo2Buffer)

    _Message.writeTag(buffer, 31)
    local roleInfo3Buffer = _CodedBuffer.new()
    RoleInfo.encode(self.roleInfo3, roleInfo3Buffer)
    buffer:writeBuffer(roleInfo3Buffer)

    if #self.roleList > 0 then
        _Message.writeTag(buffer, 35)
        local roleListBuffer = _CodedBuffer.new()
        roleListBuffer:writeInt(#self.roleList)
        for i, value in ipairs(self.roleList) do
            test_message_role_RoleInfo.encode(value, roleListBuffer)
        end
        buffer:writeBuffer(roleListBuffer)
    end

    if #self.roleSet > 0 then
        _Message.writeTag(buffer, 39)
        local roleSetBuffer = _CodedBuffer.new()
        roleSetBuffer:writeInt(#self.roleSet)
        for i, value in ipairs(self.roleSet) do
            test_message_role_RoleInfo.encode(value, roleSetBuffer)
        end
        buffer:writeBuffer(roleSetBuffer)
    end

    local roleMapSize = table.size(self.roleMap)
    if roleMapSize > 0 then
        _Message.writeTag(buffer, 43)
        local roleMapBuffer = _CodedBuffer.new()
        roleMapBuffer:writeInt(roleMapSize)
        for key, value in pairs(self.roleMap) do
            roleMapBuffer:writeInt(key)
            test_message_role_RoleInfo.encode(value, roleMapBuffer)
        end
        buffer:writeBuffer(roleMapBuffer)
    end

    if #self.f11 > 0 then
        _Message.writeTag(buffer, 47)
        buffer:writeBytes(self.f11)
    end

    if self.f12 then
        _Message.writeTag(buffer, 48)
        buffer:writeBool(self.f12)
    end

    if self.f13 then
        _Message.writeTag(buffer, 52)
        buffer:writeBool(self.f13)
    end

    if self.f14 ~= 0 then
        _Message.writeTag(buffer, 56)
        buffer:writeShort(self.f14)
    end

    if self.f15 ~= 0 then
        _Message.writeTag(buffer, 61)
        buffer:writeFloat(self.f15)
    end

    if self.f16 ~= 0 then
        _Message.writeTag(buffer, 64)
        buffer:writeFloat(self.f16, 2)
    end

    if self.f17 ~= 0 then
        _Message.writeTag(buffer, 70)
        buffer:writeDouble(self.f17)
    end

    if self.f18 ~= 0 then
        _Message.writeTag(buffer, 72)
        buffer:writeDouble(self.f18, 2)
    end

    if self.alias ~= nil then
        _Message.writeTag(buffer, 79)
        buffer:writeString(self.alias)
    end

    _Message.writeTag(buffer, 0);

    return buffer
end

---
---[用户信息].解码
---@param buffer quan.message.CodedBuffer 不能为空
---@param self test.message.user.UserInfo 可以为空
---@return test.message.user.UserInfo
---
function UserInfo.decode(buffer, self)
    assert(type(buffer) == "table" and buffer.class == _CodedBuffer.class, "参数[buffer]类型错误")
    assert(self == nil or type(self) == "table" and self.class == UserInfo.class, "参数[self]类型错误")

    self = self or UserInfo.new()

    while true do
        local tag = _Message.readTag(buffer)
        if tag == 0 then
            break
        elseif tag == 4 then
            self.id = buffer:readInt()
        elseif tag == 11 then
            self.name = buffer:readString()
        elseif tag == 12 then
            self.level = buffer:readInt()
        elseif tag == 16 then
            self.type = buffer:readInt()
        elseif tag == 23 then
            buffer:readInt()
            self.roleInfo1 = test_message_role_RoleInfo.decode(buffer)
        elseif tag == 27 then
            buffer:readInt()
            self.roleInfo2 = RoleInfo.decode(buffer, self.roleInfo2)
        elseif tag == 31 then
            buffer:readInt()
            self.roleInfo3 = RoleInfo.decode(buffer, self.roleInfo3)
        elseif tag == 35 then
            buffer:readInt()
            for i = 1, buffer:readInt() do
                self.roleList[i] = test_message_role_RoleInfo.decode(buffer)
            end
        elseif tag == 39 then
            buffer:readInt()
            for i = 1, buffer:readInt() do
                self.roleSet[i] = test_message_role_RoleInfo.decode(buffer)
            end
        elseif tag == 43 then
            buffer:readInt()
            for i = 1, buffer:readInt() do
                self.roleMap[buffer:readInt()] = test_message_role_RoleInfo.decode(buffer)
            end
        elseif tag == 47 then
            self.f11 = buffer:readBytes()
        elseif tag == 48 then
            self.f12 = buffer:readBool()
        elseif tag == 52 then
            self.f13 = buffer:readBool()
        elseif tag == 56 then
            self.f14 = buffer:readShort()
        elseif tag == 61 then
            self.f15 = buffer:readFloat()
        elseif tag == 64 then
            self.f16 = buffer:readFloat(2)
        elseif tag == 70 then
            self.f17 = buffer:readDouble()
        elseif tag == 72 then
            self.f18 = buffer:readDouble(2)
        elseif tag == 79 then
            self.alias = buffer:readString()
        else
            _Message.skipField(tag, buffer)
        end
    end

    return self
end

UserInfo = table.readOnly(UserInfo)
return UserInfo