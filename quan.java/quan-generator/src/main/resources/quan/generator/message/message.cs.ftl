using Quan.Utils;
using Quan.Message;
<#list imports?keys as import>
using ${import};
</#list>

namespace ${getFullPackageName("cs")}
{
    /// <summary>
<#if comment !="">
    /// ${comment}<br/>
</#if>
    /// 代码自动生成，请勿手动修改
    /// </summary>
    public class ${name} : <#if kind ==2>Bean<#else>MessageBase</#if>
    {
<#if kind ==3>
        /// <summary>
        /// 消息ID
        /// </summary>
        public override int Id => ${id?c};

</#if>
<#list selfFields as field>
    <#if field.type == "set" || field.type == "list">
        <#if field.comment !="">
        /// <summary>
        /// ${field.comment}
        /// </summary>
        </#if>
        public ${field.classType}<${field.valueClassType}> ${field.name} { get; } = new ${field.classType}<${field.valueClassType}>();

    <#elseif field.type == "map">
        <#if field.comment !="">
        /// <summary>
        /// ${field.comment}
        /// </summary>
        </#if>
        public Dictionary<${field.keyClassType}, ${field.valueClassType}> ${field.name} { get; } = new Dictionary<${field.keyClassType}, ${field.valueClassType}>();

    <#elseif field.type == "string" || field.type == "bytes">
        <#if field.type == "string">
        private string _${field.name}<#if !field.optional> = ""</#if>;
        <#else>
        private byte[] _${field.name}<#if !field.optional> = Array.Empty<byte>()</#if>;
        </#if>

        <#if field.comment !="">
        /// <summary>
        /// ${field.comment}
        /// </summary>
        </#if>
        public ${field.basicType} ${field.name}
        {
            get => _${field.name};
            set => _${field.name} = value<#if !field.optional> ?? throw new NullReferenceException()</#if>;
        }

    <#elseif field.min?? || field.max?? || field.scale gt 0 >
        private ${field.basicType} _${field.name};

        <#if field.comment !="">
        /// <summary>
        /// ${field.comment}
        /// </summary>
        </#if>
        public ${field.basicType} ${field.name}
        {
            get => _${field.name};
            set
            {
            <#if field.min?? && field.max??>
                CheckRange(value, ${field.min}, ${field.max});
            <#elseif field.min??>
                CheckMin(value, ${field.min});
            <#elseif field.max??>
                CheckMax(value, ${field.max});
            </#if>
            <#if field.scale gt 0>
                CodedBuffer.CheckScale(value, ${field.scale});
            </#if>
                _${field.name} = value;
            }
        }

    <#elseif field.builtinType>
        <#if field.comment !="">
        /// <summary>
        /// ${field.comment}
        /// </summary>
        </#if>
        public ${field.basicType} ${field.name} { get; set; }

    <#elseif !field.optional && !field.enumType>
        private ${field.classType} _${field.name} = new ${field.classType}();

        <#if field.comment !="">
        /// <summary>
        /// ${field.comment}
        /// </summary>
        </#if>
        public ${field.classType} ${field.name}
        {
            get => _${field.name};
            set => _${field.name} = value ?? throw new NullReferenceException();
        }

    <#else>
        <#if field.comment !="">
        /// <summary>
        /// ${field.comment}
        /// </summary>
        </#if>
        public ${field.classType} ${field.name} { get; set; }

