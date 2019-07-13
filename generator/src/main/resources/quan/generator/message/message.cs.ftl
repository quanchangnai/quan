using System;
using System.Collections.Generic;
using message_cs;
using Buffer = message_cs.Buffer;
<#list imports as import>
using ${import};
</#list>

namespace ${packageName}
{
	/// <summary>
	<#if comment !="">
	/// ${comment}<br/>
	</#if>
	/// Created by 自动生成
	/// </summary>
    public class ${name} : <#if definitionType ==2>Bean<#elseif definitionType ==3>Message</#if>
    {
<#list fields as field>
    <#if field.type == "set" || field.type == "list">
		public ${field.classType}<${field.basicValueType}> ${field.name} { get; } = new ${field.classType}<${field.basicValueType}>();

    <#elseif field.type == "map">
		public Dictionary<${field.basicKeyType}, ${field.basicValueType}> ${field.name} { get; } = new Dictionary<${field.basicKeyType}, ${field.basicValueType}>();

    <#elseif field.type == "string">
		private string _${field.name} = "";

		public string ${field.name}
		{
	    	get => _${field.name};
	    	set => _${field.name} = value ?? throw new NullReferenceException();
		}

    <#elseif field.type == "bytes">
		private byte[] _${field.name} = new byte[0];
        
		public byte[] ${field.name}
		{
            get => _${field.name};
            set => _${field.name} = value ?? throw new NullReferenceException();
		}

    <#elseif field.builtInType || field.enumType>
		public ${field.basicType} ${field.name} { get; set; }

    <#elseif !field.optional>
		private ${field.basicType} _${field.name} = new ${field.type}();

		public ${field.basicType} ${field.name}
		{
	    	get => _${field.name};
	    	set => _${field.name} = value ?? throw new NullReferenceException();
		}

    <#else>
		private ${field.basicType} ${field.name} { get; set; }

    </#if>
</#list>

		public ${name}()<#if definitionType ==3>: base(${id})</#if>
		{
		}
<#if definitionType ==3>

		public override Message Create()
		{
			return new ${name}();
		}
</#if>

		public override void Encode(Buffer buffer)
		{
	    	base.Encode(buffer);

<#list fields as field>
	<#if field.type=="set" || field.type=="list">
		<#if field_index gt 0 && !fields[field_index-1].optional && !fields[field_index-1].collectionType >

		</#if>
		    buffer.WriteInt(${field.name}.Count);
		    foreach (var _${field.name}_Value in ${field.name}) {
		<#if field.valueBuiltInType>
			    buffer.Write${field.valueType?cap_first}(_${field.name}_Value);
		<#else>
			    _${field.name}_Value.Encode(buffer);
		</#if>
		    }

	<#elseif field.type=="map">
		<#if field_index gt 0 && !fields[field_index-1].optional && !fields[field_index-1].collectionType >

		</#if>
		    buffer.WriteInt(${field.name}.Count);
		    foreach (var _${field.name}_Key in ${field.name}.Keys) {
		        buffer.Write${field.keyType?cap_first}(_${field.name}_Key);
		<#if field.valueBuiltInType>
			    buffer.Write${field.valueType?cap_first}(${field.name}[_${field.name}_Key]);
		<#else>
			    ${field.name}[_${field.name}_Key].Encode(buffer);
		</#if>
		    }

	<#elseif field.builtInType>
		    buffer.Write${field.type?cap_first}(${field.name});
	<#elseif field.enumType>
			buffer.WriteInt((int)${field.name});
	<#elseif field.optional>
		<#if field_index gt 0 && !fields[field_index-1].optional && !fields[field_index-1].collectionType >

		</#if>
		    buffer.WriteBool(${field.name} != null);
		    ${field.name}?.Encode(buffer);

	<#else>
		    ${field.name}.Encode(buffer);
	</#if>
</#list>
		}

		public override void Decode(Buffer buffer)
		{
	    	base.Decode(buffer);

<#list fields as field>
	<#if field.type=="set" || field.type=="list">
		<#if field_index gt 0 && !fields[field_index-1].optional && !fields[field_index-1].collectionType >

		</#if>
		    var _${field.name}_Size = buffer.ReadInt();
		    for (var _index_ = 0; _index_ < _${field.name}_Size; _index_++) {
		<#if field.valueBuiltInType>
			    ${field.name}.Add(buffer.Read${field.valueType?cap_first}());
		<#else>
			    var _${field.name}_Value = new ${field.valueType}();
			    _${field.name}_Value.Decode(buffer);
			    ${field.name}.Add(_${field.name}_Value);
		</#if>
		    }

	<#elseif field.type=="map">
		<#if field_index gt 0 && !fields[field_index-1].optional && !fields[field_index-1].collectionType >

		</#if>
		    var _${field.name}_Size = buffer.ReadInt();
		    for (var _index_ = 0; _index_ < _${field.name}_Size; _index_++) {
		<#if field.valueBuiltInType>
			    ${field.name}.Add(buffer.Read${field.keyType?cap_first}(), buffer.Read${field.valueType?cap_first}());
		<#else>
			    var _${field.name}_Key = buffer.Read${field.keyType?cap_first}();
			    var _${field.name}_Value = new ${field.valueType}();
			    _${field.name}_Value.Decode(buffer);
			    ${field.name}.Add(_${field.name}_Key, _${field.name}_Value);
		</#if>
		    }

	<#elseif field.builtInType>
		    ${field.name} = buffer.Read${field.type?cap_first}();
	<#elseif field.enumType>
		    ${field.name} = (${field.type})buffer.ReadInt();
	<#elseif field.optional>
		<#if field_index gt 0 && !fields[field_index-1].optional && !fields[field_index-1].collectionType >

		</#if>
		    if (buffer.ReadBool()) {
		        if (${field.name} == null) {
		            ${field.name} = new ${field.type}();
		        }
		        ${field.name}.Decode(buffer);
            }

	<#else>
		    ${field.name}.Decode(buffer);
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
					<#lt>${field.name}='" + ${field.name} + '\'' +
				<#elseif field.type == "bytes">
					<#lt>${field.name}=" + Convert.ToBase64String(${field.name}) +
				<#elseif field.collectionType>
					<#lt>${field.name}=" + ToString(${field.name}) +
				<#else>
					<#lt>${field.name}=" + ${field.name} +
				</#if>
			</#list>
					'}';
		}
    }
}