package ${fullPackageName};

<#list imports as import>
import ${import};
    <#if !import?has_next>

    </#if>
</#list>
/**
<#if comment !="">
 * ${comment}<br/>
</#if>
 * 自动生成
 */
public class ${name} {

<#list rows?keys as key>
    <#if rows[key] !="">
    /**
     * ${rows[key]}
     */
    </#if>
    <#if valueField.type=="map">
    public static ${valueField.basicType}<${valueField.keyType},${valueField.keyType}> ${key}() {
        return ${configDefinition.name}.getBy${keyField.name?cap_first}("${key}").get${valueField.name?cap_first}();
    }

    <#elseif valueField.type=="list" || valueField.type=="set">
    public static ${valueField.basicType}<${valueField.valueType}> ${key}() {
        return ${configDefinition.name}.getBy${keyField.name?cap_first}("${key}").get${valueField.name?cap_first}();
    }

    <#else>
    public static ${valueField.basicType} ${key}() {
        return ${configDefinition.name}.getBy${keyField.name?cap_first}("${key}").get${valueField.name?cap_first}();
    }

    </#if>
</#list>
}
