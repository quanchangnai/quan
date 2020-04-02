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

    public static class Codec extends EntityCodec<${name}> {

        public Codec(CodecRegistry registry) {
            super(registry);
        }

        @Override
        public ${name} decode(BsonReader reader, DecoderContext decoderContext) {
            reader.readStartDocument();
            <#if definitionType ==5>
                <#if idType == "Integer">
            ${name} value = new ${name}(reader.readInt32("_id")); 
                <#elseif idType == "Long">
            ${name} value = new ${name}(reader.readInt64("_id"));    
                <#else>
            ${name} value = new ${name}(reader.read${idType}("_id"));
                </#if>
            <#else> 
            ${name} value = new ${name}(); 
            </#if>

            while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                switch (reader.readName()) {
                    <#list fields as field>
                    case "${field.name}":
                        <#if field.type == "int" || field.enumType>
                        value.${field.name}.setValue(reader.readInt32());
                        <#elseif field.type == "long">
                        value.${field.name}.setValue(reader.readInt64());
                        <#elseif field.type == "bool">
                        value.${field.name}.setValue(reader.readBoolean());
                        <#elseif field.type == "short">
                        value.${field.name}.setValue((short) reader.readInt32());
                        <#elseif field.type == "float">
                        value.${field.name}.setValue((float) reader.readDouble());
                        <#elseif field.beanType>
                        value.${field.name}.setValue(decoderContext.decodeWithChildContext(registry.get(${field.type}.class), reader));
                        <#elseif field.type == "list" || field.type == "set">
                        reader.readStartArray();
                        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                            <#if field.valueType == "short">
                            value.${field.name}.add(reader.readInt32());
                            <#elseif field.valueType == "int">
                            value.${field.name}.add((short) reader.readInt32());
                            <#elseif field.valueType == "long">
                            value.${field.name}.add(reader.readInt64());
                            <#elseif field.valueType == "bool">
                            value.${field.name}.add(reader.readBoolean());
                            <#elseif field.valueType == "float">
                            value.${field.name}.add((float) reader.readDouble());
                            <#elseif field.beanValueType>
                            value.${field.name}.add(decoderContext.decodeWithChildContext(registry.get(${field.valueType}.class), reader));
                            <#else>
                            value.${field.name}.add(reader.read${field.valueType?cap_first}());
                            </#if>
                        }
                        reader.readEndArray();
                        <#elseif field.type == "map">
                        reader.readStartDocument();
                        
                        reader.readEndDocument(
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
                <#if idType == "Integer">
            writer.writeInt32("_id", value._getId());
                <#elseif idType == "Long">
            writer.writeInt64("_id", value._getId());
                <#else>
            writer.write${idType}("_id", value._getId());
                </#if>
            </#if>

            <#list fields as field>
                <#if field.type == "int" || field.enumType || field.type == "short">
            writer.writeInt32("${field.name}", value.${field.name}.getValue());
                <#elseif field.type == "long">
            writer.writeInt64("${field.name}", value.${field.name}.getValue());
                <#elseif field.type == "bool">
            writer.writeBoolean("${field.name}", value.${field.name}.getValue());      
                <#elseif field.type == "float">
            writer.writeDouble("${field.name}", value.${field.name}.getValue());     
                <#elseif field.beanType>
            if (value.${field.name}.getValue() != null) {
                writer.writeName("${field.name}");
                encoderContext.encodeWithChildContext(registry.get(${field.type}.class), writer, value.${field.name}.getValue());
            }
                <#elseif field.type == "map">
                <#elseif field.type == "list" || field.type == "set">
            writer.writeStartArray("${field.name}");
            for (${field.classValueType} ${field.name}Value : value.${field.name}.getValue()) {
                    <#if field.valueType == "int" || field.valueType == "short">
                writer.writeInt32(${field.name}Value);
                    <#elseif field.valueType == "long">
                writer.writeInt64(${field.name}Value);
                    <#elseif field.valueType == "bool">
                writer.writeBoolean(${field.name}Value);
                    <#elseif field.valueType == "float">
                writer.writeDouble(${field.name}Value);
                    <#elseif field.beanValueType>
                encoderContext.encodeWithChildContext(registry.get(${field.classValueType}.class), writer, ${field.name}Value);
                    <#else>
                writer.write${field.valueType?cap_first}(${field.name}Value);
                    </#if>
            }
            writer.writeEndArray();
                <#elseif field.type == "map">
                
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