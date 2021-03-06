---
---消息头
---代码自动生成，请勿手动修改
---

local _Buffer = require("quan.message.Buffer")
local _Message = require("quan.message.Message")

---
---消息头
---
local MessageHeader = {
    ---类名
    class = "test.message.common.MessageHeader",
}

---
---[消息头].编码
---@param buffer quan.message.Buffer 可以为空
---@return quan.message.Buffer
---
function MessageHeader:encode(buffer)
    assert(buffer == nil or type(buffer) == "table" and buffer.class == _Buffer.class, "参数[buffer]类型错误")

    buffer = buffer or _Buffer.new()

    buffer:writeLong(self.seq)
    buffer:writeInt(self.error)

    return buffer
end

---
---[消息头].解码
---@param buffer quan.message.Buffer 不能为空
---@param self test.message.common.MessageHeader 可以为空
---@return test.message.common.MessageHeader
---
function MessageHeader.decode(buffer, self)
    assert(type(buffer) == "table" and buffer.class == _Buffer.class, "参数[buffer]类型错误")

    self = self or MessageHeader.new()

    self.seq = buffer:readLong()
    self.error = buffer:readInt()

    return self
end

MessageHeader = table.readOnly(MessageHeader)
return MessageHeader