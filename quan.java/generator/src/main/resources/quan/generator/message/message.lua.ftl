local Buffer = require("quan.message.Buffer")
local Message = require("quan.message.Message")
<#list imports as import>
local ${import[import?last_index_of(".")+1..]} = require("${import}")
</#list>

---
<#if comment !="">
---${comment}
</#if>
---自动生成
---
local ${name} = {}
${name}.__index = ${name}

<#if definitionType ==3>
---消息ID
${name}.id = ${id?c}
---消息类名
${name}.class = "${fullName}"

</#if>
---
---<#if comment !="">${comment}<#else>${name}</#if>.构造
---@param args 参数列表可以为空
---
function ${name}.new(args)
    assert(args == nil or type(args) == "table", "参数错误")
    args = args or {}

    local instance = {
<#if definitionType ==3>
        ---消息序号
        seq = args.seq or 0,
</#if>
<#list fields as field>
    <#if field.comment !="">
        ---${field.comment}
    </#if>
    <#if field.numberType || field.enumType>
        ${field.name} = args.${field.name} or 0<#if field.type="float" || field.type="double">.0</#if>,
    <#elseif field.type="bool">
        ${field.name} = args.${field.name} or false,
    <#elseif field.type == "string" || field.type == "bytes">
        ${field.name} = args.${field.name} or "",
    <#elseif field.collectionType>
        ${field.name} = args.${field.name} or {},
    <#elseif !field.optional>
        ${field.name} = args.${field.name} or ${field.type}.new(),
    <#else>
        ${field.name} = args.${field.name},
    </#if>
</#list>
    }

    return setmetatable(instance, ${name})
end

---
---<#if comment !="">${comment}<#else>${name}</#if>.编码
---@param msg ${fullName} 不能为空
---@param buffer quan.message.Buffer 可以为空
---@return quan.message.Buffer
---
function ${name}.encode(msg, buffer)
    assert(msg ~= nil, "参数[msg]不能为空")
<#if definitionType ==3>
    buffer = Message.encode(msg, buffer)
</#if>

<#list fields as field>
    <#if field.type=="set" || field.type=="list">
        <#if field_index gt 0>

        </#if>
    buffer:writeInt(#msg.${field.name})
    for i, value in ipairs(msg.${field.name}) do
        <#if field.builtinValueType>
        buffer:write${field.valueType?cap_first}(value);
        <#else>
        ${field.valueType}.encode(value, buffer)
        </#if>
    end
    <#if field_has_next && !fields[field_index+1].collectionType && (fields[field_index+1].primitiveType || fields[field_index+1].enumType || !fields[field_index+1].optional) >

    </#if>
    <#elseif field.type=="map">
        <#if field_index gt 0>

        </#if>
    buffer:writeInt(table.size(msg.${field.name}))
    for key, value in pairs(msg.${field.name}) do
        buffer:write${field.keyType?cap_first}(key)
        <#if field.builtinValueType>
        buffer:write${field.valueType?cap_first}(value)
        <#else>
        ${field.valueType}.encode(value, buffer)
        </#if>
    end
    <#if field_has_next && !fields[field_index+1].collectionType && (fields[field_index+1].primitiveType || fields[field_index+1].enumType || !fields[field_index+1].optional) >

    </#if>
    <#elseif field.type=="float"||field.type=="double">
    buffer:write${field.type?cap_first}(msg.${field.name}<#if field.scale gt 0>, ${field.scale}</#if>)
    <#elseif field.builtinType>
    buffer:write${field.type?cap_first}(msg.${field.name})
    <#elseif field.enumType>
    buffer:writeInt(msg.${field.name} or 0);
    <#elseif field.optional>
        <#if field_index gt 0>

        </#if>
    buffer:writeBool(msg.${field.name} ~= nil);
    if msg.${field.name} ~= nil then
        ${field.type}.encode(msg.${field.name}, buffer)
    end
    <#if field_has_next && !fields[field_index+1].collectionType && (fields[field_index+1].primitiveType || fields[field_index+1].enumType || !fields[field_index+1].optional) >

    </#if>
    <#else>
    ${field.type}.encode(msg.${field.name}, buffer)
    </#if>
</#list>

    return buffer
end

---
---<#if comment !="">${comment}<#else>${name}</#if>.解码
---@param buffer quan.message.Buffer 不能为空
---@param msg ${fullName} 可以为空
---@return ${fullName}
---
function ${name}.decode(buffer, msg)
    assert(buffer ~= nil, "参数[buffer]不能为空")
    msg = msg or ${name}.new()
<#if definitionType ==3>
    Message.decode(buffer, msg)
</#if>

<#list fields as field>
    <#if field.type=="set" || field.type=="list">
        <#if field_index gt 0>

        </#if>
    for i = 1, buffer:readInt() do
        <#if field.builtinValueType>
        msg.${field.name}[i] = buffer:read${field.valueType?cap_first}()
        <#else>
        msg.${field.name}[i] = ${field.valueType}.decode(buffer)
        </#if>
    end
    <#if field_has_next && !fields[field_index+1].collectionType && (fields[field_index+1].primitiveType || fields[field_index+1].enumType || !fields[field_index+1].optional) >

    </#if>
    <#elseif field.type=="map">
        <#if field_index gt 0>

        </#if>
    for i = 1, buffer:readInt() do
        <#if field.builtinValueType>
        msg.${field.name}[buffer:read${field.keyType?cap_first}()] = buffer:read${field.valueType?cap_first}()
        <#else>
        msg.${field.name}[buffer:read${field.keyType?cap_first}()] = ${field.valueType}.decode(buffer)
        </#if>
    end
    <#if field_has_next && !fields[field_index+1].collectionType && (fields[field_index+1].primitiveType || fields[field_index+1].enumType || !fields[field_index+1].optional) >

    </#if>
    <#elseif field.type=="float"||field.type=="double">
    msg.${field.name} = buffer:read${field.type?cap_first}(<#if field.scale gt 0>${field.scale}</#if>)
    <#elseif field.builtinType>
    msg.${field.name} = buffer:read${field.type?cap_first}()
    <#elseif field.enumType>
    msg.${field.name} = buffer:readInt();
    <#elseif field.optional>
        <#if field_index gt 0>

        </#if>
    if buffer:readBool() then
        msg.${field.name} = ${field.type}.decode(buffer)
    end
    <#if field_has_next && !fields[field_index+1].collectionType && (fields[field_index+1].primitiveType || fields[field_index+1].enumType || !fields[field_index+1].optional) >

    </#if>
    <#else>
    msg.${field.name} = ${field.type}.decode(buffer, msg.${field.name})
    </#if>
</#list>

    return msg
end

return ${name}