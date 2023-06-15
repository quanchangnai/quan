---
---角色信息
---代码自动生成，请勿手动修改
---

local _CodedBuffer = require("quan.message.CodedBuffer")
local _Message = require("quan.message.Message")

---
---角色信息
---
local RoleInfo = {
    ---类名
    class = "test.message.role.RoleInfo",
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

    if key == "alias" then
        assert(value == nil or type(value) == "string", propTypeError)
    end

    if key == "type" then
        assert(type(value) == "number", propTypeError)
    end

    if key == "b" then
        assert(type(value) == "boolean", propTypeError)
    end

    if key == "s" then
        assert(type(value) == "number", propTypeError)
    end

    if key == "i" then
        assert(type(value) == "number", propTypeError)
        _Message.validateRange(value, 1, 20);
    end

    if key == "d" then
        assert(type(value) == "number", propTypeError)
    end

    if key == "bb1" then
        assert(type(value) == "string", propTypeError)
    end

    if key == "bb2" then
        assert(value == nil or type(value) == "string", propTypeError)
    end

    if key == "list" then
        assert(type(value) == "table", propTypeError)
    end

    if key == "set" then
        assert(type(value) == "table", propTypeError)
    end

    if key == "map" then
        assert(type(value) == "table", propTypeError)
    end

    rawset(self, key, value)
end

local function toString(self)
    return "RoleInfo{" ..
            "id=" .. tostring(self.id) ..
            ",name='" .. tostring(self.name) .. '\'' ..
            ",alias='" .. tostring(self.alias) .. '\'' ..
            ",type=" .. tostring(self.type) ..
            ",b=" .. tostring(self.b) ..
            ",s=" .. tostring(self.s) ..
            ",i=" .. tostring(self.i) ..
            ",d=" .. tostring(self.d) ..
            ",bb1=" .. tostring(self.bb1) ..
            ",bb2=" .. tostring(self.bb2) ..
            ",list=" .. table.toString(self.list) ..
            ",set=" .. table.toString(self.set) ..
            ",map=" .. table.toString(self.map) ..
            '}';
end

---元表
local meta = { __index = RoleInfo, __newindex = onSet, __tostring = toString }

---
---[角色信息].构造
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
        alias = args.alias,
        type = args.type or 0,
        b = args.b or false,
        s = args.s or 0,
        i = args.i or 0,
        d = args.d or 0.0,
        bb1 = args.bb1 or "",
        bb2 = args.bb2,
        list = args.list or {},
        set = args.set or {},
        map = args.map or {},
    }

    instance = setmetatable(instance, meta)

    return instance
end

setmetatable(RoleInfo, { __call = RoleInfo.new })

---
---[角色信息].编码
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

    buffer:writeBool(self.alias ~= nil)
    if self.alias ~= nil then
        buffer:writeString(self.alias) 
    end

    buffer:writeInt(self.type or 0)
    buffer:writeBool(self.b)
    buffer:writeShort(self.s)
    buffer:writeInt(self.i)
    buffer:writeDouble(self.d)
    buffer:writeBytes(self.bb1)

    buffer:writeBool(self.bb2 ~= nil)
    if self.bb2 ~= nil then
        buffer:writeBytes(self.bb2) 
    end

    buffer:writeInt(#self.list)
    for i, value in ipairs(self.list) do
        buffer:writeInt(value)
    end

    buffer:writeInt(#self.set)
    for i, value in ipairs(self.set) do
        buffer:writeInt(value)
    end

    return buffer
end

---
---[角色信息].解码
---@param buffer quan.message.CodedBuffer 不能为空
---@param self test.message.role.RoleInfo 可以为空
---@return test.message.role.RoleInfo
---
function RoleInfo.decode(buffer, self)
    assert(type(buffer) == "table" and buffer.class == _CodedBuffer.class, "参数[buffer]类型错误")
    assert(self == nil or type(self) == "table" and self.class == RoleInfo.class, "参数[self]类型错误")

    self = self or RoleInfo.new()

    self.id = buffer:readInt()
    self.name = buffer:readString()

    if buffer:readBool() then
        self.alias = buffer:readString()
    end

    self.type = buffer:readInt()
    self.b = buffer:readBool()
    self.s = buffer:readShort()
    self.i = buffer:readInt()
    self.d = buffer:readDouble()
    self.bb1 = buffer:readBytes()

    if buffer:readBool() then
        self.bb2 = buffer:readBytes()
    end

    for i = 1, buffer:readInt() do
        self.list[i] = buffer:readInt()
    end

    for i = 1, buffer:readInt() do
        self.set[i] = buffer:readInt()
    end

    self:validate()

    return self
end

function RoleInfo:validate()
    assert(type(self.id) == "number", "属性[id]类型错误")
    assert(type(self.name) == "string",  "属性[name]类型错误")
    assert(self.alias == nil or type(self.alias) == "string",  "属性[alias]类型错误")
    assert(type(self.type) == "number", "属性[type]类型错误")
    assert(type(self.b) == "boolean", "属性[b]类型错误")
    assert(type(self.s) == "number", "属性[s]类型错误")
    assert(type(self.i) == "number", "属性[i]类型错误")
    _Message.validateRange(self.i, 1, 20, "属性[i]");
    assert(type(self.d) == "number", "属性[d]类型错误")
    assert(type(self.bb1) == "string",  "属性[bb1]类型错误")
    assert(self.bb2 == nil or type(self.bb2) == "string",  "属性[bb2]类型错误")
    assert(type(self.list) == "table",  "属性[list]类型错误")
    assert(type(self.set) == "table",  "属性[set]类型错误")
    assert(type(self.map) == "table",  "属性[map]类型错误")
end

RoleInfo = table.readOnly(RoleInfo)
return RoleInfo