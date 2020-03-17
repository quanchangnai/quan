local Buffer = require("quan.message.Buffer")
local Message = require("quan.message.Message")

---
---@author 自动生成
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
            '}';
end

---元表
local meta = { __index = UserInfo, __newindex = onSet, __tostring = toString }

---
---UserInfo.构造
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
    }

    instance = setmetatable(instance, meta)
    return instance
end

---
---UserInfo.编码
---@param buffer quan.message.Buffer 可以为空
---@return quan.message.Buffer
---
function UserInfo:encode(buffer)
    assert(type(self) == "table" and self.class == UserInfo.class, "参数[self]类型错误")
    assert(buffer == nil or type(buffer) == "table" and buffer.class == Buffer.class, "参数[buffer]类型错误")
    buffer = buffer or Buffer.new()

    buffer:writeLong(self.id)
    buffer:writeString(self.name)
    buffer:writeInt(self.level)

    return buffer
end

---
---UserInfo.解码
---@param buffer quan.message.Buffer 不能为空
---@param self test.message.user.UserInfo 可以为空
---@return test.message.user.UserInfo
---
function UserInfo.decode(buffer, self)
    assert(type(buffer) == "table" and buffer.class == Buffer.class, "参数[buffer]类型错误")
    assert(self == nil or type(self) == "table" and self.class == UserInfo.class, "参数[self]类型错误")
    self = self or UserInfo.new()

    self.id = buffer:readLong()
    self.name = buffer:readString()
    self.level = buffer:readInt()

    return self
end

UserInfo = table.readOnly(UserInfo)
return UserInfo