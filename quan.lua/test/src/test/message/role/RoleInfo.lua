local Buffer = require("quan.message.Buffer")
local Message = require("quan.message.Message")

---
---角色信息
---自动生成
---
local RoleInfo = {}

local meta = {
    __newindex = function()
        error("该操作不支持")
    end
}

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

    return setmetatable(instance, meta)
end

---
---角色信息.编码
---@param msg test.message.role.RoleInfo 不能为空
---@param buffer quan.message.Buffer 可以为空
---@return quan.message.Buffer
---
function RoleInfo.encode(msg, buffer)
    assert(msg ~= nil, "参数[msg]不能为空")

    buffer:writeLong(msg.id)
    buffer:writeString(msg.name)
    buffer:writeInt(msg.type or 0);
    buffer:writeBool(msg.b)
    buffer:writeShort(msg.s)
    buffer:writeInt(msg.i)
    buffer:writeFloat(msg.f, 2)
    buffer:writeDouble(msg.d)
    buffer:writeBytes(msg.data)

    buffer:writeInt(#msg.list)
    for i, value in ipairs(msg.list) do
        buffer:writeInt(value);
    end

    buffer:writeInt(#msg.set)
    for i, value in ipairs(msg.set) do
        buffer:writeInt(value);
    end

    buffer:writeInt(table.size(msg.map))
    for key, value in pairs(msg.map) do
        buffer:writeInt(key)
        buffer:writeInt(value)
    end

    return buffer
end

---
---角色信息.解码
---@param buffer buffer quan.message.Buffer 不能为空
---@param msg test.message.role.RoleInfo 可以为空
---@return test.message.role.RoleInfo
---
function RoleInfo.decode(buffer, msg)
    assert(buffer ~= nil, "参数[buffer]不能为空")
    msg = msg or RoleInfo.new()

    msg.id = buffer:readLong()
    msg.name = buffer:readString()
    msg.type = buffer:readInt();
    msg.b = buffer:readBool()
    msg.s = buffer:readShort()
    msg.i = buffer:readInt()
    msg.f = buffer:readFloat(2)
    msg.d = buffer:readDouble()
    msg.data = buffer:readBytes()

    for i = 1, buffer:readInt() do
        table.insert(msg.list, buffer:readInt())
    end

    for i = 1, buffer:readInt() do
        table.insert(msg.set, buffer:readInt())
    end

    for i = 1, buffer:readInt() do
        msg.map[buffer:readInt()] = buffer:readInt()
    end

    return msg
end

return setmetatable(RoleInfo, meta)