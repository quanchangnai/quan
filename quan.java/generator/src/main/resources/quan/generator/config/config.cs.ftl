<#if definitionType == 6>
using System.Collections.Generic;
using System.Collections.Immutable;
</#if>
using Newtonsoft.Json.Linq;
using Quan.Common;
<#if !(parentName??) || definitionType == 6>
using Quan.Config;
</#if>
<#list imports as import>
using ${import};
</#list>

namespace ${getFullPackageName("cs")}
{
    /// <summary>
<#if comment !="">
	/// ${comment}<br/>
</#if>
	/// 自动生成
	/// </summary>
    public class ${name} : <#if parentName??>${parentName}<#elseif definitionType ==2>Bean<#else>ConfigBase</#if>
    {
<#if !selfFields??>
    <#assign selfFields = fields>
</#if>
<#assign supportCs = supportLanguage("cs")>
<#list selfFields as field>
    <#if !(supportCs &&field.supportLanguage("cs"))>
        <#continue>
    </#if>
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

        public readonly string ${field.name?cap_first}_;
    <#else >
        public readonly ${field.basicType} ${field.name?cap_first};
    </#if>

</#list>

        public ${name}(JObject json) : base(json)
        {
<#list selfFields as field>
    <#if !(supportCs &&field.supportLanguage("cs"))>
        <#continue>
    </#if>
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
                    ${field.name}2 =${field.name}2.Add(${field.classValueType}.Create(${field.name}Value.Value<JObject>()));
                <#else>
                    ${field.name}2 =${field.name}2.Add(${field.name}Value.Value<${field.valueType}>());
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
                foreach (var ${field.name}KeyValue in ${field.name}1)
                {
                <#if field.beanValueType>
                    ${field.name}2 = ${field.name}2.Add(${field.classKeyType}.Parse(${field.name}KeyValue.Key), ${field.classValueType}.Create(${field.name}KeyValue.Value.Value<JObject>()));
                <#else>
                    ${field.name}2 = ${field.name}2.Add(${field.classKeyType}.Parse(${field.name}KeyValue.Key), ${field.name}KeyValue.Value.Value<${field.classValueType}>());
                </#if>
                }
            }
            ${field.name?cap_first} = ${field.name}2;
        <#if field_has_next && !selfFields[field_index+1].collectionType>

        </#if>
    <#elseif field.timeType>
            ${field.name?cap_first} = ToDateTime(json["${field.name}"]?.Value<long>() ?? default);
            ${field.name?cap_first}_ = json["${field.name}_"]?.Value<string>() ?? "";
    <#elseif field.type=="string">
            ${field.name?cap_first} = json["${field.name}"]?.Value<${field.type}>() ?? "";
    <#elseif field.builtinType>
            ${field.name?cap_first} = json["${field.name}"]?.Value<${field.type}>() ?? default;
    <#elseif field.enumType>
            ${field.name?cap_first} = (${field.type}) (json["${field.name}"]?.Value<int>() ?? default);
    <#else>
            ${field.name?cap_first} = json.ContainsKey("${field.name}") ? ${field.type}.Create(json["${field.name}"].Value<JObject>()) : null;
    </#if>
</#list>
        }

