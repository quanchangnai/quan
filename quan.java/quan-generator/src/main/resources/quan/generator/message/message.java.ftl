package ${getFullPackageName("java")};

import quan.message.*;
<#list imports?keys as import>
import ${import};
</#list>

/**
<#if comment !="">
 * ${comment}<br/>
</#if>
 * 代码自动生成，请勿手动修改
 */
public<#if kind ==9> abstract</#if> class ${name} extends <#if kind ==2>Bean<#elseif kind ==3 && header??>${header.name}<#else>Message</#if> {

<#if kind ==3>
    /**
     * 消息ID
     */
    public static final int ID = ${id?c};

</#if>
<#if kind ==9>
    <#assign fieldModifier = "protected">
<#else>
    <#assign fieldModifier = "private">
</#if>
<#list selfFields as field>
    <#if field.comment !="">
    //${field.comment}
    </#if>
    <#if field.type == "set" || field.type == "list">
    ${fieldModifier} ${field.basicType}<${field.classValueType}> ${field.name} = new ${field.classType}<>();
    <#elseif field.type == "map">
    ${fieldModifier} ${field.basicType}<${field.classKeyType}, ${field.classValueType}> ${field.name} = new ${field.classType}<>();
    <#elseif field.type == "string">
    ${fieldModifier} ${field.basicType} ${field.name} = "";
    <#elseif field.type == "bytes">
    ${fieldModifier} ${field.basicType} ${field.name} = new byte[0];
    <#elseif field.builtinType>
    ${fieldModifier} ${field.basicType} ${field.name};
    <#elseif !field.optional && !field.enumType>
    ${fieldModifier} ${field.classType} ${field.name} = new ${field.classType}();
    <#else>
    ${fieldModifier} ${field.classType} ${field.name};
    </#if>

</#list>
<#if kind !=9 && selfFields?size <= 5>
    public ${name}() {
    }

    public ${name}(<#rt/>
    <#list selfFields as field>
        <#if field.type == "set" || field.type == "list">
        ${field.basicType}<${field.classValueType}> ${field.name}<#t/>
        <#elseif field.type == "map">
        ${field.basicType}<${field.classKeyType}, ${field.classValueType}> ${field.name}<#t/>
        <#elseif field.builtinType>
        ${field.basicType} ${field.name}<#t/>
        <#else>
        ${field.classType} ${field.name}<#t/>
        </#if>
        <#if field?has_next>, </#if><#t/>
    </#list>
    ) {<#lt/>
    <#list selfFields as field>
        <#if field.type == "set" || field.type == "list">
        this.${field.name}.addAll(${field.name});
        <#elseif field.type == "map">
        this.${field.name}.putAll(${field.name});
        <#else>
        this.set${field.name?cap_first}(${field.name});
        </#if>
    </#list>
    }
</#if>

<#if kind ==3>
    /**
     * 消息ID
     */
    @Override
    public final int getId() {
        return ID;
    }

</#if>
<#if header??>
    <#list header.fields as field>
        <#if field.comment !="">
    /**
     * ${field.comment}
     */
        </#if>
    @Override
    public ${name} set${field.name?cap_first}(${field.basicType} ${field.name}) {
        super.set${field.name?cap_first}(${field.name});
        return this;
    }

    </#list>
</#if>
<#list selfFields as field>
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

    <#elseif field.builtinType>
    public ${field.basicType} get${field.name?cap_first}() {
        return ${field.name};
    }

    <#if field.comment !="">
    /**
     * ${field.comment}
     */
    </#if>
    public ${name} set${field.name?cap_first}(${field.basicType} ${field.name}) {
        <#if field.type == "string" || field.type == "bytes">
        Objects.requireNonNull(${field.name});<#if field.scale gt 0>, ${field.scale}</#if>
        <#elseif (field.type=="float"||field.type=="double") && field.scale gt 0>
        Buffer.checkScale(${field.name}, ${field.scale});
        </#if>
        this.${field.name} = ${field.name};
        return this;
    }

    <#else>
    public ${field.classType} get${field.name?cap_first}() {
        return ${field.name};
    }

    <#if field.comment !="">
    /**
     * ${field.comment}
     */
    </#if>
    public ${name} set${field.name?cap_first}(${field.classType} ${field.name}) {
        <#if !field.enumType && !field.optional>
        Objects.requireNonNull(${field.name});
        </#if>
        this.${field.name} = ${field.name};
        return this;
    }

    </#if>
</#list>
<#if kind ==3>
    @Override
    public ${name} create() {
        return new ${name}();
    }

