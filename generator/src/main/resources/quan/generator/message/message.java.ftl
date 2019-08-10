package ${fullPackageName};

import java.util.*;
import java.io.IOException;
<#list imports as import>
import ${import};
</#list>

/**
<#if comment !="">
 * ${comment}<br/>
</#if>
 * Created by 自动生成
 */
public class ${name} extends <#if definitionType ==2>Bean<#elseif definitionType ==3>Message</#if> {

<#if definitionType ==3>
    /**
     * 消息ID
     */
    public static final int ID = ${id?c};

</#if>
<#list fields as field>
    <#if field.comment !="">
    //${field.comment}
    </#if>
    <#if field.type == "set" || field.type == "list">
    private ${field.classType}<${field.classValueType}> ${field.name} = new ${field.classType}<>();
    <#elseif field.type == "map">
    private ${field.classType}<${field.classKeyType}, ${field.classValueType}> ${field.name} = new ${field.classType}<>();
    <#elseif field.type == "string">
    private ${field.basicType} ${field.name} = "";
    <#elseif field.type == "bytes">
    private ${field.basicType} ${field.name} = new byte[0];
    <#elseif field.builtInType || field.enumType>
    private ${field.basicType} ${field.name};
    <#elseif !field.optional>
    private ${field.basicType} ${field.name} = new ${field.type}();
    <#else>
    private ${field.basicType} ${field.name};
    </#if>

</#list>
    public ${name}() {
<#if definitionType ==3>
        super(ID);
</#if>
    }

<#list fields as field>
    <#if field.comment !="">
    /**
     * ${field.comment}
     */
    </#if>
    <#if field.type == "list" || field.type == "set">
    public ${field.classType}<${field.classValueType}> get${field.name?cap_first}() {
        return ${field.name};
    }

    <#elseif field.type == "map">
    public ${field.classType}<${field.classKeyType}, ${field.classValueType}> get${field.name?cap_first}() {
        return ${field.name};
    }

    <#else>
    public ${field.basicType} get${field.name?cap_first}() {
        return ${field.name};
    }

    <#if field.comment !="">
    /**
     * ${field.comment}
     */
    </#if>
    public ${name} set${field.name?cap_first}(${field.basicType} ${field.name}) {
        <#if (!field.builtInType && !field.optional && !field.enumType) || field.type == "string" || field.type == "bytes">
        Objects.requireNonNull(${field.name});
        </#if>
        this.${field.name} = ${field.name};
        return this;
    }

    </#if>
</#list>
<#if definitionType ==3>
    @Override
    public ${name} create() {
        return new ${name}();
    }

</#if>
    @Override
    public void encode(Buffer buffer) throws IOException {
        super.encode(buffer);

<#list fields as field>
    <#if field.type=="set" || field.type=="list">
        <#if field_index gt 0>

        </#if>
        buffer.writeInt(${field.name}.size());
        for (${field.basicValueType} $${field.name}$Value : ${field.name}) {
        <#if field.builtInValueType>
            buffer.write${field.valueType?cap_first}($${field.name}$Value);
        <#else>
            $${field.name}$Value.encode(buffer);
        </#if>
        }
        <#if field_has_next && !fields[field_index+1].collectionType && (fields[field_index+1].primitiveType || !fields[field_index+1].optional) >

        </#if>
    <#elseif field.type=="map">
        <#if field_index gt 0>

        </#if>
        buffer.writeInt(${field.name}.size());
        for (${field.basicKeyType} $${field.name}$Key : ${field.name}.keySet()) {
            buffer.write${field.keyType?cap_first}($${field.name}$Key);
        <#if field.builtInValueType>
            buffer.write${field.valueType?cap_first}(${field.name}.get($${field.name}$Key));
        <#else>
            ${field.name}.get($${field.name}$Key).encode(buffer);
        </#if>
        }
        <#if field_has_next && !fields[field_index+1].collectionType && (fields[field_index+1].primitiveType || !fields[field_index+1].optional) >

        </#if>
    <#elseif field.builtInType>
        buffer.write${field.type?cap_first}(${field.name});
    <#elseif field.enumType>
        <#if field_index gt 0>

        </#if>
        if(${field.name} != null) {
            buffer.writeInt(${field.name}.getValue());
        }else {
            buffer.writeInt(0);
        }
        <#if field_has_next && !fields[field_index+1].collectionType && (fields[field_index+1].primitiveType || !fields[field_index+1].optional) >

        </#if>
    <#elseif field.optional>
        <#if field_index gt 0>

        </#if>
        buffer.writeBool(${field.name} != null);
        if (${field.name} != null) {
            ${field.name}.encode(buffer);
        }
        <#if field_has_next && !fields[field_index+1].collectionType && (fields[field_index+1].primitiveType || !fields[field_index+1].optional) >

        </#if>
    <#else>
        ${field.name}.encode(buffer);
    </#if>
</#list>
    }

    @Override
    public void decode(Buffer buffer) throws IOException {
        super.decode(buffer);

<#list fields as field>
    <#if field.type=="set" || field.type=="list">
        <#if field_index gt 0>

        </#if>
        int $${field.name}$Size = buffer.readInt();
        for (int i = 0; i < $${field.name}$Size; i++) {
        <#if field.builtInValueType>
            ${field.name}.add(buffer.read${field.valueType?cap_first}());
        <#else>
            ${field.valueType} $${field.name}$Value = new ${field.valueType}();
            $${field.name}$Value.decode(buffer);
            ${field.name}.add($${field.name}$Value);
        </#if>
        }
        <#if field_has_next && !fields[field_index+1].collectionType && (fields[field_index+1].primitiveType || !fields[field_index+1].optional) >

        </#if>
    <#elseif field.type=="map">
        <#if field_index gt 0>

        </#if>
        int $${field.name}$Size = buffer.readInt();
        for (int i = 0; i < $${field.name}$Size; i++) {
        <#if field.builtInValueType>
            ${field.name}.put(buffer.read${field.keyType?cap_first}(), buffer.read${field.valueType?cap_first}());
        <#else>
            ${field.basicKeyType} $${field.name}$Key = buffer.read${field.keyType?cap_first}();
            ${field.basicValueType} $${field.name}$Value = new ${field.valueType}();
            $${field.name}$Value.decode(buffer);
            ${field.name}.put($${field.name}$Key, $${field.name}$Value);
        </#if>
        }
        <#if field_has_next && !fields[field_index+1].collectionType && (fields[field_index+1].primitiveType || !fields[field_index+1].optional) >

        </#if>
    <#elseif field.builtInType>
        ${field.name} = buffer.read${field.type?cap_first}();
    <#elseif field.enumType>
        ${field.name} = ${field.type}.valueOf(buffer.readInt());
    <#elseif field.optional>
        <#if field_index gt 0>

        </#if>
        if (buffer.readBool()) {
            if (${field.name} == null) {
                ${field.name} = new ${field.type}();
            }
            ${field.name}.decode(buffer);
        }
        <#if field_has_next && !fields[field_index+1].collectionType && (fields[field_index+1].primitiveType || !fields[field_index+1].optional) >

        </#if>
    <#else>
        ${field.name}.decode(buffer);
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
            <#elseif field.type == "bytes">
                <#lt>${field.name}=" + Arrays.toString(${field.name}) +
            <#else>
                <#lt>${field.name}=" + ${field.name} +
            </#if>
        </#list>
                '}';

    }

}
