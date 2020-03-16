local Config = require("quan.config.Config")

---所有${name}
local configs = {
<#list rows as row>
    ${row},    
</#list>
}

<#list indexes as index>
    <#if index.comment !="">
---${index.comment}
    </#if>
local ${index.name}Configs = {}

</#list>
---加载配置，建立索引
local function loadConfigs()
<#list children as child>
    local ${child.name} = require("${child.getFullName("lua")}")
    for i, ${child.name?uncap_first} in ipairs(${child.name}.getConfigs()) do
        table.insert(configs, ${child.name?uncap_first})
    end

</#list>
    for i, config in ipairs(configs) do
<#list indexes as index>
    <#if index.fields?size==1>
        Config.load(${index.name}Configs, config, ${index.unique?c}, { "${index.fields[0].name}" }, { config.${index.fields[0].name} })
    <#elseif index.fields?size==2>
        Config.load(${index.name}Configs, config, ${index.unique?c}, { "${index.fields[0].name}", "${index.fields[1].name}" }, { config.${index.fields[0].name}, config.${index.fields[1].name} })
    <#elseif index.fields?size==3>
        Config.load(${index.name}Configs, config, ${index.unique?c}, { "${index.fields[0].name}", "${index.fields[1].name}", "${index.fields[2].name}" }, { config.${index.fields[0].name}, config.${index.fields[1].name}, config.${index.fields[2].name} })
    </#if>
</#list>
    end
end

loadConfigs()

<#if comment !="">
---${comment}
</#if>
local ${name} = {}

---
---获取所有${name}
---@return list<${name}>
function ${name}.getConfigs()
    return configs
end
 
<#list indexes as index>
   <#if index.unique && index.fields?size==1>
---
---通过索引[${index.name}]获取${name}
---@param ${index.fields[0].name} ${index.fields[0].type} ${index.fields[0].comment}
---@return map<${index.fields[0].name} ${index.fields[0].type},${name}> | ${name}
function ${name}.getBy${index.name?cap_first}(${index.fields[0].name})
    if (not ${index.fields[0].name}) then
        return ${index.name}Configs
    end
    return ${index.name}Configs[${index.fields[0].name}]
end

   <#elseif index.normal && index.fields?size==1>
---
---通过索引[${index.name}]获取${name}
---@param ${index.fields[0].name} ${index.fields[0].type} ${index.fields[0].comment}
---@return map<${index.fields[0].name} ${index.fields[0].type},list<${name}>> | list<${name}>
function ${name}.getBy${index.name?cap_first}(${index.fields[0].name})
    return ${index.name}Configs[${index.fields[0].name}] or {}
end

   <#elseif index.unique && index.fields?size==2>
---
---通过索引[${index.name}]获取${name}
---@param ${index.fields[0].name} ${index.fields[0].type} ${index.fields[0].comment}
---@param ${index.fields[1].name} ${index.fields[1].type} ${index.fields[1].comment}
---@return map<${index.fields[0].name} ${index.fields[0].type},map<${index.fields[1].name} ${index.fields[1].type},${name}>> | map<${index.fields[1].name} ${index.fields[1].type},${name}> | ${name}
function ${name}.getBy${index.name?cap_first}(${index.fields[0].name}, ${index.fields[1].name})
    if (not ${index.fields[0].name}) then
        return ${index.name}Configs
    end

    local map = ${index.name}Configs[${index.fields[0].name}] or {}
    if (not ${index.fields[1].name}) then
        return map
    end
    
    return map[${index.fields[1].name}]
end

   <#elseif index.normal && index.fields?size==2>
---
---通过索引[${index.name}]获取${name}
---@param ${index.fields[0].name} ${index.fields[0].type} ${index.fields[0].comment}
---@param ${index.fields[1].name} ${index.fields[1].type} ${index.fields[1].comment}
---@return map<${index.fields[0].name} ${index.fields[0].type},map<${index.fields[1].name} ${index.fields[1].type},list<${name}>>> | map<${index.fields[1].name} ${index.fields[1].type},list<${name}>> | list<${name}>
function ${name}.getBy${index.name?cap_first}(${index.fields[0].name}, ${index.fields[1].name})
    if (not ${index.fields[0].name}) then
        return ${index.name}Configs
    end

    local map = ${index.name}Configs[${index.fields[0].name}] or {}
    if (not ${index.fields[1].name}) then
        return map
    end
    return map[${index.fields[1].name}]
end

   <#elseif index.unique && index.fields?size==3>
---
---通过索引[${index.name}]获取${name}
---@param ${index.fields[0].name} ${index.fields[0].type} ${index.fields[0].comment}
---@param ${index.fields[1].name} ${index.fields[1].type} ${index.fields[1].comment}
---@param ${index.fields[2].name} ${index.fields[2].type} ${index.fields[2].comment}
---@return  map<${index.fields[0].name} ${index.fields[0].type},map<${index.fields[1].name} ${index.fields[1].type},map<${index.fields[2].name} ${index.fields[2].type},${name}>>> | map<${index.fields[1].name} ${index.fields[1].type},map<${index.fields[2].name} ${index.fields[2].type},${name}>> | map<${index.fields[2].name} ${index.fields[2].type},${name}> | ${name}
function ${name}.getBy${index.name?cap_first}(${index.fields[0].name}, ${index.fields[1].name}, ${index.fields[2].name})
    if (not ${index.fields[0].name}) then
        return ${index.name}Configs
    end

    local map1 = ${index.name}Configs[${index.fields[0].name}] or {}
    if (not ${index.fields[1].name}) then
        return map1
    end

    local map2 = map1[${index.fields[1].name}] or {}
    if (not ${index.fields[2].name}) then
        return map2
    end

    return map2[${index.fields[2].name}]
end

   <#elseif index.normal && index.fields?size==3>
---
---通过索引[${index.name}]获取${name}
---@param ${index.fields[0].name} ${index.fields[0].type} ${index.fields[0].comment}
---@param ${index.fields[1].name} ${index.fields[1].type} ${index.fields[1].comment}
---@param ${index.fields[2].name} ${index.fields[2].type} ${index.fields[2].comment}
---@return map<${index.fields[0].name} ${index.fields[0].type},map<${index.fields[1].name} ${index.fields[1].type},map<${index.fields[2].name} ${index.fields[2].type},list<${name}>>>> | map<${index.fields[1].name} ${index.fields[1].type},map<${index.fields[2].name} ${index.fields[2].type},list<${name}>>> | map<${index.fields[2].name} ${index.fields[2].type},list<${name}>> | list<${name}>
function ${name}.getBy${index.name?cap_first}(${index.fields[0].name}, ${index.fields[1].name}, ${index.fields[2].name})
    if (not ${index.fields[0].name}) then
        return ${index.name}Configs
    end

    local map1 = ${index.name}Configs[${index.fields[0].name}] or {}
    if (not ${index.fields[1].name}) then
        return map1
    end

    local map2 = map1[${index.fields[1].name}] or {}
    if (not ${index.fields[2].name}) then
        return map2
    end

    return map2[${index.fields[2].name}]
end

   </#if>
</#list>
return ${name}