 <#if definitionType ==6>
        protected override ConfigBase Create(JObject json)
        {
            return new ${name}(json);
        }
<#else>
        public<#if parentName?? && parentName!=""> new</#if> static ${name} Create(JObject json)
        {
            <#if (children?size>0)>
            var clazz = json["class"].Value<string>() ?? "";
            switch (clazz) 
            {
                <#list children as child>
                case "${child.name}":
                    return ${child.name}.Create(json);
                </#list> 
                case "":
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

        public override string ToString()
        {
            return "${name}{" +
            <#list fields as field>
                <#if !(supportCs &&field.supportLanguage("cs"))>
                    <#continue>
                </#if>
                   "<#rt>
                <#if field_index gt 0>
                   <#lt>,<#rt>
                </#if>
                <#if field.type == "string">
                   <#lt>${field.name?cap_first}='" + ${field.name?cap_first} + '\'' +
                <#elseif field.timeType>
                    <#lt>${field.name?cap_first}='" + ${field.name?cap_first}_ + '\'' +
                <#else>
                   <#lt>${field.name?cap_first}=" + ${field.name?cap_first}.ToString2() +
                </#if>
            </#list>
                   '}';
        }
<#if definitionType ==6>

        // 所有${name}
        private static volatile IList<${name}> _configs = new List<${name}>();

    <#list indexes as index>
        <#if index.comment !="">
        // 索引:${index.comment}
        </#if>
        <#if index.unique && index.fields?size==1>
        private static volatile IDictionary<${index.fields[0].classType}, ${name}> _${index.name}Configs = new Dictionary<${index.fields[0].classType}, ${name}>();

        <#elseif index.normal && index.fields?size==1>
        private static volatile IDictionary<${index.fields[0].classType}, IList<${name}>> _${index.name}Configs = new Dictionary<${index.fields[0].classType}, IList<${name}>>();

        <#elseif index.unique && index.fields?size==2>
        private static volatile IDictionary<${index.fields[0].classType}, IDictionary<${index.fields[1].classType}, ${name}>> _${index.name}Configs = new Dictionary<${index.fields[0].classType}, IDictionary<${index.fields[1].classType}, ${name}>>();

        <#elseif index.normal && index.fields?size==2>
        private static volatile IDictionary<${index.fields[0].classType}, IDictionary<${index.fields[1].classType}, IList<${name}>>> _${index.name}Configs = new Dictionary<${index.fields[0].classType}, IDictionary<${index.fields[1].classType}, IList<${name}>>>();

        <#elseif index.unique && index.fields?size==3>
        private static volatile IDictionary<${index.fields[0].classType}, IDictionary<${index.fields[1].classType}, IDictionary<${index.fields[2].classType}, ${name}>>> _${index.name}Configs = new Dictionary<${index.fields[0].classType}, IDictionary<${index.fields[1].classType}, IDictionary<${index.fields[2].classType}, ${name}>>>();

        <#elseif index.normal && index.fields?size==3>
        private static volatile IDictionary<${index.fields[0].classType}, IDictionary<${index.fields[1].classType}, IDictionary<${index.fields[2].classType}, IList<${name}>>>> _${index.name}Configs = new Dictionary<${index.fields[0].classType}, IDictionary<${index.fields[1].classType}, IDictionary<${index.fields[2].classType}, IList<${name}>>>>();

        </#if>
    </#list>
        public <#if parentName??>new </#if>static IList<${name}> GetConfigs()
        {
            return _configs;
        }

    <#list indexes as index>
        <#assign newMethod><#if !isSelfIndex(index)>new <#else></#if></#assign>
        <#if index.unique && index.fields?size==1>
        public ${newMethod}static IDictionary<${index.fields[0].classType}, ${name}> Get${index.name?cap_first}Configs()
        {
            return _${index.name}Configs;
        }

        public ${newMethod}static ${name} GetBy${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name})
        {
            _${index.name}Configs.TryGetValue(${index.fields[0].name}, out var result);
            return result;
        }

        <#elseif index.normal && index.fields?size==1>
        public ${newMethod}static IDictionary<${index.fields[0].classType}, IList<${name}>> Get${index.name?cap_first}Configs()
        {
            return _${index.name}Configs;
        }

        public ${newMethod}static IList<${name}> GetBy${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name})
        {
            _${index.name}Configs.TryGetValue(${index.fields[0].name}, out var result);
            return result ?? ImmutableList<${name}>.Empty;
        }

        <#elseif index.unique && index.fields?size==2>
        public ${newMethod}static IDictionary<${index.fields[0].classType}, IDictionary<${index.fields[1].classType}, ${name}>> Get${index.name?cap_first}Configs()
        {
            return _${index.name}Configs;
        }

