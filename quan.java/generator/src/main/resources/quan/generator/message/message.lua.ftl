---
<#if comment !="">
---${comment}
</#if>
---代码自动生成，请勿手动修改
---

local _Buffer = require("quan.message.Buffer")
local _Message = require("quan.message.Message")
<#if header??>
local _MessageHeader = require("${header.getFullName("lua")}")
</#if>
<#list imports?keys as import>
local ${imports[import]?replace('.','_')} = require("${import}")
</#list>

<#if comment !="">
---
---${comment}
---
</#if>
local ${name} = {
    ---类名
    class = "${getFullName('lua')}",
<#if kind ==3>
    ---消息ID
    id = ${id?c}
</#if>
}

<#if kind !=9>
local function onSet(self, key, value)
    assert(not ${name}[key], "不允许修改只读属性:" .. key)
    rawset(self, key, value)
end

local function toString(self)
    return "${name}{" ..
    <#list fields as field>
            "<#rt>
        <#if field?index gt 0>
             <#lt>,<#rt>
        </#if>
        <#if field.type == "string">
             <#lt>${field.name}='" .. tostring(self.${field.name}) .. '\'' ..
        <#elseif field.collectionType>
             <#lt>${field.name}=" .. table.${field.type}ToString(self.${field.name}) ..
        <#else>
             <#lt>${field.name}=" .. tostring(self.${field.name}) ..
        </#if>
    </#list>
            '}';
end

---元表
local meta = { __index = ${name}, __newindex = onSet, __tostring = toString }

---
---<#if comment !="">[${comment}]<#else>${name}</#if>.构造
---@param args 参数列表可以为空
---
function ${name}.new(args)
    assert(args == nil or type(args) == "table", "参数错误")
    args = args or {}

    local instance = {
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
        ${field.name} = args.${field.name} or ${field.classType?replace('.','_')}.new(),
    <#else>
        ${field.name} = args.${field.name},
    </#if>
</#list>
    }

    instance = setmetatable(instance, meta)
    return instance
end

</#if>
<#if kind ==3>
---
---<#if comment !="">[${comment}]<#else>${name}</#if>.编码
---@return quan.message.Buffer
---
function ${name}:encode()
    assert(type(self) == "table" and self.class == ${name}.class, "参数[self]类型错误")

    local buffer = _Message.encode(self)
    <#if header??>
    _MessageHeader.encode(self, buffer)

    </#if>
<#else>
---
---<#if comment !="">[${comment}]<#else>${name}</#if>.编码
---@param buffer quan.message.Buffer 可以为空
---@return quan.message.Buffer
---
function ${name}:encode(buffer)
    <#if kind ==2>
    assert(type(self) == "table" and self.class == ${name}.class, "参数[self]类型错误")
    </#if>
    assert(buffer == nil or type(buffer) == "table" and buffer.class == _Buffer.class, "参数[buffer]类型错误")

    buffer = buffer or _Buffer.new()

    <#if header??>
    _MessageHeader.encode(self,buffer)

    </#if>
