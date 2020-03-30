---
---消息工厂
---自动生成
---
local MessageFactory = {}

local registry = {
<#list messages as msg>
    [${msg.id?c}] = "${msg.getFullName("lua")}",
</#list> 
}

---
---消息工厂.创建消息
---@param msgId 消息ID
---
function MessageFactory.create(msgId)
    assert(math.type(msgId) == "integer", "参数[msgId]类型错误")
    local msgName = registry[msgId]
    if msgName then
        return require(msgName).new()
    end
end

return MessageFactory