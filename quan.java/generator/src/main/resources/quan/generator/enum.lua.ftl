---
<#if comment !="">
---${comment}
</#if>
---自动生成
---
local ${name} = {
<#list fields as field>
    <#if field.comment !="">--- ${field.comment}</#if>
    <#if field_has_next>${field.name} = ${field.value},<#else>${field.name} = ${field.value}</#if>
</#list>
}

local meta = {
    __index = ${name},
    __newindex = function()
        error("枚举不能修改")
    end
}

return setmetatable({}, meta)
