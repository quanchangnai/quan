local ${configDefinition.name} = require("${configDefinition.getFullName("lua")}")

---
<#if comment !="">
---${comment}
</#if>
---@author 自动生成
local ${name} = {}

<#list rows?keys as key>
---
    <#if rows[key].right !="">
---${rows[key].right}
    </#if>
    <#if valueField.type=="map">
---@return ${valueField.basicType}<${valueField.keyType},${valueField.keyType}>
    <#elseif valueField.type=="list" || valueField.type=="set">
---@return ${valueField.basicType}<${valueField.valueType}>
    <#else>
---@return ${valueField.basicType}
    </#if>
function ${name}.${key}()
    return ${configDefinition.name}.getBy${keyField.name?cap_first}("${key}").${valueField.name}
end

</#list>
return ${name}