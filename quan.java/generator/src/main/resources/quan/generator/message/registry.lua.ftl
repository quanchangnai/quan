---
---消息注册表
---自动生成，请勿修改
---
local MessageRegistry = {}

local messages = {
<#list messages as msg>
    [${msg.id?c}] = "${msg.getFullName("lua")}",
</#list>
}

---
---消息注册表.获取消息全名
---@param msgId 消息ID
---
function MessageRegistry.getName(msgId)
    assert(math.type(msgId) == "integer", "参数[msgId]类型错误")
    return messages[msgId]
end

---
---消息注册表.创建消息
---@param msgId 消息ID
---
function MessageRegistry.create(msgId)
    assert(math.type(msgId) == "integer", "参数[msgId]类型错误")
    local msgName = messages[msgId]
    if msgName then
        return require(msgName).new()
    end
end

return MessageRegistry