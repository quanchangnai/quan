package ${getFullPackageName("java")};

import java.util.*;
import org.bson.*;
import org.bson.codecs.*;
import org.bson.codecs.configuration.CodecRegistry;
import quan.database.*;
import quan.database.field.*;
<#list imports as import>
import ${import};
</#list>

/**
<#if comment !="">
 * ${comment}<br/>
</#if>
 * 自动生成
 */
public class ${name} extends <#if definitionType ==2>Entity<#elseif definitionType ==5>Data<${idField.classType}></#if> {
<#if definitionType ==5>

    /**
     * 对应的表名
     */
    public static final String _NAME = "${underscoreName}";

    /**
     * 索引
     */
    public static final List<Index> _INDEXES;

    static {
        List<Index> indexes = new ArrayList<>();
    <#list indexes as index>
        <#if index.fields?size==1>
        indexes.add(new Index("${index.name}", Collections.singletonList(${name}.${index.fields[0].underscoreName}), ${index.unique?c}));
        <#elseif index.fields?size==2>
        indexes.add(new Index("${index.name}", Arrays.asList(${name}.${index.fields[0].underscoreName}, ${name}.${index.fields[1].underscoreName}), ${index.unique?c}));
        <#elseif index.fields?size==3>
        indexes.add(new Index("${index.name}", Arrays.asList(${name}.${index.fields[0].underscoreName}, ${name}.${index.fields[1].underscoreName}, ${name}.${index.fields[2].underscoreName}), ${index.unique?c}));
        </#if>
    </#list>
        _INDEXES = Collections.unmodifiableList(indexes);
    }

</#if>
<#list fields as field>

    <#if field.comment !="">
    /**
     * ${field.comment}
     */
    </#if>
    public static final String ${field.underscoreName} = "${field.name}";
</#list>

<#assign root><#if definitionType ==5>this<#else>_getLogRoot()</#if></#assign>
<#list fields as field>

    <#if field.type == "set" || field.type == "list">
    private ${field.classType}<${field.classValueType}> ${field.name} = new ${field.classType}<>(${root});
    <#elseif field.type == "map">
    private ${field.classType}<${field.classKeyType}, ${field.classValueType}> ${field.name} = new ${field.classType}<>(${root});
    <#elseif field.enumType>
    private IntField ${field.name} = new IntField();
    <#elseif field.primitiveType>
    private ${field.type?cap_first}Field ${field.name} = new ${field.type?cap_first}Field();
    <#else>
    private EntityField<${field.classType}> ${field.name} = new EntityField<>();
    </#if>
</#list>

