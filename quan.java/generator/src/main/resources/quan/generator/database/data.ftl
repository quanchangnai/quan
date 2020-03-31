package ${getFullPackageName("java")};

import java.util.*;
import quan.database.*;
<#list imports as import>
import ${import};
</#list>

/**
<#if comment !="">
 * ${comment}<br/>
</#if>
 * 自动生成
 */
public class ${name} extends <#if definitionType ==2>Entity<#elseif definitionType ==5>Data<${idType}></#if> {

<#list fields as field>
    <#if field.comment !="">
    //${field.comment}
    </#if>
    <#if field.type == "set" || field.type == "list">
    private ${field.classType}<${field.classValueType}> ${field.name} = new ${field.classType}<>(_getRoot());

    <#elseif field.type == "map">
    private ${field.classType}<${field.classKeyType}, ${field.classValueType}> ${field.name} = new ${field.classType}<>(_getRoot());

    <#elseif field.type = "string">
    private BaseField<${field.classType}> ${field.name} = new BaseField<>("");

    <#elseif field.type = "short">
    private BaseField<${field.classType}> ${field.name} = new BaseField<>((short) 0);

    <#elseif field.type = "int" || field.enumType>
    private BaseField<Integer> ${field.name} = new BaseField<>(0);

    <#elseif field.type = "long">
    private BaseField<${field.classType}> ${field.name} = new BaseField<>(0L);

    <#elseif field.type = "float">
    private BaseField<${field.classType}> ${field.name} = new BaseField<>(0F);

    <#elseif field.type = "double">
    private BaseField<${field.classType}> ${field.name} = new BaseField<>(0D);

    <#elseif field.type = "bool">
    private BaseField<${field.classType}> ${field.name} = new BaseField<>(false);

    <#else>
    private EntityField<${field.classType}> ${field.name} = new EntityField<>();

    </#if>
</#list>
<#if definitionType ==5>

    public ${name}(${idType} ${idName}) {
        this.${idName}.setLogValue(${idName}, _getRoot());
    }
  
    /**
     * 主键
     */
    @Override
    public ${idType} _getId() {
        return ${idName}.getValue();
    }
</#if>

<#list fields as field>
    <#if field.comment !="">
    /**
     * ${field.comment}
     */
    </#if>
    <#if field.type == "list" || field.type == "set">
    public ${field.basicType}<${field.classValueType}> get${field.name?cap_first}() {
        return ${field.name};
    }

    <#elseif field.type == "map">
    public ${field.basicType}<${field.classKeyType}, ${field.classValueType}> get${field.name?cap_first}() {
        return ${field.name};
    }

    <#elseif definitionType ==5 && field.name == idName>
    public ${field.basicType} get${field.name?cap_first}() {
        return ${field.name}.getValue();
    }

    <#elseif field.enumType>
    public ${field.type} get${field.name?cap_first}() {
        return ${field.type}.valueOf(${field.name}.getValue());
    }

    <#if field.comment !="">
    /**
     * ${field.comment}
     */
    </#if>
    public ${name} set${field.name?cap_first}(${field.basicType} ${field.name}) {
        this.${field.name}.setLogValue(${field.name}.value(), _getRoot());
        return this;
    }

    <#else>
    public ${field.basicType} get${field.name?cap_first}() {
        return ${field.name}.getValue();
    }

    <#if field.comment !="">
    /**
     * ${field.comment}
     */
    </#if>
    public ${name} set${field.name?cap_first}(${field.basicType} ${field.name}) {
        this.${field.name}.setLogValue(${field.name}, _getRoot());
        return this;
    }

    </#if>
</#list>

    @Override
    protected void _setChildrenLogRoot(Data root) {
<#list fields as field>
    <#if field.collectionType>
        _setLogRoot(${field.name}, root);
    <#elseif !field.builtinType && !field.enumType>
        <#if field_index gt 0 && fields[field_index-1].collectionType>

        </#if>
        ${field.type} $${field.name} = this.${field.name}.getValue();
        if ($${field.name} != null) {
            _setLogRoot($${field.name}, root);
        }

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
            <#elseif field.enumType>
                <#lt>${field.name}=" + ${field.type}.valueOf(${field.name}.getValue()) +
            <#else>
                <#lt>${field.name}=" + ${field.name} +
            </#if>
        </#list>
                '}';

    }

}