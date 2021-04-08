<#list imports?keys as import>
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
    /// 代码自动生成，请勿手动修改
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
        ${key} = ${rows[key].left}<#if key?has_next>,</#if>
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
        public ${valueField.basicType}<${valueField.keyType},${valueField.classValueType}> Value => ${configDefinition.name}.GetBy${keyField.name?cap_first}(_key).${valueField.name};
    <#elseif valueField.type=="list" || valueField.type=="set">
        public ${valueField.basicType}<${valueField.classValueType}> Value => ${configDefinition.name}.GetBy${keyField.name?cap_first}(_key).${valueField.name};
    <#else>
        public ${valueField.classType} Value => ${configDefinition.name}.GetBy${keyField.name?cap_first}(_key).${valueField.name};
    </#if>


    <#list rows?keys as key>
        <#if rows[key].right !="">
        /// <summary>
        /// ${rows[key].right}
        /// </summary>
        </#if>
        public static readonly ${name} ${key} = new ${name}("${key}");
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
        public static ${valueField.basicType}<${valueField.keyType},${valueField.classValueType}> ${key} => ${configDefinition.name}.GetBy${keyField.name?cap_first}("${key}").${valueField.name};
        <#elseif valueField.type=="list" || valueField.type=="set">
        public static ${valueField.basicType}<${valueField.classValueType}> ${key} => ${configDefinition.name}.GetBy${keyField.name?cap_first}("${key}").${valueField.name};
        <#else>
        public static ${valueField.classType} ${key} => ${configDefinition.name}.GetBy${keyField.name?cap_first}("${key}").${valueField.name};
        </#if>
        <#if key?has_next>

        </#if>
    </#list>
    }
</#if>
}