<#if definitionType ==5>

    <#if idField.type=="string">    
    public ${name}(String ${idName}) {
        Objects.requireNonNull(${idName}, "参数[${idName}]不能为空");
    <#else>
    public ${name}(${idField.type} ${idName}) {
    </#if>
        this.${idName}.setValue(${idName});
    }

    /**
     * 表名
     */
    @Override
    public String _name() {
        return _NAME;
    }

    /**
     * 主键(_id)
     */
    @Override
    public ${idField.classType} _id() {
        return ${idName}.getValue();
    }

    /**
     * 索引
     */
    @Override
    public List<Index> _indexes() {
        return _INDEXES;
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
        return ${field.name}.getDelegate();
    }

    <#elseif field.type == "map">
    public ${field.basicType}<${field.classKeyType}, ${field.classValueType}> get${field.name?cap_first}() {
        return ${field.name}.getDelegate();
    }

    <#elseif definitionType ==5 && field.name == idName>
    public ${field.basicType} get${field.name?cap_first}() {
        return ${field.name}.getValue();
    }

    <#elseif field.enumType>
    public ${field.type} get${field.name?cap_first}() {
        return ${field.type}.valueOf(${field.name}.getLogValue());
    }

    <#if field.comment !="">
    /**
     * ${field.comment}
     */
    </#if>
    public ${name} set${field.name?cap_first}(${field.basicType} ${field.name}) {
        this.${field.name}.setLogValue(${field.name}.value(), ${root});
        return this;
    }

    <#else>
    public ${field.basicType} get${field.name?cap_first}() {
        return ${field.name}.getLogValue();
    }

    <#if field.comment !="">
    /**
     * ${field.comment}
     */
    </#if>
    public ${name} set${field.name?cap_first}(${field.basicType} ${field.name}) {
        this.${field.name}.setLogValue(${field.name}, ${root});
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
<#if definitionType !=5>

    @Override
    protected void _setChildrenLogRoot(Data<?> root) {
    <#list fields as field>
        <#if field.collectionType>
        _setLogRoot(${field.name}, root);
        <#elseif field.beanType>
        _setLogRoot(${field.name}.getLogValue(), root);
        </#if>
    </#list>
    }
</#if>

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
    <#assign bsonTypes={"byte":"Int32","bool":"Boolean","short":"Int32","int":"Int32","long":"Int64","float":"Double","double":"Double","string":"String"}/>
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
            ${name} value = new ${name}(reader.read${bsonTypes[idField.type]}(${name}._ID));
            <#else>
            ${name} value = new ${name}(); 
            </#if>

            while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                switch (reader.readName()) {
                    <#list fields as field>
                        <#if field.ignore || definitionType == 5 && field.name == idName >
                            <#continue/>
                        </#if>
                    case ${name}.${field.underscoreName}:
                        <#if field.enumType>
                        value.${field.name}.setValue(reader.readInt32());
                        <#elseif field.primitiveType>
                        value.${field.name}.setValue(<#if convertTypes[field.type]??>(${field.basicType}) </#if>reader.read${bsonTypes[field.type]}());
                        <#elseif field.beanType>
                        value.${field.name}.setValue(decoderContext.decodeWithChildContext(registry.get(${field.type}.class), reader));
                        <#elseif field.type == "list" || field.type == "set">
                        reader.readStartArray();
                        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                            <#if field.primitiveValueType>
                            value.${field.name}.plus(<#if convertTypes[field.type]??>(${field.basicType}) </#if>reader.read${bsonTypes[field.valueType]}());
                            <#elseif field.beanValueType>
                            value.${field.name}.plus(decoderContext.decodeWithChildContext(registry.get(${field.valueType}.class), reader));
                            <#else>
                            value.${field.name}.plus(reader.read${field.valueType?cap_first}());
                            </#if>
                        }
                        reader.readEndArray();
                        <#elseif field.type == "map">
                        reader.readStartDocument();
                        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                            <#if field.primitiveValueType>
                            value.${field.name}.plus(<#if convertTypes[field.keyType]??>(${field.basicValueType}) </#if>reader.read${bsonTypes[field.keyType]}(), <#if convertTypes[field.valueType]??>(${field.basicValueType})</#if>reader.read${bsonTypes[field.valueType]}());
                            <#else>
                            value.${field.name}.plus(<#if convertTypes[field.keyType]??>(${field.basicValueType}) </#if>reader.read${bsonTypes[field.keyType]}(), decoderContext.decodeWithChildContext(registry.get(${field.classValueType}.class), reader));
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
            writer.write${bsonTypes[idField.type]}(${name}._ID, value._id());
            </#if>

            <#list fields as field>
                <#if field.ignore || definitionType == 5 && field.name == idName >
                    <#continue/>
                <#elseif field.enumType>
            writer.writeInt32(${name}.${field.underscoreName}, value.${field.name}.getValue());
                <#elseif field.primitiveType>
            writer.write${bsonTypes[field.type]}(${name}.${field.underscoreName}, value.${field.name}.getValue());
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
            if (!value.${field.name}.get${field.type?cap_first}().isEmpty()) {
                writer.writeStartArray(${name}.${field.underscoreName});
                for (${field.classValueType} ${field.name}Value : value.${field.name}.get${field.type?cap_first}()) {
                    <#if field.primitiveValueType>
                    writer.write${bsonTypes[field.valueType]}(${field.name}Value);
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
            if (!value.${field.name}.getMap().isEmpty()) {
                writer.writeStartDocument(${name}.${field.underscoreName});
                for (${field.classKeyType} ${field.name}Key : value.${field.name}.getMap().keySet()) {
                    writer.write${bsonTypes[field.keyType]}(${field.name}Key);
                    <#if field.primitiveValueType>
                    writer.write${bsonTypes[field.valueType]}(value.${field.name}.getMap().get(${field.name}Key));
                    <#elseif field.beanValueType>
                    encoderContext.encodeWithChildContext(registry.get(${field.classValueType}.class), writer, value.${field.name}.getMap().get(${field.name}Key));
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