</#if>
<#list fields as field>
    <#if field.ignore>
        <#continue/>
    </#if>
    <#if definedFieldId>
    buffer:writeTag(${field.tag})
    </#if>
    <#if field.type=="set" || field.type=="list">
        <#if definedFieldId>
    local ${field.name}Buffer = _Buffer.new()
    ${field.name}Buffer:writeInt(#self.${field.name})
    for i, value in ipairs(self.${field.name}) do
        <#if field.builtinValueType>
        ${field.name}Buffer:write${field.valueType?cap_first}(value)
        <#else>
        ${field.classValueType?replace('.','_')}.encode(value, ${field.name}Buffer)
        </#if>
    end
    buffer:writeBuffer(${field.name}Buffer)
        <#else>
        <#if field?index gt 0>

        </#if>
    buffer:writeInt(#self.${field.name})
    for i, value in ipairs(self.${field.name}) do
        <#if field.builtinValueType>
        buffer:write${field.classValueType?replace('.','_')?cap_first}(value)
        <#else>
        ${field.valueType}.encode(value, buffer)
        </#if>
    end
        <#if field?has_next && !fields[field?index+1].collectionType && (fields[field?index+1].primitiveType || fields[field?index+1].enumType || !fields[field?index+1].optional) >

        </#if>
        </#if>
    <#elseif field.type=="map">
        <#if definedFieldId>
    local ${field.name}Buffer = _Buffer.new()
    ${field.name}Buffer:writeInt(table.size(self.${field.name}))
    for key, value in pairs(self.${field.name}) do
        ${field.name}Buffer:write${field.keyType?cap_first}(key)
        <#if field.builtinValueType>
        ${field.name}Buffer:write${field.valueType?cap_first}(value)
        <#else>
        ${field.classValueType}.encode(value, ${field.name}Buffer)
        </#if>
    end
    buffer:writeBuffer(${field.name}Buffer)
        <#else>
        <#if field?index gt 0>

        </#if>
    buffer:writeInt(table.size(self.${field.name}))
    for key, value in pairs(self.${field.name}) do
        buffer:write${field.keyType?cap_first}(key)
        <#if field.builtinValueType>
        buffer:write${field.valueType?cap_first}(value)
        <#else>
        ${field.classValueType?replace('.','_')}.encode(value, buffer)
        </#if>
    end
        <#if field?has_next && !fields[field?index+1].collectionType && (fields[field?index+1].primitiveType || fields[field?index+1].enumType || !fields[field?index+1].optional) >

        </#if>
        </#if>
    <#elseif field.type=="float"||field.type=="double">
    buffer:write${field.type?cap_first}(self.${field.name}<#if field.scale gt 0>, ${field.scale}</#if>)
    <#elseif field.builtinType>
    buffer:write${field.type?cap_first}(self.${field.name})
    <#elseif field.enumType>
    buffer:writeInt(self.${field.name} or 0)
    <#elseif field.optional>
        <#if definedFieldId>
    local ${field.name}Buffer = _Buffer.new()
    ${field.name}Buffer:writeBool(self.${field.name} ~= nil)
    if self.${field.name} ~= nil then
        ${field.classType?replace('.','_')}.encode(self.${field.name}, ${field.name}Buffer)
    end
    buffer:writeBuffer(${field.name}Buffer)
        <#else>
        <#if field?index gt 0>

        </#if>
    buffer:writeBool(self.${field.name} ~= nil)
    if self.${field.name} ~= nil then
        ${field.classType?replace('.','_')}.encode(self.${field.name}, buffer)
    end
        <#if field?has_next && !fields[field?index+1].collectionType && (fields[field?index+1].primitiveType || fields[field?index+1].enumType || !fields[field?index+1].optional) >

        </#if>
        </#if>
    <#else>
    <#if definedFieldId>
    local ${field.name}Buffer = _Buffer.new()
    ${field.classType?replace('.','_')}.encode(self.${field.name}, ${field.name}Buffer)
    buffer:writeBuffer(${field.name}Buffer)
    <#else>
    ${field.classType?replace('.','_')}.encode(self.${field.name}, buffer)
    </#if>
    </#if>
    <#if definedFieldId>

    </#if>
</#list>
<#if definedFieldId>
    buffer:writeTag(0);
</#if>

    return buffer
end

<#if kind ==3>
---
---<#if comment !="">[${comment}]<#else>${name}</#if>.解码
---@param buffer quan.message.Buffer 不能为空
---@return ${getFullName("lua")}
---
function ${name}.decode(buffer)
    assert(type(buffer) == "table" and buffer.class == _Buffer.class, "参数[buffer]类型错误")

    local self = ${name}.new()

    _Message.decode(buffer, self)
    <#if header??>
    _MessageHeader.decode(buffer, self)

    </#if>
<#else>
---
---<#if comment !="">[${comment}]<#else>${name}</#if>.解码
---@param buffer quan.message.Buffer 不能为空
---@param self ${getFullName("lua")} 可以为空
---@return ${getFullName("lua")}
---
function ${name}.decode(buffer, self)
    assert(type(buffer) == "table" and buffer.class == _Buffer.class, "参数[buffer]类型错误")
    <#if kind ==2>
    assert(self == nil or type(self) == "table" and self.class == ${name}.class, "参数[self]类型错误")
    </#if>

    self = self or ${name}.new()

    <#if header??>
    _MessageHeader.decode(buffer, self)

    </#if>
</#if>
<#if definedFieldId>
    while true do
        local tag = buffer:readTag()
        if tag == 0 then
            break
    <#list fields as field>
        <#if field.ignore><#continue/></#if>
        elseif tag == ${field.tag} then
        <#if field.type=="set" || field.type=="list">
            buffer:readInt()
            for i = 1, buffer:readInt() do
            <#if field.builtinValueType>
                self.${field.name}[i] = buffer:read${field.valueType?cap_first}()
            <#else>
                self.${field.name}[i] = ${field.classValueType?replace('.','_')}.decode(buffer)
            </#if>
            end
        <#elseif field.type=="map">
            buffer:readInt()
            for i = 1, buffer:readInt() do
            <#if field.builtinValueType>
                self.${field.name}[buffer:read${field.keyType?cap_first}()] = buffer:read${field.valueType?cap_first}()
            <#else>
                self.${field.name}[buffer:read${field.keyType?cap_first}()] = ${field.classValueType?replace('.','_')}.decode(buffer)
            </#if>
            end
        <#elseif field.type=="float"||field.type=="double">
            self.${field.name} = buffer:read${field.type?cap_first}(<#if field.scale gt 0>${field.scale}</#if>)
        <#elseif field.builtinType>
            self.${field.name} = buffer:read${field.type?cap_first}()
        <#elseif field.enumType>
            self.${field.name} = buffer:readInt()
        <#elseif field.optional>
            buffer:readInt()
            if buffer:readBool() then
                self.${field.name} = ${field.classType?replace('.','_')}.decode(buffer)
            end
        <#else>
            buffer:readInt()
            self.${field.name} = ${field.classType?replace('.','_')}.decode(buffer, self.${field.name})
        </#if>
    </#list>
    <#if fields?size==0>
    _Message.skipField(tag,buffer)
    <#else>
        else
            _Message.skipField(tag, buffer)
        end
    </#if>
    end
<#else>
<#list fields as field>
    <#if field.ignore>
        <#continue/>
    <#elseif field.type=="set" || field.type=="list">
        <#if field?index gt 0>

        </#if>
    for i = 1, buffer:readInt() do
        <#if field.builtinValueType>
        self.${field.name}[i] = buffer:read${field.valueType?cap_first}()
        <#else>
        self.${field.name}[i] = ${field.classValueType?replace('.','_')}.decode(buffer)
        </#if>
    end
        <#if field?has_next && !fields[field?index+1].collectionType && (fields[field?index+1].primitiveType || fields[field?index+1].enumType || !fields[field?index+1].optional) >

        </#if>
    <#elseif field.type=="map">
        <#if field?index gt 0>

        </#if>
    for i = 1, buffer:readInt() do
        <#if field.builtinValueType>
        self.${field.name}[buffer:read${field.keyType?cap_first}()] = buffer:read${field.valueType?cap_first}()
        <#else>
        self.${field.name}[buffer:read${field.keyType?cap_first}()] = ${field.classValueType?replace('.','_')}.decode(buffer)
        </#if>
    end
        <#if field?has_next && !fields[field?index+1].collectionType && (fields[field?index+1].primitiveType || fields[field?index+1].enumType || !fields[field?index+1].optional) >

        </#if>
    <#elseif field.type=="float"||field.type=="double">
    self.${field.name} = buffer:read${field.type?cap_first}(<#if field.scale gt 0>${field.scale}</#if>)
    <#elseif field.builtinType>
    self.${field.name} = buffer:read${field.type?cap_first}()
    <#elseif field.enumType>
    self.${field.name} = buffer:readInt()
    <#elseif field.optional>
        <#if field?index gt 0>

        </#if>
    if buffer:readBool() then
        self.${field.name} = ${field.classType?replace('.','_')}.decode(buffer)
    end
        <#if field?has_next && !fields[field?index+1].collectionType && (fields[field?index+1].primitiveType || fields[field?index+1].enumType || !fields[field?index+1].optional) >

        </#if>
    <#else>
    self.${field.name} = ${field.classType?replace('.','_')}.decode(buffer, self.${field.name})
    </#if>
</#list>
</#if>

    return self
end

${name} = table.readOnly(${name})
return ${name}