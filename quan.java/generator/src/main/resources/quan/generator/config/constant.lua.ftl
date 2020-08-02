local ${configDefinition.name} = require("${configDefinition.getFullName("lua")}")

---
<#if comment !="">
---${comment}
</#if>
---@author 代码自动生成，请勿手动修改
local ${name} = {}

<#list rows?keys as key>
---
    <#if rows[key].right !="">
---${rows[key].right}
    </#if>
    <#if valueField.type=="map">
---@return ${valueField.basicType}<${valueField.keyType},${valueField.classValueType}>
    <#elseif valueField.type=="list" || valueField.type=="set">
---@return ${valueField.basicType}<${valueField.classValueType}>
    <#else>
---@return ${valueField.basicType}
    </#if>
function ${name}.${key}()
    return ${configDefinition.name}.getBy${keyField.name?cap_first}("${key}").${valueField.name}
end

</#list>
return ${name}