---
<#if comment !="">
---${comment}
</#if>
---代码自动生成，请勿手动修改
---

local _CodedBuffer = require("quan.message.CodedBuffer")
local _Message = require("quan.message.Message")
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

local function onSet(self, key, value)
    assert(not ${name}[key], "不允许修改只读属性:" .. key)

<#if fields?size gt 0 >
    local propTypeError = string.format("属性%s类型%s错误", key, type(value))
</#if>

<#list fields as field>
    if key == "${field.name}" then
    <#if field.numberType || field.enumType>
        assert(type(value) == "number", propTypeError)
    <#elseif field.type == "bool">
        assert(type(value) == "boolean", propTypeError)
    <#elseif field.type == "string" || field.type == "bytes">
        assert(<#if field.optional>value == nil or </#if>type(value) == "string", propTypeError)
    <#elseif field.collectionType || field.beanType && !field.optional>
        assert(type(value) == "table", propTypeError)
    <#else>
        assert(<#if field.optional>value == nil or </#if>type(value) == "table" and value.class == ${field.classType?replace('.','_')}.class, propTypeError)
    </#if>
    <#if field.min?? && field.max??>
        _Message.validateRange(value, ${field.min}, ${field.max});
    <#elseif field.min??>
        _Message.validateMin(value, ${field.min});
    <#elseif field.max??>
        _Message.validateMax(value, ${field.max});
    </#if>
    end

</#list>
    rawset(self, key, value)
end

local function toString(self)
    return "${name}{" ..
    <#if kind ==3>
            "_id=" .. tostring(self.id) ..
    </#if>
    <#list fields as field>
            "<#rt>
        <#if field?index gt 0 || kind ==3>
             <#lt>,<#rt>
        </#if>
        <#if field.type == "string">
             <#lt>${field.name}='" .. tostring(self.${field.name}) .. '\'' ..
        <#elseif field.collectionType>
             <#lt>${field.name}=" .. table.toString(self.${field.name}) ..
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
        ${field.name} = args.${field.name}<#if !field.optional> or ""</#if>,
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

setmetatable(${name}, { __call = ${name}.new })

<#if kind == 3>
---
---<#if comment !="">[${comment}]<#else>${name}</#if>.编码
---@return quan.message.CodedBuffer
---
function ${name}:encode()
    assert(type(self) == "table" and self.class == ${name}.class, "参数[self]类型错误")

    local buffer = _Message.encode(self)
<#else>
---
---<#if comment !="">[${comment}]<#else>${name}</#if>.编码
---@param buffer quan.message.CodedBuffer 可以为空
---@return quan.message.CodedBuffer
---
function ${name}:encode(buffer)
    <#if kind ==2>
    assert(type(self) == "table" and self.class == ${name}.class, "参数[self]类型错误")
    </#if>
    assert(buffer == nil or type(buffer) == "table" and buffer.class == _CodedBuffer.class, "参数[buffer]类型错误")
    self:validate()

    buffer = buffer or _CodedBuffer.new()

</#if>
<#list fields as field>
    <#if field.ignore>
        <#continue/>
    </#if>
    <#if compatible>
        <#if field.type=="set" || field.type=="list">
    if #self.${field.name} > 0 then
        _Message.writeTag(buffer, ${field.tag})
        local ${field.name}Buffer = _CodedBuffer.new()
        ${field.name}Buffer:writeInt(#self.${field.name})
        for i, value in ipairs(self.${field.name}) do
        <#if field.builtinValueType>
            ${field.name}Buffer:write${field.valueType?cap_first}(value)
        <#else>
            ${field.valueClassType?replace('.','_')}.encode(value, ${field.name}Buffer)
        </#if>
        end
        buffer:writeBuffer(${field.name}Buffer)
    end
        <#elseif field.type=="map">
    local ${field.name}Size = table.size(self.${field.name})
    if ${field.name}Size > 0 then
        _Message.writeTag(buffer, ${field.tag})
        local ${field.name}Buffer = _CodedBuffer.new()
        ${field.name}Buffer:writeInt(${field.name}Size)
        for key, value in pairs(self.${field.name}) do
            ${field.name}Buffer:write${field.keyType?cap_first}(key)
        <#if field.builtinValueType>
            ${field.name}Buffer:write${field.valueType?cap_first}(value)
        <#else>
            ${field.valueClassType?replace('.','_')}.encode(value, ${field.name}Buffer)
        </#if>
        end
        buffer:writeBuffer(${field.name}Buffer)
    end
        <#elseif field.type=="float"||field.type=="double">
    if self.${field.name} ~= 0 then
        _Message.writeTag(buffer, ${field.tag})
        buffer:write${field.type?cap_first}(self.${field.name}<#if field.scale gt 0>, ${field.scale}</#if>)
    end
        <#elseif field.numberType>
    if self.${field.name} ~= 0 then
        _Message.writeTag(buffer, ${field.tag})
        buffer:write${field.type?cap_first}(self.${field.name})
    end
        <#elseif field.type=="bool">
    if self.${field.name} then
        _Message.writeTag(buffer, ${field.tag})
        buffer:write${field.type?cap_first}(self.${field.name})
    end
        <#elseif field.type=="string" || field.type=="bytes">
        <#if field.optional>
    if self.${field.name} ~= nil then
        <#else>
    if #self.${field.name} > 0 then
        </#if>
        _Message.writeTag(buffer, ${field.tag})
        buffer:write${field.type?cap_first}(self.${field.name})
    end
        <#elseif field.enumType>
    if self.${field.name} ~= nil and self.${field.name} ~= 0 then
        _Message.writeTag(buffer, ${field.tag})
        buffer:writeInt(self.${field.name})
    end
        <#elseif field.optional>
    if self.${field.name} ~= nil then
        _Message.writeTag(buffer, ${field.tag})
        local ${field.name}Buffer = _CodedBuffer.new()
        ${field.classType?replace('.','_')}.encode(self.${field.name}, ${field.name}Buffer)
        buffer:writeBuffer(${field.name}Buffer)
    end
    <#else>
    _Message.writeTag(buffer, ${field.tag})
    local ${field.name}Buffer = _CodedBuffer.new()
    ${field.classType?replace('.','_')}.encode(self.${field.name}, ${field.name}Buffer)
    buffer:writeBuffer(${field.name}Buffer)
    </#if>

    <#else>
        <#if field.type=="set" || field.type=="list">
        <#if field?index gt 0>

        </#if>
    buffer:writeInt(#self.${field.name})
    for i, value in ipairs(self.${field.name}) do
        <#if field.builtinValueType>
        buffer:write${field.valueClassType?replace('.','_')?cap_first}(value)
        <#else>
        ${field.valueType}.encode(value, buffer)
        </#if>
    end
        <#if field?has_next && !fields[field?index+1].collectionType && (fields[field?index+1].primitiveType || fields[field?index+1].enumType || !fields[field?index+1].optional) >

        </#if>
        <#elseif field.type=="map">
        <#if field?index gt 0>

        </#if>
    buffer:writeInt(table.size(self.${field.name}))
    for key, value in pairs(self.${field.name}) do
        buffer:write${field.keyType?cap_first}(key)
        <#if field.builtinValueType>
        buffer:write${field.valueType?cap_first}(value)
        <#else>
        ${field.valueClassType?replace('.','_')}.encode(value, buffer)
        </#if>
    end
        <#if field?has_next && !fields[field?index+1].collectionType && (fields[field?index+1].primitiveType || fields[field?index+1].enumType || !fields[field?index+1].optional) >

        </#if>
        <#elseif field.type=="float"||field.type=="double">
    buffer:write${field.type?cap_first}(self.${field.name}<#if field.scale gt 0>, ${field.scale}</#if>)
        <#elseif (field.type=="string" || field.type=="bytes") && field.optional>
        <#if field?index gt 0>

        </#if>
    buffer:writeBool(self.${field.name} ~= nil)
    if self.${field.name} ~= nil then
        buffer:write${field.type?cap_first}(self.${field.name}) 
    end
        <#if field?has_next && !fields[field?index+1].collectionType && (fields[field?index+1].primitiveType || fields[field?index+1].enumType || !fields[field?index+1].optional) >

        </#if>
        <#elseif field.builtinType>
    buffer:write${field.type?cap_first}(self.${field.name})
        <#elseif field.enumType>
    buffer:writeInt(self.${field.name} or 0)
        <#elseif field.optional>
        <#if field?index gt 0>

        </#if>
    buffer:writeBool(self.${field.name} ~= nil)
    if self.${field.name} ~= nil then
        ${field.classType?replace('.','_')}.encode(self.${field.name}, buffer)
    end
        <#if field?has_next && !fields[field?index+1].collectionType && (fields[field?index+1].primitiveType || fields[field?index+1].enumType || !fields[field?index+1].optional) >

        </#if>
        <#else>
    ${field.classType?replace('.','_')}.encode(self.${field.name}, buffer)
        </#if>
    </#if>
</#list>
<#if compatible>
    _Message.writeTag(buffer, 0);
</#if>

    return buffer
end

<#if kind ==3>
---
---<#if comment !="">[${comment}]<#else>${name}</#if>.解码
---@param buffer quan.message.CodedBuffer 不能为空
---@return ${getFullName("lua")}
---
function ${name}.decode(buffer)
    assert(type(buffer) == "table" and buffer.class == _CodedBuffer.class, "参数[buffer]类型错误")

    local self = ${name}.new()

    _Message.decode(buffer, self)
<#else>
---
---<#if comment !="">[${comment}]<#else>${name}</#if>.解码
---@param buffer quan.message.CodedBuffer 不能为空
---@param self ${getFullName("lua")} 可以为空
---@return ${getFullName("lua")}
---
function ${name}.decode(buffer, self)
    assert(type(buffer) == "table" and buffer.class == _CodedBuffer.class, "参数[buffer]类型错误")
    <#if kind ==2>
    assert(self == nil or type(self) == "table" and self.class == ${name}.class, "参数[self]类型错误")
    </#if>

    self = self or ${name}.new()

</#if>
<#if compatible>
    while true do
        local tag = _Message.readTag(buffer)
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
                self.${field.name}[i] = ${field.valueClassType?replace('.','_')}.decode(buffer)
            </#if>
            end
        <#elseif field.type=="map">
            buffer:readInt()
            for i = 1, buffer:readInt() do
            <#if field.builtinValueType>
                self.${field.name}[buffer:read${field.keyType?cap_first}()] = buffer:read${field.valueType?cap_first}()
            <#else>
                self.${field.name}[buffer:read${field.keyType?cap_first}()] = ${field.valueClassType?replace('.','_')}.decode(buffer)
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
            self.${field.name} = ${field.classType?replace('.','_')}.decode(buffer)
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
        self.${field.name}[i] = ${field.valueClassType?replace('.','_')}.decode(buffer)
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
        self.${field.name}[buffer:read${field.keyType?cap_first}()] = ${field.valueClassType?replace('.','_')}.decode(buffer)
        </#if>
    end
        <#if field?has_next && !fields[field?index+1].collectionType && (fields[field?index+1].primitiveType || fields[field?index+1].enumType || !fields[field?index+1].optional) >

        </#if>
    <#elseif field.type=="float"||field.type=="double">
    self.${field.name} = buffer:read${field.type?cap_first}(<#if field.scale gt 0>${field.scale}</#if>)
    <#elseif (field.type=="string" || field.type=="bytes") && field.optional>
        <#if field?index gt 0>

        </#if>
    if buffer:readBool() then
        self.${field.name} = buffer:read${field.type?cap_first}()
    end
        <#if field?has_next && !fields[field?index+1].collectionType && (fields[field?index+1].primitiveType || fields[field?index+1].enumType || !fields[field?index+1].optional) >

        </#if>    
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

    self:validate()

    return self
end

function ${name}:validate()
<#list fields as field>
    <#assign fieldTypeError = "属性[${field.name}]类型错误"/>
    <#if field.numberType || field.enumType>
    assert(type(self.${field.name}) == "number", "${fieldTypeError}")
    <#elseif field.type == "bool">
    assert(type(self.${field.name}) == "boolean", "${fieldTypeError}")
    <#elseif field.type == "string" || field.type == "bytes">
    assert(<#if field.optional>self.${field.name} == nil or </#if>type(self.${field.name}) == "string",  "${fieldTypeError}")
    <#elseif field.collectionType || field.beanType && !field.optional>
    assert(type(self.${field.name}) == "table",  "${fieldTypeError}")
    <#else>
    assert(<#if field.optional>self.${field.name} == nil or </#if>type(self.${field.name}) == "table" and self.${field.name}.class == ${field.classType?replace('.','_')}.class, "${fieldTypeError}")
    </#if>
    <#if field.min?? && field.max??>
    _Message.validateRange(self.${field.name}, ${field.min}, ${field.max}, "属性[${field.name}]");
    <#elseif field.min??>
    _Message.validateMin(self.${field.name}, ${field.min}, "属性[${field.name}]");
    <#elseif field.max??>
    _Message.validateMax(self.${field.name}, ${field.max}, "属性[${field.name}]");
    </#if>
</#list>
end

${name} = table.readOnly(${name})
return ${name}