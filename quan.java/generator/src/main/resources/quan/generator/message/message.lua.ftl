local Buffer = require("quan.message.Buffer")
local Message = require("quan.message.Message")
<#list imports as import>
local ${import[import?last_index_of(".")+1..]} = require("${import}")
</#list>

---
<#if comment !="">
---${comment}
</#if>
---@author 自动生成
---
local ${name} = {
    ---类名
    class = "${name}",
<#if definitionType ==3>
    ---消息ID
    id = ${id?c}
</#if>
}
<#if headedFields??>
    <#assign allFields = headedFields>
<#else>
    <#assign allFields = fields>
</#if>

local function onSet(self, key, value)
    assert(not ${name}[key], "不允许修改只读属性:" .. key)
    rawset(self, key, value)
end

local function toString(self)
    return "${name}{" ..
    <#list allFields as field>
            "<#rt>
        <#if field_index gt 0>
             <#lt>,<#rt>
        </#if>
        <#if field.type == "string">
             <#lt>${field.name}='" .. tostring(self.${field.name}) .. '\'' ..
        <#elseif field.collectionType>
             <#lt>${field.name}=" .. Message.${field.type}ToString(self.${field.name}) ..
        <#else>
             <#lt>${field.name}=" .. tostring(self.${field.name}) ..
        </#if>
    </#list>
            '}';
end

---元表
local meta = { __index = ${name}, __newindex = onSet, __tostring = toString }

