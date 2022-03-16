package ${getFullPackageName("java")};

import com.alibaba.fastjson.*;
<#if (!(parentClassName??) || kind == 6) && getFullPackageName("java")!="quan.config">
import quan.config.*;
</#if>
<#if (!(parentClassName??) || kind == 6) && getFullPackageName("java")!="quan.config.loader">
import quan.config.loader.ConfigLoader;
</#if>
<#list imports?keys as import>
import ${import};
</#list>

/**
<#if comment !="">
 * ${comment}<br/>
</#if>
 * 代码自动生成，请勿手动修改
 */
public class ${name} extends <#if parentClassName??>${parentClassName}<#elseif kind ==2>Bean<#else>Config</#if> {
<#if !selfFields??>
    <#assign selfFields = fields>
</#if>
<#assign supportJava = isSupportLanguage("java")>
<#list selfFields as field>
    <#if !(supportJava &&field.isSupportLanguage("java"))>
        <#continue>
    </#if>

    <#if field.comment !="">
    /**
     * ${field.comment}
     */
    </#if>
    <#if field.type=="list">
    public final ${field.basicType}<${field.classValueType}> ${field.name};
    <#elseif field.type=="set">
    public final ${field.basicType}<${field.classValueType}> ${field.name};
    <#elseif field.type=="map">
    public final ${field.basicType}<${field.classKeyType}, ${field.classValueType}> ${field.name};
    <#elseif  field.timeType>
    public final ${field.basicType} ${field.name};

    <#if field.comment !="">
    //${field.comment}
    </#if>
    public final String ${field.name}_;
    <#elseif field.builtinType>
    public final ${field.basicType} ${field.name};
    <#else>
    public final ${field.classType} ${field.name};
    </#if>
</#list>


    public ${name}(JSONObject json) {
        super(json);

<#list selfFields as field>
    <#if !(supportJava &&field.isSupportLanguage("java"))>
        <#continue>
    </#if>
    <#if field.type=="string">
        this.${field.name} = json.getOrDefault("${field.name}", "").toString();
    <#elseif field.type=="bool">
        this.${field.name} = json.getBooleanValue("${field.name}");
    <#elseif field.timeType>
        this.${field.name} = json.getDate("${field.name}");
        this.${field.name}_ = json.getOrDefault("${field.name}_", "").toString();
    <#elseif field.type=="list" || field.type=="set">
        <#if field_index gt 0 >

        </#if>
        JSONArray ${field.name}$1 = json.getJSONArray("${field.name}");
        ${field.basicType}<${field.classValueType}> ${field.name}$2 = new ${field.classType}<>();
        if (${field.name}$1 != null) {
            for (int i = 0; i < ${field.name}$1.size(); i++) {
                <#if field.beanValueType>
                ${field.classValueType} ${field.name}$Value = ${field.classValueType}.create(${field.name}$1.getJSONObject(i));
                ${field.name}$2.add(${field.name}$Value);
                <#else>
                ${field.name}$2.add(${field.name}$1.get${field.classValueType}(i));
                </#if>
            }
        }
        this.${field.name} = Collections.unmodifiable${field.basicType}(${field.name}$2);
        <#if field_has_next && (selfFields[field_index+1].primitiveType ||selfFields[field_index+1].timeType || selfFields[field_index+1].enumType) >

        </#if>
    <#elseif field.type=="map">
        <#if field_index gt 0 >

        </#if>
        JSONObject ${field.name}$1 = json.getJSONObject("${field.name}");
        Map<${field.classKeyType}, ${field.classValueType}> ${field.name}$2 = new HashMap<>();
        if (${field.name}$1 != null) {
            for (String ${field.name}$Key : ${field.name}$1.keySet()) {
                <#if field.beanValueType>
                ${field.classValueType} ${field.name}$Value = ${field.classValueType}.create(${field.name}$1.getJSONObject(${field.name}$Key));
                ${field.name}$2.put(${field.classKeyType}.valueOf(${field.name}$Key), ${field.name}$Value);
                <#else>
                ${field.name}$2.put(${field.classKeyType}.valueOf(${field.name}$Key), ${field.name}$1.get${field.classValueType}(${field.name}$Key));
                </#if>
            }
        }
        this.${field.name} = Collections.unmodifiableMap(${field.name}$2);
        <#if field_has_next && (selfFields[field_index+1].primitiveType ||selfFields[field_index+1].timeType || selfFields[field_index+1].enumType) >

        </#if>
    <#elseif field.builtinType>
        this.${field.name} = json.get${field.type?cap_first}Value("${field.name}");
    <#elseif field.enumType>
        this.${field.name} = ${field.type}.valueOf(json.getIntValue("${field.name}"));
    <#else>
        <#if field_index gt 0 >

        </#if>
        JSONObject ${field.name} = json.getJSONObject("${field.name}");
        if (${field.name} != null) {
            this.${field.name} = ${field.classType}.create(${field.name});
        } else {
            this.${field.name} = null;
        }
        <#if field_has_next && (selfFields[field_index+1].primitiveType ||selfFields[field_index+1].timeType || selfFields[field_index+1].enumType) >

        </#if>
    </#if>
</#list>
    }

 <#if kind ==6>
    @Override
    public ${name} create(JSONObject json) {
        return new ${name}(json);
    }
