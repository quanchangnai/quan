---
---用户信息
---自动生成
---

local Buffer = require("quan.message.Buffer")
local Message = require("quan.message.Message")
local RoleInfo = require("test.message.role.RoleInfo")

---
---用户信息
---
local UserInfo = {
    ---类名
    class = "UserInfo",
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
            ",roleList=" .. Message.listToString(self.roleList) ..
            ",roleSet=" .. Message.setToString(self.roleSet) ..
            ",roleMap=" .. Message.mapToString(self.roleMap) ..
            '}';
end

---元表
local meta = { __index = UserInfo, __newindex = onSet, __tostring = toString }

---
---用户信息.构造
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
---用户信息.编码
---@param buffer quan.message.Buffer 可以为空
---@return quan.message.Buffer
---
function UserInfo:encode(buffer)
    assert(type(self) == "table" and self.class == UserInfo.class, "参数[self]类型错误")
    assert(buffer == nil or type(buffer) == "table" and buffer.class == Buffer.class, "参数[buffer]类型错误")

    buffer = buffer or Buffer.new()

    buffer:writeTag(4)
    buffer:writeLong(self.id)

    buffer:writeTag(11)
    buffer:writeString(self.name)

    buffer:writeTag(12)
    buffer:writeInt(self.level)

    buffer:writeTag(19)
    local roleInfo1Buffer_ = Buffer.new()
    roleInfo1Buffer_:writeBool(self.roleInfo1 ~= nil)
    if self.roleInfo1 ~= nil then
        RoleInfo.encode(self.roleInfo1, roleInfo1Buffer_)
    end
    buffer:writeBuffer(roleInfo1Buffer_)

    buffer:writeTag(23)
    local roleInfo2Buffer_ = Buffer.new()
    RoleInfo.encode(self.roleInfo2, roleInfo2Buffer_)
    buffer:writeBuffer(roleInfo2Buffer_)

    buffer:writeTag(27)
    local roleListBuffer_ = Buffer.new()
    roleListBuffer_:writeInt(#self.roleList)
    for i, value in ipairs(self.roleList) do
        RoleInfo.encode(value, roleListBuffer_)
    end
    buffer:writeBuffer(roleListBuffer_)

    buffer:writeTag(31)
    local roleSetBuffer_ = Buffer.new()
    roleSetBuffer_:writeInt(#self.roleSet)
    for i, value in ipairs(self.roleSet) do
        RoleInfo.encode(value, roleSetBuffer_)
    end
    buffer:writeBuffer(roleSetBuffer_)

    buffer:writeTag(35)
    local roleMapBuffer_ = Buffer.new()
    roleMapBuffer_:writeInt(table.size(self.roleMap))
    for key, value in pairs(self.roleMap) do
        roleMapBuffer_:writeLong(key)
        RoleInfo.encode(value, roleMapBuffer_)
    end
    buffer:writeBuffer(roleMapBuffer_)

    buffer:writeTag(0);

    return buffer
end

---
---用户信息.解码
---@param buffer quan.message.Buffer 不能为空
---@param self test.message.user.UserInfo 可以为空
---@return test.message.user.UserInfo
---
function UserInfo.decode(buffer, self)
    assert(type(buffer) == "table" and buffer.class == Buffer.class, "参数[buffer]类型错误")
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
                self.roleInfo1 = RoleInfo.decode(buffer)
            end
        elseif tag == 23 then
            buffer:readInt()
            self.roleInfo2 = RoleInfo.decode(buffer, self.roleInfo2)
        elseif tag == 27 then
            buffer:readInt()
            for i = 1, buffer:readInt() do
                self.roleList[i] = RoleInfo.decode(buffer)
            end
        elseif tag == 31 then
            buffer:readInt()
            for i = 1, buffer:readInt() do
                self.roleSet[i] = RoleInfo.decode(buffer)
            end
        elseif tag == 35 then
            buffer:readInt()
            for i = 1, buffer:readInt() do
                self.roleMap[buffer:readLong()] = RoleInfo.decode(buffer)
            end
        else
            Message.skipField(tag, buffer)
        end
    end

    return self
end

UserInfo = table.readOnly(UserInfo)
return UserInfo