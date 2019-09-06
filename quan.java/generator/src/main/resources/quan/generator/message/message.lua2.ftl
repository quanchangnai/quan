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
local ${name} = {
    ---类名
    class = "${fullName}",
<#if definitionType ==3>
    ---消息ID
    id = ${id?c}
</#if>
}

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
        ${field.name} = {},
    <#elseif !field.optional>
        ${field.name} = ${field.type}.new(),
    <#else>
        ${field.name} = nil,
    </#if>
</#list>
    }

    local origin = instance

    local function onUpdateProp(table, name, value)
<#if definitionType ==3>
        if ${name}[name] then
            error("不能修改只读属性:" .. name, 2)
        end

</#if>
        local valueType = type(value)
        valueType = valueType ~= "number" and valueType or math.type(value)
        local typeError = "属性类型错误，期望类型:%s,实际类型:%s"

<#if definitionType ==3>
        if name == "seq" and math.type(value) ~= "integer" then
            error(string.format(typeError, "integer", valueType), 2)
        end

</#if>
<#list fields as field>
    <#if field.type=="float"||field.type=="double">
        if name == "${field.name}" and valueType ~= "float" and valueType ~= "integer" then
            error(string.format(typeError, "number", type(value)), 2)
        end
    <#elseif field.numberType || field.enumType>
        if name == "${field.name}" and valueType ~= "integer" then
            error(string.format(typeError, "integer", valueType), 2)
        end
    <#elseif field.type=="bool">
        if name == "${field.name}" and valueType ~= "boolean" then
            error(string.format(typeError, "boolean", valueType), 2)
        end
    <#elseif field.type=="string" || field.type=="bytes">
        if name == "${field.name}" and valueType ~= "string" then
            error(string.format(typeError, "string", valueType), 2)
        end
    <#elseif field.collectionType>
        if name == "${field.name}" and valueType ~= "table" then
            error(string.format(typeError, "${field.type}", valueType), 2)
        end
    <#elseif field.optional>
        if name == "${field.name}" and valueType ~= "nil" and (valueType ~= "table" or value.class ~= "${field.classTypeFullName}") then
            error(string.format(typeError, "nil|${field.classTypeFullName}", valueType), 2)
        end
    <#else>
        if name == "${field.name}" and (valueType ~= "table" or value.class ~= "${field.classTypeFullName}") then
            error(string.format(typeError, "${field.classTypeFullName}", valueType), 2)
        end
    </#if>

</#list>
        origin[name] = value
    end

    instance = setmetatable(instance, { __index = ${name} })
    instance = setmetatable({}, { __index = instance, __newindex = onUpdateProp })
    return instance
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

${name} = table.readOnly(${name})
return ${name}