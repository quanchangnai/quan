using System;
using System.Collections.Generic;
using Quan.Common;
using Quan.Message;
using Buffer = Quan.Message.Buffer;
<#list imports as import>
using ${import};
</#list>

namespace ${fullPackageName}
{
	/// <summary>
<#if comment !="">
	/// ${comment}<br/>
</#if>
	/// 自动生成
	/// </summary>
    public class ${name} : <#if definitionType ==2>Bean<#elseif definitionType ==3>MessageBase</#if>
    {
<#if definitionType ==3>
		/// <summary>
		/// 消息ID
		/// </summary>
		public override int Id => ${id?c};

</#if>
<#list fields as field>
    <#if field.type == "set" || field.type == "list">
        <#if field.comment !="">
        /// <summary>
		/// ${field.comment}
		/// </summary>
        </#if>
		public ${field.classType}<${field.basicValueType}> ${field.name?cap_first} { get; } = new ${field.classType}<${field.basicValueType}>();

    <#elseif field.type == "map">
        <#if field.comment !="">
        /// <summary>
		/// ${field.comment}
		/// </summary>
        </#if>
		public Dictionary<${field.basicKeyType}, ${field.basicValueType}> ${field.name?cap_first} { get; } = new Dictionary<${field.basicKeyType}, ${field.basicValueType}>();

    <#elseif field.type == "string">
		private string _${field.name} = "";

        <#if field.comment !="">
        /// <summary>
		/// ${field.comment}
		/// </summary>
        </#if>
		public string ${field.name?cap_first}
		{
	    	get => _${field.name};
	    	set => _${field.name} = value ?? throw new NullReferenceException();
		}

    <#elseif field.type == "bytes">
		private byte[] _${field.name} = new byte[0];

        <#if field.comment !="">
        /// <summary>
		/// ${field.comment}
		/// </summary>
        </#if>
		public byte[] ${field.name?cap_first}
		{
            get => _${field.name};
            set => _${field.name} = value ?? throw new NullReferenceException();
		}

    <#elseif field.builtInType || field.enumType>
        <#if field.comment !="">
        /// <summary>
		/// ${field.comment}
		/// </summary>
        </#if>
		public ${field.basicType} ${field.name?cap_first} { get; set; }

    <#elseif !field.optional>
		private ${field.basicType} _${field.name} = new ${field.type}();

        <#if field.comment !="">
        /// <summary>
		/// ${field.comment}
		/// </summary>
        </#if>
		public ${field.basicType} ${field.name?cap_first}
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
		private ${field.basicType} ${field.name?cap_first} { get; set; }