    </#if>
</#list>

<#if kind ==3>
        public override MessageBase Create()
        {
            return new ${name}();
        }

</#if>
        public override void Encode(CodedBuffer buffer)
        {
            base.Encode(buffer);

<#list selfFields as field>
     <#if field.ignore>
        <#continue/>
    </#if>
    <#if field.name=="buffer">
        <#assign thisField1 = "this.${field.name}">
    <#else>
        <#assign thisField1 = field.name>
    </#if>
    <#if compatible>
        <#if field.type=="set" || field.type=="list">
            if (${thisField1}.Count > 0)
            {
                WriteTag(buffer, ${field.tag});
                var ${field.name}Buffer = new CodedBuffer();
                ${field.name}Buffer.WriteInt(${thisField1}.Count);
                foreach (var ${field.name}Value in ${thisField1})
                {
                <#if field.builtinValueType>
                    ${field.name}Buffer.Write${field.valueType?cap_first}(${field.name}Value);
                <#else>
                    ${field.name}Value.Encode(${field.name}Buffer);
                </#if>
                }
                buffer.WriteBuffer(${field.name}Buffer);
            }
        <#elseif field.type=="map">
            if (${thisField1}.Count > 0)
            {
                WriteTag(buffer, ${field.tag});
                var ${field.name}Buffer = new CodedBuffer();
                ${field.name}Buffer.WriteInt(${thisField1}.Count);
                foreach (var ${field.name}Key in ${thisField1}.Keys)
                {
                    ${field.name}Buffer.Write${field.keyType?cap_first}(${field.name}Key);
                <#if field.builtinValueType>
                    ${field.name}Buffer.Write${field.valueType?cap_first}(${thisField1}[${field.name}Key]);
                <#else>
                    ${thisField1}[${field.name}Key].Encode(${field.name}Buffer);
                </#if>
                }
                buffer.WriteBuffer(${field.name}Buffer);
            }
        <#elseif field.type=="float"||field.type=="double">
            if (${thisField1} != 0)
            {
                WriteTag(buffer, ${field.tag});
                buffer.Write${field.type?cap_first}(${thisField1}<#if field.scale gt 0>, ${field.scale}</#if>);
            }
        <#elseif field.numberType>
            if (${thisField1} != 0)
            {
                WriteTag(buffer, ${field.tag});
                buffer.Write${field.type?cap_first}(${thisField1});
            }
        <#elseif field.type=="bool">
            if (${thisField1})
            {
                WriteTag(buffer, ${field.tag});
                buffer.Write${field.type?cap_first}(${thisField1});
            }
        <#elseif field.type=="string">
            if (${thisField1}.Length > 0)
            {
                WriteTag(buffer, ${field.tag});
                buffer.Write${field.type?cap_first}(${thisField1});
            }
        <#elseif field.type=="bytes">
            if (${thisField1}.Length > 0)
            {
                WriteTag(buffer, ${field.tag});
                buffer.Write${field.type?cap_first}(${thisField1});
            }
        <#elseif field.enumType>
            if (${thisField1} != 0)
            {
                WriteTag(buffer, ${field.tag});
                buffer.WriteInt((int) ${thisField1});
            }
        <#elseif field.optional>
            if (${thisField1} != null)
            {
                WriteTag(buffer, ${field.tag});
                var ${field.name}Buffer = new CodedBuffer();
                ${thisField1}.Encode(${field.name}Buffer);
                buffer.WriteBuffer(${field.name}Buffer);
            }
        <#else>
            WriteTag(buffer, ${field.tag});
            var ${field.name}Buffer = new CodedBuffer();
            ${thisField1}.Encode(${field.name}Buffer);
            buffer.WriteBuffer(${field.name}Buffer);
        </#if>

    <#else>
        <#if field.type=="set" || field.type=="list">
            <#if field?index gt 0>

            </#if>
            buffer.WriteInt(${thisField1}.Count);
            foreach (var ${field.name}Value in ${thisField1})
            {
            <#if field.builtinValueType>
                buffer.Write${field.valueType?cap_first}(${field.name}Value);
            <#else>
                ${field.name}Value.Encode(buffer);
            </#if>
            }
            <#if field?has_next && !selfFields[field?index+1].collectionType && (selfFields[field?index+1].primitiveType || !selfFields[field?index+1].optional) >

            </#if>
        <#elseif field.type=="map">
            <#if field?index gt 0>

            </#if>
            buffer.WriteInt(${thisField1}.Count);
            foreach (var ${field.name}Key in ${thisField1}.Keys)
            {
                buffer.Write${field.keyType?cap_first}(${field.name}Key);
            <#if field.builtinValueType>
                buffer.Write${field.valueType?cap_first}(${thisField1}[${field.name}Key]);
            <#else>
                ${thisField1}[${field.name}Key].Encode(buffer);
            </#if>
            }
            <#if field?has_next && !selfFields[field?index+1].collectionType && (selfFields[field?index+1].primitiveType || !selfFields[field?index+1].optional) >

            </#if>
        <#elseif field.type=="float"||field.type=="double">
            buffer.Write${field.type?cap_first}(${thisField1}<#if field.scale gt 0>, ${field.scale}</#if>);
        <#elseif field.builtinType>
            buffer.Write${field.type?cap_first}(${thisField1});
        <#elseif field.enumType>
            buffer.WriteInt((int) ${thisField1});
        <#elseif field.optional>
            <#if field?index gt 0>

            </#if>
            buffer.WriteBool(${thisField1} != null);
            ${thisField1}?.Encode(buffer);
            <#if field?has_next && !selfFields[field?index+1].collectionType && (selfFields[field?index+1].primitiveType || !selfFields[field?index+1].optional) >

            </#if>
        <#else>
            ${thisField1}.Encode(buffer);
        </#if>
    </#if>
</#list>
<#if compatible>
            WriteTag(buffer,0);
</#if>
        }

        public override void Decode(CodedBuffer buffer)
        {
            base.Decode(buffer);

<#if compatible>
            for (var tag = ReadTag(buffer); tag != 0; tag = ReadTag(buffer))
            {
                switch (tag)
                {
                    <#list selfFields as field>
                    <#if field.ignore>
                        <#continue/>
                    </#if>
                    <#if field.name=="buffer">
                        <#assign thisField1 = "this.${field.name}" thisField2 = "this.${field.name}">
                    <#elseif field.name=="i">
                        <#assign thisField1 = field.name thisField2 = "this.${field.name}">
                    <#else>
                        <#assign thisField1 = field.name thisField2 = field.name>
                    </#if>
                    case ${field.tag}:
                    <#if field.type=="set" || field.type=="list">
                        buffer.ReadInt();
                        var ${field.name}_Size = buffer.ReadInt();
                        for (var i = 0; i < ${field.name}_Size; i++) 
                        {
                        <#if field.builtinValueType>
                            ${thisField2}.Add(buffer.Read${field.valueType?cap_first}());
                        <#else>
                            var ${field.name}Value = new ${field.valueClassType}();
                            ${field.name}Value.Decode(buffer);
                            ${thisField2}.Add(${field.name}Value);
                        </#if>
                        }
                    <#elseif field.type=="map">
                        buffer.ReadInt();
                        var ${field.name}_Size = buffer.ReadInt();
                        for (var i = 0; i < ${field.name}_Size; i++)
                        {
                        <#if field.builtinValueType>
                            ${thisField2}.Add(buffer.Read${field.keyType?cap_first}(), buffer.Read${field.valueType?cap_first}());
                        <#else>
                            var ${field.name}Key = buffer.Read${field.keyType?cap_first}();
                            var ${field.name}Value = new ${field.valueClassType}();
                            ${field.name}Value.Decode(buffer);
                            ${thisField2}.Add(${field.name}Key, ${field.name}Value);
                        </#if>
                        }
                    <#elseif field.type=="float"||field.type=="double">
                        ${thisField1} = buffer.Read${field.type?cap_first}(<#if field.scale gt 0>${field.scale}</#if>);
                    <#elseif field.builtinType>
                        ${thisField1} = buffer.Read${field.type?cap_first}();
                    <#elseif field.enumType>
                        ${thisField1} = (${field.type}) buffer.ReadInt();
                    <#elseif field.optional>
                        buffer.ReadInt();
                        ${thisField1} = ${thisField1} ?? new ${field.classType}();
                        ${thisField1}.Decode(buffer);
                        <#else>
                        buffer.ReadInt();
                        ${thisField1}.Decode(buffer);
                    </#if>
                        break;
        </#list>
                    default:
                        SkipField(tag, buffer);
                        break;
                }
            }
<#else>
<#list selfFields as field>
    <#if field.ignore>
        <#continue/>
    </#if>
    <#if field.name=="buffer">
        <#assign thisField1 = "this.${field.name}" thisField2 = "this.${field.name}">
    <#elseif field.name=="i">
        <#assign thisField1 = field.name thisField2 = "this.${field.name}">
    <#else>
        <#assign thisField1 = field.name thisField2 = field.name>
    </#if>
    <#if field.type=="set" || field.type=="list">
        <#if field?index gt 0>

        </#if>
            var ${field.name}_Size = buffer.ReadInt();
            for (var i = 0; i < ${field.name}_Size; i++)
            {
        <#if field.builtinValueType>
                ${thisField2}.Add(buffer.Read${field.valueType?cap_first}());
        <#else>
                var ${field.name}Value = new ${field.valueClassType}();
                ${field.name}Value.Decode(buffer);
                ${thisField2}.Add(${field.name}Value);
        </#if>
            }
        <#if field?has_next && !selfFields[field?index+1].collectionType && (selfFields[field?index+1].primitiveType || !selfFields[field?index+1].optional) >

        </#if>
    <#elseif field.type=="map">
        <#if field?index gt 0>

        </#if>
            var ${field.name}_Size = buffer.ReadInt();
            for (var i = 0; i < ${field.name}_Size; i++)
            {
        <#if field.builtinValueType>
                ${thisField2}.Add(buffer.Read${field.keyType?cap_first}(), buffer.Read${field.valueType?cap_first}());
        <#else>
                var ${field.name}Key = buffer.Read${field.keyType?cap_first}();
                var ${field.name}Value = new ${field.valueClassType}();
                ${field.name}Value.Decode(buffer);
                ${thisField2}.Add(${field.name}Key, ${field.name}Value);
        </#if>
            }
        <#if field?has_next && !selfFields[field?index+1].collectionType && (selfFields[field?index+1].primitiveType || !selfFields[field?index+1].optional) >

        </#if>
    <#elseif field.type=="float"||field.type=="double">
            ${thisField1} = buffer.Read${field.type?cap_first}(<#if field.scale gt 0>${field.scale}</#if>);
    <#elseif field.builtinType>
            ${thisField1} = buffer.Read${field.type?cap_first}();
    <#elseif field.enumType>
            ${thisField1} = (${field.type}) buffer.ReadInt();
    <#elseif field.optional>
        <#if field?index gt 0>

        </#if>
            if (buffer.ReadBool()) 
            {
                ${thisField1} = ${thisField1} ?? new ${field.classType}();
                ${thisField1}.Decode(buffer);
            }
        <#if field?has_next && !selfFields[field?index+1].collectionType && (selfFields[field?index+1].primitiveType || !selfFields[field?index+1].optional) >

        </#if>
    <#else>
            ${thisField1}.Decode(buffer);
    </#if>
</#list>
</#if>
        }

        public override string ToString()
        {
            return "${name}{" +
                <#if kind ==3>
                   "_id=" + Id +
                </#if>
                <#list fields as field>
                   "<#rt>
                <#if field?index gt 0 || kind ==3>
                    <#lt>,<#rt>
                </#if>
                <#if field.type == "string">
                    <#lt>${field.name}='" + ${field.name} + '\'' +
                <#elseif field.type == "bytes">
                    <#lt>${field.name}=" + ${field.name}.ToString2() +
                <#elseif field.collectionType>
                    <#lt>${field.name}=" + ${field.name}.ToString2() +
                <#else>
                    <#lt>${field.name}=" + ${field.name}.ToString2() +
                </#if>
            </#list>
                   '}';
        }
    }
}