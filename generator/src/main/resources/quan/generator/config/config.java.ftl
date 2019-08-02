package ${packageName};

import java.util.*;
import com.alibaba.fastjson.*;
import quan.config.*;
<#list imports as import>
import ${import};
</#list>

/**
<#if comment !="">
* ${comment}<br/>
</#if>
* Created by 自动生成
*/
@SuppressWarnings({"unchecked"})
public class ${name} extends <#if definitionType ==2>Bean<#elseif definitionType ==6 && (!parent?? || parent=="")>Config<#elseif definitionType ==6>${parent}</#if> {
<#if !selfFields??>
    <#assign selfFields = fields>
</#if>

<#list selfFields as field>
    <#if field.comment !="">
    //${field.comment}
    </#if>
    <#if field.type=="list">
    protected ${field.basicType}<${field.classValueType}> ${field.name} = new ArrayList<>();
    <#elseif field.type=="set">
    protected ${field.basicType}<${field.classValueType}> ${field.name} = new HashSet<>();
    <#elseif field.type=="map">
    protected ${field.basicType}<${field.classKeyType}, ${field.classValueType}> ${field.name} = new HashMap<>();
    <#else >
    protected ${field.basicType} ${field.name};
    </#if>

</#list>
<#list selfFields as field>
    <#if field.comment !="">
    /**
     * ${field.comment}
     */
    </#if>
    <#if field.type=="list" || field.type=="set">
    public ${field.basicType}<${field.classValueType}> get${field.name?cap_first}() {
        return ${field.name};
    }
    <#elseif field.type=="map">
    public ${field.basicType}<${field.classKeyType}, ${field.classValueType}> get${field.name?cap_first}() {
        return ${field.name};
    }
    <#else >
    public ${field.basicType} get${field.name?cap_first}() {
        return ${field.name};
    }
    </#if>

</#list>

    @Override
    public void parse(JSONObject object) {
        super.parse(object);

<#list selfFields as field>
    <#if field.type=="string">
        ${field.name} = object.getString("${field.name}");
    <#elseif field.type=="list" || field.type=="set">
        <#if field_index gt 0 >

        </#if>
        JSONArray $${field.name} = object.getJSONArray("${field.name}");
        if ($${field.name} != null) {
            for (int i = 0; i < $${field.name}.size(); i++) {
                <#if field.valueBeanType>
                ${field.classValueType} $${field.name}$Value = new ${field.classValueType}();
                $${field.name}$Value.parse($${field.name}.getJSONObject(i));
                ${field.name}.add($${field.name}$Value);
                <#else>
                ${field.name}.add($${field.name}.get${field.classValueType}(i));
                </#if>
            }
        }
        ${field.name} = Collections.unmodifiable${field.basicType}(${field.name});
        <#if field_has_next && selfFields[field_index+1].primitiveType >

        </#if>
    <#elseif field.type=="map">
        <#if field_index gt 0 >

        </#if>
        JSONObject $${field.name} = object.getJSONObject("${field.name}");
        if ($${field.name} != null) {
            for (String $${field.name}$Key : $${field.name}.keySet()) {
                <#if field.valueBeanType>
                ${field.classValueType} $${field.name}$Value = new ${field.classValueType}();
                $${field.name}$Value.parse($${field.name}.getJSONObject($${field.name}$Key));
                ${field.name}.put(${field.classKeyType}.valueOf($${field.name}$Key), $${field.name}$Value);
                <#else>
                ${field.name}.put(${field.classKeyType}.valueOf($${field.name}$Key), $${field.name}.get${field.classValueType}($${field.name}$Key));
                </#if>
            }
        }
        ${field.name} = Collections.unmodifiableMap(${field.name});
        <#if field_has_next && selfFields[field_index+1].primitiveType >

        </#if>
    <#elseif field.type=="bool">
        ${field.name} = object.getBooleanValue("${field.name}");
     <#elseif field.builtInType>
        ${field.name} = object.get${field.type?cap_first}Value("${field.name}");
     <#elseif field.enumType>
       <#if field_index gt 0 >

        </#if>
        String $${field.name} = object.getString("${field.name}");
        if ($${field.name} != null) {
            ${field.name} = ${field.type}.valueOf($${field.name});
        }
         <#if field_has_next && selfFields[field_index+1].primitiveType >

        </#if>
    <#else>
        <#if field_index gt 0 >

        </#if>
        JSONObject $${field.name} = object.getJSONObject("${field.name}");
        if ($${field.name} != null) {
            ${field.name} = new ${field.type}();
            ${field.name}.parse($reward);
        }
        <#if field_has_next && selfFields[field_index+1].primitiveType >

        </#if>
    </#if>
</#list>
    }

    @Override
    public String toString() {
        return "${name}{" +
        <#list fields as field>
                "<#rt>
            <#if field_index gt 0>
                <#lt>,<#rt>
            </#if>
            <#if field.type == "string">
                <#lt>${field.name}='" + ${field.name} + '\'' +
            <#else>
                <#lt>${field.name}=" + ${field.name} +
            </#if>
        </#list>
                '}';

    }
