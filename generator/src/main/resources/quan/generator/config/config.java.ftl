package ${fullPackageName};

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
public class ${name} extends <#if definitionType ==2>Bean<#elseif definitionType ==6 && (!parent?? || parent=="")>Config<#elseif definitionType ==6>${parent}</#if> {
<#if !selfFields??>
    <#assign selfFields = fields>
</#if>

<#list selfFields as field>
    <#if field.comment !="">
    //${field.comment}
    </#if>
    <#if field.type=="list">
    protected final ${field.basicType}<${field.classValueType}> ${field.name};
    <#elseif field.type=="set">
    protected final ${field.basicType}<${field.classValueType}> ${field.name};
    <#elseif field.type=="map">
    protected final ${field.basicType}<${field.classKeyType}, ${field.classValueType}> ${field.name};
    <#elseif  field.timeType>
    protected final ${field.basicType} ${field.name};

    <#if field.comment !="">
    //${field.comment}
    </#if>
    protected final String ${field.name}$Str;
    <#else >
    protected final ${field.basicType} ${field.name};
    </#if>

</#list>

    public ${name}(JSONObject json) {
        super(json);

<#list selfFields as field>
    <#if field.type=="string">
        this.${field.name} = json.getOrDefault("${field.name}", "").toString();
    <#elseif field.type=="bool">
        this.${field.name} = json.getBooleanValue("${field.name}");
    <#elseif field.timeType>
        this.${field.name} = json.getDate("${field.name}");
        this.${field.name}$Str = json.getOrDefault("${field.name}$Str", "").toString();
    <#elseif field.type=="list" || field.type=="set">
        <#if field_index gt 0 >

        </#if>
        JSONArray $${field.name}$1 = json.getJSONArray("${field.name}");
        ${field.basicType}<${field.classValueType}> $${field.name}$2 = new ${field.classType}<>();
        if ($${field.name}$1 != null) {
            for (int i = 0; i < $${field.name}$1.size(); i++) {
                <#if field.beanValueType>
                ${field.classValueType} $${field.name}$Value = new ${field.classValueType}($${field.name}$1.getJSONObject(i));
                $${field.name}$2.add($${field.name}$Value);
                <#else>
                $${field.name}$2.add($${field.name}$1.get${field.classValueType}(i));
                </#if>
            }
        }
        this.${field.name} = Collections.unmodifiable${field.basicType}($${field.name}$2);
        <#if field_has_next && (selfFields[field_index+1].primitiveType ||selfFields[field_index+1].timeType) >

        </#if>
    <#elseif field.type=="map">
        <#if field_index gt 0 >

        </#if>
        JSONObject $${field.name}$1 = json.getJSONObject("${field.name}");
        Map<${field.classKeyType}, ${field.classValueType}> $${field.name}$2 = new HashMap<>();
        if ($${field.name}$1 != null) {
            for (String $${field.name}$Key : $${field.name}$1.keySet()) {
                <#if field.beanValueType>
                ${field.classValueType} $${field.name}$Value = new ${field.classValueType}($${field.name}$1.getJSONObject($${field.name}$Key));
                $${field.name}$2.put(${field.classKeyType}.valueOf($${field.name}$Key), $${field.name}$Value);
                <#else>
                $${field.name}$2.put(${field.classKeyType}.valueOf($${field.name}$Key), $${field.name}$1.get${field.classValueType}($${field.name}$Key));
                </#if>
            }
        }
        this.${field.name} = Collections.unmodifiableMap($${field.name}$2);
        <#if field_has_next && (selfFields[field_index+1].primitiveType ||selfFields[field_index+1].timeType) >

        </#if>
    <#elseif field.builtInType>
        this.${field.name} = json.get${field.type?cap_first}Value("${field.name}");
    <#elseif field.enumType>
       <#if field_index gt 0 >

        </#if>
        String $${field.name} = json.getString("${field.name}");
        if ($${field.name} != null) {
            this.${field.name} = ${field.type}.valueOf($${field.name});
        } else {
            this.${field.name} = null;
        }
         <#if field_has_next && (selfFields[field_index+1].primitiveType ||selfFields[field_index+1].timeType) >

        </#if>
    <#else>
        <#if field_index gt 0 >

        </#if>
        JSONObject $${field.name} = json.getJSONObject("${field.name}");
        if ($${field.name} != null) {
            this.${field.name} = new ${field.type}($reward);
        } else {
            this.${field.name} = null;
        }
        <#if field_has_next && selfFields[field_index+1].primitiveType >

        </#if>
    </#if>
</#list>
    }

