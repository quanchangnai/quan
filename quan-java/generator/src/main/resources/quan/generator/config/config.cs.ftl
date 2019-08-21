using System;
using System.Collections.Generic;
using System.Collections.Immutable;
using Newtonsoft.Json.Linq;
using ConfigCS;
<#list imports as import>
using ${import};
</#list>

namespace ${fullPackageName}
{
    /// <summary>
<#if comment !="">
	/// ${comment}<br/>
</#if>
	/// Created by 自动生成
	/// </summary>
    public class ${name} : <#if definitionType ==2>Bean<#elseif definitionType ==6 && (!parent?? || parent=="")>Config<#elseif definitionType ==6>${parent}</#if>
    {
<#if !selfFields??>
    <#assign selfFields = fields>
</#if>

<#list selfFields as field>
    <#if field.comment !="">
        /// <summary>
        /// ${field.comment}
        /// </summary>
    </#if>
    <#if field.type=="list">
        public readonly ${field.basicType}<${field.classValueType}> ${field.name?cap_first};
    <#elseif field.type=="set">
        public readonly ${field.basicType}<${field.classValueType}> ${field.name?cap_first};
    <#elseif field.type=="map">
        public readonly ${field.basicType}<${field.classKeyType}, ${field.classValueType}> ${field.name?cap_first};
    <#elseif  field.timeType>
        public readonly ${field.basicType} ${field.name?cap_first};

        public readonly string ${field.name?cap_first}_Str;
    <#else >
        public readonly ${field.basicType} ${field.name?cap_first};
    </#if>

</#list>

        public ${name}(JObject json): base(json)
        {
<#list selfFields as field>
    <#if field.type=="list" || field.type=="set">
        <#if field_index gt 0 >

        </#if>
            var ${field.name}1 = json["${field.name}"]?.Value<JArray>();
            var ${field.name}2 = Immutable${field.classType}<${field.valueType}>.Empty;
            if (${field.name}1 != null)
            {
                foreach (var ${field.name}Value in ${field.name}1)
                {
                <#if field.beanValueType>
                    ${field.name}2.Add(new ${field.classValueType}(${field.name}Value.Value<JObject>()));
                <#else>
                    ${field.name}2.Add(${field.name}Value.Value<${field.valueType}>());
                </#if>
                }
            }
            ${field.name?cap_first} = ${field.name}2;
        <#if field_has_next && !selfFields[field_index+1].collectionType>

        </#if>
    <#elseif field.type=="map">
        <#if field_index gt 0 >

        </#if>
            var ${field.name}1 = json["${field.name}"]?.Value<JObject>();
            var ${field.name}2 = ImmutableDictionary<${field.classKeyType}, ${field.classValueType}>.Empty;
            if (${field.name}1 != null)
            {
                foreach (var ${field.name}Prop in ${field.name}1.Properties())
                {
                <#if field.beanValueType>
                    ${field.name}2.Add(${field.classKeyType}.Parse(${field.name}Prop.Name), new ${field.classValueType}(${field.name}Prop.Value<JObject>()));
                <#else>
                    ${field.name}2.Add(${field.classKeyType}.Parse(${field.name}Prop.Name), ${field.name}Prop.Value<${field.classValueType}>());
                </#if>
                }
            }
            ${field.name?cap_first} = ${field.name}2;
        <#if field_has_next && !selfFields[field_index+1].collectionType>

        </#if>
    <#elseif field.timeType>
            ${field.name?cap_first} = json["${field.name}"]?.Value<DateTime>()?? default;
            ${field.name?cap_first}_Str = json["${field.name}$Str"]?.Value<string>()?? "";
    <#elseif field.type=="string">
            ${field.name?cap_first} = json["${field.name}"]?.Value<${field.type}>()?? "";
    <#elseif field.builtInType>
            ${field.name?cap_first} = json["${field.name}"]?.Value<${field.type}>()?? default;
    <#elseif field.enumType>
            ${field.name?cap_first} = (${field.type}) (json["${field.name}"]?.Value<int>()?? default);
    <#else>
            ${field.name?cap_first} = json.ContainsKey("${field.name}") ? new ${field.type}(json["${field.name}"].Value<JObject>()) : null;
    </#if>
</#list>
        }

