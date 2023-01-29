package ${getFullPackageName("java")};

<#list imports?keys as import>
import ${import};
    <#if !import?has_next>

    </#if>
</#list>
/**
<#if comment !="">
 * ${comment}<br/>
</#if>
 * 代码自动生成，请勿手动修改
 */
<#if useEnum>
public enum ${name} {
    <#list rows?keys as key>

    <#if rows[key].right !="">
    /**
     * ${rows[key].right}
     */
    </#if>
    ${key}<#if key?has_next>,<#else>;</#if>
    <#else>
    ;
    </#list>
    <#if valueField.type=="map">

    public ${valueField.basicType}<${valueField.keyType},${valueField.valueClassType}> value() {
        return ${ownerDefinition.name}.getBy${keyField.name?cap_first}(name()").${valueField.name};
    }
    <#elseif valueField.type=="list" || valueField.type=="set">

    public ${valueField.basicType}<${valueField.valueClassType}> value() {
        return ${ownerDefinition.name}.getBy${keyField.name?cap_first}(name()).${valueField.name};
    }
    <#else>

    public ${valueField.basicType} value() {
        return ${ownerDefinition.name}.getBy${keyFieldIndex.name?cap_first}(name()).${valueField.name};
    }
    </#if>
<#else>
public class ${name} {
    <#list rows?keys as key>

    <#if rows[key].right !="">
    /**
     * ${rows[key].right}
     */
    </#if>
    <#if valueField.type=="map">
    public static ${valueField.basicType}<${valueField.keyType},${valueField.valueClassType}> ${key}() {
        return ${ownerDefinition.name}.getBy${keyField.name?cap_first}("${key}").${valueField.name};
    }
    <#elseif valueField.type=="list" || valueField.type=="set">
    public static ${valueField.basicType}<${valueField.valueClassType}> ${key}() {
        return ${ownerDefinition.name}.getBy${keyField.name?cap_first}("${key}").${valueField.name};
    }
    <#else>
    public static ${valueField.basicType} ${key}() {
        return ${ownerDefinition.name}.getBy${keyField.name?cap_first}("${key}").${valueField.name};
    }
    </#if>
    </#list>
</#if>

}
