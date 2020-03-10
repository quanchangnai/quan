---
---消息工厂
---自动生成
---
local MessageFactory = {}

local prototypes = {
    [544233] = "test.message.role.CRoleLogin",
    [763075] = "test.message.role.SRoleLogin",
}

---
---消息工厂.创建消息
---@param msgId 消息ID
---
function MessageFactory.create(msgId)
    assert(math.type(msgId) == "integer", "参数[msgId]类型错误")
    return require(prototypes[msgId]).new()
end

return MessageFactory