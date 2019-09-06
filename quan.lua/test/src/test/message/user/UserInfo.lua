local Buffer = require("quan.message.Buffer")
local Message = require("quan.message.Message")

---
---自动生成
---
local UserInfo = {
    ---类名
    class = "test.message.user.UserInfo",
}

local function onUpdateProp(table, key, value)
    assert(not UserInfo[key], "不允许修改只读属性:" .. key)
    rawset(table, key, value)
end

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

    instance = setmetatable(instance, { __index = UserInfo, __newindex = onUpdateProp })
    return instance
end

---
---UserInfo.编码
---@param msg test.message.user.UserInfo 不能为空
---@param buffer quan.message.Buffer 可以为空
---@return quan.message.Buffer
---
function UserInfo.encode(msg, buffer)
    assert(msg ~= nil, "参数[msg]不能为空")

    buffer:writeLong(msg.id)
    buffer:writeString(msg.name)
    buffer:writeInt(msg.level)

    return buffer
end

---
---UserInfo.解码
---@param buffer quan.message.Buffer 不能为空
---@param msg test.message.user.UserInfo 可以为空
---@return test.message.user.UserInfo
---
function UserInfo.decode(buffer, msg)
    assert(buffer ~= nil, "参数[buffer]不能为空")
    msg = msg or UserInfo.new()

    msg.id = buffer:readLong()
    msg.name = buffer:readString()
    msg.level = buffer:readInt()

    return msg
end

UserInfo = table.readOnly(UserInfo)
return UserInfo