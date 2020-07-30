<#list imports as import>
using ${import};
    <#if !import?has_next>

    </#if>
</#list>
namespace ${getFullPackageName("cs")}
{
    /// <summary>
    <#if comment !="">
    /// ${comment}<br/>
    </#if>
    /// 自动生成，请勿修改
    /// </summary>
<#if useEnum && (valueField.type=="short" || valueField.type=="int"||valueField.type=="long")>
    public enum ${name}<#if valueField.type!="int">: ${valueField.type}</#if>
    {
    <#list rows?keys as key>
        <#if rows[key].right !="">
        /// <summary>
        /// ${rows[key].right}
        /// </summary>
        </#if>
        ${key?cap_first} = ${rows[key].left}<#if key?has_next>,</#if>
        <#if key?has_next>

        </#if>
    </#list>
     }
<#elseif useEnum>
    public class ${name}
    {
        private readonly string _key;

        private ${name}(string key)
        {
            _key = key;
        }

    <#if valueField.type=="map">
        public ${valueField.basicType}<${valueField.keyType},${valueField.keyType}> Value => ${configDefinition.name}.GetBy${keyField.name?cap_first}(_key).${valueField.name?cap_first};
    <#elseif valueField.type=="list" || valueField.type=="set">
        public ${valueField.basicType}<${valueField.classValueType}> Value => ${configDefinition.name}.GetBy${keyField.name?cap_first}(_key).${valueField.name?cap_first};
    <#else>
        public ${valueField.classType} Value => ${configDefinition.name}.GetBy${keyField.name?cap_first}(_key).${valueField.name?cap_first};
    </#if>


    <#list rows?keys as key>
        <#if rows[key].right !="">
        /// <summary>
        /// ${rows[key].right}
        /// </summary>
        </#if>
        public static readonly ${name} ${key?cap_first} = new ${name}("${key}");
        <#if key?has_next>

        </#if>
    </#list>
    }
<#else>
    public class ${name} 
    {
    <#list rows?keys as key>
        <#if rows[key].right !="">
        /// <summary>
        /// ${rows[key].right}
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
</#if>
}