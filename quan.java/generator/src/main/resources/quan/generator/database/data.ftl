package ${getFullPackageName("java")};

import java.util.*;
import org.bson.*;
import org.bson.codecs.*;
import org.bson.codecs.configuration.CodecRegistry;
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
<#if definitionType ==5>

    public static final String _NAME = "${underscoreName}";
</#if>
<#list fields as field>

    <#if field.comment !="">
    /**
     * ${field.comment}
     */
    </#if>
    public static final String ${field.underscoreName} = "${field.name}";
</#list>

<#list fields as field>
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
        <#if field.numberType>

        <#if field.comment !="">
    /**
     * ${field.comment}
     */
        </#if>
    public ${name} add${field.name?cap_first}(${field.basicType} ${field.name}) {
        set${field.name?cap_first}(<#if field.type=="short">(short) (</#if>get${field.name?cap_first}() + ${field.name}<#if field.type=="short">)</#if>);
        return this;
    }
        </#if>

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
    <#assign basicTypes={"byte":"Int32","bool":"Boolean","short":"Int32","int":"Int32","long":"Int64","float":"Double","double":"Double","string":"String"}/>
    <#assign classTypes={"Byte":"Int32","Boolean":"Boolean","Short":"Int32","Integer":"Int32","Long":"Int64","Float":"Double","Double":"Double","String":"String"}/>
    <#assign convertTypes={"byte":"byte","short":"short","float":"float"}/>

    public static class Codec implements org.bson.codecs.Codec<${name}> {

        private CodecRegistry registry;

        public Codec(CodecRegistry registry) {
            this.registry = registry;
        }

        public CodecRegistry getRegistry() {
            return registry;
        }

        @Override
        public ${name} decode(BsonReader reader, DecoderContext decoderContext) {
            reader.readStartDocument();
            <#if definitionType ==5>
            ${name} value = new ${name}(reader.read${classTypes[idType]}(${name}._ID));
            <#else>
            ${name} value = new ${name}(); 
            </#if>

            while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                switch (reader.readName()) {
                    <#list fields as field>
                        <#if field.ignore || definitionType == 5 && field.name == idName ><#continue/></#if>
                    case ${name}.${field.underscoreName}:
                        <#if field.enumType>
                        value.${field.name}.setValue(reader.readInt32());
                        <#elseif field.primitiveType>
                        value.${field.name}.setValue(<#if convertTypes[field.type]??>(${field.basicType}) </#if>reader.read${basicTypes[field.type]}());
                        <#elseif field.beanType>
                        value.${field.name}.setValue(decoderContext.decodeWithChildContext(registry.get(${field.type}.class), reader));
                        <#elseif field.type == "list" || field.type == "set">
                        reader.readStartArray();
                        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                            <#if field.primitiveValueType>
                            value.${field.name}.add(<#if convertTypes[field.type]??>(${field.basicType}) </#if>reader.read${basicTypes[field.valueType]}());
                            <#elseif field.beanValueType>
                            value.${field.name}.add(decoderContext.decodeWithChildContext(registry.get(${field.valueType}.class), reader));
                            <#else>
                            value.${field.name}.add(reader.read${field.valueType?cap_first}());
                            </#if>
                        }
                        reader.readEndArray();
                        <#elseif field.type == "map">
                        reader.readStartDocument();
                        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                            <#if field.primitiveValueType>
                            value.${field.name}.put(<#if convertTypes[field.keyType]??>(${field.basicValueType}) </#if>reader.read${basicTypes[field.keyType]}(), <#if convertTypes[field.valueType]??>(${field.basicValueType})</#if>reader.read${basicTypes[field.valueType]}());
                            <#else>
                            value.${field.name}.put(<#if convertTypes[field.keyType]??>(${field.basicValueType}) </#if>reader.read${basicTypes[field.keyType]}(), decoderContext.decodeWithChildContext(registry.get(${field.classValueType}.class), reader));
                            </#if>
                        }
                        reader.readEndDocument();
                        <#else>
                        value.${field.name}.setValue(reader.read${field.type?cap_first}());
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

        @Override
        public void encode(BsonWriter writer, ${name} value, EncoderContext encoderContext) {
            writer.writeStartDocument();
            <#if definitionType ==5>
            writer.write${classTypes[idType]}(${name}._ID, value._getId());
            </#if>

            <#list fields as field>
                <#if field.ignore || definitionType == 5 && field.name == idName ><#continue/></#if>
                <#if field.enumType>
            writer.writeInt32(${name}.${field.underscoreName}, value.${field.name}.getValue());
                <#elseif field.primitiveType>
            writer.write${basicTypes[field.type]}(${name}.${field.underscoreName}, value.${field.name}.getValue());
                <#elseif field.beanType>
                    <#if field_index gt 0 >

                    </#if>
            if (value.${field.name}.getValue() != null) {
                writer.writeName(${name}.${field.underscoreName});
                encoderContext.encodeWithChildContext(registry.get(${field.type}.class), writer, value.${field.name}.getValue());
            }
                    <#if field_has_next && fields[field_index+1].primitiveType >

                    </#if>
                <#elseif field.type == "list" || field.type == "set">
                    <#if field_index gt 0 >

                    </#if>
            if (!value.${field.name}.getValue().isEmpty()) {
                writer.writeStartArray(${name}.${field.underscoreName});
                for (${field.classValueType} ${field.name}Value : value.${field.name}.getValue()) {
                    <#if field.primitiveValueType>
                    writer.write${basicTypes[field.valueType]}(${field.name}Value);
                    <#elseif field.beanValueType>
                    encoderContext.encodeWithChildContext(registry.get(${field.classValueType}.class), writer, ${field.name}Value);
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
            if (!value.${field.name}.getValue().isEmpty()) {
                writer.writeStartDocument(${name}.${field.underscoreName});
                for (${field.classKeyType} ${field.name}Key : value.${field.name}.getValue().keySet()) {
                    writer.write${basicTypes[field.keyType]}(${field.name}Key);
                    <#if field.primitiveValueType>
                    writer.write${basicTypes[field.valueType]}(value.${field.name}.getValue().get(${field.name}Key));
                    <#elseif field.beanValueType>
                    encoderContext.encodeWithChildContext(registry.get(${field.classValueType}.class), writer, value.${field.name}.getValue().get(${field.name}Key));
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

        @Override
        public Class<${name}> getEncoderClass() {
            return ${name}.class;
        }

    }

}