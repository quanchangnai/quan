<#list imports?keys as import>
using ${import};
</#list>

<#assign JObject=getDependentName("JObject") JArray=getDependentName("JArray")>
<#assign IList=getDependentName("IList") List=getDependentName("List") ImmutableList=getDependentName("ImmutableList")>
<#assign IDictionary=getDependentName("IDictionary") Dictionary=getDependentName("Dictionary") ImmutableDictionary=getDependentName("ImmutableDictionary")>
<#assign Bean=getDependentName("Bean") ConfigBase=getDependentName("ConfigBase")>
namespace ${getFullPackageName("cs")}
{
    /// <summary>
<#if comment !="">
	/// ${comment}<br/>
</#if>
	/// 代码自动生成，请勿手动修改
	/// </summary>
    public class ${name} : <#if parentClassName??>${parentClassName}<#elseif kind ==2>${Bean}<#else>${ConfigBase}</#if>
    {
<#if !selfFields??>
    <#assign selfFields = fields>
</#if>
<#assign supportCs = isSupportLanguage("cs")>
<#list selfFields as field>
    <#if !(supportCs &&field.isSupportLanguage("cs"))>
        <#continue>
    </#if>
    <#if field.comment !="">
        /// <summary>
        /// ${field.comment}
        /// </summary>
    </#if>
    <#if field.type=="list">
        public readonly ${field.basicType}<${field.valueClassType}> ${field.name};
    <#elseif field.type=="set">
        public readonly ${field.basicType}<${field.valueClassType}> ${field.name};
    <#elseif field.type=="map">
        public readonly ${field.basicType}<${field.keyClassType}, ${field.valueClassType}> ${field.name};
    <#elseif  field.timeType>
        public readonly ${field.basicType} ${field.name};

        public readonly string ${field.name}_;
    <#elseif field.builtinType>
        public readonly ${field.basicType} ${field.name};
    <#else >
        public readonly ${field.classType} ${field.name};
    </#if>
    <#if field.simpleRef>

        <#if field.refIndex.unique>
        public ${field.refType} ${field.name}_Ref => <#rt/>
        <#else >
        public ${IList}<${field.refType}> ${field.name}_Ref => <#rt/>
        </#if>
        <#lt/>${field.refType}.Get${field.refIndex.suffix}(${field.name});
    </#if>

</#list>

        public ${name}(${JObject} json) : base(json)
        {
<#list selfFields as field>
    <#if !(supportCs &&field.isSupportLanguage("cs"))>
        <#continue>
    </#if>
    <#if field.type=="list" || field.type=="set">
        <#if field_index gt 0 >

        </#if>
            var ${field.name}1 = json["${field.name}"]?.Value<${JArray}>();
            var ${field.name}2 = ${getDependentName("Immutable${field.classType}")}<${field.valueClassType}>.Empty;
            if (${field.name}1 != null)
            {
                foreach (var ${field.name}Value in ${field.name}1)
                {
                <#if field.beanValueType>
                    ${field.name}2 =${field.name}2.Add(${field.valueClassType}.Create(${field.name}Value.Value<${JObject}>()));
                <#else>
                    ${field.name}2 =${field.name}2.Add(${field.name}Value.Value<${field.valueType}>());
                </#if>
                }
            }
            ${field.name} = ${field.name}2;
        <#if field_has_next && !selfFields[field_index+1].collectionType>

        </#if>
    <#elseif field.type=="map">
        <#if field_index gt 0 >

        </#if>
            var ${field.name}1 = json["${field.name}"]?.Value<${JObject}>();
            var ${field.name}2 = ${ImmutableDictionary}<${field.keyClassType}, ${field.valueClassType}>.Empty;
            if (${field.name}1 != null)
            {
                foreach (var ${field.name}KeyValue in ${field.name}1)
                {
                <#if field.beanValueType>
                    ${field.name}2 = ${field.name}2.Add(${field.keyClassType}.Parse(${field.name}KeyValue.Key), ${field.valueClassType}.Create(${field.name}KeyValue.Value.Value<${JObject}>()));
                <#else>
                    ${field.name}2 = ${field.name}2.Add(${field.keyClassType}.Parse(${field.name}KeyValue.Key), ${field.name}KeyValue.Value.Value<${field.valueClassType}>());
                </#if>
                }
            }
            ${field.name} = ${field.name}2;
        <#if field_has_next && !selfFields[field_index+1].collectionType>

        </#if>
    <#elseif field.timeType>
            ${field.name} = ToDateTime(json["${field.name}"]?.Value<long>() ?? default);
            ${field.name}_ = json["${field.name}_"]?.Value<string>() ?? "";
    <#elseif field.type=="string">
            ${field.name} = json["${field.name}"]?.Value<${field.type}>() ?? "";
    <#elseif field.builtinType>
            ${field.name} = json["${field.name}"]?.Value<${field.type}>() ?? default;
    <#elseif field.enumType>
            ${field.name} = (${field.classType}) (json["${field.name}"]?.Value<int>() ?? default);
    <#else>
            ${field.name} = json.ContainsKey("${field.name}") ? ${field.classType}.Create(json["${field.name}"].Value<${JObject}>()) : null;
    </#if>
</#list>
        }

