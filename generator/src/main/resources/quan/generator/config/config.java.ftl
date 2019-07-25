package ${packageName};

import quan.config.*;
import java.util.*;
import com.alibaba.fastjson.*;
<#list imports as import>
    import ${import};
    <#if !import_has_next>

    </#if>
</#list>

/**
<#if comment !="">
* ${comment}<br/>
</#if>
* Created by 自动生成
*/
public class ${name} extends <#if definitionType ==2>Bean<#elseif definitionType ==6>Config</#if> {
<#if definitionType ==6>

    <#list indexes as index>
        <#if index.comment !="">
    //${index.comment}
        </#if>
        <#if index.unique && index.fields?size==1>
	private static Map<${index.field1.classType}, ${name}> ${index.name}Configs = new HashMap<>();

        <#elseif index.normal && index.fields?size==1>
    private static Map<${index.field1.classType}, List<${name}>> ${index.name}Configs = new HashMap<>();

        <#elseif index.unique && index.fields?size==2>
    private static Map<${index.field1.classType}, Map<${index.field2.classType}, ${name}>> ${index.name}Configs = new HashMap<>();

        <#elseif index.normal && index.fields?size==2>
    private static Map<${index.field1.classType}, Map<${index.field2.classType}, List<${name}>>> ${index.name}Configs = new HashMap<>();

        <#elseif index.unique && index.fields?size==3>
    private static Map<${index.field1.classType}, Map<${index.field2.classType}, Map<${index.field3.classType}, ${name}>>> ${index.name}Configs = new HashMap<>();

        <#elseif index.normal && index.fields?size==3>
    private static Map<${index.field1.classType}, Map<${index.field2.classType}, Map<${index.field3.classType}, List<${name}>>>> ${index.name}Configs = new HashMap<>();

        </#if>
    </#list>

    <#list indexes as index>
        <#if index.unique && index.fields?size==1>
    public static Map<${index.field1.classType}, ${name}> get${index.name?cap_first}Configs() {
        return ${index.name}Configs;
    }

    public static ${name} getBy${index.name?cap_first}(${index.field1.basicType} ${index.field1.name}) {
        return ${index.name}Configs.get(${index.field1.name});
    }

        <#elseif index.normal && index.fields?size==1>
    public static Map<${index.field1.classType}, List<${name}>> get${index.name?cap_first}Configs() {
        return ${index.name}Configs;
    }

    public static List<${name}> getBy${index.name?cap_first}(${index.field1.basicType} ${index.field1.name}) {
        return ${index.name}Configs.getOrDefault(${index.field1.name}, Collections.emptyList());
    }

        <#elseif index.unique && index.fields?size==2>
    public static Map<${index.field1.classType}, Map<${index.field2.classType}, ${name}>> get${index.name?cap_first}Configs() {
        return ${index.name}Configs;
    }

    public static Map<${index.field2.classType}, ${name}> getBy${index.name?cap_first}(${index.field1.basicType} ${index.field1.name}) {
        return ${index.name}Configs.getOrDefault(${index.field1.name}, Collections.emptyMap());
    }

    public static ${name} getBy${index.name?cap_first}(${index.field1.basicType} ${index.field1.name}, ${index.field2.basicType} ${index.field2.name}) {
        return getBy${index.name?cap_first}(${index.field1.name}).get(${index.field2.name});
    }

        <#elseif index.normal && index.fields?size==2>
    public static Map<${index.field1.classType}, Map<${index.field2.classType}, List<${name}>>> get${index.name?cap_first}Configs() {
        return ${index.name}Configs;
    }

    public static Map<${index.field2.classType}, List<${name}>> getBy${index.name?cap_first}(${index.field1.basicType} ${index.field1.name}) {
        return ${index.name}Configs.getOrDefault(${index.field1.name}, Collections.emptyMap());
    }

    public static List<${name}> getBy${index.name?cap_first}(${index.field1.basicType} ${index.field1.name}, ${index.field2.basicType} ${index.field2.name}) {
        return getBy${index.name?cap_first}(${index.field1.name}).getOrDefault(${index.field2.name}, Collections.emptyList());
    }

        <#elseif index.unique && index.fields?size==3>
    public static Map<${index.field1.classType}, Map<${index.field2.classType}, Map<${index.field3.classType}, ${name}>>> get${index.name?cap_first}Configs() {
        return ${index.name}Configs;
    }

    public static Map<${index.field2.classType}, Map<${index.field3.classType}, ${name}>> getBy${index.name?cap_first}(${index.field1.basicType} ${index.field1.name}) {
        return ${index.name}Configs.getOrDefault(${index.field1.name}, Collections.emptyMap());
    }

    public static Map<${index.field3.classType}, ${name}> getBy${index.name?cap_first}(${index.field1.basicType} ${index.field1.name}, ${index.field2.basicType} ${index.field2.name}) {
        return getBy${index.name?cap_first}(${index.field1.name}).getOrDefault(${index.field2.name}, Collections.emptyMap());
    }

    public static ${name} getBy${index.name?cap_first}(${index.field1.basicType} ${index.field1.name}, ${index.field2.basicType} ${index.field2.name}, ${index.field3.basicType} ${index.field3.name}) {
        return getBy${index.name?cap_first}(${index.field1.name}, ${index.field3.name}).get(${index.field3.name});
    }

        <#elseif index.normal && index.fields?size==3>
    public static Map<${index.field1.classType}, Map<${index.field2.classType}, Map<${index.field3.classType}, List<${name}>>>> get${index.name?cap_first}Configs() {
        return ${index.name}Configs;
    }

    public static Map<${index.field2.classType}, Map<${index.field3.classType}, List<${name}>>> getBy${index.name?cap_first}(${index.field1.basicType} ${index.field1.name}) {
        return ${index.name}Configs.getOrDefault(${index.field1.name}, Collections.emptyMap());
    }

    public static Map<${index.field3.classType}, List<${name}>> getBy${index.name?cap_first}(${index.field1.basicType} ${index.field1.name}, ${index.field2.basicType} ${index.field2.name}) {
        return getBy${index.name?cap_first}(${index.field1.name}).getOrDefault(${index.field2.name}, Collections.emptyMap());
    }

    public static List<${name}> getBy${index.name?cap_first}(${index.field1.basicType} ${index.field1.name}, ${index.field2.basicType} ${index.field2.name}, ${index.field3.basicType} ${index.field3.name}) {
        return getBy${index.name?cap_first}(${index.field1.name}, ${index.field2.name}).getOrDefault(${index.field3.name}, Collections.emptyList());
    }

        </#if>
    </#list>

    static void index(List<${name}> configs) {
    <#list indexes as index>
        <#if index.unique && index.fields?size==1>
        Map<${index.field1.classType}, ${name}> ${index.name}Configs = new HashMap<>();
        <#elseif index.normal && index.fields?size==1>
        Map<${index.field1.classType}, List<${name}>> ${index.name}Configs = new HashMap<>();
        <#elseif index.unique && index.fields?size==2>
        Map<${index.field1.classType}, Map<${index.field2.classType}, ${name}>> ${index.name}Configs = new HashMap<>();
        <#elseif index.normal && index.fields?size==2>
        Map<${index.field1.classType}, Map<${index.field2.classType}, List<${name}>>> ${index.name}Configs = new HashMap<>();
        <#elseif index.unique && index.fields?size==3>
        Map<${index.field1.classType}, Map<${index.field2.classType}, Map<${index.field3.classType}, ${name}>>> ${index.name}Configs = new HashMap<>();
        <#elseif index.normal && index.fields?size==3>
        Map<${index.field1.classType}, Map<${index.field2.classType}, Map<${index.field3.classType}, List<${name}>>>> ${index.name}Configs = new HashMap<>();
        </#if>
    </#list>

        for (${name} config : configs) {
    <#list indexes as index>
        <#if index.unique && index.fields?size==1>
            if (${index.name}Configs.put(config.${index.field1.name}, config) != null) {
                throw new RuntimeException("配置[${name}]的索引[${index.field1.name}]:[" + config.${index.field1.name} + "]有重复");
            }
        <#elseif index.normal && index.fields?size==1>
            ${index.name}Configs.computeIfAbsent(config.${index.field1.name}, k -> new ArrayList<>()).add(config);
        <#elseif index.unique && index.fields?size==2>
            if (${index.name}Configs.computeIfAbsent(config.${index.field1.name}, k -> new HashMap<>()).put(config.${index.field2.name}, config) != null) {
                throw new RuntimeException("配置[${name}]的索引[[${index.field1.name},[${index.field2.name}]:[" + config.${index.field1.name} + "," + config.${index.field2.name} + "]有重复");
            }
        <#elseif index.normal && index.fields?size==2>
            ${index.name}Configs.computeIfAbsent(config.${index.field1.name}, k -> new HashMap<>()).computeIfAbsent(config.${index.field2.name}, k -> new ArrayList<>()).add(config);
        <#elseif index.unique && index.fields?size==3>
            if (${index.name}Configs.computeIfAbsent(config.${index.field1.name}, k -> new HashMap<>()).computeIfAbsent(config.${index.field2.name}, k -> new HashMap<>()).put(config.${index.field3.name}, config) != null) {
                throw new RuntimeException("配置[${name}]的索引[${index.field1.name},${index.field2.name},${index.field3.name}]:[" + config.${index.field1.name} + "," + config.${index.field2.name} + "," + config.${index.field3.name} + "]有重复");
            }
        <#elseif index.normal && index.fields?size==3>
            ${index.name}Configs.computeIfAbsent(config.${index.field1.name}, k -> new HashMap<>()).computeIfAbsent(config.${index.field2.name}, k -> new HashMap<>()).computeIfAbsent(config.${index.field3.name}, k -> new ArrayList<>()).add(config);
        </#if>
        <#if index_index<indexes?size-1>

        </#if>
    </#list>
        }

    <#list indexes as index>
        ${name}.${index.name}Configs = unmodifiable(${index.name}Configs);
    </#list>
    }

