<#if packageName !=".">package ${packageName};

</#if>
<#list imports as import>
import ${import};
<#if !import_has_next>

</#if>
</#list>
/**
<#if comment !="">
 * ${comment}<br/>
</#if>
 * Created by 自动生成
 */
public class ${name} extends <#if definitionType ==2>Bean<#elseif definitionType ==3>Message</#if> {

<#list fields as field>
    <#if field.comment !="">//${field.comment}</#if>
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
        super(${id});
</#if>
    }

<#list fields as field>
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
        <#if field_index gt 0 && !fields[field_index-1].optional && !fields[field_index-1].collectionType >

        </#if>
        buffer.writeInt(${field.name}.size());
        for (${field.basicValueType} _${field.name}_Value : ${field.name}) {
        <#if field.valueBuiltInType>
            buffer.write${field.valueType?cap_first}(_${field.name}_Value);
        <#else>
            _${field.name}_Value.encode(buffer);
        </#if>
        }

    <#elseif field.type=="map">
        <#if field_index gt 0 && !fields[field_index-1].optional && !fields[field_index-1].collectionType >

        </#if>
        buffer.writeInt(${field.name}.size());
        for (${field.basicKeyType} _${field.name}_Key : ${field.name}.keySet()) {
            buffer.write${field.keyType?cap_first}(_${field.name}_Key);
        <#if field.valueBuiltInType>
            buffer.write${field.valueType?cap_first}(${field.name}.get(_${field.name}_Key));
        <#else>
            ${field.name}.get(_${field.name}_Key).encode(buffer);
        </#if>
        }

    <#elseif field.builtInType>
        buffer.write${field.type?cap_first}(${field.name});
    <#elseif field.enumType>
        <#if field_index gt 0 && !fields[field_index-1].optional && !fields[field_index-1].collectionType >

        </#if>
        if(${field.name} != null) {
            buffer.writeInt(${field.name}.getValue());
        }else {
            buffer.writeInt(0);
        }

    <#elseif field.optional>
        <#if field_index gt 0 && !fields[field_index-1].optional && !fields[field_index-1].collectionType >

        </#if>
        buffer.writeBool(${field.name} != null);
        if (${field.name} != null) {
            ${field.name}.encode(buffer);
        }

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
        <#if field_index gt 0 && !fields[field_index-1].optional && !fields[field_index-1].collectionType >

        </#if>
        int _${field.name}_Size = buffer.readInt();
        for (int i = 0; i < _${field.name}_Size; i++) {
        <#if field.valueBuiltInType>
            ${field.name}.add(buffer.read${field.valueType?cap_first}());
        <#else>
            ${field.valueType} _${field.name}_Value = new ${field.valueType}();
            _${field.name}_Value.decode(buffer);
            ${field.name}.add(_${field.name}_Value);
        </#if>
        }

    <#elseif field.type=="map">
        <#if field_index gt 0 && !fields[field_index-1].optional && !fields[field_index-1].collectionType >

        </#if>
        int _${field.name}_Size = buffer.readInt();
        for (int i = 0; i < _${field.name}_Size; i++) {
        <#if field.valueBuiltInType>
            ${field.name}.put(buffer.read${field.keyType?cap_first}(), buffer.read${field.valueType?cap_first}());
        <#else>
            ${field.basicKeyType} _${field.name}_Key = buffer.read${field.keyType?cap_first}();
            ${field.basicValueType} _${field.name}_Value = new ${field.valueType}();
            _${field.name}_Value.decode(buffer);
            ${field.name}.put(_${field.name}_Key, _${field.name}_Value);
        </#if>
        }

    <#elseif field.builtInType>
        ${field.name} = buffer.read${field.type?cap_first}();
    <#elseif field.enumType>
        ${field.name} = ${field.type}.valueOf(buffer.readInt());
    <#elseif field.optional>
        <#if field_index gt 0 && !fields[field_index-1].optional && !fields[field_index-1].collectionType >

        </#if>
        if (buffer.readBool()) {
            if (${field.name} == null) {
                ${field.name} = new ${field.type}();
            }
            ${field.name}.decode(buffer);
        }

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
