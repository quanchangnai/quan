---
---角色信息2
---代码自动生成，请勿手动修改
---

local _CodedBuffer = require("quan.message.CodedBuffer")
local _Message = require("quan.message.Message")

---
---角色信息2
---
local RoleInfo = {
    ---类名
    class = "test.message.user.RoleInfo",
}

local function onSet(self, key, value)
    assert(not RoleInfo[key], "不允许修改只读属性:" .. key)

    local propTypeError = string.format("属性%s类型%s错误", key, type(value))

    if key == "id" then
        assert(type(value) == "number", propTypeError)
    end

    if key == "name" then
        assert(type(value) == "string", propTypeError)
    end

    rawset(self, key, value)
end

local function toString(self)
    return "RoleInfo{" ..
            "id=" .. tostring(self.id) ..
            ",name='" .. tostring(self.name) .. '\'' ..
            '}';
end

---元表
local meta = { __index = RoleInfo, __newindex = onSet, __tostring = toString }

---
---[角色信息2].构造
---@param args 参数列表可以为空
---
function RoleInfo.new(args)
    assert(args == nil or type(args) == "table", "参数错误")
    args = args or {}

    local instance = {
        ---角色id
        id = args.id or 0,
        ---角色名
        name = args.name or "",
    }

    instance = setmetatable(instance, meta)

    return instance
end

setmetatable(RoleInfo, { __call = RoleInfo.new })

---
---[角色信息2].编码
---@param buffer quan.message.CodedBuffer 可以为空
---@return quan.message.CodedBuffer
---
function RoleInfo:encode(buffer)
    assert(type(self) == "table" and self.class == RoleInfo.class, "参数[self]类型错误")
    assert(buffer == nil or type(buffer) == "table" and buffer.class == _CodedBuffer.class, "参数[buffer]类型错误")
    self:validate()

    buffer = buffer or _CodedBuffer.new()

    buffer:writeInt(self.id)
    buffer:writeString(self.name)

    return buffer
end

---
---[角色信息2].解码
---@param buffer quan.message.CodedBuffer 不能为空
---@param self test.message.user.RoleInfo 可以为空
---@return test.message.user.RoleInfo
---
function RoleInfo.decode(buffer, self)
    assert(type(buffer) == "table" and buffer.class == _CodedBuffer.class, "参数[buffer]类型错误")
    assert(self == nil or type(self) == "table" and self.class == RoleInfo.class, "参数[self]类型错误")

    self = self or RoleInfo.new()

    self.id = buffer:readInt()
    self.name = buffer:readString()

    self:validate()

    return self
end

function RoleInfo:validate()
    assert(type(self.id) == "number", "属性[id]类型错误")
    assert(type(self.name) == "string",  "属性[name]类型错误")
end

RoleInfo = table.readOnly(RoleInfo)
return RoleInfo