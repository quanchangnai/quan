---
--- Generated by EmmyLua(https://github.com/EmmyLua)
--- Created by quanchangnai.
--- DateTime: 2019/9/4 16:14
---

local Buffer = require("quan.message.Buffer")

local Message = {}

function Message.encode(msg, buffer)
    if buffer then
        buffer:reset()
    else
        buffer = Buffer.new()
    end
    buffer:writeInt(msg.id);
    return buffer
end

function Message.decode(buffer, msg)
    local msgId = buffer:readInt();
    if msgId ~= msg.id then
        error(string.format("消息ID不匹配,期望值[%s],实际值[%s]", msg.id, msgId))
    end
    return msg
end

--按位与
local band;
if _VERSION == "Lua 5.3" or _VERSION == "Lua 5.4" then
    band = require("quan.message.bits").band
else
    band = require("bit").band
end

function Message.skipField(tag, buffer)
    local t = band(tag, 3)
    if t == 0 then
        buffer:readLong()
    elseif t == 1 then
        buffer:readFloat()
    elseif t == 2 then
        buffer:readDouble()
    else
        buffer:skipField()
    end
end

return Message
