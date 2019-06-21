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
public class ${name} extends <#if definitionType ==2>Bean<#elseif definitionType ==3>Message</#if> {

<#list fields as field>
    <#if field.type == "set" || field.type == "list">
    private ${field.classType}<${field.classValueType}> ${field.name};<#if field.comment !="">//${field.comment}</#if>
    <#elseif field.type == "map">
    private ${field.classType}<${field.classKeyType}, ${field.classValueType}> ${field.name};<#if field.comment !="">//${field.comment}</#if>
    <#else>
    private ${field.basicType} ${field.name};<#if field.comment !="">//${field.comment}</#if>
    </#if>
</#list>

    public ${name}() {
<#if definitionType ==3>
        super(${id});
</#if>
<#list fields as field>
    <#if field.type == "list" || field.type == "set" ||field.type == "map" >
        ${field.name} = new ${field.classType}<>();
    <#elseif field.type == "string">
        ${field.name} = <#if field.value?? && field.value!= "">"${field.value}"<#else>""</#if>;
    <#elseif field.type == "bytes">
        ${field.name} = new byte[0];
    <#elseif field.type == "byte">
        <#if field.value?? && field.value != "">
        ${field.name} = (byte) ${field.value};
        </#if>
    <#elseif field.type == "short">
        <#if field.value?? && field.value != "">
        ${field.name} = (short) ${field.value};
        </#if>
    <#elseif field.type == "long">
        <#if field.value?? && field.value != "">
        ${field.name} = ${field.value}L;
        </#if>
    <#elseif field.type == "float">
        <#if field.value?? && field.value != "">
        ${field.name} = ${field.value}F;
        </#if>
    <#elseif field.type == "double">
        <#if field.value?? && field.value != "">
        ${field.name} = ${field.value}D;
        </#if>
    <#elseif field.builtInType>
        <#if field.value?? && field.value != "">
        ${field.name} = ${field.value};
        </#if>
    <#elseif field.enumType>
        <#if field.value??>
        ${field.name} = ${field.type}.${field.value};
        </#if>
    <#elseif !field.optional>
        ${field.name} = new ${field.type}();
    </#if>
</#list>
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

    public void set${field.name?cap_first}(${field.basicType} ${field.name}) {
        <#if (!field.builtInType && !field.optional) || field.type == "string" || field.type == "bytes">
        if (${field.name} == null){
            throw new NullPointerException();
        }
        </#if>
        this.${field.name} = ${field.name};
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
        buffer.writeInt(${field.name}.size());
        for (${field.basicValueType} ${field.name}Value : ${field.name}) {
        <#if field.valueBuiltInType>
            buffer.write${field.valueType?cap_first}(${field.name}Value);
        <#else>
            ${field.name}Value.encode(buffer);
        </#if>
        }
    <#elseif field.type=="map">
        buffer.writeInt(${field.name}.size());
        for (${field.basicKeyType} ${field.name}Key : ${field.name}.keySet()) {
            buffer.write${field.keyType?cap_first}(${field.name}Key);
        <#if field.valueBuiltInType>
            buffer.write${field.valueType?cap_first}(${field.name}.get(${field.name}Key));
        <#else>
            ${field.name}.get(${field.name}Key).encode(buffer);
        </#if>
        }
    <#elseif field.builtInType>
        buffer.write${field.type?cap_first}(${field.name});
    <#elseif field.enumType>
        buffer.writeInt(${field.name}.getValue());
    <#elseif field.optional>
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
        int ${field.name}Size = buffer.readInt();
        for (int i = 0; i < ${field.name}Size; i++) {
        <#if field.valueBuiltInType>
            ${field.name}.add(buffer.read${field.valueType?cap_first}());
        <#else>
            ${field.valueType} ${field.name}Value = new ${field.valueType}();
            ${field.name}Value.decode(buffer);
            ${field.name}.add(${field.name}Value);
        </#if>
        }
    <#elseif field.type=="map">
        int ${field.name}Size = buffer.readInt();
        for (int i = 0; i < ${field.name}Size; i++) {
        <#if field.valueBuiltInType>
            ${field.name}.put(buffer.read${field.keyType?cap_first}(), buffer.read${field.valueType?cap_first}());
        <#else>
            ${field.basicKeyType} ${field.name}Key = buffer.read${field.keyType?cap_first}();
            ${field.basicValueType} ${field.name}Value = new ${field.valueType}();
            ${field.name}Value.decode(buffer);
            ${field.name}.put(${field.name}Key, ${field.name}Value);
        </#if>
        }
    <#elseif field.builtInType>
        ${field.name} = buffer.read${field.type?cap_first}();
    <#elseif field.enumType>
        ${field.name} = ${field.type}.valueOf(buffer.readInt());
    <#elseif field.optional>
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