---
---<#if comment !="">${comment}<#else>${name}</#if>.构造
---@param args 参数列表可以为空
---
function ${name}.new(args)
    assert(args == nil or type(args) == "table", "参数错误")
    args = args or {}

    local instance = {
<#list allFields as field>
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

    instance = setmetatable(instance, meta)
    return instance
end

<#if definitionType ==3>
---
---<#if comment !="">${comment}<#else>${name}</#if>.编码
---@return quan.message.Buffer
---
function ${name}:encode()
    assert(type(self) == "table" and self.class == ${name}.class, "参数[self]类型错误")
    local buffer = Message.encode(self)

<#else>
---
---<#if comment !="">${comment}<#else>${name}</#if>.编码
---@param buffer quan.message.Buffer 可以为空
---@return quan.message.Buffer
---
function ${name}:encode(buffer)
    assert(type(self) == "table" and self.class == ${name}.class, "参数[self]类型错误")
    assert(buffer == nil or type(buffer) == "table" and buffer.class == Buffer.class, "参数[buffer]类型错误")
    buffer = buffer or Buffer.new()

</#if>
<#list allFields as field>
    <#if field.ignore>
        <#continue/>
    <#elseif field.type=="set" || field.type=="list">
        <#if field_index gt 0>

        </#if>
    buffer:writeInt(#self.${field.name})
    for i, value in ipairs(self.${field.name}) do
        <#if field.builtinValueType>
        buffer:write${field.valueType?cap_first}(value);
        <#else>
        ${field.valueType}.encode(value, buffer)
        </#if>
    end
    <#if field_has_next && !allFields[field_index+1].collectionType && (allFields[field_index+1].primitiveType || allFields[field_index+1].enumType || !allFields[field_index+1].optional) >

    </#if>
    <#elseif field.type=="map">
        <#if field_index gt 0>

        </#if>
    buffer:writeInt(table.size(self.${field.name}))
    for key, value in pairs(self.${field.name}) do
        buffer:write${field.keyType?cap_first}(key)
        <#if field.builtinValueType>
        buffer:write${field.valueType?cap_first}(value)
        <#else>
        ${field.valueType}.encode(value, buffer)
        </#if>
    end
    <#if field_has_next && !allFields[field_index+1].collectionType && (allFields[field_index+1].primitiveType || allFields[field_index+1].enumType || !allFields[field_index+1].optional) >

    </#if>
    <#elseif field.type=="float"||field.type=="double">
    buffer:write${field.type?cap_first}(self.${field.name}<#if field.scale gt 0>, ${field.scale}</#if>)
    <#elseif field.builtinType>
    buffer:write${field.type?cap_first}(self.${field.name})
    <#elseif field.enumType>
    buffer:writeInt(self.${field.name} or 0);
    <#elseif field.optional>
        <#if field_index gt 0>

        </#if>
    buffer:writeBool(self.${field.name} ~= nil);
    if self.${field.name} ~= nil then
        ${field.type}.encode(self.${field.name}, buffer)
    end
    <#if field_has_next && !allFields[field_index+1].collectionType && (allFields[field_index+1].primitiveType || allFields[field_index+1].enumType || !allFields[field_index+1].optional) >

    </#if>
    <#else>
    ${field.type}.encode(self.${field.name}, buffer)
    </#if>
</#list>

    return buffer
end

<#if definitionType ==3>
---
---<#if comment !="">${comment}<#else>${name}</#if>.解码
---@param buffer quan.message.Buffer 不能为空
---@return ${getFullName("lua")}
---
function ${name}.decode(buffer)
    assert(type(buffer) == "table" and buffer.class == Buffer.class, "参数[buffer]类型错误")
    local self = ${name}.new()
    Message.decode(buffer, self)
<#else>
---
---<#if comment !="">${comment}<#else>${name}</#if>.解码
---@param buffer quan.message.Buffer 不能为空
---@param self ${getFullName("lua")} 可以为空
---@return ${getFullName("lua")}
---
function ${name}.decode(buffer, self)
    assert(type(buffer) == "table" and buffer.class == Buffer.class, "参数[buffer]类型错误")
    assert(self == nil or type(self) == "table" and self.class == ${name}.class, "参数[self]类型错误")
    self = self or ${name}.new()

</#if>
<#list allFields as field>
    <#if field.ignore>
        <#continue/>
    <#elseif field.type=="set" || field.type=="list">
        <#if field_index gt 0>

        </#if>
    for i = 1, buffer:readInt() do
        <#if field.builtinValueType>
        self.${field.name}[i] = buffer:read${field.valueType?cap_first}()
        <#else>
        self.${field.name}[i] = ${field.valueType}.decode(buffer)
        </#if>
    end
    <#if field_has_next && !allFields[field_index+1].collectionType && (allFields[field_index+1].primitiveType || allFields[field_index+1].enumType || !allFields[field_index+1].optional) >

    </#if>
    <#elseif field.type=="map">
        <#if field_index gt 0>

        </#if>
    for i = 1, buffer:readInt() do
        <#if field.builtinValueType>
        self.${field.name}[buffer:read${field.keyType?cap_first}()] = buffer:read${field.valueType?cap_first}()
        <#else>
        self.${field.name}[buffer:read${field.keyType?cap_first}()] = ${field.valueType}.decode(buffer)
        </#if>
    end
    <#if field_has_next && !allFields[field_index+1].collectionType && (allFields[field_index+1].primitiveType || allFields[field_index+1].enumType || !allFields[field_index+1].optional) >

    </#if>
    <#elseif field.type=="float"||field.type=="double">
    self.${field.name} = buffer:read${field.type?cap_first}(<#if field.scale gt 0>${field.scale}</#if>)
    <#elseif field.builtinType>
    self.${field.name} = buffer:read${field.type?cap_first}()
    <#elseif field.enumType>
    self.${field.name} = buffer:readInt();
    <#elseif field.optional>
        <#if field_index gt 0>

        </#if>
    if buffer:readBool() then
        self.${field.name} = ${field.type}.decode(buffer)
    end
    <#if field_has_next && !allFields[field_index+1].collectionType && (allFields[field_index+1].primitiveType || allFields[field_index+1].enumType || !allFields[field_index+1].optional) >

    </#if>
    <#else>
    self.${field.name} = ${field.type}.decode(buffer, self.${field.name})
    </#if>
</#list>

    return self
end

${name} = table.readOnly(${name})
return ${name}