 <#if kind ==6>
        protected override ${ConfigBase} Create(${JObject} json)
        {
            return new ${name}(json);
        }
<#else>
        public<#if parentClassName?? && parentClassName!=""> new</#if> static ${name} Create(${JObject} json)
        {
            <#if (dependentChildren?size>0)>
            var clazz = json["class"].Value<string>() ?? "";
            switch (clazz) 
            {
                <#list dependentChildren?keys as key>
                    <#assign childPackageName>
                        <#if key?contains('.')>${key?substring(0,key?last_index_of('.'))}<#else></#if><#t>
                    </#assign>
                    <#assign childClassName>
                        <#if key?contains('.')>${key?substring(key?last_index_of('.')+1)}<#else>${key}</#if><#t>
                    </#assign>
                <#if childPackageName == packageName>
                case "${childClassName}":
                </#if>
                case "${key}":
                    return ${dependentChildren[key]}.Create(json);
                </#list> 
                case "${name}":
                case "${longName}":
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
                <#if !(supportCs &&field.isSupportLanguage("cs"))>
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
                   <#lt>${field.name}=" + ${field.name}.ToString2() +
                </#if>
            </#list>
                   '}';
        }
<#if kind ==6>

        // 所有${name}
        private static volatile ${IList}<${name}> _configs = new ${List}<${name}>();

    <#list indexes as index>
        <#if index.comment !="">
        // 索引:${index.comment}
        </#if>
        <#if index.unique && index.fields?size==1>
        private static volatile ${IDictionary}<${index.fields[0].classType}, ${name}> _${index.name}Configs = new ${Dictionary}<${index.fields[0].classType}, ${name}>();

        <#elseif index.normal && index.fields?size==1>
        private static volatile ${IDictionary}<${index.fields[0].classType}, ${IList}<${name}>> _${index.name}Configs = new ${Dictionary}<${index.fields[0].classType}, ${IList}<${name}>>();

        <#elseif index.unique && index.fields?size==2>
        private static volatile ${IDictionary}<${index.fields[0].classType}, ${IDictionary}<${index.fields[1].classType}, ${name}>> _${index.name}Configs = new ${Dictionary}<${index.fields[0].classType}, ${IDictionary}<${index.fields[1].classType}, ${name}>>();

        <#elseif index.normal && index.fields?size==2>
        private static volatile ${IDictionary}<${index.fields[0].classType}, ${IDictionary}<${index.fields[1].classType}, ${IList}<${name}>>> _${index.name}Configs = new ${Dictionary}<${index.fields[0].classType}, ${IDictionary}<${index.fields[1].classType}, ${IList}<${name}>>>();

        <#elseif index.unique && index.fields?size==3>
        private static volatile ${IDictionary}<${index.fields[0].classType}, ${IDictionary}<${index.fields[1].classType}, ${IDictionary}<${index.fields[2].classType}, ${name}>>> _${index.name}Configs = new ${Dictionary}<${index.fields[0].classType}, ${IDictionary}<${index.fields[1].classType}, ${IDictionary}<${index.fields[2].classType}, ${name}>>>();

        <#elseif index.normal && index.fields?size==3>
        private static volatile ${IDictionary}<${index.fields[0].classType}, ${IDictionary}<${index.fields[1].classType}, ${IDictionary}<${index.fields[2].classType}, ${IList}<${name}>>>> _${index.name}Configs = new ${Dictionary}<${index.fields[0].classType}, ${IDictionary}<${index.fields[1].classType}, ${IDictionary}<${index.fields[2].classType}, ${IList}<${name}>>>>();

        </#if>
    </#list>
        public <#if parentClassName??>new </#if>static ${IList}<${name}> GetAll()
        {
            return _configs;
        }

    <#list indexes as index>
        <#assign newMethod><#if !isSelfIndex(index)>new <#else></#if></#assign>
        <#if index.unique && index.fields?size==1>
        public ${newMethod}static ${IDictionary}<${index.fields[0].classType}, ${name}> Get${index.name?cap_first}All()
        {
            return _${index.name}Configs;
        }

        public ${newMethod}static ${name} Get${index.suffix}(${index.fields[0].basicType} ${index.fields[0].name})
        {
            _${index.name}Configs.TryGetValue(${index.fields[0].name}, out var result);
            return result;
        }

        <#elseif index.normal && index.fields?size==1>
        public ${newMethod}static ${IDictionary}<${index.fields[0].classType}, ${IList}<${name}>> Get${index.name?cap_first}All()
        {
            return _${index.name}Configs;
        }

        public ${newMethod}static ${IList}<${name}> Get${index.suffix}(${index.fields[0].basicType} ${index.fields[0].name})
        {
            _${index.name}Configs.TryGetValue(${index.fields[0].name}, out var result);
            return result ?? ${ImmutableList}<${name}>.Empty;
        }

        <#elseif index.unique && index.fields?size==2>
        public ${newMethod}static ${IDictionary}<${index.fields[0].classType}, ${IDictionary}<${index.fields[1].classType}, ${name}>> Get${index.name?cap_first}All()
        {
            return _${index.name}Configs;
        }

        public ${newMethod}static ${IDictionary}<${index.fields[1].classType}, ${name}> Get${index.suffix}(${index.fields[0].basicType} ${index.fields[0].name})
        {
            _${index.name}Configs.TryGetValue(${index.fields[0].name}, out var result);
            return result ?? ${ImmutableDictionary}<${index.fields[1].classType}, ${name}>.Empty;
        }

        public ${newMethod}static ${name} Get${index.suffix}(${index.fields[0].basicType} ${index.fields[0].name}, ${index.fields[1].basicType} ${index.fields[1].name})
        {
            Get${index.suffix}(${index.fields[0].name}).TryGetValue(${index.fields[1].name}, out var result);
            return result;
        }

        <#elseif index.normal && index.fields?size==2>
        public ${newMethod}static ${IDictionary}<${index.fields[0].classType}, ${IDictionary}<${index.fields[1].classType}, ${IList}<${name}>>> Get${index.name?cap_first}All()
        {
            return _${index.name}Configs;
        }

        public ${newMethod}static ${IDictionary}<${index.fields[1].classType}, ${IList}<${name}>> Get${index.suffix}(${index.fields[0].basicType} ${index.fields[0].name})
        {
            _${index.name}Configs.TryGetValue(${index.fields[0].name}, out var result);
            return result ?? ${ImmutableDictionary}<${index.fields[1].classType}, ${IList}<${name}>>.Empty;
        }

        public ${newMethod}static ${IList}<${name}> Get${index.suffix}(${index.fields[0].basicType} ${index.fields[0].name}, ${index.fields[1].basicType} ${index.fields[1].name})
        {
            Get${index.suffix}(${index.fields[0].name}).TryGetValue(${index.fields[1].name}, out var result);
            return result ?? ${ImmutableList}<${name}>.Empty;
        }

        <#elseif index.unique && index.fields?size==3>
        public ${newMethod}static ${IDictionary}<${index.fields[0].classType}, ${IDictionary}<${index.fields[1].classType}, ${IDictionary}<${index.fields[2].classType}, ${name}>>> Get${index.name?cap_first}All()
        {
            return _${index.name}Configs;
        }

        public ${newMethod}static ${IDictionary}<${index.fields[1].classType}, ${IDictionary}<${index.fields[2].classType}, ${name}>> Get${index.suffix}(${index.fields[0].basicType} ${index.fields[0].name})
        {
            _${index.name}Configs.TryGetValue(${index.fields[0].name}, out var result);
            return result ?? ${ImmutableDictionary}<${index.fields[1].classType}, ${IDictionary}<${index.fields[2].classType}, ${name}>>.Empty;
        }

        public ${newMethod}static ${IDictionary}<${index.fields[2].classType}, ${name}> Get${index.suffix}(${index.fields[0].basicType} ${index.fields[0].name}, ${index.fields[1].basicType} ${index.fields[1].name})
        {
            Get${index.suffix}(${index.fields[0].name}).TryGetValue(${index.fields[1].name}, out var result);
            return result ?? ${ImmutableDictionary}<${index.fields[2].classType}, ${name}>.Empty;
        }

        public  ${newMethod}static ${name} Get${index.suffix}(${index.fields[0].basicType} ${index.fields[0].name}, ${index.fields[1].basicType} ${index.fields[1].name}, ${index.fields[2].basicType} ${index.fields[2].name})
        {
            Get${index.suffix}(${index.fields[0].name}, ${index.fields[1].name}).TryGetValue(${index.fields[2].name}, out var result);
            return result;
        }

        <#elseif index.normal && index.fields?size==3>
        public ${newMethod}static ${IDictionary}<${index.fields[0].classType}, ${IDictionary}<${index.fields[1].classType}, ${IDictionary}<${index.fields[2].classType}, ${IList}<${name}>>>> Get${index.name?cap_first}All()
        {
            return _${index.name}Configs;
        }

        public ${newMethod}static ${IDictionary}<${index.fields[1].classType}, ${IDictionary}<${index.fields[2].classType}, ${IList}<${name}>>> Get${index.suffix}(${index.fields[0].basicType} ${index.fields[0].name})
        {
            _${index.name}Configs.TryGetValue(${index.fields[0].name}, out var result);
            return result ?? ${ImmutableDictionary}<${index.fields[1].classType}, ${IDictionary}<${index.fields[2].classType}, ${IList}<${name}>>>.Empty;
        }

        public ${newMethod}static ${IDictionary}<${index.fields[2].classType}, ${IList}<${name}>> Get${index.suffix}(${index.fields[0].basicType} ${index.fields[0].name}, ${index.fields[1].basicType} ${index.fields[1].name})
        {
            Get${index.suffix}(${index.fields[0].name}).TryGetValue(${index.fields[1].name}, out var result);
            return result ?? ${ImmutableDictionary}<${index.fields[2].classType}, ${IList}<${name}>>.Empty;
        }

        public ${newMethod}static ${IList}<${name}> Get${index.suffix}(${index.fields[0].basicType} ${index.fields[0].name}, ${index.fields[1].basicType} ${index.fields[1].name}, ${index.fields[2].basicType} ${index.fields[2].name})
        {
            Get${index.suffix}(${index.fields[0].name}, ${index.fields[1].name}).TryGetValue(${index.fields[2].name}, out var result);
            return result ?? ${ImmutableList}<${name}>.Empty;
        }

        </#if>
    </#list>

        public static void Load(${IList}<${name}> configs)
        {
    <#list indexes as index>
        <#if index.unique && index.fields?size==1>
            ${IDictionary}<${index.fields[0].classType}, ${name}> ${index.name}Configs = new ${Dictionary}<${index.fields[0].classType}, ${name}>();
        <#elseif index.normal && index.fields?size==1>
            ${IDictionary}<${index.fields[0].classType}, ${IList}<${name}>> ${index.name}Configs = new ${Dictionary}<${index.fields[0].classType}, ${IList}<${name}>>();
        <#elseif index.unique && index.fields?size==2>
            ${IDictionary}<${index.fields[0].classType}, ${IDictionary}<${index.fields[1].classType}, ${name}>> ${index.name}Configs = new ${Dictionary}<${index.fields[0].classType}, ${IDictionary}<${index.fields[1].classType}, ${name}>>();
        <#elseif index.normal && index.fields?size==2>
            ${IDictionary}<${index.fields[0].classType}, ${IDictionary}<${index.fields[1].classType}, ${IList}<${name}>>> ${index.name}Configs = new ${Dictionary}<${index.fields[0].classType}, ${IDictionary}<${index.fields[1].classType}, ${IList}<${name}>>>();
        <#elseif index.unique && index.fields?size==3>
            ${IDictionary}<${index.fields[0].classType}, ${IDictionary}<${index.fields[1].classType}, ${IDictionary}<${index.fields[2].classType}, ${name}>>> ${index.name}Configs = new ${Dictionary}<${index.fields[0].classType}, ${IDictionary}<${index.fields[1].classType}, ${IDictionary}<${index.fields[2].classType}, ${name}>>>();
        <#elseif index.normal && index.fields?size==3>
            ${IDictionary}<${index.fields[0].classType}, ${IDictionary}<${index.fields[1].classType}, ${IDictionary}<${index.fields[2].classType}, ${IList}<${name}>>>> ${index.name}Configs = new ${Dictionary}<${index.fields[0].classType}, ${IDictionary}<${index.fields[1].classType}, ${IDictionary}<${index.fields[2].classType}, ${IList}<${name}>>>>();
        </#if>
    </#list>

            foreach (var config in configs)
            {
    <#list indexes as index>
        <#if index.fields?size==1>
                <#if parentClassName??>ConfigBase.</#if>Load(${index.name}Configs, config, config.${index.fields[0].name});
        <#elseif index.fields?size==2>
                <#if parentClassName??>ConfigBase.</#if>Load(${index.name}Configs, config, config.${index.fields[0].name}, config.${index.fields[1].name});
        <#elseif index.fields?size==3>
                <#if parentClassName??>ConfigBase.</#if>Load(${index.name}Configs, config, config.${index.fields[0].name}, config.${index.fields[1].name}, config.${index.fields[2].name});
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