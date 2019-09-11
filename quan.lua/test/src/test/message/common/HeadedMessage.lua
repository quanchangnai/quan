local Buffer = require("quan.message.Buffer")
local Message = require("quan.message.Message")

---
---自动生成
---
local HeadedMessage = {
    ---类名
    class = "test.message.common.HeadedMessage",
}

local function onSet(table, key, value)
    assert(not HeadedMessage[key], "不允许修改只读属性:" .. key)
    rawset(table, key, value)
end

---
---HeadedMessage.构造
---@param args 参数列表可以为空
---
function HeadedMessage.new(args)
    assert(args == nil or type(args) == "table", "参数错误")
    args = args or {}

    local instance = {
        h1 = args.h1 or 0,
        h2 = args.h2 or "",
    }

    instance = setmetatable(instance, { __index = HeadedMessage, __newindex = onSet })
    return instance
end

---
---HeadedMessage.编码
---@param msg test.message.common.HeadedMessage 不能为空
---@param buffer quan.message.Buffer 可以为空
---@return quan.message.Buffer
---
function HeadedMessage.encode(msg, buffer)
    assert(type(msg) == "table" and msg.class == HeadedMessage.class, "参数[msg]类型错误")
    assert(buffer == nil or type(buffer) == "table" and buffer.class == Buffer.class, "参数[buffer]类型错误")

    buffer:writeLong(msg.h1)
    buffer:writeString(msg.h2)

    return buffer
end

---
---HeadedMessage.解码
---@param buffer quan.message.Buffer 不能为空
---@param msg test.message.common.HeadedMessage 可以为空
---@return test.message.common.HeadedMessage
---
function HeadedMessage.decode(buffer, msg)
    assert(type(buffer) == "table" and buffer.class == Buffer.class, "参数[buffer]类型错误")
    assert(msg == nil or type(msg) == "table" and msg.class == HeadedMessage.class, "参数[msg]类型错误")

    msg = msg or HeadedMessage.new()
    msg.h1 = buffer:readLong()
    msg.h2 = buffer:readString()

    return msg
end

HeadedMessage = table.readOnly(HeadedMessage)
return HeadedMessage