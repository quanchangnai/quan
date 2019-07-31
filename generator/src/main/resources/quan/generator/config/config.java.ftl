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
                ${field.name}.add($${field.name}.get${field.classValueType}(i));
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
                ${field.name}.put(${field.classKeyType}.valueOf($${field.name}$Key), $${field.name}.get${field.classValueType}($${field.name}$Key));
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

 <#if definitionType ==6>
    @Override
    public ${name} create() {
        return new ${name}();
    }

    public static class get {
        
        private get() {
        }

    <#list indexes as index>
        <#if index.comment !="">
        //${index.comment}
        </#if>
        <#if index.unique && index.fields?size==1>
	    private static Map<${index.fields[0].classType}, ${name}> ${index.name}Configs = new HashMap<>();

        <#elseif index.normal && index.fields?size==1>
        private static Map<${index.fields[0].classType}, List<${name}>> ${index.name}Configs = new HashMap<>();

        <#elseif index.unique && index.fields?size==2>
        private static Map<${index.fields[0].classType}, Map<${index.fields[1].classType}, ${name}>> ${index.name}Configs = new HashMap<>();
    
        <#elseif index.normal && index.fields?size==2>
        private static Map<${index.fields[0].classType}, Map<${index.fields[1].classType}, List<${name}>>> ${index.name}Configs = new HashMap<>();

        <#elseif index.unique && index.fields?size==3>
        private static Map<${index.fields[0].classType}, Map<${index.fields[1].classType}, Map<${index.fields[2].classType}, ${name}>>> ${index.name}Configs = new HashMap<>();

        <#elseif index.normal && index.fields?size==3>
        private static Map<${index.fields[0].classType}, Map<${index.fields[1].classType}, Map<${index.fields[2].classType}, List<${name}>>>> ${index.name}Configs = new HashMap<>();

        </#if>
    </#list>

    <#list indexes as index>
        <#if index.unique && index.fields?size==1>
        public static Map<${index.fields[0].classType}, ${name}> ${index.name}Configs() {
            return ${index.name}Configs;
        }

        public static ${name} by${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name}) {
            return ${index.name}Configs.get(${index.fields[0].name});
        }

        <#elseif index.normal && index.fields?size==1>
        public static Map<${index.fields[0].classType}, List<${name}>> ${index.name}Configs() {
            return ${index.name}Configs;
        }

        public static List<${name}> by${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name}) {
            return ${index.name}Configs.getOrDefault(${index.fields[0].name}, Collections.emptyList());
        }

        <#elseif index.unique && index.fields?size==2>
        public static Map<${index.fields[0].classType}, Map<${index.fields[1].classType}, ${name}>> ${index.name}Configs() {
            return ${index.name}Configs;
        }

        public static Map<${index.fields[1].classType}, ${name}> by${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name}) {
            return ${index.name}Configs.getOrDefault(${index.fields[0].name}, Collections.emptyMap());
        }

        public static ${name} by${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name}, ${index.fields[1].basicType} ${index.fields[1].name}) {
            return by${index.name?cap_first}(${index.fields[0].name}).get(${index.fields[1].name});
        }

        <#elseif index.normal && index.fields?size==2>
        public static Map<${index.fields[0].classType}, Map<${index.fields[1].classType}, List<${name}>>> ${index.name}Configs() {
            return ${index.name}Configs;
        }

        public static Map<${index.fields[1].classType}, List<${name}>> by${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name}) {
            return ${index.name}Configs.getOrDefault(${index.fields[0].name}, Collections.emptyMap());
        }

        public static List<${name}> by${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name}, ${index.fields[1].basicType} ${index.fields[1].name}) {
            return by${index.name?cap_first}(${index.fields[0].name}).getOrDefault(${index.fields[1].name}, Collections.emptyList());
        }

        <#elseif index.unique && index.fields?size==3>
        public static Map<${index.fields[0].classType}, Map<${index.fields[1].classType}, Map<${index.fields[2].classType}, ${name}>>> ${index.name}Configs() {
            return ${index.name}Configs;
        }

        public static Map<${index.fields[1].classType}, Map<${index.fields[2].classType}, ${name}>> by${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name}) {
            return ${index.name}Configs.getOrDefault(${index.fields[0].name}, Collections.emptyMap());
        }

        public static Map<${index.fields[2].classType}, ${name}> by${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name}, ${index.fields[1].basicType} ${index.fields[1].name}) {
            return by${index.name?cap_first}(${index.fields[0].name}).getOrDefault(${index.fields[1].name}, Collections.emptyMap());
        }

        public static ${name} by${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name}, ${index.fields[1].basicType} ${index.fields[1].name}, ${index.fields[2].basicType} ${index.fields[2].name}) {
            return by${index.name?cap_first}(${index.fields[0].name}, ${index.fields[1].name}).get(${index.fields[2].name});
        }

        <#elseif index.normal && index.fields?size==3>
        public static Map<${index.fields[0].classType}, Map<${index.fields[1].classType}, Map<${index.fields[2].classType}, List<${name}>>>> ${index.name}Configs() {
            return ${index.name}Configs;
        }

        public static Map<${index.fields[1].classType}, Map<${index.fields[2].classType}, List<${name}>>> by${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name}) {
            return ${index.name}Configs.getOrDefault(${index.fields[0].name}, Collections.emptyMap());
        }

        public static Map<${index.fields[2].classType}, List<${name}>> by${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name}, ${index.fields[1].basicType} ${index.fields[1].name}) {
            return by${index.name?cap_first}(${index.fields[0].name}).getOrDefault(${index.fields[1].name}, Collections.emptyMap());
        }

        public static List<${name}> by${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name}, ${index.fields[1].basicType} ${index.fields[1].name}, ${index.fields[2].basicType} ${index.fields[2].name}) {
            return by${index.name?cap_first}(${index.fields[0].name}, ${index.fields[1].name}).getOrDefault(${index.fields[2].name}, Collections.emptyList());
        }

        </#if>
    </#list>

        public static void index(List<${name}> configs) {
    <#list indexes as index>
        <#if index.unique && index.fields?size==1>
            Map<${index.fields[0].classType}, ${name}> ${index.name}Configs = new HashMap<>();
        <#elseif index.normal && index.fields?size==1>
            Map<${index.fields[0].classType}, List<${name}>> ${index.name}Configs = new HashMap<>();
        <#elseif index.unique && index.fields?size==2>
            Map<${index.fields[0].classType}, Map<${index.fields[1].classType}, ${name}>> ${index.name}Configs = new HashMap<>();
        <#elseif index.normal && index.fields?size==2>
            Map<${index.fields[0].classType}, Map<${index.fields[1].classType}, List<${name}>>> ${index.name}Configs = new HashMap<>();
        <#elseif index.unique && index.fields?size==3>
            Map<${index.fields[0].classType}, Map<${index.fields[1].classType}, Map<${index.fields[2].classType}, ${name}>>> ${index.name}Configs = new HashMap<>();
        <#elseif index.normal && index.fields?size==3>
            Map<${index.fields[0].classType}, Map<${index.fields[1].classType}, Map<${index.fields[2].classType}, List<${name}>>>> ${index.name}Configs = new HashMap<>();
        </#if>
    </#list>

            ${name} oldConfig;
            for (${name} config : configs) {
    <#list indexes as index>
        <#if index.unique && index.fields?size==1>
                oldConfig = ${index.name}Configs.put(config.${index.fields[0].name}, config);
                if (oldConfig != null) {
                    String repeatedConfigs = config.getClass().getSimpleName();
                    if (oldConfig.getClass() != config.getClass()) {
                        repeatedConfigs += "," + oldConfig.getClass().getSimpleName();
                    }
                    throw new ConfigException("配置[" + repeatedConfigs + "]有重复索引[${index.fields[0].name}:" + config.${index.fields[0].name} + "]");
                }
        <#elseif index.normal && index.fields?size==1>
                ${index.name}Configs.computeIfAbsent(config.${index.fields[0].name}, k -> new ArrayList<>()).add(config);
        <#elseif index.unique && index.fields?size==2>
                oldConfig = ${index.name}Configs.computeIfAbsent(config.${index.fields[0].name}, k -> new HashMap<>()).put(config.${index.fields[1].name}, config);
                if (oldConfig != null) {
                    String repeatedConfigs = config.getClass().getSimpleName();
                    if (oldConfig.getClass() != config.getClass()) {
                        repeatedConfigs += "," + oldConfig.getClass().getSimpleName();
                    }
                    throw new ConfigException("配置[" + repeatedConfigs + "]有重复索引[${index.fields[0].name},${index.fields[1].name}:" + config.${index.fields[0].name} + "," + config.${index.fields[1].name} + "]");
                }
        <#elseif index.normal && index.fields?size==2>
                ${index.name}Configs.computeIfAbsent(config.${index.fields[0].name}, k -> new HashMap<>()).computeIfAbsent(config.${index.fields[1].name}, k -> new ArrayList<>()).add(config);
        <#elseif index.unique && index.fields?size==3>
                oldConfig = ${index.name}Configs.computeIfAbsent(config.${index.fields[0].name}, k -> new HashMap<>()).computeIfAbsent(config.${index.fields[1].name}, k -> new HashMap<>()).put(config.${index.fields[2].name}, config);
                if (oldConfig != null) {
                    String repeatedConfigs = config.getClass().getSimpleName();
                    if (oldConfig.getClass() != config.getClass()) {
                        repeatedConfigs += "," + oldConfig.getClass().getSimpleName();
                    }
                    throw new ConfigException("配置[" + repeatedConfigs + "]有重复索引[${index.fields[0].name},${index.fields[1].name},${index.fields[2].name}:" + config.${index.fields[0].name} + "," + config.${index.fields[1].name} + "," + config.${index.fields[2].name} + "]");
                }
        <#elseif index.normal && index.fields?size==3>
                ${index.name}Configs.computeIfAbsent(config.${index.fields[0].name}, k -> new HashMap<>()).computeIfAbsent(config.${index.fields[1].name}, k -> new HashMap<>()).computeIfAbsent(config.${index.fields[2].name}, k -> new ArrayList<>()).add(config);
        </#if>
        <#if index_index<indexes?size-1>

        </#if>
    </#list>
            }

    <#list indexes as index>
            get.${index.name}Configs = unmodifiable(${index.name}Configs);
    </#list>

        }

    }

</#if>
}
