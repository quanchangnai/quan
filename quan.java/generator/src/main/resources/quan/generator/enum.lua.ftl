---
<#if comment !="">
---${comment}<br/>
</#if>
---自动生成
---
local ${name} = {
<#list fields as field>
    <#if field.comment !="">--- ${field.comment}</#if>
    <#if field_has_next>${field.name} = ${field.value},<#else>${field.name} = ${field.value}</#if>
</#list>
}

return ${name}
