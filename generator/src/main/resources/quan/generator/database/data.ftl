<#if packageName !=".">package ${packageName};

</#if>
<#list imports as import>
import ${import};
<#if !import_has_next>

</#if>
</#list>
/**
<#if comment !="">
 * ${comment}
</#if>
 * Created by 自动生成
 */
public class ${name} extends <#if definitionType ==2>Bean<#elseif definitionType ==5>Data<${primaryKeyType}></#if> {

<#list fields as field>
    <#if field.type == "set" || field.type == "list">
    private ${field.classType}<${field.classValueType}> ${field.name} = new ${field.classType}<>(getRoot());<#if field.comment !="">//${field.comment}</#if>
    <#elseif field.type == "map">
    private ${field.classType}<${field.classKeyType}, ${field.classValueType}> ${field.name} = new ${field.classType}<>(getRoot());<#if field.comment !="">//${field.comment}</#if>
    <#elseif field.type = "string">
    private BaseField<${field.classType}> ${field.name} = new BaseField<>("");<#if field.comment !="">//${field.comment}</#if>
    <#elseif field.type = "byte">
    private BaseField<${field.classType}> ${field.name} = new BaseField<>((byte) 0);<#if field.comment !="">//${field.comment}</#if>
    <#elseif field.type = "short">
    private BaseField<${field.classType}> ${field.name} = new BaseField<>((short) 0);<#if field.comment !="">//${field.comment}</#if>
    <#elseif field.type = "int">
    private BaseField<${field.classType}> ${field.name} = new BaseField<>(0);<#if field.comment !="">//${field.comment}</#if>
    <#elseif field.type = "long">
    private BaseField<${field.classType}> ${field.name} = new BaseField<>(0L);<#if field.comment !="">//${field.comment}</#if>
    <#elseif field.type = "float">
    private BaseField<${field.classType}> ${field.name} = new BaseField<>(0F);<#if field.comment !="">//${field.comment}</#if>
    <#elseif field.type = "double">
    private BaseField<${field.classType}> ${field.name} = new BaseField<>(0D);<#if field.comment !="">//${field.comment}</#if>
    <#elseif field.type = "bool">
    private BaseField<${field.classType}> ${field.name} = new BaseField<>(false);<#if field.comment !="">//${field.comment}</#if>
    <#else>
    private BeanField<${field.classType}> ${field.name} = new BeanField<>();<#if field.comment !="">//${field.comment}</#if>
    </#if>

</#list>
<#if definitionType ==5>
    @Override
    public ${primaryKeyType} primaryKey() {
        return get${primaryKeyName?cap_first}();
    }

</#if>
<#list fields as field>
    <#if field.type == "list" || field.type == "set">
    public ${field.basicType}<${field.classValueType}> get${field.name?cap_first}() {
        return ${field.name};
    }

    <#elseif field.type == "map">
    public ${field.basicType}<${field.classKeyType}, ${field.classValueType}> get${field.name?cap_first}() {
        return ${field.name};
    }

    <#else>
    public ${field.basicType} get${field.name?cap_first}() {
        return ${field.name}.getValue();
    }

    public void set${field.name?cap_first}(${field.basicType} ${field.name}) {
        this.${field.name}.setLogValue(${field.name}, getRoot());
    }

    </#if>
</#list>
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

}