</#if>

<#list fields as field>
    <#if field.comment !="">
    //${field.comment}
    </#if>
    <#if field.type=="list">
    private ${field.basicType}<${field.classValueType}> ${field.name} = new ArrayList<>();
    <#elseif field.type=="set">
    private ${field.basicType}<${field.classValueType}> ${field.name} = new HashSet<>();
    <#elseif field.type=="map">
    private ${field.basicType}<${field.classKeyType}, ${field.classValueType}> ${field.name} = new HashMap<>();
    <#else >
    private ${field.basicType} ${field.name};
    </#if>

</#list>

<#list fields as field>
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
    protected void parse(JSONObject object) {
<#list fields as field>
    <#if field.type=="string">
        ${field.name} = object.getString("${field.name}");
    <#elseif field.type=="list" || field.type=="set">
        <#if field_index gt 0 >

        </#if>
        JSONArray $${field.name} = object.getJSONArray("${field.name}");
        if ($${field.name} != null) {
            for (int i = 0; i < $${field.name}.size(); i++) {
                ${field.name}.add($${field.name}.get${field.classValueType}(i));
            }
        }
        ${field.name} = Collections.unmodifiable${field.basicType}(${field.name});
        <#if field_has_next && fields[field_index+1].primitiveType >

        </#if>
    <#elseif field.type=="map">
        <#if field_index gt 0 >

        </#if>
        JSONObject $${field.name} = object.getJSONObject("${field.name}");
        if ($${field.name} != null) {
            for (String $${field.name}$Key : $${field.name}.keySet()) {
                ${field.name}.put(${field.classKeyType}.valueOf($${field.name}$Key), $${field.name}.get${field.classValueType}($${field.name}$Key));
            }
        }
        ${field.name} = Collections.unmodifiableMap(${field.name});
        <#if field_has_next && fields[field_index+1].primitiveType >

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
            ${field.name} = QuestType.valueOf($${field.name});
        }
         <#if field_has_next && fields[field_index+1].primitiveType >

        </#if>
    <#else>
        <#if field_index gt 0 >

        </#if>
        JSONObject $${field.name} = object.getJSONObject("${field.name}");
        if ($${field.name} != null) {
            ${field.name} = new ${field.basicType}();
            ${field.name}.parse($reward);
        }
        <#if field_has_next && fields[field_index+1].primitiveType >

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
            <#elseif field.enumType>
                <#lt>${field.name}=" + ${field.type}.valueOf(${field.name}.getValue()) +
            <#else>
                <#lt>${field.name}=" + ${field.name} +
            </#if>
        </#list>
                '}';

        }

}