 <#if definitionType ==6>
        protected override Config Create(JObject json) 
        {
            return new ${name}(json);
        }
</#if>


        public override string ToString()
        {
            return "${name}{" +
            <#list fields as field>
                   "<#rt>
                <#if field_index gt 0>
                   <#lt>,<#rt>
                </#if>
                <#if field.type == "string">
                   <#lt>${field.name?cap_first}='" + ${field.name?cap_first} + '\'' +
                <#elseif field.timeType>
                   <#lt>${field.name?cap_first}='" + ${field.name?cap_first}_Str + '\'' +
                <#else>
                   <#lt>${field.name?cap_first}=" + ${field.name?cap_first} +
                </#if>
            </#list>
                   '}';
        }

<#macro indexer tab>
        ${tab}private static volatile IList<${name}> _configs = new List<${name}>();

    <#list indexes as index>
        <#if index.comment !="">
        ${tab}/// <summary>
        ${tab}/// ${index.comment}
        ${tab}/// </summary>
        </#if>
        <#if index.unique && index.fields?size==1>
        ${tab}private static volatile IDictionary<${index.fields[0].classType}, ${name}> _${index.name}Configs = new Dictionary<${index.fields[0].classType}, ${name}>();

        <#elseif index.normal && index.fields?size==1>
        ${tab}private static volatile IDictionary<${index.fields[0].classType}, IList<${name}>> _${index.name}Configs = new Dictionary<${index.fields[0].classType}, IList<${name}>>();

        <#elseif index.unique && index.fields?size==2>
        ${tab}private static volatile IDictionary<${index.fields[0].classType}, IDictionary<${index.fields[1].classType}, ${name}>> _${index.name}Configs = new Dictionary<${index.fields[0].classType}, IDictionary<${index.fields[1].classType}, ${name}>>();

        <#elseif index.normal && index.fields?size==2>
        ${tab}private static volatile IDictionary<${index.fields[0].classType}, IDictionary<${index.fields[1].classType}, IList<${name}>>> _${index.name}Configs = new Dictionary<${index.fields[0].classType}, IDictionary<${index.fields[1].classType}, IList<${name}>>>();

        <#elseif index.unique && index.fields?size==3>
        ${tab}private static volatile IDictionary<${index.fields[0].classType}, IDictionary<${index.fields[1].classType}, IDictionary<${index.fields[2].classType}, ${name}>>> _${index.name}Configs = new Dictionary<${index.fields[0].classType}, IDictionary<${index.fields[1].classType}, IDictionary<${index.fields[2].classType}, ${name}>>>();

        <#elseif index.normal && index.fields?size==3>
        ${tab}private static volatile IDictionary<${index.fields[0].classType}, IDictionary<${index.fields[1].classType}, IDictionary<${index.fields[2].classType}, IList<${name}>>>> _${index.name}Configs = new Dictionary<${index.fields[0].classType}, IDictionary<${index.fields[1].classType}, IDictionary<${index.fields[2].classType}, IList<${name}>>>>();

        </#if>
    </#list>
        ${tab}public static IList<${name}> GetConfigs() 
        ${tab}{
            ${tab}return _configs;
        ${tab}}

    <#list indexes as index>
        <#if index.unique && index.fields?size==1>
        ${tab}public static IDictionary<${index.fields[0].classType}, ${name}> Get${index.name?cap_first}Configs() 
        ${tab}{
            ${tab}return _${index.name}Configs;
        ${tab}}

        ${tab}public static ${name} GetBy${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name})
        ${tab}{
            ${tab}return _${index.name}Configs.ContainsKey(${index.fields[0].name}) ? _${index.name}Configs[${index.fields[0].name}] : null;
        ${tab}}

        <#elseif index.normal && index.fields?size==1>
        ${tab}public static IDictionary<${index.fields[0].classType}, IList<${name}>> Get${index.name?cap_first}Configs() 
        ${tab}{
            ${tab}return _${index.name}Configs;
        ${tab}}