<#macro indexer tab>
    <#list indexes as index>
        <#if index.comment !="">
    ${tab}//${index.comment}
        </#if>
        <#if index.unique && index.fields?size==1>
    ${tab}private static Map<${index.fields[0].classType}, ${name}> ${index.name}Configs = new HashMap<>();

        <#elseif index.normal && index.fields?size==1>
    ${tab}private static Map<${index.fields[0].classType}, List<${name}>> ${index.name}Configs = new HashMap<>();

        <#elseif index.unique && index.fields?size==2>
    ${tab}private static Map<${index.fields[0].classType}, Map<${index.fields[1].classType}, ${name}>> ${index.name}Configs = new HashMap<>();
    
        <#elseif index.normal && index.fields?size==2>
    ${tab}private static Map<${index.fields[0].classType}, Map<${index.fields[1].classType}, List<${name}>>> ${index.name}Configs = new HashMap<>();

        <#elseif index.unique && index.fields?size==3>
    ${tab}private static Map<${index.fields[0].classType}, Map<${index.fields[1].classType}, Map<${index.fields[2].classType}, ${name}>>> ${index.name}Configs = new HashMap<>();

        <#elseif index.normal && index.fields?size==3>
    ${tab}private static Map<${index.fields[0].classType}, Map<${index.fields[1].classType}, Map<${index.fields[2].classType}, List<${name}>>>> ${index.name}Configs = new HashMap<>();

        </#if>
    </#list>
    
    <#list indexes as index>
        <#if index.unique && index.fields?size==1>
    ${tab}public static Map<${index.fields[0].classType}, ${name}> get${index.name?cap_first}Configs() {
        ${tab}return ${index.name}Configs;
    ${tab}}

    ${tab}public static ${name} getBy${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name}) {
        ${tab}return ${index.name}Configs.get(${index.fields[0].name});
    ${tab}}

        <#elseif index.normal && index.fields?size==1>
    ${tab}public static Map<${index.fields[0].classType}, List<${name}>> get${index.name?cap_first}Configs() {
        ${tab}return ${index.name}Configs;
    ${tab}}

    ${tab}public static List<${name}> getBy${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name}) {
        ${tab}return ${index.name}Configs.getOrDefault(${index.fields[0].name}, Collections.emptyList());
    ${tab}}

        <#elseif index.unique && index.fields?size==2>
    ${tab}public static Map<${index.fields[0].classType}, Map<${index.fields[1].classType}, ${name}>> get${index.name?cap_first}Configs() {
        ${tab}return ${index.name}Configs;
    ${tab}}

    ${tab}public static Map<${index.fields[1].classType}, ${name}> getBy${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name}) {
        ${tab}return ${index.name}Configs.getOrDefault(${index.fields[0].name}, Collections.emptyMap());
    ${tab}}

    ${tab}public static ${name} getBy${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name}, ${index.fields[1].basicType} ${index.fields[1].name}) {
        ${tab}return getBy${index.name?cap_first}(${index.fields[0].name}).get(${index.fields[1].name});
    ${tab}}

        <#elseif index.normal && index.fields?size==2>
    ${tab}public static Map<${index.fields[0].classType}, Map<${index.fields[1].classType}, List<${name}>>> get${index.name?cap_first}Configs() {
        ${tab}return ${index.name}Configs;
    ${tab}}

    ${tab}public static Map<${index.fields[1].classType}, List<${name}>> getBy${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name}) {
        ${tab}return ${index.name}Configs.getOrDefault(${index.fields[0].name}, Collections.emptyMap());
    ${tab}}

    ${tab}public static List<${name}> getBy${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name}, ${index.fields[1].basicType} ${index.fields[1].name}) {
        ${tab}return getBy${index.name?cap_first}(${index.fields[0].name}).getOrDefault(${index.fields[1].name}, Collections.emptyList());
    ${tab}}

        <#elseif index.unique && index.fields?size==3>
    ${tab}public static Map<${index.fields[0].classType}, Map<${index.fields[1].classType}, Map<${index.fields[2].classType}, ${name}>>> get${index.name?cap_first}Configs() {
        ${tab}return ${index.name}Configs;
    ${tab}}

    ${tab}public static Map<${index.fields[1].classType}, Map<${index.fields[2].classType}, ${name}>> getBy${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name}) {
        ${tab}return ${index.name}Configs.getOrDefault(${index.fields[0].name}, Collections.emptyMap());
    ${tab}}

    ${tab}public static Map<${index.fields[2].classType}, ${name}> getBy${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name}, ${index.fields[1].basicType} ${index.fields[1].name}) {
        ${tab}return getBy${index.name?cap_first}(${index.fields[0].name}).getOrDefault(${index.fields[1].name}, Collections.emptyMap());
    ${tab}}

    ${tab}public static ${name} getBy${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name}, ${index.fields[1].basicType} ${index.fields[1].name}, ${index.fields[2].basicType} ${index.fields[2].name}) {
        ${tab}return getBy${index.name?cap_first}(${index.fields[0].name}, ${index.fields[1].name}).get(${index.fields[2].name});
    ${tab}}

        <#elseif index.normal && index.fields?size==3>
    ${tab}public static Map<${index.fields[0].classType}, Map<${index.fields[1].classType}, Map<${index.fields[2].classType}, List<${name}>>>> get${index.name?cap_first}Configs() {
        ${tab}return ${index.name}Configs;
    ${tab}}

    ${tab}public static Map<${index.fields[1].classType}, Map<${index.fields[2].classType}, List<${name}>>> getBy${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name}) {
        ${tab}return ${index.name}Configs.getOrDefault(${index.fields[0].name}, Collections.emptyMap());
    ${tab}}

    ${tab}public static Map<${index.fields[2].classType}, List<${name}>> getBy${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name}, ${index.fields[1].basicType} ${index.fields[1].name}) {
        ${tab}return getBy${index.name?cap_first}(${index.fields[0].name}).getOrDefault(${index.fields[1].name}, Collections.emptyMap());
    ${tab}}

    ${tab}public static List<${name}> getBy${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name}, ${index.fields[1].basicType} ${index.fields[1].name}, ${index.fields[2].basicType} ${index.fields[2].name}) {
        ${tab}return getBy${index.name?cap_first}(${index.fields[0].name}, ${index.fields[1].name}).getOrDefault(${index.fields[2].name}, Collections.emptyList());
    ${tab}}

        </#if>
    </#list>

    ${tab}public static List<String> index(List<${name}> configs) {
    <#list indexes as index>
        <#if index.unique && index.fields?size==1>
        ${tab}Map<${index.fields[0].classType}, ${name}> _${index.name}Configs = new HashMap<>();
        <#elseif index.normal && index.fields?size==1>
        ${tab}Map<${index.fields[0].classType}, List<${name}>> _${index.name}Configs = new HashMap<>();
        <#elseif index.unique && index.fields?size==2>
        ${tab}Map<${index.fields[0].classType}, Map<${index.fields[1].classType}, ${name}>> _${index.name}Configs = new HashMap<>();
        <#elseif index.normal && index.fields?size==2>
        ${tab}Map<${index.fields[0].classType}, Map<${index.fields[1].classType}, List<${name}>>> _${index.name}Configs = new HashMap<>();
        <#elseif index.unique && index.fields?size==3>
        ${tab}Map<${index.fields[0].classType}, Map<${index.fields[1].classType}, Map<${index.fields[2].classType}, ${name}>>> _${index.name}Configs = new HashMap<>();
        <#elseif index.normal && index.fields?size==3>
        ${tab}Map<${index.fields[0].classType}, Map<${index.fields[1].classType}, Map<${index.fields[2].classType}, List<${name}>>>> _${index.name}Configs = new HashMap<>();
        </#if>
    </#list>

        ${tab}List<String> errors = new ArrayList<>();
        ${tab}${name} oldConfig;

        ${tab}for (${name} config : configs) {
    <#list indexes as index>
        <#if index.unique && index.fields?size==1>
            ${tab}oldConfig = _${index.name}Configs.put(config.${index.fields[0].name}, config);
            ${tab}if (oldConfig != null) {
                ${tab}String repeatedConfigs = config.getClass().getSimpleName();
                ${tab}if (oldConfig.getClass() != config.getClass()) {
                    ${tab}repeatedConfigs += "," + oldConfig.getClass().getSimpleName();
                ${tab}}
                ${tab}errors.add("配置[" + repeatedConfigs + "]有重复[${index.fields[0].name}]:[" + config.${index.fields[0].name} + "]");
            ${tab}}
        <#elseif index.normal && index.fields?size==1>
            ${tab}_${index.name}Configs.computeIfAbsent(config.${index.fields[0].name}, k -> new ArrayList<>()).add(config);
        <#elseif index.unique && index.fields?size==2>
            ${tab}oldConfig = _${index.name}Configs.computeIfAbsent(config.${index.fields[0].name}, k -> new HashMap<>()).put(config.${index.fields[1].name}, config);
            ${tab}if (oldConfig != null) {
                ${tab}String repeatedConfigs = config.getClass().getSimpleName();
                ${tab}if (oldConfig.getClass() != config.getClass()) {
                    ${tab}repeatedConfigs += "," + oldConfig.getClass().getSimpleName();
                ${tab}}
                ${tab}errors.add("配置[" + repeatedConfigs + "]有重复[${index.fields[0].name},${index.fields[1].name}]:[" + config.${index.fields[0].name} + "," + config.${index.fields[1].name} + "]");
            ${tab}}
        <#elseif index.normal && index.fields?size==2>
            ${tab}_${index.name}Configs.computeIfAbsent(config.${index.fields[0].name}, k -> new HashMap<>()).computeIfAbsent(config.${index.fields[1].name}, k -> new ArrayList<>()).add(config);
        <#elseif index.unique && index.fields?size==3>
            ${tab}oldConfig = _${index.name}Configs.computeIfAbsent(config.${index.fields[0].name}, k -> new HashMap<>()).computeIfAbsent(config.${index.fields[1].name}, k -> new HashMap<>()).put(config.${index.fields[2].name}, config);
            ${tab}if (oldConfig != null) {
                ${tab}String repeatedConfigs = config.getClass().getSimpleName();
                ${tab}if (oldConfig.getClass() != config.getClass()) {
                    ${tab}repeatedConfigs += "," + oldConfig.getClass().getSimpleName();
                ${tab}}
                ${tab}errors.add("配置[" + repeatedConfigs + "]有重复[${index.fields[0].name},${index.fields[1].name},${index.fields[2].name}]:[" + config.${index.fields[0].name} + "," + config.${index.fields[1].name} + "," + config.${index.fields[2].name} + "]");
            }
        <#elseif index.normal && index.fields?size==3>
            ${tab}_${index.name}Configs.computeIfAbsent(config.${index.fields[0].name}, k -> new HashMap<>()).computeIfAbsent(config.${index.fields[1].name}, k -> new HashMap<>()).computeIfAbsent(config.${index.fields[2].name}, k -> new ArrayList<>()).add(config);
        </#if>
        <#if index_index<indexes?size-1>

        </#if>
    </#list>
        ${tab}}

    <#list indexes as index>
        ${tab}${index.name}Configs = unmodifiable(_${index.name}Configs);
    </#list>

        ${tab}return errors;
    ${tab}}
    </#macro>

 <#if definitionType ==6>
    @Override
    public ${name} create() {
        return new ${name}();
    }

    <#if parent??>
    public static class self {
        
        private self() {
        }

            <@indexer tab="    "/>

    }
    <#else>
<@indexer tab=""/>
    </#if>

</#if>
}
