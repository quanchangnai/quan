using System;
using System.Collections.Generic;
using Quan.Common.Utils;
using Quan.Message;
using Buffer = Quan.Message.Buffer;
<#list imports as import>
using ${import};
</#list>

namespace ${getFullPackageName("cs")}
{
	/// <summary>
<#if comment !="">
	/// ${comment}<br/>
</#if>
	/// 自动生成
	/// </summary>
    public<#if kind ==9> abstract</#if> class ${name} : <#if kind ==2>Bean<#elseif kind ==3 && header??>${header.name}<#else>MessageBase</#if>
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

    <#elseif (field.type=="float"||field.type=="double") && field.scale gt 0>
        private ${field.basicType} _${field.name};

        <#if field.comment !="">
        /// <summary>
		/// ${field.comment}
		/// </summary>
        </#if>
        public ${field.basicType} ${field.name?cap_first}
        {
            get => _${field.name};
            set
            {
                Buffer.CheckScale(value, ${field.scale}, false);
                _${field.name} = value;
            }
        }

    <#elseif field.builtinType>
        <#if field.comment !="">
        /// <summary>
		/// ${field.comment}
		/// </summary>
        </#if>
		public ${field.basicType} ${field.name?cap_first} { get; set; }

    <#elseif !field.optional && !field.enumType>
		private ${field.classType} _${field.name} = new ${field.classType}();

        <#if field.comment !="">
        /// <summary>
		/// ${field.comment}
		/// </summary>
        </#if>
		public ${field.classType} ${field.name?cap_first}
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
		public ${field.classType} ${field.name?cap_first} { get; set; }