<#else>
    public static ${name} create(JSONObject json) {
        <#if (dependentChildren?size>0)>
        String clazz = json.getOrDefault("class", "").toString();
        switch (clazz) {
            <#list dependentChildren?keys as key>
            case "${dependentChildren[key].left}":
                return ${dependentChildren[key].right}.create(json);
            </#list>
            case "${name}":
                return new ${name}(json);
            default:
                return null;
        }
        <#else>
        return new ${name}(json);
        </#if>
    }
</#if>

    @Override
    public String toString() {
        return "${name}{" +
        <#list fields as field>
            <#if !(supportJava &&field.isSupportLanguage("java"))>
                <#continue>
            </#if>
                "<#rt>
            <#if field_index gt 0>
                <#lt>,<#rt>
            </#if>
            <#if field.type == "string">
                <#lt>${field.name}='" + ${field.name} + '\'' +
            <#elseif field.timeType>
                <#lt>${field.name}='" + ${field.name}_ + '\'' +
            <#else>
                <#lt>${field.name}=" + ${field.name} +
            </#if>
        </#list>
                '}';

    }
<#macro indexer tab>
    ${tab}//所有${name}
    ${tab}private static volatile List<${name}> configs = Collections.emptyList();

    <#list indexes as index>
        <#if index.comment !="">
    ${tab}//索引:${index.comment}
        </#if>
        <#if index.unique && index.fields?size==1>
    ${tab}private static volatile Map<${index.fields[0].classType}, ${name}> ${index.name}Configs = Collections.emptyMap();

        <#elseif index.normal && index.fields?size==1>
    ${tab}private static volatile Map<${index.fields[0].classType}, List<${name}>> ${index.name}Configs = Collections.emptyMap();

        <#elseif index.unique && index.fields?size==2>
    ${tab}private static volatile Map<${index.fields[0].classType}, Map<${index.fields[1].classType}, ${name}>> ${index.name}Configs = Collections.emptyMap();

        <#elseif index.normal && index.fields?size==2>
    ${tab}private static volatile Map<${index.fields[0].classType}, Map<${index.fields[1].classType}, List<${name}>>> ${index.name}Configs = Collections.emptyMap();

        <#elseif index.unique && index.fields?size==3>
    ${tab}private static volatile Map<${index.fields[0].classType}, Map<${index.fields[1].classType}, Map<${index.fields[2].classType}, ${name}>>> ${index.name}Configs = Collections.emptyMap();

        <#elseif index.normal && index.fields?size==3>
    ${tab}private static volatile Map<${index.fields[0].classType}, Map<${index.fields[1].classType}, Map<${index.fields[2].classType}, List<${name}>>>> ${index.name}Configs = Collections.emptyMap();

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

    ${tab}public static ${name} get${index.suffix}(${index.fields[0].basicType} ${index.fields[0].name}) {
        ${tab}return ${index.name}Configs.get(${index.fields[0].name});
    ${tab}}

        <#elseif index.normal && index.fields?size==1>
    ${tab}public static Map<${index.fields[0].classType}, List<${name}>> get${index.name?cap_first}Configs() {
        ${tab}return ${index.name}Configs;
    ${tab}}

    ${tab}public static List<${name}> get${index.suffix}(${index.fields[0].basicType} ${index.fields[0].name}) {
        ${tab}return ${index.name}Configs.getOrDefault(${index.fields[0].name}, Collections.emptyList());
    ${tab}}

        <#elseif index.unique && index.fields?size==2>
    ${tab}public static Map<${index.fields[0].classType}, Map<${index.fields[1].classType}, ${name}>> get${index.name?cap_first}Configs() {
        ${tab}return ${index.name}Configs;
    ${tab}}

    ${tab}public static Map<${index.fields[1].classType}, ${name}> get${index.suffix}(${index.fields[0].basicType} ${index.fields[0].name}) {
        ${tab}return ${index.name}Configs.getOrDefault(${index.fields[0].name}, Collections.emptyMap());
    ${tab}}

    ${tab}public static ${name} get${index.suffix}(${index.fields[0].basicType} ${index.fields[0].name}, ${index.fields[1].basicType} ${index.fields[1].name}) {
        ${tab}return get${index.suffix}(${index.fields[0].name}).get(${index.fields[1].name});
    ${tab}}

        <#elseif index.normal && index.fields?size==2>
    ${tab}public static Map<${index.fields[0].classType}, Map<${index.fields[1].classType}, List<${name}>>> get${index.name?cap_first}Configs() {
        ${tab}return ${index.name}Configs;
    ${tab}}

    ${tab}public static Map<${index.fields[1].classType}, List<${name}>> get${index.suffix}(${index.fields[0].basicType} ${index.fields[0].name}) {
        ${tab}return ${index.name}Configs.getOrDefault(${index.fields[0].name}, Collections.emptyMap());
    ${tab}}

    ${tab}public static List<${name}> get${index.suffix}(${index.fields[0].basicType} ${index.fields[0].name}, ${index.fields[1].basicType} ${index.fields[1].name}) {
        ${tab}return get${index.suffix}(${index.fields[0].name}).getOrDefault(${index.fields[1].name}, Collections.emptyList());
    ${tab}}

        <#elseif index.unique && index.fields?size==3>
    ${tab}public static Map<${index.fields[0].classType}, Map<${index.fields[1].classType}, Map<${index.fields[2].classType}, ${name}>>> get${index.name?cap_first}Configs() {
        ${tab}return ${index.name}Configs;
    ${tab}}

    ${tab}public static Map<${index.fields[1].classType}, Map<${index.fields[2].classType}, ${name}>> get${index.suffix}(${index.fields[0].basicType} ${index.fields[0].name}) {
        ${tab}return ${index.name}Configs.getOrDefault(${index.fields[0].name}, Collections.emptyMap());
    ${tab}}

    ${tab}public static Map<${index.fields[2].classType}, ${name}> get${index.suffix}(${index.fields[0].basicType} ${index.fields[0].name}, ${index.fields[1].basicType} ${index.fields[1].name}) {
        ${tab}return get${index.suffix}(${index.fields[0].name}).getOrDefault(${index.fields[1].name}, Collections.emptyMap());
    ${tab}}

    ${tab}public static ${name} get${index.suffix}(${index.fields[0].basicType} ${index.fields[0].name}, ${index.fields[1].basicType} ${index.fields[1].name}, ${index.fields[2].basicType} ${index.fields[2].name}) {
        ${tab}return get${index.suffix}(${index.fields[0].name}, ${index.fields[1].name}).get(${index.fields[2].name});
    ${tab}}

        <#elseif index.normal && index.fields?size==3>
    ${tab}public static Map<${index.fields[0].classType}, Map<${index.fields[1].classType}, Map<${index.fields[2].classType}, List<${name}>>>> get${index.name?cap_first}Configs() {
        ${tab}return ${index.name}Configs;
    ${tab}}

    ${tab}public static Map<${index.fields[1].classType}, Map<${index.fields[2].classType}, List<${name}>>> get${index.suffix}(${index.fields[0].basicType} ${index.fields[0].name}) {
        ${tab}return ${index.name}Configs.getOrDefault(${index.fields[0].name}, Collections.emptyMap());
    ${tab}}

    ${tab}public static Map<${index.fields[2].classType}, List<${name}>> get${index.suffix}(${index.fields[0].basicType} ${index.fields[0].name}, ${index.fields[1].basicType} ${index.fields[1].name}) {
        ${tab}return get${index.suffix}(${index.fields[0].name}).getOrDefault(${index.fields[1].name}, Collections.emptyMap());
    ${tab}}

    ${tab}public static List<${name}> get${index.suffix}(${index.fields[0].basicType} ${index.fields[0].name}, ${index.fields[1].basicType} ${index.fields[1].name}, ${index.fields[2].basicType} ${index.fields[2].name}) {
        ${tab}return get${index.suffix}(${index.fields[0].name}, ${index.fields[1].name}).getOrDefault(${index.fields[2].name}, Collections.emptyList());
    ${tab}}

        </#if>
    </#list>

    ${tab}/**
     ${tab}* 加载配置，建立索引
     ${tab}* @param configs 所有配置
     ${tab}* @return 错误信息
     ${tab}*/
    ${tab}@SuppressWarnings({"unchecked"})
    ${tab}private static List<String> load(List<${name}> configs) {
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

        ${tab}for (${name} config : configs) {
    <#list indexes as index>
        <#if index.fields?size==1>
            <#if isConstantKeyField("${index.fields[0].name}")>
            ${tab}if (!config.${index.fields[0].name}.equals("")) {
                ${tab}<#if parent??>Config.</#if>load(${index.name}Configs, errors, config, ${index.unique?c}, Collections.singletonList("${index.fields[0].name}"), config.${index.fields[0].name});
            ${tab}}
            <#else>
            ${tab}<#if parent??>Config.</#if>load(${index.name}Configs, errors, config, ${index.unique?c}, Collections.singletonList("${index.fields[0].name}"), config.${index.fields[0].name});
            </#if>
        <#elseif index.fields?size==2>
            ${tab}<#if parent??>Config.</#if>load(${index.name}Configs, errors, config, ${index.unique?c}, Arrays.asList("${index.fields[0].name}", "${index.fields[1].name}"), config.${index.fields[0].name}, config.${index.fields[1].name});
        <#elseif index.fields?size==3>
            ${tab}<#if parent??>Config.</#if>load(${index.name}Configs, errors, config, ${index.unique?c}, Arrays.asList("${index.fields[0].name}", "${index.fields[1].name}", "${index.fields[2].name}"), config.${index.fields[0].name}, config.${index.fields[1].name}, config.${index.fields[2].name});
        </#if>
    </#list>
        ${tab}}

        ${tab}configs = Collections.unmodifiableList(configs);
    <#list indexes as index>
        ${tab}${index.name}Configs = unmodifiableMap(${index.name}Configs);
    </#list>

        ${tab}${name}.<#if parent??>self.</#if>configs = configs;
    <#list indexes as index>
        ${tab}${name}.<#if parent??>self.</#if>${index.name}Configs = ${index.name}Configs;
    </#list>

        ${tab}return errors;
    ${tab}}
</#macro>

 <#if kind ==6>

    <#if parent??>
    public static class self {

        private self() {
        }

        <@indexer tab="    "/>

    }
    <#else>
        <@indexer tab=""/>
    </#if>

    static {
        ConfigLoader.registerLoadFunction(${name}.class, ${name}<#if parent??>.self</#if>::load);
    }

</#if>
}
