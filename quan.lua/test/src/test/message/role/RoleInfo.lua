local Buffer = require("quan.message.Buffer")
local Message = require("quan.message.Message")

---
---角色信息
---自动生成
---
local RoleInfo = {
    ---类名
    class = "test.message.role.RoleInfo",
}

local function onSet(table, key, value)
    assert(not RoleInfo[key], "不允许修改只读属性:" .. key)
    rawset(table, key, value)
end

---
---角色信息.构造
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
        type = args.type or 0,
        b = args.b or false,
        s = args.s or 0,
        i = args.i or 0,
        f = args.f or 0.0,
        d = args.d or 0.0,
        data = args.data or "",
        list = args.list or {},
        set = args.set or {},
        map = args.map or {},
    }

    instance = setmetatable(instance, { __index = RoleInfo, __newindex = onSet })
    return instance
end

---
---角色信息.编码
---@param buffer quan.message.Buffer 可以为空
---@return quan.message.Buffer
---
function RoleInfo:encode(buffer)
    assert(type(self) == "table" and self.class == RoleInfo.class, "参数[self]类型错误")
    assert(buffer == nil or type(buffer) == "table" and buffer.class == Buffer.class, "参数[buffer]类型错误")
    buffer = buffer or Buffer.new()

    buffer:writeLong(self.id)
    buffer:writeString(self.name)
    buffer:writeInt(self.type or 0);
    buffer:writeBool(self.b)
    buffer:writeShort(self.s)
    buffer:writeInt(self.i)
    buffer:writeFloat(self.f, 2)
    buffer:writeDouble(self.d)
    buffer:writeBytes(self.data)

    buffer:writeInt(#self.list)
    for i, value in ipairs(self.list) do
        buffer:writeInt(value);
    end

    buffer:writeInt(#self.set)
    for i, value in ipairs(self.set) do
        buffer:writeInt(value);
    end

    buffer:writeInt(table.size(self.map))
    for key, value in pairs(self.map) do
        buffer:writeInt(key)
        buffer:writeInt(value)
    end

    return buffer
end

---
---角色信息.解码
---@param buffer quan.message.Buffer 不能为空
---@param self test.message.role.RoleInfo 可以为空
---@return test.message.role.RoleInfo
---
function RoleInfo.decode(buffer, self)
    assert(type(buffer) == "table" and buffer.class == Buffer.class, "参数[buffer]类型错误")
    assert(self == nil or type(self) == "table" and self.class == RoleInfo.class, "参数[self]类型错误")
    self = self or RoleInfo.new()

    self.id = buffer:readLong()
    self.name = buffer:readString()
    self.type = buffer:readInt();
    self.b = buffer:readBool()
    self.s = buffer:readShort()
    self.i = buffer:readInt()
    self.f = buffer:readFloat(2)
    self.d = buffer:readDouble()
    self.data = buffer:readBytes()

    for i = 1, buffer:readInt() do
        self.list[i] = buffer:readInt()
    end

    for i = 1, buffer:readInt() do
        self.set[i] = buffer:readInt()
    end

    for i = 1, buffer:readInt() do
        self.map[buffer:readInt()] = buffer:readInt()
    end

    return self
end

RoleInfo = table.readOnly(RoleInfo)
return RoleInfo