    </#if>
</#list>

<#if kind ==3>
		public override MessageBase Create()
		{
			return new ${name}();
		}

</#if>
		public override void Encode(Buffer buffer)
		{
	    	base.Encode(buffer);

<#list selfFields as field>
	 <#if field.ignore>
        <#continue/>
	</#if>
	 <#if definedFieldId>
        	buffer.WriteTag(${field.tag});
    </#if>
	<#if field.type=="set" || field.type=="list">
		<#if definedFieldId>
			Buffer ${field.name}Buffer = new Buffer();
			${field.name}Buffer.WriteInt(${field.name?cap_first}.Count);
		    foreach (var ${field.name}Value in ${field.name?cap_first}) {
			<#if field.builtinValueType>
			    ${field.name}Buffer.Write${field.valueType?cap_first}(${field.name}Value);
			<#else>
				${field.name}Value.Encode(${field.name}Buffer);
			</#if>
		    }
			buffer.WriteBuffer($${field.name}Buffer);
		<#else>
			<#if field_index gt 0>

        	</#if>
		    buffer.WriteInt(${field.name?cap_first}.Count);
		    foreach (var ${field.name}Value in ${field.name?cap_first}) {
			<#if field.builtinValueType>
			    buffer.Write${field.valueType?cap_first}(${field.name}Value);
			<#else>
				${field.name}Value.Encode(buffer);
			</#if>
		    }
        	<#if field_has_next && !selfFields[field_index+1].collectionType && (selfFields[field_index+1].primitiveType || !selfFields[field_index+1].optional) >

        	</#if>
		</#if>
	<#elseif field.type=="map">
		<#if definedFieldId>
			Buffer ${field.name}Buffer = new Buffer();
			${field.name}Buffer.WriteInt(${field.name?cap_first}.Count);
		    foreach (var ${field.name}Key in ${field.name?cap_first}.Keys) {
		        ${field.name}Buffer.Write${field.keyType?cap_first}(${field.name}Key);
			<#if field.builtinValueType>
			    ${field.name}Buffer.Write${field.valueType?cap_first}(${field.name?cap_first}[${field.name}Key]);
			<#else>
			    ${field.name?cap_first}[${field.name}Key].Encode(${field.name}Buffer);
			</#if>
		    }
			buffer.WriteBuffer($${field.name}Buffer);
		<#else>
			<#if field_index gt 0>

        	</#if>
		    buffer.WriteInt(${field.name?cap_first}.Count);
		    foreach (var ${field.name}Key in ${field.name?cap_first}.Keys) {
		        buffer.Write${field.keyType?cap_first}(${field.name}Key);
			<#if field.builtinValueType>
			    buffer.Write${field.valueType?cap_first}(${field.name?cap_first}[${field.name}Key]);
			<#else>
			    ${field.name?cap_first}[${field.name}Key].Encode(buffer);
			</#if>
		    }
        	<#if field_has_next && !selfFields[field_index+1].collectionType && (selfFields[field_index+1].primitiveType || !selfFields[field_index+1].optional) >

        	</#if>
		</#if>
	<#elseif field.type=="float"||field.type=="double">
			buffer.Write${field.type?cap_first}(${field.name?cap_first}<#if field.scale gt 0>, ${field.scale}</#if>);
	<#elseif field.builtinType>
		    buffer.Write${field.type?cap_first}(${field.name?cap_first});
	<#elseif field.enumType>
			buffer.WriteInt((int)${field.name?cap_first});
	<#elseif field.optional>
		<#if definedFieldId>
			Buffer ${field.name}Buffer = new Buffer();
			${field.name}Buffer.WriteBool(${field.name?cap_first} != null);
		    ${field.name?cap_first}?.Encode(${field.name}Buffer);
			buffer.WriteBuffer($${field.name}Buffer);
		<#else>
			<#if field_index gt 0>

        	</#if>
		    buffer.WriteBool(${field.name?cap_first} != null);
		    ${field.name?cap_first}?.Encode(buffer);
        	<#if field_has_next && !selfFields[field_index+1].collectionType && (selfFields[field_index+1].primitiveType || !selfFields[field_index+1].optional) >

        	</#if>
		</#if>
	<#else>
		<#if definedFieldId>
        	Buffer ${field.name}Buffer = new Buffer();
        	${field.name}.Encode(${field.name}Buffer);
        	buffer.writeBuffer(${field.name}Buffer);
        <#else>
		    ${field.name?cap_first}.Encode(buffer);
		</#if>
	</#if>
	<#if definedFieldId>

    </#if>
</#list>
<#if definedFieldId>
        	buffer.WriteTag(0);
</#if>
		}

		public override void Decode(Buffer buffer)
		{
	    	base.Decode(buffer);

<#if definedFieldId>
			for (var tag = buffer.ReadTag(); tag != 0; tag = buffer.ReadTag()) 
			{
            	switch (tag) 
				{
            		<#list selfFields as field>
                	<#if field.ignore>
                    	<#continue/>
                	</#if>
                	case ${field.tag}:
                	<#if field.type=="set" || field.type=="list">
                    	buffer.ReadInt();
                    	var ${field.name}Size = buffer.ReadInt();
		    			for (var i = 0; i < ${field.name}Size; i++) 
						{
						<#if field.builtinValueType>
			    			${field.name?cap_first}.Add(buffer.Read${field.valueType?cap_first}());
						<#else>
			    		var ${field.name}Value = new ${field.classValueType}();
			  			${field.name}Value.Decode(buffer);
			    		${field.name?cap_first}.Add(${field.name}Value);
						</#if>
		    			}
               	 	<#elseif field.type=="map">
                    	buffer.ReadInt();
                    	var ${field.name}Size = buffer.ReadInt();
		    			for (var i = 0; i < ${field.name}Size; i++) 
						{
						<#if field.builtinValueType>
			    			${field.name?cap_first}.Add(buffer.Read${field.keyType?cap_first}(), buffer.Read${field.valueType?cap_first}());
						<#else>
			    			var ${field.name}Key = buffer.Read${field.keyType?cap_first}();
			    			var ${field.name}Value = new ${field.classValueType}();
							${field.name}Value.Decode(buffer);
			    			${field.name?cap_first}.Add(${field.name}Key, ${field.name}Value);
						</#if>
                	<#elseif field.type=="float"||field.type=="double">
                    	${field.name?cap_first} = buffer.Read${field.type?cap_first}(<#if field.scale gt 0>${field.scale}</#if>);
                	<#elseif field.builtinType>
                    	${field.name?cap_first} = buffer.Read${field.type?cap_first}();
                	<#elseif field.enumType>
                    	${field.name?cap_first} = (${field.type})buffer.ReadInt();
                	<#elseif field.optional>
                    	buffer.ReadInt();
                    	if (buffer.ReadBool()) 
						{
		        			if (${field.name?cap_first} == null) 
							{
		            			${field.name?cap_first} = new ${field.classType}();
		        			}
		        			${field.name?cap_first}.Decode(buffer);
            			}
           	 		<#else>
                    	buffer.ReadInt();
                    	${field.name}.Decode(buffer);
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
	<#elseif field.type=="set" || field.type=="list">
		<#if field_index gt 0>

        </#if>
		    var ${field.name}Size = buffer.ReadInt();
		    for (var i = 0; i < ${field.name}Size; i++) 
			{
		<#if field.builtinValueType>
			    ${field.name?cap_first}.Add(buffer.Read${field.valueType?cap_first}());
		<#else>
			    var ${field.name}Value = new ${field.classValueType}();
			  	${field.name}Value.Decode(buffer);
			    ${field.name?cap_first}.Add(${field.name}Value);
		</#if>
		    }
        <#if field_has_next && !selfFields[field_index+1].collectionType && (selfFields[field_index+1].primitiveType || !selfFields[field_index+1].optional) >

        </#if>
	<#elseif field.type=="map">
		<#if field_index gt 0>

        </#if>
		    var ${field.name}Size = buffer.ReadInt();
		    for (var i = 0; i < ${field.name}Size; i++) 
			{
		<#if field.builtinValueType>
			    ${field.name?cap_first}.Add(buffer.Read${field.keyType?cap_first}(), buffer.Read${field.valueType?cap_first}());
		<#else>
			    var ${field.name}Key = buffer.Read${field.keyType?cap_first}();
			    var ${field.name}Value = new ${field.classValueType}();
				${field.name}Value.Decode(buffer);
			    ${field.name?cap_first}.Add(${field.name}Key, ${field.name}Value);
		</#if>
		    }
        <#if field_has_next && !selfFields[field_index+1].collectionType && (selfFields[field_index+1].primitiveType || !selfFields[field_index+1].optional) >

        </#if>
	<#elseif field.type=="float"||field.type=="double">
			${field.name?cap_first} = buffer.Read${field.type?cap_first}(<#if field.scale gt 0>${field.scale}</#if>);
	<#elseif field.builtinType>
		    ${field.name?cap_first} = buffer.Read${field.type?cap_first}();
	<#elseif field.enumType>
		    ${field.name?cap_first} = (${field.type})buffer.ReadInt();
	<#elseif field.optional>
		<#if field_index gt 0>

        </#if>
		    if (buffer.ReadBool()) {
		        if (${field.name?cap_first} == null) 
				{
		            ${field.name?cap_first} = new ${field.classType}();
		        }
		        ${field.name?cap_first}.Decode(buffer);
            }
        <#if field_has_next && !selfFields[field_index+1].collectionType && (selfFields[field_index+1].primitiveType || !selfFields[field_index+1].optional) >

        </#if>
	<#else>
		    ${field.name?cap_first}.Decode(buffer);
	</#if>
</#list>
</#if>
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