</#if>
    @Override
    public void encode(Buffer buffer) {
        super.encode(buffer);

<#list selfFields as field>
    <#if field.ignore>
        <#continue/>
    </#if>
    <#if definedFieldId>
        buffer.writeTag(${field.tag});
    </#if>
    <#if field.type=="set" || field.type=="list">
        <#if definedFieldId>
        buffer.getTemp().writeInt(this.${field.name}.size());
        for (${field.classValueType} ${field.name}$Value : this.${field.name}) {
            <#if field.builtinValueType>
            buffer.getTemp().write${field.valueType?cap_first}(${field.name}$Value);
            <#else>
            ${field.name}$Value.encode(buffer.getTemp());
            </#if>
        }
        buffer.writeTemp();
        <#else>
            <#if field?index gt 0>

            </#if>
        buffer.writeInt(this.${field.name}.size());
        for (${field.classValueType} ${field.name}$Value : this.${field.name}) {
            <#if field.builtinValueType>
            buffer.write${field.valueType?cap_first}(${field.name}$Value);
            <#else>
            ${field.name}$Value.encode(buffer);
            </#if>
        }
            <#if field?has_next && !selfFields[field?index+1].collectionType && (selfFields[field?index+1].primitiveType|| selfFields[field?index+1].enumType || !selfFields[field?index+1].optional) >

            </#if>
        </#if>
    <#elseif field.type=="map">
        <#if definedFieldId>
        buffer.getTemp().writeInt(this.${field.name}.size());
        for (${field.classKeyType} ${field.name}$Key : this.${field.name}.keySet()) {
            buffer.getTemp().write${field.keyType?cap_first}(${field.name}$Key);
            <#if field.builtinValueType>
            buffer.getTemp().write${field.valueType?cap_first}(this.${field.name}.get(${field.name}$Key));
            <#else>
            this.${field.name}.get(${field.name}$Key).encode(buffer.getTemp());
            </#if>
        }
        buffer.writeTemp();
        <#else>
            <#if field?index gt 0>

            </#if>
        buffer.writeInt(this.${field.name}.size());
        for (${field.classKeyType} ${field.name}$Key : this.${field.name}.keySet()) {
            buffer.write${field.keyType?cap_first}(${field.name}$Key);
            <#if field.builtinValueType>
            buffer.write${field.valueType?cap_first}(this.${field.name}.get(${field.name}$Key));
            <#else>
            this.${field.name}.get(${field.name}$Key).encode(buffer);
            </#if>
        }
            <#if field?has_next && !selfFields[field?index+1].collectionType && (selfFields[field?index+1].primitiveType|| selfFields[field?index+1].enumType || !selfFields[field?index+1].optional) >

            </#if>
        </#if>
    <#elseif field.type=="float"||field.type=="double">
        buffer.write${field.type?cap_first}(this.${field.name}<#if field.scale gt 0>, ${field.scale}</#if>);
    <#elseif field.builtinType>
        buffer.write${field.type?cap_first}(this.${field.name});
    <#elseif field.enumType>
        buffer.writeInt(this.${field.name} == null ? 0 : this.${field.name}.value());
    <#elseif field.optional>
        <#if definedFieldId>
        buffer.getTemp().writeBool(this.${field.name} != null);
        if (this.${field.name} != null) {
            this.${field.name}.encode(buffer.getTemp());
        }
        buffer.writeTemp();
        <#else>
            <#if field?index gt 0>

            </#if>
        buffer.writeBool(this.${field.name} != null);
        if (this.${field.name} != null) {
            this.${field.name}.encode(buffer);
        }
            <#if field?has_next && !selfFields[field?index+1].collectionType && (selfFields[field?index+1].primitiveType || selfFields[field?index+1].enumType || !selfFields[field?index+1].optional) >

            </#if>
        </#if>
    <#else>
        <#if definedFieldId>
        this.${field.name}.encode(buffer.getTemp());
        buffer.writeTemp();
        <#else>
        this.${field.name}.encode(buffer);
        </#if>
    </#if>
    <#if definedFieldId>

    </#if>
</#list>
<#if definedFieldId>
        buffer.writeTag(0);
</#if>
    }

    @Override
    public void decode(Buffer buffer) {
        super.decode(buffer);

<#if definedFieldId>
        for (int tag = buffer.readTag(); tag != 0; tag = buffer.readTag()) {
            switch (tag) {
            <#list selfFields as field>
                <#if field.ignore><#continue/></#if>
                case ${field.tag}:
                <#if field.type=="set" || field.type=="list">
                    buffer.readInt();
                    int ${field.name}$Size = buffer.readInt();
                    for (int i = 0; i < ${field.name}$Size; i++) {     
                    <#if field.builtinValueType>
                        this.${field.name}.add(buffer.read${field.valueType?cap_first}());
                    <#else>
                        ${field.classValueType} ${field.name}$Value = new ${field.classValueType}();
                        ${field.name}$Value.decode(buffer);
                        this.${field.name}.add(${field.name}$Value);
                    </#if>
                    }
                <#elseif field.type=="map">
                    buffer.readInt();
                    int ${field.name}$Size = buffer.readInt();
                    for (int i = 0; i < ${field.name}$Size; i++) {
                    <#if field.builtinValueType>
                        this.${field.name}.put(buffer.read${field.keyType?cap_first}(), buffer.read${field.valueType?cap_first}());
                    <#else>
                        ${field.classKeyType} ${field.name}$Key = buffer.read${field.keyType?cap_first}();
                        ${field.classValueType} ${field.name}$Value = new ${field.classValueType}();
                        ${field.name}$Value.decode(buffer);
                        this.${field.name}.put(${field.name}$Key, ${field.name}$Value);
                    </#if>
                    }
                <#elseif field.type=="float"||field.type=="double">
                    this.${field.name} = buffer.read${field.type?cap_first}(<#if field.scale gt 0>${field.scale}</#if>);
                <#elseif field.builtinType>
                    this.${field.name} = buffer.read${field.type?cap_first}();
                <#elseif field.enumType>
                    this.${field.name} = ${field.type}.valueOf(buffer.readInt());
                <#elseif field.optional>
                    buffer.readInt();
                    if (buffer.readBool()) {
                        if (this.${field.name} == null) {
                            this.${field.name} = new ${field.classType}();
                        }
                        this.${field.name}.decode(buffer);
                    }
            <#else>
                    buffer.readInt();
                    this.${field.name}.decode(buffer);
            </#if>
                    break;
        </#list>
                default:
                    skipField(tag, buffer);
            }
        }
<#else>
<#list selfFields as field>
    <#if field.ignore>
        <#continue/>
    <#elseif field.type=="set" || field.type=="list">
        <#if field?index gt 0>

        </#if>
        int ${field.name}$Size = buffer.readInt();
        for (int i = 0; i < ${field.name}$Size; i++) {
        <#if field.builtinValueType>
            this.${field.name}.add(buffer.read${field.valueType?cap_first}());
        <#else>
            ${field.classValueType} ${field.name}$Value = new ${field.classValueType}();
            ${field.name}$Value.decode(buffer);
            this.${field.name}.add(${field.name}$Value);
        </#if>
        }
        <#if field?has_next && !selfFields[field?index+1].collectionType && (selfFields[field?index+1].primitiveType|| selfFields[field?index+1].enumType || !selfFields[field?index+1].optional) >

        </#if>
    <#elseif field.type=="map">
        <#if field?index gt 0>

        </#if>
        int ${field.name}$Size = buffer.readInt();
        for (int i = 0; i < ${field.name}$Size; i++) {
        <#if field.builtinValueType>
            this.${field.name}.put(buffer.read${field.keyType?cap_first}(), buffer.read${field.valueType?cap_first}());
        <#else>
            ${field.classKeyType} ${field.name}$Key = buffer.read${field.keyType?cap_first}();
            ${field.classValueType} ${field.name}$Value = new ${field.classValueType}();
            ${field.name}$Value.decode(buffer);
            this.${field.name}.put(${field.name}$Key, ${field.name}$Value);
        </#if>
        }
        <#if field?has_next && !selfFields[field?index+1].collectionType && (selfFields[field?index+1].primitiveType|| selfFields[field?index+1].enumType || !selfFields[field?index+1].optional) >

        </#if>
    <#elseif field.type=="float"||field.type=="double">
        this.${field.name} = buffer.read${field.type?cap_first}(<#if field.scale gt 0>${field.scale}</#if>);
    <#elseif field.builtinType>
        this.${field.name} = buffer.read${field.type?cap_first}();
    <#elseif field.enumType>
        this.${field.name} = ${field.type}.valueOf(buffer.readInt());
    <#elseif field.optional>
        <#if field?index gt 0>

        </#if>
        if (buffer.readBool()) {
            if (this.${field.name} == null) {
                this.${field.name} = new ${field.classType}();
            }
            this.${field.name}.decode(buffer);
        }
        <#if field?has_next && !selfFields[field?index+1].collectionType && (selfFields[field?index+1].primitiveType|| selfFields[field?index+1].enumType || !selfFields[field?index+1].optional) >

        </#if>
    <#else>
        this.${field.name}.decode(buffer);
    </#if>
</#list>
</#if>
    }

    @Override
    public String toString() {
        return "${name}{" +
        <#list fields as field>
                "<#rt>
            <#if field?index gt 0>
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
