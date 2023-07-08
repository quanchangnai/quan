package ${getFullPackageName("java")};

<#list imports?keys as import>
import ${import};
</#list>

<#assign String=dn("String") Objects=dn("Objects") Arrays=dn("Arrays") Override=dn("Override")>
<#assign Bean=dn("Bean") Message=dn("Message") CodedBuffer=dn("CodedBuffer") NumberUtils=dn("NumberUtils")>
/**
<#if comment !="">
 * ${comment}<br/>
</#if>
 * 代码自动生成，请勿手动修改
 */
public class ${name} extends <#if kind ==2>${Bean}<#else>${Message}</#if> {

<#if kind == 3>
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
    private final ${field.basicType}<${field.valueClassType}> ${field.name} = new ${field.classType}<>();
    <#elseif field.type == "map">
    private final ${field.basicType}<${field.keyClassType}, ${field.valueClassType}> ${field.name} = new ${field.classType}<>();
    <#elseif field.type == "string">
    private ${field.basicType} ${field.name}<#if !field.optional> = ""</#if>;
    <#elseif field.type == "bytes">
    private ${field.basicType} ${field.name}<#if !field.optional> = new byte[0]</#if>;
    <#elseif field.builtinType>
    private ${field.basicType} ${field.name};
    <#elseif !field.optional && !field.enumType>
    private ${field.classType} ${field.name} = new ${field.classType}();
    <#else>
    private ${field.classType} ${field.name};
    </#if>

