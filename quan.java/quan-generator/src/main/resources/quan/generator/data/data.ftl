package ${getFullPackageName("java")};

<#list imports?keys as import>
import ${import};
</#list>

/**
<#if comment !="">
 * ${comment}<br/>
</#if>
 * 代码自动生成，请勿手动修改
 */
<#if kind ==5>
<#list indexes as index>
@${dn("Index")}(name = "${index.name}", fields = {<#rt/>
    <#list index.fields as field>
     <#lt/>${name}.${field.underscoreName}<#if field_has_next>, </#if><#rt/>
    </#list>
    <#lt/>}, type = ${dn("Index")}.Type.<#if index.text>TEXT<#elseif index.unique>UNIQUE<#else>NORMAL</#if>)
</#list>
</#if>
public class ${name} extends <#if kind ==2>${dn("Bean")}<#elseif kind ==5>${dn("Data")}<${idField.classType}></#if> {
<#if kind ==5>

    /**
     * 对应的表名
     */
    public static final ${dn("String")} _NAME = "${underscoreName}";

</#if>
<#list fields as field>

    <#if field.comment !="">
    /**
     * ${field.comment}
     */
    </#if>
    public static final ${dn("String")} ${field.underscoreName} = "${field.name}";
</#list>

<#assign owner><#if kind ==5>this<#else>_getLogOwner()</#if></#assign>
<#macro position index><#if kind ==5>${index + 1}<#else>_getLogPosition()</#if></#macro>
<#list fields as field>

    <#if field.type == "set" || field.type == "list">
    private final ${field.classType}<${field.valueClassType}> ${field.name} = new ${field.classType}<>(${owner}, <@position field?index/>);
    <#elseif field.type == "map">
    private final ${field.classType}<${field.keyClassType}, ${field.valueClassType}> ${field.name} = new ${field.classType}<>(${owner}, <@position field?index/>);
    <#elseif field.enumType || field.type="int">
    private final ${dn("BaseField")}<${dn("Integer")}> ${field.name} = new ${dn("BaseField")}<>(0);
    <#elseif field.type =="bool">
    private final ${dn("BaseField")}<${field.classType}> ${field.name} = new ${dn("BaseField")}<>(false);
    <#elseif field.type =="string">
    private final ${dn("BaseField")}<${field.classType}> ${field.name} = new ${dn("BaseField")}<>("");
    <#elseif field.numberType>
    private final ${dn("BaseField")}<${field.classType}> ${field.name} = new ${dn("BaseField")}<>((${field.basicType}) 0);
    <#else>
    private final ${dn("BeanField")}<${field.classType}> ${field.name} = new ${dn("BeanField")}<>();
    </#if>
</#list>

<#if kind ==5>
    private ${name}() {
    }

    public ${name}(${idField.type} ${idName}) {
    <#if idField.type=="string">    
        ${dn("Objects")}.requireNonNull(${idName}, "参数[${idName}]不能为空");
    </#if>
        this.set${idName?cap_first}(${idName});
    }

    /**
     * 主键
     */
    @Override
    public ${idField.classType} id() {
        return ${idName}.getValue();
    }

<#elseif selfFields?size<=5>
    public ${name}() {
    }
    
    public ${name}(<#rt/>
    <#list selfFields as field>
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

<#list fields as field>
    <#if field.comment !="">
    /**
     * ${field.comment}
     */
    </#if>
    <#if field.type == "list" || field.type == "set">
    public ${field.basicType}<${field.valueClassType}> get${field.name?cap_first}() {
        return ${field.name}.getDelegate();
    }

    <#elseif field.type == "map">
    public ${field.basicType}<${field.keyClassType}, ${field.valueClassType}> get${field.name?cap_first}() {
        return ${field.name}.getDelegate();
    }

    <#elseif field.enumType>
    public ${field.classType} get${field.name?cap_first}() {
        return ${field.classType}.valueOf(${field.name}.getValue());
    }

    <#if field.comment !="">
    /**
     * ${field.comment}
     */
    </#if>
    public ${name} set${field.name?cap_first}(${field.classType} ${field.name}) {
        this.${field.name}.setValue(${field.name}.value, ${owner}, <@position field?index/>);
        return this;
    }

    <#elseif field.builtinType>
    public ${field.basicType} get${field.name?cap_first}() {
        return ${field.name}.getValue();
    }

    <#if field.comment !="">
    /**
     * ${field.comment}
     */
    </#if>
    <#if !idName?? || field.name != idName>public<#else>private</#if> ${name} set${field.name?cap_first}(${field.basicType} ${field.name}) {
    <#if field.min?? && field.max??>
        ${dn("NumberUtils")}.validateRange(${field.name}, ${field.min}, ${field.max}, "参数[${field.name}]");
    <#elseif field.min??>
        ${dn("NumberUtils")}.validateMin(${field.name}, ${field.min}, "参数[${field.name}]");
    <#elseif field.max??>
        ${dn("NumberUtils")}.validateMax(${field.name}, ${field.max}, "参数[${field.name}]");
    </#if>
        this.${field.name}.setValue(${field.name}<#if !idName?? || field.name != idName>, ${owner}, <@position field?index/></#if>);
        return this;
    }

    <#else>
    public ${field.classType} get${field.name?cap_first}() {
        return ${field.name}.getValue();
    }

    <#if field.comment !="">
    /**
     * ${field.comment}
     */
    </#if>
    public ${name} set${field.name?cap_first}(${field.classType} ${field.name}) {
        this.${field.name}.setValue(${field.name}, ${owner}, <@position field?index/>);
        return this;
    }

    </#if>
</#list>
<#if kind !=5>

    @${dn("Override")}
    protected void _setChildrenLogOwner(${dn("Data")}<?> owner, int position) {
    <#list fields as field>
        <#if field.collectionType>
        _setLogOwner(${field.name}, owner, position);
        <#elseif field.beanType>
        _setLogOwner(${field.name}.getValue(), owner, positionr);
        </#if>
    </#list>
    }
 <#elseif fields?size gt 1>

    @${dn("Override")}
    protected ${dn("Map")}<${dn("String")}, ${dn("Object")}> _getUpdatePatch() {
        if (_updatedFields.isEmpty()) {
            return null;
        }

        ${dn("Transaction")} transaction = ${dn("Transaction")}.get();
        ${dn("Map")}<${dn("String")}, ${dn("Object")}> patch = new ${dn("HashMap")}<>();

    <#list fields as field>
        <#if field.ignore || field==idField>
            <#continue/>
        <#else>
        if (_updatedFields.get(${field?index+1}))
            patch.put(${field.underscoreName}, ${field.name}.<#if field.collectionType>getCurrent<#else>getValue</#if>(transaction));

       </#if>
    </#list>
        return patch;
    }

</#if>

    @${dn("Override")}
    public ${dn("String")} toString() {
        return "${name}{" +
        <#list fields as field>
                "<#rt>
            <#if field_index gt 0>
                <#lt>,<#rt>
            </#if>
            <#if field.type == "string">
                <#lt>${field.name}='" + ${field.name} + '\'' +
            <#elseif field.enumType>
                <#lt>${field.name}=" + ${field.classType}.valueOf(${field.name}.getValue()) +
            <#else>
                <#lt>${field.name}=" + ${field.name} +
            </#if>
        </#list>
                '}';

    }

    public static ${name} parseJson(${dn("String")} json) {
        return Entity.parseJson(${name}.class, json);
    }
    <#assign bsonTypes={"byte":"Int32","bool":"Boolean","short":"Int32","int":"Int32","long":"Int64","float":"Double","double":"Double","string":"String"}/>
    <#assign convertTypes={"byte":"byte","short":"short","float":"float"}/>

    public static class CodecImpl implements ${dn("Codec")}<${name}> {

        private final ${dn("CodecRegistry")} registry;

        public CodecImpl(${dn("CodecRegistry")} registry) {
            this.registry = registry;
        }

        public ${dn("CodecRegistry")} getRegistry() {
            return registry;
        }

        @${dn("Override")}
        public ${name} decode(${dn("BsonReader")} reader, ${dn("DecoderContext")} decoderContext) {
            reader.readStartDocument();
            ${name} value = new ${name}(); 
        
            while (reader.readBsonType() != ${dn("BsonType")}.END_OF_DOCUMENT) {
                switch (reader.readName()) {
                    <#list fields as field>
                        <#if field.ignore>
                            <#continue/>
                        </#if>
                        <#if kind == 5 && field.name == idName >
                    case ${name}._ID:
                        </#if>
                    case ${name}.${field.underscoreName}:
                        <#if field.enumType>
                        value.${field.name}.setValue(reader.readInt32());
                        <#elseif field.primitiveType>
                        value.${field.name}.setValue(<#if convertTypes[field.type]??>(${field.type}) </#if>reader.read${bsonTypes[field.type]}());
                        <#elseif field.beanType>
                        value.${field.name}.setValue(decoderContext.decodeWithChildContext(registry.get(${field.classType}.class), reader));
                        <#elseif field.type == "list" || field.type == "set">
                        reader.readStartArray();
                        while (reader.readBsonType() != ${dn("BsonType")}.END_OF_DOCUMENT) {
                            <#if field.primitiveValueType>
                            value.${field.name}.plus(<#if convertTypes[field.type]??>(${field.classType}) </#if>reader.read${bsonTypes[field.valueType]}());
                            <#elseif field.beanValueType>
                            value.${field.name}.plus(decoderContext.decodeWithChildContext(registry.get(${field.valueClassType}.class), reader));
                            <#else>
                            value.${field.name}.plus(reader.read${field.valueType?cap_first}());
                            </#if>
                        }
                        reader.readEndArray();
                        <#elseif field.type == "map">
                        reader.readStartDocument();
                        while (reader.readBsonType() != ${dn("BsonType")}.END_OF_DOCUMENT) {
                            <#if field.primitiveValueType>
                            value.${field.name}.plus(<#if convertTypes[field.keyType]??>(${field.valueBasicType}) </#if>${field.keyClassType}.valueOf(reader.readName()), <#if convertTypes[field.valueType]??>(${field.valueBasicType})</#if>reader.read${bsonTypes[field.valueType]}());
                            <#else>
                            value.${field.name}.plus(<#if convertTypes[field.keyType]??>(${field.valueBasicType}) </#if>${field.keyClassType}.valueOf(reader.readName()), decoderContext.decodeWithChildContext(registry.get(${field.valueClassType}.class), reader));
                            </#if>
                        }
                        reader.readEndDocument();
                        <#else>
                        value.${field.name}.setValue(reader.read${field.classType?cap_first}());
                        </#if>
                        break;
                    </#list>
                    default:
                        reader.skipValue();
                }
            }

            reader.readEndDocument();
            return value;
        }

        @${dn("Override")}
        public void encode(${dn("BsonWriter")} writer, ${name} value, ${dn("EncoderContext")} encoderContext) {
            ${dn("Transaction")} transaction = ${dn("Transaction")}.get();
            writer.writeStartDocument();
            <#if kind ==5>

            if (writer instanceof ${dn("JsonWriter")}) {
                writer.write${bsonTypes[idField.type]}(${name}.${idField.underscoreName}, value.${idField.name}.getValue(transaction));
            } else {
                writer.write${bsonTypes[idField.type]}(${name}._ID, value.${idField.name}.getValue(transaction));
            }
            </#if>

            <#list fields as field>
                <#if field.ignore || kind == 5 && field.name == idName >
                    <#continue/>
                <#elseif field.enumType>
            writer.writeInt32(${name}.${field.underscoreName}, value.${field.name}.getValue(transaction));
                <#elseif field.primitiveType>
            writer.write${bsonTypes[field.type]}(${name}.${field.underscoreName}, value.${field.name}.getValue(transaction));
                <#elseif field.beanType>
                    <#if field_index gt 0 >

                    </#if>
            ${field.classType} $${field.name} = value.${field.name}.getValue(transaction);
            if ($${field.name} != null) {
                writer.writeName(${name}.${field.underscoreName});
                encoderContext.encodeWithChildContext(registry.get(${field.classType}.class), writer, $${field.name});
            }
                    <#if field_has_next && fields[field_index+1].primitiveType >

                    </#if>
                <#elseif field.type == "list" || field.type == "set">
                    <#if field_index gt 0 >

                    </#if>
            ${dn("Collection")}<${field.valueClassType}> $${field.name} = value.${field.name}.getCurrent(transaction);
            if (!$${field.name}.isEmpty()) {
                writer.writeStartArray(${name}.${field.underscoreName});
                for (${field.valueClassType} ${field.name}Value : $${field.name}) {
                    <#if field.primitiveValueType>
                    writer.write${bsonTypes[field.valueType]}(${field.name}Value);
                    <#elseif field.beanValueType>
                    encoderContext.encodeWithChildContext(registry.get(${field.valueClassType}.class), writer, ${field.name}Value);
                    <#else>
                    writer.write${field.valueType?cap_first}(${field.name}Value);
                    </#if>
                }
                writer.writeEndArray();
            }
                    <#if field_has_next && fields[field_index+1].primitiveType >

                    </#if>
                <#elseif field.type == "map">
                    <#if field_index gt 0 >

                    </#if>
            ${dn("Map")}<${field.keyClassType}, ${field.valueClassType}> $${field.name} = value.${field.name}.getCurrent(transaction);
            if (!$${field.name}.isEmpty()) {
                writer.writeStartDocument(${name}.${field.underscoreName});
                for (Map.Entry<${field.keyClassType}, ${field.valueClassType}> ${field.name}Entry : $${field.name}.entrySet()) {
                    writer.writeName(String.valueOf(${field.name}Entry.getKey()));
                    <#if field.primitiveValueType>
                    writer.write${bsonTypes[field.valueType]}(${field.name}Entry.getValue());
                    <#elseif field.beanValueType>
                    encoderContext.encodeWithChildContext(registry.get(${field.valueClassType}.class), writer, ${field.name}Entry.getValue());
                    <#else>
                    writer.write${field.valueType?cap_first}(${field.name}Value);
                    </#if>
                }
                writer.writeEndDocument();
            }
                    <#if field_has_next && fields[field_index+1].primitiveType >

                    </#if>
                <#else>
            writer.write${field.type?cap_first}("${field.name}", value.${field.name}.getValue());
                </#if>
            </#list>

            writer.writeEndDocument();
        }
        
        @${dn("Override")}
        public ${dn("Class")}<${name}> getEncoderClass() {
            return ${name}.class;
        }

    }

}