    </#if>
</#list>

<#if definitionType ==3>
		public override MessageBase Create()
		{
			return new ${name}();
		}

</#if>
		public override void Encode(Buffer buffer)
		{
	    	base.Encode(buffer);

<#list fields as field>
	<#if field.type=="set" || field.type=="list">
		<#if field_index gt 0>

        </#if>
		    buffer.WriteInt(${field.name?cap_first}.Count);
		    foreach (var ${field.name}Value in ${field.name?cap_first}) {
		<#if field.builtInValueType>
			    buffer.Write${field.valueType?cap_first}(${field.name}Value);
		<#else>
				${field.name}Value.Encode(buffer);
		</#if>
		    }
        <#if field_has_next && !fields[field_index+1].collectionType && (fields[field_index+1].primitiveType || !fields[field_index+1].optional) >

        </#if>
	<#elseif field.type=="map">
		<#if field_index gt 0>

        </#if>
		    buffer.WriteInt(${field.name?cap_first}.Count);
		    foreach (var ${field.name}Key in ${field.name?cap_first}.Keys) {
		        buffer.Write${field.keyType?cap_first}(${field.name}Key);
		<#if field.builtInValueType>
			    buffer.Write${field.valueType?cap_first}(${field.name?cap_first}[${field.name}Key]);
		<#else>
			    ${field.name?cap_first}[${field.name}Key].Encode(buffer);
		</#if>
		    }
        <#if field_has_next && !fields[field_index+1].collectionType && (fields[field_index+1].primitiveType || !fields[field_index+1].optional) >

        </#if>
	<#elseif field.type=="float"||field.type=="double">
			buffer.Write${field.type?cap_first}(${field.name?cap_first}<#if field.scale gt 0>, ${field.scale}</#if>);
	<#elseif field.builtInType>
		    buffer.Write${field.type?cap_first}(${field.name?cap_first});
	<#elseif field.enumType>
			buffer.WriteInt((int)${field.name?cap_first});
	<#elseif field.optional>
		<#if field_index gt 0>

        </#if>
		    buffer.WriteBool(${field.name?cap_first} != null);
		    ${field.name?cap_first}?.Encode(buffer);
        <#if field_has_next && !fields[field_index+1].collectionType && (fields[field_index+1].primitiveType || !fields[field_index+1].optional) >

        </#if>
	<#else>
		    ${field.name?cap_first}.Encode(buffer);
	</#if>
</#list>
		}

		public override void Decode(Buffer buffer)
		{
	    	base.Decode(buffer);

<#list fields as field>
	<#if field.type=="set" || field.type=="list">
		<#if field_index gt 0>

        </#if>
		    var ${field.name}Size = buffer.ReadInt();
		    for (var i = 0; i < ${field.name}Size; i++) {
		<#if field.builtInValueType>
			    ${field.name?cap_first}.Add(buffer.Read${field.valueType?cap_first}());
		<#else>
			    var ${field.name}Value = new ${field.valueType}();
			  	${field.name}Value.Decode(buffer);
			    ${field.name?cap_first}.Add(${field.name}Value);
		</#if>
		    }
        <#if field_has_next && !fields[field_index+1].collectionType && (fields[field_index+1].primitiveType || !fields[field_index+1].optional) >

        </#if>
	<#elseif field.type=="map">
		<#if field_index gt 0>

        </#if>
		    var ${field.name}Size = buffer.ReadInt();
		    for (var i = 0; i < ${field.name}Size; i++) {
		<#if field.builtInValueType>
			    ${field.name?cap_first}.Add(buffer.Read${field.keyType?cap_first}(), buffer.Read${field.valueType?cap_first}());
		<#else>
			    var ${field.name}Key = buffer.Read${field.keyType?cap_first}();
			    var ${field.name}Value = new ${field.valueType}();
				${field.name}Value.Decode(buffer);
			    ${field.name?cap_first}.Add(${field.name}Key, ${field.name}Value);
		</#if>
		    }
        <#if field_has_next && !fields[field_index+1].collectionType && (fields[field_index+1].primitiveType || !fields[field_index+1].optional) >

        </#if>
	<#elseif field.type=="float"||field.type=="double">
			${field.name?cap_first} = buffer.Read${field.type?cap_first}(<#if field.scale gt 0>${field.scale}</#if>);
	<#elseif field.builtInType>
		    ${field.name?cap_first} = buffer.Read${field.type?cap_first}();
	<#elseif field.enumType>
		    ${field.name?cap_first} = (${field.type})buffer.ReadInt();
	<#elseif field.optional>
		<#if field_index gt 0>

        </#if>
		    if (buffer.ReadBool()) {
		        if (${field.name?cap_first} == null) {
		            ${field.name?cap_first} = new ${field.type}();
		        }
		        ${field.name?cap_first}.Decode(buffer);
            }
        <#if field_has_next && !fields[field_index+1].collectionType && (fields[field_index+1].primitiveType || !fields[field_index+1].optional) >

        </#if>
	<#else>
		    ${field.name?cap_first}.Decode(buffer);
	</#if>
</#list>
		}

		public override string ToString()
		{
			return "${name}{" +
			<#list fields as field>
					"<#rt>
				<#if field_index gt 0>
					<#lt>,<#rt>
				</#if>
				<#if field.type == "string">
					<#lt>${field.name}='" + ${field.name?cap_first} + '\'' +
				<#elseif field.type == "bytes">
					<#lt>${field.name}=" + ${field.name?cap_first}.ToString2() +
				<#elseif field.collectionType>
					<#lt>${field.name}=" + ${field.name?cap_first}.ToString2() +
				<#else>
					<#lt>${field.name}=" + ${field.name?cap_first}.ToString2() +
				</#if>
			</#list>
					'}';
		}
    }
}