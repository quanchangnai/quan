require("quan.table")

---
<#if comment !="">
---${comment}
</#if>
---代码自动生成，请勿手动修改
---
local ${name} = {
<#list fields as field>
    <#if field.comment !="">--- ${field.comment}</#if>
    <#if field_has_next>${field.name} = ${field.enumValue},<#else>${field.name} = ${field.enumValue}</#if>
</#list>
}

${name} = table.readOnly(${name})
return ${name}