<#list selfFields as field>
    <#if field.comment !="">
    /**
     * ${field.comment}
     */
    </#if>
    <#if field.type=="list" || field.type=="set">
    public final ${field.basicType}<${field.classValueType}> get${field.name?cap_first}() {
        return ${field.name};
    }
    <#elseif field.type=="map">
    public final ${field.basicType}<${field.classKeyType}, ${field.classValueType}> get${field.name?cap_first}() {
        return ${field.name};
    }
    <#elseif field.timeType>
    public final ${field.basicType} get${field.name?cap_first}() {
        return ${field.name};
    }

    <#if field.comment !="">
    /**
     * ${field.comment}
     */
    </#if>
    public final String get${field.name?cap_first}$Str() {
        return ${field.name}$Str;
    }
    <#else >
    public final ${field.basicType} get${field.name?cap_first}() {
        return ${field.name};
    }
    </#if>

</#list>

 <#if definitionType ==6>
    @Override
    protected ${name} create(JSONObject json) {
        return new ${name}(json);
    }
</#if>

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
            <#elseif field.timeType>
                <#lt>${field.name}='" + ${field.name}$Str + '\'' +
            <#else>
                <#lt>${field.name}=" + ${field.name} +
            </#if>
        </#list>
                '}';

    }

<#macro indexer tab>
    ${tab}private volatile static List<${name}> configs = new ArrayList<>();

    <#list indexes as index>
        <#if index.comment !="">
    ${tab}//${index.comment}
        </#if>
        <#if index.unique && index.fields?size==1>
    ${tab}private volatile static Map<${index.fields[0].classType}, ${name}> ${index.name}Configs = new HashMap<>();

        <#elseif index.normal && index.fields?size==1>
    ${tab}private volatile static Map<${index.fields[0].classType}, List<${name}>> ${index.name}Configs = new HashMap<>();

        <#elseif index.unique && index.fields?size==2>
    ${tab}private volatile static Map<${index.fields[0].classType}, Map<${index.fields[1].classType}, ${name}>> ${index.name}Configs = new HashMap<>();

        <#elseif index.normal && index.fields?size==2>
    ${tab}private volatile static Map<${index.fields[0].classType}, Map<${index.fields[1].classType}, List<${name}>>> ${index.name}Configs = new HashMap<>();

        <#elseif index.unique && index.fields?size==3>
    ${tab}private volatile static Map<${index.fields[0].classType}, Map<${index.fields[1].classType}, Map<${index.fields[2].classType}, ${name}>>> ${index.name}Configs = new HashMap<>();

        <#elseif index.normal && index.fields?size==3>
    ${tab}private volatile static Map<${index.fields[0].classType}, Map<${index.fields[1].classType}, Map<${index.fields[2].classType}, List<${name}>>>> ${index.name}Configs = new HashMap<>();

        </#if>
    </#list>
    ${tab}public static List<${name}> getConfigs() {
        ${tab}return configs;
    ${tab}}

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

    ${tab}@SuppressWarnings({"unchecked"})
    ${tab}public static List<String> index(List<${name}> configs) {
    <#list indexes as index>
        <#if index.unique && index.fields?size==1>
        ${tab}Map<${index.fields[0].classType}, ${name}> ${index.name}Configs = new HashMap<>();
        <#elseif index.normal && index.fields?size==1>
        ${tab}Map<${index.fields[0].classType}, List<${name}>> ${index.name}Configs = new HashMap<>();
        <#elseif index.unique && index.fields?size==2>
        ${tab}Map<${index.fields[0].classType}, Map<${index.fields[1].classType}, ${name}>> ${index.name}Configs = new HashMap<>();
        <#elseif index.normal && index.fields?size==2>
        ${tab}Map<${index.fields[0].classType}, Map<${index.fields[1].classType}, List<${name}>>> ${index.name}Configs = new HashMap<>();
        <#elseif index.unique && index.fields?size==3>
        ${tab}Map<${index.fields[0].classType}, Map<${index.fields[1].classType}, Map<${index.fields[2].classType}, ${name}>>> ${index.name}Configs = new HashMap<>();
        <#elseif index.normal && index.fields?size==3>
        ${tab}Map<${index.fields[0].classType}, Map<${index.fields[1].classType}, Map<${index.fields[2].classType}, List<${name}>>>> ${index.name}Configs = new HashMap<>();
        </#if>
    </#list>

        ${tab}List<String> errors = new ArrayList<>();
        ${tab}${name} oldConfig;

        ${tab}for (${name} config : configs) {
    <#list indexes as index>
        <#if index.unique && index.fields?size==1>
            ${tab}oldConfig = ${index.name}Configs.put(config.${index.fields[0].name}, config);
            ${tab}if (oldConfig != null) {
                ${tab}String repeatedConfigs = config.getClass().getSimpleName();
                ${tab}if (oldConfig.getClass() != config.getClass()) {
                    ${tab}repeatedConfigs += "," + oldConfig.getClass().getSimpleName();
                ${tab}}
                ${tab}errors.add(String.format("配置[%s]有重复数据[%s = %s]", repeatedConfigs, "${index.fields[0].name}", config.${index.fields[0].name}));
            ${tab}}
        <#elseif index.normal && index.fields?size==1>
            ${tab}${index.name}Configs.computeIfAbsent(config.${index.fields[0].name}, k -> new ArrayList<>()).add(config);
        <#elseif index.unique && index.fields?size==2>
            ${tab}oldConfig = ${index.name}Configs.computeIfAbsent(config.${index.fields[0].name}, k -> new HashMap<>()).put(config.${index.fields[1].name}, config);
            ${tab}if (oldConfig != null) {
                ${tab}String repeatedConfigs = config.getClass().getSimpleName();
                ${tab}if (oldConfig.getClass() != config.getClass()) {
                    ${tab}repeatedConfigs += "," + oldConfig.getClass().getSimpleName();
                ${tab}}
                ${tab}errors.add(String.format("配置[%s]有重复数据[%s,%s = %s,%s]", repeatedConfigs, "${index.fields[0].name}", "${index.fields[1].name}", config.${index.fields[0].name}, config.${index.fields[1].name}));
            ${tab}}
        <#elseif index.normal && index.fields?size==2>
            ${tab}${index.name}Configs.computeIfAbsent(config.${index.fields[0].name}, k -> new HashMap<>()).computeIfAbsent(config.${index.fields[1].name}, k -> new ArrayList<>()).add(config);
        <#elseif index.unique && index.fields?size==3>
            ${tab}oldConfig = ${index.name}Configs.computeIfAbsent(config.${index.fields[0].name}, k -> new HashMap<>()).computeIfAbsent(config.${index.fields[1].name}, k -> new HashMap<>()).put(config.${index.fields[2].name}, config);
            ${tab}if (oldConfig != null) {
                ${tab}String repeatedConfigs = config.getClass().getSimpleName();
                ${tab}if (oldConfig.getClass() != config.getClass()) {
                    ${tab}repeatedConfigs += "," + oldConfig.getClass().getSimpleName();
                ${tab}}
                ${tab}errors.add(String.format("配置[%s]有重复数据[%s,%s,%s = %s,%s,%s]", repeatedConfigs, "${index.fields[0].name}", "${index.fields[1].name}", "${index.fields[2].name}", config.${index.fields[0].name}, config.${index.fields[1].name}, config.${index.fields[2].name}));
            }
        <#elseif index.normal && index.fields?size==3>
            ${tab}${index.name}Configs.computeIfAbsent(config.${index.fields[0].name}, k -> new HashMap<>()).computeIfAbsent(config.${index.fields[1].name}, k -> new HashMap<>()).computeIfAbsent(config.${index.fields[2].name}, k -> new ArrayList<>()).add(config);
        </#if>
        <#if index_index<indexes?size-1>

        </#if>
    </#list>
        ${tab}}

    <#if parent??>
        ${tab}${name}.self.configs = Collections.unmodifiableList(configs);
    <#else>
        ${tab}${name}.configs = Collections.unmodifiableList(configs);
    </#if>
    <#list indexes as index>
        <#if parent??>
        ${tab}${name}.self.${index.name}Configs = unmodifiableMap(${index.name}Configs);
        <#else>
        ${tab}${name}.${index.name}Configs = unmodifiableMap(${index.name}Configs);
        </#if>
    </#list>

        ${tab}return errors;
    ${tab}}
    </#macro>

 <#if definitionType ==6>
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