        public ${newMethod}static IDictionary<${index.fields[1].classType}, ${name}> GetBy${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name})
        {
            _${index.name}Configs.TryGetValue(${index.fields[0].name}, out var result);
            return result ?? ImmutableDictionary<${index.fields[1].classType}, ${name}>.Empty;
        }

        public ${newMethod}static ${name} GetBy${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name}, ${index.fields[1].basicType} ${index.fields[1].name})
        {
            GetBy${index.name?cap_first}(${index.fields[0].name}).TryGetValue(${index.fields[1].name}, out var result);
            return result;
        }

        <#elseif index.normal && index.fields?size==2>
        public ${newMethod}static IDictionary<${index.fields[0].classType}, IDictionary<${index.fields[1].classType}, IList<${name}>>> Get${index.name?cap_first}Configs()
        {
            return _${index.name}Configs;
        }

        public ${newMethod}static IDictionary<${index.fields[1].classType}, IList<${name}>> GetBy${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name})
        {
            _${index.name}Configs.TryGetValue(${index.fields[0].name}, out var result);
            return result ?? ImmutableDictionary<${index.fields[1].classType}, IList<${name}>>.Empty;
        }

        public ${newMethod}static IList<${name}> GetBy${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name}, ${index.fields[1].basicType} ${index.fields[1].name})
        {
            GetBy${index.name?cap_first}(${index.fields[0].name}).TryGetValue(${index.fields[1].name}, out var result);
            return result ?? ImmutableList<${name}>.Empty;
        }

        <#elseif index.unique && index.fields?size==3>
        public ${newMethod}static IDictionary<${index.fields[0].classType}, IDictionary<${index.fields[1].classType}, IDictionary<${index.fields[2].classType}, ${name}>>> Get${index.name?cap_first}Configs()
        {
            return _${index.name}Configs;
        }

        public ${newMethod}static IDictionary<${index.fields[1].classType}, IDictionary<${index.fields[2].classType}, ${name}>> GetBy${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name})
        {
            _${index.name}Configs.TryGetValue(${index.fields[0].name}, out var result);
            return result ?? ImmutableDictionary<${index.fields[1].classType}, IDictionary<${index.fields[2].classType}, ${name}>>.Empty;
        }

        public ${newMethod}static IDictionary<${index.fields[2].classType}, ${name}> GetBy${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name}, ${index.fields[1].basicType} ${index.fields[1].name})
        {
            GetBy${index.name?cap_first}(${index.fields[0].name}).TryGetValue(${index.fields[1].name}, out var result);
            return result ?? ImmutableDictionary<${index.fields[2].classType}, ${name}>.Empty;
        }

        public  ${newMethod}static ${name} GetBy${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name}, ${index.fields[1].basicType} ${index.fields[1].name}, ${index.fields[2].basicType} ${index.fields[2].name})
        {
            GetBy${index.name?cap_first}(${index.fields[0].name}, ${index.fields[1].name}).TryGetValue(${index.fields[2].name}, out var result);
            return result;
        }

        <#elseif index.normal && index.fields?size==3>
        public ${newMethod}static IDictionary<${index.fields[0].classType}, IDictionary<${index.fields[1].classType}, IDictionary<${index.fields[2].classType}, IList<${name}>>>> Get${index.name?cap_first}Configs()
        {
            return _${index.name}Configs;
        }

        public ${newMethod}static IDictionary<${index.fields[1].classType}, IDictionary<${index.fields[2].classType}, IList<${name}>>> GetBy${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name})
        {
            _${index.name}Configs.TryGetValue(${index.fields[0].name}, out var result);
            return result ?? ImmutableDictionary<${index.fields[1].classType}, IDictionary<${index.fields[2].classType}, IList<${name}>>>.Empty;
        }

        public ${newMethod}static IDictionary<${index.fields[2].classType}, IList<${name}>> GetBy${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name}, ${index.fields[1].basicType} ${index.fields[1].name})
        {
            GetBy${index.name?cap_first}(${index.fields[0].name}).TryGetValue(${index.fields[1].name}, out var result);
            return result ?? ImmutableDictionary<${index.fields[2].classType}, IList<${name}>>.Empty;
        }

        public ${newMethod}static IList<${name}> GetBy${index.name?cap_first}(${index.fields[0].basicType} ${index.fields[0].name}, ${index.fields[1].basicType} ${index.fields[1].name}, ${index.fields[2].basicType} ${index.fields[2].name})
        {
            GetBy${index.name?cap_first}(${index.fields[0].name}, ${index.fields[1].name}).TryGetValue(${index.fields[2].name}, out var result);
            return result ?? ImmutableList<${name}>.Empty;
        }

        </#if>
    </#list>

        public static void Load(IList<${name}> configs)
        {
    <#list indexes as index>
        <#if index.unique && index.fields?size==1>
            IDictionary<${index.fields[0].classType}, ${name}> ${index.name}Configs = new Dictionary<${index.fields[0].classType}, ${name}>();
        <#elseif index.normal && index.fields?size==1>
            IDictionary<${index.fields[0].classType}, IList<${name}>> ${index.name}Configs = new Dictionary<${index.fields[0].classType}, IList<${name}>>();
        <#elseif index.unique && index.fields?size==2>
            IDictionary<${index.fields[0].classType}, IDictionary<${index.fields[1].classType}, ${name}>> ${index.name}Configs = new Dictionary<${index.fields[0].classType}, IDictionary<${index.fields[1].classType}, ${name}>>();
        <#elseif index.normal && index.fields?size==2>
            IDictionary<${index.fields[0].classType}, IDictionary<${index.fields[1].classType}, IList<${name}>>> ${index.name}Configs = new Dictionary<${index.fields[0].classType}, IDictionary<${index.fields[1].classType}, IList<${name}>>>();
        <#elseif index.unique && index.fields?size==3>
            IDictionary<${index.fields[0].classType}, IDictionary<${index.fields[1].classType}, IDictionary<${index.fields[2].classType}, ${name}>>> ${index.name}Configs = new Dictionary<${index.fields[0].classType}, IDictionary<${index.fields[1].classType}, IDictionary<${index.fields[2].classType}, ${name}>>>();
        <#elseif index.normal && index.fields?size==3>
            IDictionary<${index.fields[0].classType}, IDictionary<${index.fields[1].classType}, IDictionary<${index.fields[2].classType}, IList<${name}>>>> ${index.name}Configs = new Dictionary<${index.fields[0].classType}, IDictionary<${index.fields[1].classType}, IDictionary<${index.fields[2].classType}, IList<${name}>>>>();
        </#if>
    </#list>

            foreach (var config in configs)
            {
    <#list indexes as index>
        <#if index.fields?size==1>
                <#if parentName??>ConfigBase.</#if>Load(${index.name}Configs, config, config.${index.fields[0].name?cap_first});
        <#elseif index.fields?size==2>
                <#if parentName??>ConfigBase.</#if>Load(${index.name}Configs, config, config.${index.fields[0].name?cap_first}, config.${index.fields[1].name?cap_first});
        <#elseif index.fields?size==3>
                <#if parentName??>ConfigBase.</#if>Load(${index.name}Configs, config, config.${index.fields[0].name?cap_first}, config.${index.fields[1].name?cap_first}, config.${index.fields[2].name?cap_first});
        </#if>
    </#list>
            }

            configs = configs.ToImmutableList();
    <#list indexes as index>
            ${index.name}Configs = ToImmutableDictionary(${index.name}Configs);
    </#list>

            _configs = configs;
    <#list indexes as index>
            _${index.name}Configs = ${index.name}Configs;
    </#list>
        }
</#if>
    }
}