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
 * Created by {@link quan.protocol.generator.JavaGenerator}
 */
public class ${name} extends <#if definitionType ==2>ProtoObject<#elseif definitionType ==3>Protocol</#if> {

<#if definitionType ==3>
    public static final int _ID = ${id};//协议id

    @Override
    public int getId() {
        return _ID;
    }

</#if>
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
    <#else>
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
        this.${field.name} = ${field.name};
    }

    </#if>
</#list>

    @Override
    public void serialize(VarIntBuffer buffer) throws IOException {
<#if definitionType==3>
        buffer.writeInt(_ID);
</#if>
<#list fields as field>
    <#if field.type=="set" || field.type=="list">
        buffer.writeInt(${field.name}.size());
        for (${field.basicValueType} ${field.name}Value : ${field.name}) {
        <#if field.valueBuiltInType>
            buffer.write${field.valueType?cap_first}(${field.name}Value);
        <#else>
            ${field.name}Value.serialize(buffer);
        </#if>
        }
    <#elseif field.type=="map">
        buffer.writeInt(${field.name}.size());
        for (${field.basicKeyType} ${field.name}Key : ${field.name}.keySet()) {
            buffer.write${field.keyType?cap_first}(${field.name}Key);
        <#if field.valueBuiltInType>
            buffer.write${field.valueType?cap_first}(${field.name}.get(${field.name}Key));
        <#else>
            ${field.name}.get(${field.name}Key).serialize(buffer);
        </#if>
        }
    <#elseif field.builtInType>
        buffer.write${field.type?cap_first}(${field.name});
    <#elseif field.enumType>
        buffer.writeInt(${field.name}.getValue());
    <#else>
        ${field.name}.serialize(buffer);
    </#if>
</#list>
    }

    @Override
    public void parse(VarIntBuffer buffer) throws IOException {
<#if definitionType==3>
        int _id = buffer.readInt();
        if (_id != _ID) {
            throw new IOException("协议ID不匹配,目标值：" + _ID + "，实际值：" + _id);
        }
</#if>
<#list fields as field>
    <#if field.type=="set" || field.type=="list">
        int ${field.name}Size = buffer.readInt();
        for (int i = 0; i < ${field.name}Size; i++) {
        <#if field.valueBuiltInType>
            ${field.name}.add(buffer.read${field.valueType?cap_first}());
        <#else>
            ${field.valueType} ${field.name}Value = new ${field.valueType}();
            ${field.name}Value.parse(buffer);
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
            ${field.name}Value.parse(buffer);
            ${field.name}.put(${field.name}Key, ${field.name}Value);
        </#if>
        }
    <#elseif field.builtInType>
        ${field.name} = buffer.read${field.type?cap_first}();
    <#elseif field.enumType>
        ${field.name} = ${field.type}.valueOf(buffer.readInt());
    <#else>
        ${field.name}.parse(buffer);
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
                <#lt>${field.name}='" + ${field.name}+ '\'' +
            <#elseif field.type == "bytes">
                <#lt>${field.name}=" + Arrays.toString(${field.name}) +
            <#else>
                <#lt>${field.name}=" + ${field.name} +
            </#if>
        </#list>
                '}';

    }

}
