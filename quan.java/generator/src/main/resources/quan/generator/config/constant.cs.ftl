<#list imports as import>
using ${import};
    <#if !import?has_next>

    </#if>
</#list>
namespace ${fullPackageName}
{
    /// <summary>
    <#if comment !="">
    /// ${comment}<br/>
    </#if>
    /// 自动生成
    /// </summary>
    public class ${name} 
    {
<#list rows?keys as key>
    <#if rows[key] !="">
        /// <summary>
        /// ${rows[key]}
        /// </summary>
    </#if>
    <#if valueField.type=="map">
        public static ${valueField.basicType}<${valueField.keyType},${valueField.keyType}> ${key?cap_first} => ${configDefinition.name}.GetBy${keyField.name?cap_first}("${key}").${valueField.name?cap_first};
    <#elseif valueField.type=="list" || valueField.type=="set">
        public static ${valueField.basicType}<${valueField.valueType}> ${key?cap_first} => ${configDefinition.name}.GetBy${keyField.name?cap_first}("${key}").${valueField.name?cap_first};
    <#else>
        public static ${valueField.basicType} ${key?cap_first} => ${configDefinition.name}.GetBy${keyField.name?cap_first}("${key}").${valueField.name?cap_first};
    </#if>
    <#if key?has_next>

    </#if>
</#list>
    }
}