</#list>
<#if fields?size <= 5>
    public ${name}() {
    }

    public ${name}(<#rt/>
    <#list fields as field>
        <#if field.type == "set" || field.type == "list">
        ${field.basicType}<${field.valueClassType}> ${field.name}<#t/>
        <#elseif field.type == "map">
        ${field.basicType}<${field.keyClassType}, ${field.valueClassType}> ${field.name}<#t/>
        <#elseif field.builtinType>
        ${field.basicType} ${field.name}<#t/>
        <#else>
        ${field.classType} ${field.name}<#t/>
        </#if>
        <#if field?has_next>, </#if><#t/>
    </#list>
    ) {<#lt/>
    <#list fields as field>
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
    @${Override}
    public final int getId() {
        return ID;
    }

</#if>
<#list fields as field>
    <#if field.comment !="">
    /**
     * ${field.comment}
     */
    </#if>
    <#if field.type == "list" || field.type == "set">
    public ${field.basicType}<${field.valueClassType}> get${field.name?cap_first}() {
        return ${field.name};
    }

    <#elseif field.type == "map">
    public ${field.basicType}<${field.keyClassType}, ${field.valueClassType}> get${field.name?cap_first}() {
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
        <#if (field.type=="float"||field.type=="double") && field.scale gt 0>
        ${CodedBuffer}.validateScale(${field.name}, ${field.scale});
        </#if>
        <#if field.min?? && field.max??>
        ${NumberUtils}.validateRange(${field.name}, ${field.min}, ${field.max}, "参数[${field.name}]");
        <#elseif field.min??>
        ${NumberUtils}.validateMin(${field.name}, ${field.min}, "参数[${field.name}]");
        <#elseif field.max??>
        ${NumberUtils}.validateMax(${field.name}, ${field.max}, "参数[${field.name}]");
        <#elseif (field.type == "string" || field.type == "bytes" || field.beanType) && !field.optional>
        ${Objects}.requireNonNull(${field.name},"参数[${field.name}]不能为空");
        </#if>
        this.${field.name} = ${field.name};
        return this;
    }

    </#if>
</#list>
<#if kind ==3>
    @${Override}
    public ${name} create() {
        return new ${name}();
    }

</#if>
    @${Override}
    public void encode(${CodedBuffer} buffer) {
        super.encode(buffer);
        
        validate();

<#list fields as field>
    <#if field.ignore>
        <#continue/>
    </#if>
    <#if compatible>
        <#if field.type=="set" || field.type=="list">
        if (!this.${field.name}.isEmpty()) {
            writeTag(buffer, ${field.tag});
            buffer.getTemp().writeInt(this.${field.name}.size());
            for (${field.valueClassType} ${field.name}$Value : this.${field.name}) {
                <#if field.builtinValueType>
                buffer.getTemp().write${field.valueType?cap_first}(${field.name}$Value);
                <#else>
                ${field.name}$Value.encode(buffer.getTemp());
                </#if>
            }
            buffer.writeTemp();
        }
        <#elseif field.type=="map">
        if (!this.${field.name}.isEmpty()) {
            writeTag(buffer, ${field.tag});
            buffer.getTemp().writeInt(this.${field.name}.size());
            for (${field.keyClassType} ${field.name}$Key : this.${field.name}.keySet()) {
                buffer.getTemp().write${field.keyType?cap_first}(${field.name}$Key);
                <#if field.builtinValueType>
                buffer.getTemp().write${field.valueType?cap_first}(this.${field.name}.get(${field.name}$Key));
                <#else>
                this.${field.name}.get(${field.name}$Key).encode(buffer.getTemp());
                </#if>
            }
            buffer.writeTemp();
        }
        <#elseif field.type=="float"||field.type=="double">
        if (this.${field.name} != 0) {
            writeTag(buffer, ${field.tag});
            buffer.write${field.type?cap_first}(this.${field.name}<#if field.scale gt 0>, ${field.scale}</#if>);
        }
        <#elseif field.numberType>
        if (this.${field.name} != 0) {
            writeTag(buffer, ${field.tag});
            buffer.write${field.type?cap_first}(this.${field.name});
        }
        <#elseif field.type=="bool">
        if (this.${field.name}) {
            writeTag(buffer, ${field.tag});
            buffer.write${field.type?cap_first}(this.${field.name});
        }
        <#elseif field.type=="string">
        <#if field.optional>
        if (this.${field.name} != null) {
        <#else>
        if (!this.${field.name}.isEmpty()) {
        </#if>
            writeTag(buffer, ${field.tag});
            buffer.write${field.type?cap_first}(this.${field.name});
        }
        <#elseif field.type=="bytes">
        <#if field.optional>
        if (this.${field.name} != null) {
        <#else>
        if (this.${field.name}.length > 0) {
        </#if>
            writeTag(buffer, ${field.tag});
            buffer.write${field.type?cap_first}(this.${field.name});
        }
        <#elseif field.enumType>
        if (this.${field.name} != null) {
            writeTag(buffer, ${field.tag});
            buffer.writeInt(this.${field.name}.value);
        }
        <#elseif field.optional>
        if (this.${field.name} != null) {
            writeTag(buffer, ${field.tag});
            this.${field.name}.encode(buffer.getTemp());
            buffer.writeTemp();
        }
        <#else>
        writeTag(buffer, ${field.tag});
        this.${field.name}.encode(buffer.getTemp());
        buffer.writeTemp();
        </#if>

    <#else>
        <#if field.type=="set" || field.type=="list">
            <#if field?index gt 0>

            </#if>
        buffer.writeInt(this.${field.name}.size());
        for (${field.valueClassType} ${field.name}$Value : this.${field.name}) {
            <#if field.builtinValueType>
            buffer.write${field.valueType?cap_first}(${field.name}$Value);
            <#else>
            ${field.name}$Value.encode(buffer);
            </#if>
        }
        <#if field?has_next && !fields[field?index+1].collectionType && (fields[field?index+1].primitiveType|| fields[field?index+1].enumType || !fields[field?index+1].optional) >

        </#if>
        <#elseif field.type=="map">
            <#if field?index gt 0>

            </#if>
        buffer.writeInt(this.${field.name}.size());
        for (${field.keyClassType} ${field.name}$Key : this.${field.name}.keySet()) {
            buffer.write${field.keyType?cap_first}(${field.name}$Key);
            <#if field.builtinValueType>
            buffer.write${field.valueType?cap_first}(this.${field.name}.get(${field.name}$Key));
            <#else>
            this.${field.name}.get(${field.name}$Key).encode(buffer);
            </#if>
        }
            <#if field?has_next && !fields[field?index+1].collectionType && (fields[field?index+1].primitiveType|| fields[field?index+1].enumType || !fields[field?index+1].optional) >

            </#if>
        <#elseif field.type=="float" || field.type=="double">
        buffer.write${field.type?cap_first}(this.${field.name}<#if field.scale gt 0>, ${field.scale}</#if>);
        <#elseif field.builtinType && !field.optional>
        buffer.write${field.type?cap_first}(this.${field.name});
        <#elseif field.enumType>
        buffer.writeInt(this.${field.name} == null ? 0 : this.${field.name}.value);
        <#elseif field.optional>
            <#if field?index gt 0>

            </#if>
        buffer.writeBool(this.${field.name} != null);
        if (this.${field.name} != null) {
            <#if field.type=="string" || field.type=="bytes">
            buffer.write${field.type?cap_first}(this.${field.name});
            <#else>
            this.${field.name}.encode(buffer);
            </#if>
        }
            <#if field?has_next && !fields[field?index+1].collectionType && (fields[field?index+1].primitiveType || fields[field?index+1].enumType || !fields[field?index+1].optional) >

            </#if>
        <#else>
        this.${field.name}.encode(buffer);
        </#if>
    </#if>
</#list>
<#if compatible>
        writeTag(buffer, 0);
</#if>
    }

    @${Override}
    public void decode(${CodedBuffer} buffer) {
        super.decode(buffer);

<#if compatible>
        for (int tag = readTag(buffer); tag != 0; tag = readTag(buffer)) {
            switch (tag) {
            <#list fields as field>
                <#if field.ignore><#continue/></#if>
                case ${field.tag}:
                <#if field.type=="set" || field.type=="list">
                    buffer.readInt();
                    int ${field.name}$Size = buffer.readInt();
                    for (int i = 0; i < ${field.name}$Size; i++) {
                    <#if field.builtinValueType>
                        this.${field.name}.add(buffer.read${field.valueType?cap_first}());
                    <#else>
                        ${field.valueClassType} ${field.name}$Value = new ${field.valueClassType}();
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
                        ${field.keyClassType} ${field.name}$Key = buffer.read${field.keyType?cap_first}();
                        ${field.valueClassType} ${field.name}$Value = new ${field.valueClassType}();
                        ${field.name}$Value.decode(buffer);
                        this.${field.name}.put(${field.name}$Key, ${field.name}$Value);
                    </#if>
                    }
                <#elseif field.type=="float"||field.type=="double">
                    this.${field.name} = buffer.read${field.type?cap_first}(<#if field.scale gt 0>${field.scale}</#if>);
                <#elseif field.numberType>
                    this.${field.name} = buffer.read${field.type?cap_first}();
                <#elseif field.builtinType>
                    this.${field.name} = buffer.read${field.type?cap_first}();
                <#elseif field.enumType>
                    this.${field.name} = ${field.type}.valueOf(buffer.readInt());
                <#elseif field.optional>
                    buffer.readInt();
                    if (this.${field.name} == null) {
                        this.${field.name} = new ${field.classType}();
                    }
                    this.${field.name}.decode(buffer);
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
<#list fields as field>
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
            ${field.valueClassType} ${field.name}$Value = new ${field.valueClassType}();
            ${field.name}$Value.decode(buffer);
            this.${field.name}.add(${field.name}$Value);
        </#if>
        }
        <#if field?has_next && !fields[field?index+1].collectionType && (fields[field?index+1].primitiveType|| fields[field?index+1].enumType || !fields[field?index+1].optional) >

        </#if>
    <#elseif field.type=="map">
        <#if field?index gt 0>

        </#if>
        int ${field.name}$Size = buffer.readInt();
        for (int i = 0; i < ${field.name}$Size; i++) {
        <#if field.builtinValueType>
            this.${field.name}.put(buffer.read${field.keyType?cap_first}(), buffer.read${field.valueType?cap_first}());
        <#else>
            ${field.keyClassType} ${field.name}$Key = buffer.read${field.keyType?cap_first}();
            ${field.valueClassType} ${field.name}$Value = new ${field.valueClassType}();
            ${field.name}$Value.decode(buffer);
            this.${field.name}.put(${field.name}$Key, ${field.name}$Value);
        </#if>
        }
        <#if field?has_next && !fields[field?index+1].collectionType && (fields[field?index+1].primitiveType|| fields[field?index+1].enumType || !fields[field?index+1].optional) >

        </#if>
    <#elseif field.type=="float"||field.type=="double">
        this.${field.name} = buffer.read${field.type?cap_first}(<#if field.scale gt 0>${field.scale}</#if>);
    <#elseif field.numberType>
        this.${field.name} = buffer.read${field.type?cap_first}();
    <#elseif field.builtinType && !field.optional>
        this.${field.name} = buffer.read${field.type?cap_first}();
    <#elseif field.enumType>
        this.${field.name} = ${field.type}.valueOf(buffer.readInt());
    <#elseif field.optional>
        <#if field?index gt 0>

        </#if>
        if (buffer.readBool()) {
        <#if field.type=="string" || field.type=="bytes">
           this.${field.name} = buffer.read${field.type?cap_first}();
        <#else>
            if (this.${field.name} == null) {
                this.${field.name} = new ${field.classType}();
            }
            this.${field.name}.decode(buffer);
        </#if>
        }
        <#if field?has_next && !fields[field?index+1].collectionType && (fields[field?index+1].primitiveType|| fields[field?index+1].enumType || !fields[field?index+1].optional) >

        </#if>
    <#else>
        this.${field.name}.decode(buffer);
    </#if>
</#list>
</#if>

        validate();
    }

    @Override
    public void validate() {
        super.validate();

        <#list fields as field>
            <#if (field.type=="float"||field.type=="double") && field.scale gt 0>
        ${CodedBuffer}.validateScale(${field.name}, ${field.scale}, "字段[${field.name}]");
            </#if>
            <#if field.min?? && field.max??>
        ${NumberUtils}.validateRange(${field.name}, ${field.min}, ${field.max}, "字段[${field.name}]");
            <#elseif field.min??>
        ${NumberUtils}.validateMin(${field.name}, ${field.min}, "字段[${field.name}]");
            <#elseif field.max??>
        ${NumberUtils}.validateMax(${field.name}, ${field.max});
            <#elseif (field.type == "string" || field.type == "bytes" || field.beanType) && !field.optional>
        ${Objects}.requireNonNull(${field.name}, "字段[${field.name}]不能为空");
            </#if>
        </#list>
    }

    @${Override}
    public ${String} toString() {
        return "${name}{" +
        <#if kind ==3>
                "_id=" + ID +
        </#if>
        <#list fields as field>
                "<#rt>
            <#if field?index gt 0 || kind ==3>
                <#lt>,<#rt>
            </#if>
            <#if field.type == "string">
                <#lt>${field.name}='" + ${field.name} + '\'' +
            <#elseif field.type == "bytes">
                <#lt>${field.name}=" + ${Arrays}.toString(${field.name}) +
            <#else>
                <#lt>${field.name}=" + ${field.name} +
            </#if>
        </#list>
                '}';

    }

}