        ${tab}public static IList<${name}> GetBy${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name})
        ${tab}{
            ${tab}return _${index.name}Configs.ContainsKey(${index.fields[0].name}) ? _${index.name}Configs[${index.fields[0].name}] : ImmutableList<${name}>.Empty;
        ${tab}}

        <#elseif index.unique && index.fields?size==2>
        ${tab}public static IDictionary<${index.fields[0].classType}, IDictionary<${index.fields[1].classType}, ${name}>> Get${index.name?cap_first}Configs() 
        ${tab}{
            ${tab}return _${index.name}Configs;
        ${tab}}

        ${tab}public static IDictionary<${index.fields[1].classType}, ${name}> GetBy${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name})
        ${tab}{
            ${tab}return _${index.name}Configs.ContainsKey(${index.fields[0].name}) ? _${index.name}Configs[${index.fields[0].name}] : ImmutableDictionary<${index.fields[1].classType}, ${name}>.Empty;
        ${tab}}

        ${tab}public static ${name} GetBy${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name}, ${index.fields[1].basicType} ${index.fields[1].name})
        ${tab}{
            ${tab}var ${index.name}Configs = GetBy${index.name?cap_first}(${index.fields[0].name});
            ${tab}return ${index.name}Configs.ContainsKey(${index.fields[0].name}) ? ${index.name}Configs[${index.fields[1].name}] : null;
        ${tab}}

        <#elseif index.normal && index.fields?size==2>
        ${tab}public static IDictionary<${index.fields[0].classType}, IDictionary<${index.fields[1].classType}, IList<${name}>>> Get${index.name?cap_first}Configs()
        ${tab}{
            ${tab}return _${index.name}Configs;
        ${tab}}

        ${tab}public static IDictionary<${index.fields[1].classType}, IList<${name}>> GetBy${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name})
        ${tab}{
            ${tab}return _${index.name}Configs.ContainsKey(${index.fields[0].name}) ? _${index.name}Configs[${index.fields[0].name}] : ImmutableDictionary<${index.fields[1].classType}, IList<${name}>>.Empty;
        ${tab}}

        ${tab}public static IList<${name}> GetBy${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name}, ${index.fields[1].basicType} ${index.fields[1].name})
        ${tab}{
            ${tab}var ${index.name}Configs = GetBy${index.name?cap_first}(${index.fields[0].name});
            ${tab}return ${index.name}Configs.ContainsKey(${index.fields[1].name}) ? ${index.name}Configs[${index.fields[1].name}] : ImmutableList<${name}>.Empty;
        ${tab}}

        <#elseif index.unique && index.fields?size==3>
        ${tab}public static IDictionary<${index.fields[0].classType}, IDictionary<${index.fields[1].classType}, IDictionary<${index.fields[2].classType}, ${name}>>> Get${index.name?cap_first}Configs()
        ${tab}{
            ${tab}return _${index.name}Configs;
        ${tab}}

        ${tab}public static IDictionary<${index.fields[1].classType}, IDictionary<${index.fields[2].classType}, ${name}>> GetBy${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name})
        ${tab}{
            ${tab}return _${index.name}Configs.ContainsKey(${index.fields[0].name}) ? _${index.name}Configs[${index.fields[0].name}] : ImmutableDictionary<${index.fields[1].classType}, IDictionary<${index.fields[2].classType}, ${name}>>.Empty;
        ${tab}}

        ${tab}public static IDictionary<${index.fields[2].classType}, ${name}> GetBy${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name}, ${index.fields[1].basicType} ${index.fields[1].name})
        ${tab}{
            ${tab}var ${index.name}Configs = GetBy${index.name?cap_first}(${index.fields[0].name});
            ${tab}return ${index.name}Configs.ContainsKey(${index.fields[1].name}) ? ${index.name}Configs[${index.fields[1].name}] : ImmutableDictionary<${index.fields[2].classType}, ${name}>.Empty;
        ${tab}}

        ${tab}public static ${name} GetBy${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name}, ${index.fields[1].basicType} ${index.fields[1].name}, ${index.fields[2].basicType} ${index.fields[2].name})
        ${tab}{
            ${tab}var ${index.name}Configs = GetBy${index.name?cap_first}(${index.fields[0].name}, ${index.fields[1].name});
            ${tab}return ${index.name}Configs.ContainsKey(${index.fields[2].name}) ? ${index.name}Configs[${index.fields[2].name}] : null;
        ${tab}}

        <#elseif index.normal && index.fields?size==3>
        ${tab}public static IDictionary<${index.fields[0].classType}, IDictionary<${index.fields[1].classType}, IDictionary<${index.fields[2].classType}, IList<${name}>>>> Get${index.name?cap_first}Configs()
        ${tab}{
            ${tab}return _${index.name}Configs;
        ${tab}}

        ${tab}public static IDictionary<${index.fields[1].classType}, IDictionary<${index.fields[2].classType}, IList<${name}>>> GetBy${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name})
        ${tab}{
            ${tab}return _${index.name}Configs.ContainsKey(${index.fields[0].name}) ? _${index.name}Configs[${index.fields[0].name}] : ImmutableDictionary<${index.fields[1].classType}, IDictionary<${index.fields[2].classType}, IList<${name}>>>.Empty;
        ${tab}}

        ${tab}public static IDictionary<${index.fields[2].classType}, IList<${name}>> GetBy${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name}, ${index.fields[1].basicType} ${index.fields[1].name})
        ${tab}{
            ${tab}var ${index.name}Configs = GetBy${index.name?cap_first}(${index.fields[0].name});
            ${tab}return ${index.name}Configs.ContainsKey(${index.fields[1].name}) ? ${index.name}Configs[${index.fields[1].name}] : ImmutableDictionary<int, IList<${name}>>.Empty;
        ${tab}}

        ${tab}public static IList<${name}> GetBy${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name}, ${index.fields[1].basicType} ${index.fields[1].name}, ${index.fields[2].basicType} ${index.fields[2].name})
        ${tab}{
            ${tab}var ${index.name}Configs = GetBy${index.name?cap_first}(${index.fields[0].name}, ${index.fields[1].name});
            ${tab}return ${index.name}Configs.ContainsKey(${index.fields[2].name}) ? ${index.name}Configs[${index.fields[2].name}] : ImmutableList<${name}>.Empty;
        ${tab}}

        </#if>
    </#list>

        ${tab}public static void Index(List<${name}> configs)
        ${tab}{
    <#list indexes as index>
        <#if index.unique && index.fields?size==1>
            ${tab}var ${index.name}Configs = new Dictionary<${index.fields[0].classType}, ${name}>();
        <#elseif index.normal && index.fields?size==1>
            ${tab}var ${index.name}Configs = new Dictionary<${index.fields[0].classType}, IList<${name}>>();
        <#elseif index.unique && index.fields?size==2>
            ${tab}var ${index.name}Configs = new Dictionary<${index.fields[0].classType}, IDictionary<${index.fields[1].classType}, ${name}>>();
        <#elseif index.normal && index.fields?size==2>
            ${tab}var ${index.name}Configs = new Dictionary<${index.fields[0].classType}, IDictionary<${index.fields[1].classType}, IList<${name}>>>();
        <#elseif index.unique && index.fields?size==3>
            ${tab}var ${index.name}Configs = new Dictionary<${index.fields[0].classType}, IDictionary<${index.fields[1].classType}, IDictionary<${index.fields[2].classType}, ${name}>>>();
        <#elseif index.normal && index.fields?size==3>
            ${tab}var ${index.name}Configs = new Dictionary<${index.fields[0].classType}, IDictionary<${index.fields[1].classType}, IDictionary<${index.fields[2].classType}, IList<${name}>>>>();
        </#if>
    </#list>

            ${tab}foreach (var config in configs)
            ${tab}{
    <#list indexes as index>
        <#if index.unique && index.fields?size==1>
                ${tab}${index.name}Configs[config.${index.fields[0].name?cap_first}] = config;

        <#elseif index.normal && index.fields?size==1>
                ${tab}if (!${index.name}Configs.ContainsKey(config.${index.fields[0].name?cap_first}))
                ${tab}{
                    ${tab}${index.name}Configs.Add(config.${index.fields[0].name?cap_first}, new List<${name}>());
                ${tab}}

                ${tab}${index.name}Configs[config.${index.fields[0].name?cap_first}].Add(config);

        <#elseif index.unique && index.fields?size==2>
                ${tab}if (!${index.name}Configs.ContainsKey(config.${index.fields[0].name?cap_first}))
                ${tab}{
                    ${tab}${index.name}Configs.Add(config.${index.fields[0].name?cap_first}, new Dictionary<${index.fields[1].type}, ${name}>());
                ${tab}}

                ${tab}${index.name}Configs[config.${index.fields[0].name?cap_first}][config.${index.fields[1].name?cap_first}] = config;

        <#elseif index.normal && index.fields?size==2>
                ${tab}if (!${index.name}Configs.ContainsKey(config.${index.fields[0].name?cap_first}))
                ${tab}{
                    ${tab}${index.name}Configs.Add(config.${index.fields[0].name?cap_first}, new Dictionary<${index.fields[1].type}, IList<${name}>>());
                ${tab}}

                ${tab}if (!${index.name}Configs[config.${index.fields[0].name?cap_first}].ContainsKey(config.${index.fields[1].name?cap_first}))
                ${tab}{
                    ${tab}${index.name}Configs[config.${index.fields[0].name?cap_first}][config.${index.fields[1].name?cap_first}] = new List<${name}>();
                ${tab}}

                ${tab}${index.name}Configs[config.${index.fields[0].name?cap_first}][config.${index.fields[1].name?cap_first}].Add(config);

        <#elseif index.unique && index.fields?size==3>
                ${tab}if (!${index.name}Configs.ContainsKey(config.${index.fields[0].name?cap_first}))
                ${tab}{
                    ${tab}${index.name}Configs.Add(config.${index.fields[0].name?cap_first}, new Dictionary<${index.fields[1].type}, IDictionary<${index.fields[2].type}, ${name}>>());
                ${tab}}

                ${tab}if (!${index.name}Configs[config.${index.fields[0].name?cap_first}].ContainsKey(config.${index.fields[2].name?cap_first}))
                ${tab}{
                    ${tab}${index.name}Configs[config.${index.fields[0].name?cap_first}].Add(config.${index.fields[1].name?cap_first}, new Dictionary<${index.fields[2].type}, ${name}>());
                ${tab}}

                ${tab}${index.name}Configs[config.${index.fields[0].name?cap_first}][config.${index.fields[1].name?cap_first}][config.${index.fields[2].name?cap_first}] = config;

        <#elseif index.normal && index.fields?size==3>
                ${tab}if (!${index.name}Configs.ContainsKey(config.${index.fields[0].name?cap_first}))
                ${tab}{
                    ${tab}${index.name}Configs.Add(config.${index.fields[0].name?cap_first}, new Dictionary<${index.fields[1].type}, IDictionary<${index.fields[2].type}, IList<${name}>>>());
                ${tab}}

                ${tab}if (!${index.name}Configs[config.${index.fields[0].name?cap_first}].ContainsKey(config.${index.fields[1].name?cap_first}))
                ${tab}{
                        ${tab}${index.name}Configs[config.${index.fields[0].name?cap_first}].Add(config.${index.fields[1].name?cap_first}, new Dictionary<${index.fields[2].type}, IList<${name}>>());
                ${tab}}

                ${tab}if (!${index.name}Configs[config.${index.fields[0].name?cap_first}][config.${index.fields[1].name?cap_first}].ContainsKey(config.${index.fields[2].name?cap_first}))
                ${tab}{
                        ${tab}${index.name}Configs[config.${index.fields[0].name?cap_first}][config.${index.fields[1].name?cap_first}].Add(config.${index.fields[2].name?cap_first}, new List<${name}>());
                ${tab}}

                ${tab}${index.name}Configs[config.${index.fields[0].name?cap_first}][config.${index.fields[1].name?cap_first}][config.${index.fields[2].name?cap_first}].Add(config);

        </#if>
        <#if index_index<indexes?size-1>

        </#if>
    </#list>
            ${tab}}

            ${tab}_configs = configs.ToImmutableList();
    <#list indexes as index>
            ${tab}_${index.name}Configs = ${index.name}Configs.ToImmutableDictionary();
    </#list>

        ${tab}}
    </#macro>

<#if definitionType ==6>
    <#if parent??>
        public<#if parentConfig.parent??> new</#if> static class self 
        {
        <@indexer tab="    "/>
        }
    <#else>
        <@indexer tab=""/>
    </#if>
</#if>
    }
}