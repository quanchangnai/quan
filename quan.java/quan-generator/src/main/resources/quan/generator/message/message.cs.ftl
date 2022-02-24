using Quan.Utils;
using Quan.Message;
using Buffer = Quan.Message.Buffer;
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
    public<#if kind ==9> abstract</#if> class ${name} : <#if kind ==2>Bean<#else>MessageBase</#if>
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
		public ${field.classType}<${field.classValueType}> ${field.name} { get; } = new ${field.classType}<${field.classValueType}>();

    <#elseif field.type == "map">
        <#if field.comment !="">
        /// <summary>
		/// ${field.comment}
		/// </summary>
        </#if>
		public Dictionary<${field.classKeyType}, ${field.classValueType}> ${field.name} { get; } = new Dictionary<${field.classKeyType}, ${field.classValueType}>();

    <#elseif field.type == "string">
		private string _${field.name} = "";

        <#if field.comment !="">
        /// <summary>
		/// ${field.comment}
		/// </summary>
        </#if>
		public string ${field.name}
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
		public byte[] ${field.name}
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
        public ${field.basicType} ${field.name}
        {
            get => _${field.name};
            set
            {
                Buffer.CheckScale(value, ${field.scale});
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
		public override void Encode(Buffer buffer)
		{
	    	base.Encode(buffer);

<#list selfFields as field>
	 <#if field.ignore>
        <#continue/>
	</#if>
	<#if definedFieldId>
		<#if field.type=="set" || field.type=="list">
			if (${field.name}.Count > 0) {
				buffer.WriteTag(${field.tag});
				var ${field.name}Buffer = new Buffer();
				${field.name}Buffer.WriteInt(${field.name}.Count);
		    	foreach (var ${field.name}Value in ${field.name}) {
				<#if field.builtinValueType>
			    	${field.name}Buffer.Write${field.valueType?cap_first}(${field.name}Value);
				<#else>
					${field.name}Value.Encode(${field.name}Buffer);
				</#if>
		    	}
				buffer.WriteBuffer(${field.name}Buffer);
			}
		<#elseif field.type=="map">
			if (${field.name}.Count > 0) {
				buffer.WriteTag(${field.tag});
				var ${field.name}Buffer = new Buffer();
				${field.name}Buffer.WriteInt(${field.name}.Count);
		    	foreach (var ${field.name}Key in ${field.name}.Keys) {
		        	${field.name}Buffer.Write${field.keyType?cap_first}(${field.name}Key);
				<#if field.builtinValueType>
			    	${field.name}Buffer.Write${field.valueType?cap_first}(${field.name}[${field.name}Key]);
				<#else>
			    	${field.name}[${field.name}Key].Encode(${field.name}Buffer);
				</#if>
		    	}
				buffer.WriteBuffer(${field.name}Buffer);
			}
		<#elseif field.type=="float"||field.type=="double">
        	if (${field.name} != 0) {
            	buffer.WriteTag(${field.tag});
            	buffer.Write${field.type?cap_first}(${field.name}<#if field.scale gt 0>, ${field.scale}</#if>);
        	}
		<#elseif field.numberType>
        	if (${field.name} != 0) {
           	 	buffer.WriteTag(${field.tag});
            	buffer.Write${field.type?cap_first}(${field.name});
        	}
		<#elseif field.type=="bool">
        	if (${field.name}) {
            	buffer.WriteTag(${field.tag});
            	buffer.Write${field.type?cap_first}(${field.name});
        	}
		<#elseif field.type=="string">
        	if (${field.name}.Length > 0) {
            	buffer.WriteTag(${field.tag});
            	buffer.Write${field.type?cap_first}(${field.name});
        	}
		<#elseif field.type=="bytes">
        	if (${field.name}.Length > 0) {
            	buffer.WriteTag(${field.tag});
           		buffer.Write${field.type?cap_first}(${field.name});
        	}
		<#elseif field.enumType>
        	if (${field.name} != 0) {
            	buffer.WriteTag(${field.tag});
            	buffer.WriteInt((int)${field.name});
        	}
		<#elseif field.optional>
		 	if (${field.name} != null) {
				buffer.WriteTag(${field.tag});
				var ${field.name}Buffer = new Buffer();
		    	${field.name}.Encode(${field.name}Buffer);
				buffer.WriteBuffer(${field.name}Buffer);
		 	}
		<#else>
			buffer.WriteTag(${field.tag});
        	var ${field.name}Buffer = new Buffer();
        	${field.name}.Encode(${field.name}Buffer);
        	buffer.WriteBuffer(${field.name}Buffer);
		</#if>

	<#else>
		<#if field.type=="set" || field.type=="list">
			<#if field?index gt 0>

        	</#if>
		    buffer.WriteInt(${field.name}.Count);
		    foreach (var ${field.name}Value in ${field.name}) {
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
		    buffer.WriteInt(${field.name}.Count);
		    foreach (var ${field.name}Key in ${field.name}.Keys) {
		        buffer.Write${field.keyType?cap_first}(${field.name}Key);
			<#if field.builtinValueType>
			    buffer.Write${field.valueType?cap_first}(${field.name}[${field.name}Key]);
			<#else>
			    ${field.name}[${field.name}Key].Encode(buffer);
			</#if>
		    }
        	<#if field?has_next && !selfFields[field?index+1].collectionType && (selfFields[field?index+1].primitiveType || !selfFields[field?index+1].optional) >

        	</#if>
		<#elseif field.type=="float"||field.type=="double">
        	buffer.Write${field.type?cap_first}(${field.name}<#if field.scale gt 0>, ${field.scale}</#if>);
		<#elseif field.builtinType>
			buffer.Write${field.type?cap_first}(${field.name});
		<#elseif field.enumType>
        	buffer.WriteInt((int)${field.name});
		<#elseif field.optional>
			<#if field?index gt 0>

        	</#if>
		    buffer.WriteBool(${field.name} != null);
		    ${field.name}?.Encode(buffer);
        	<#if field?has_next && !selfFields[field?index+1].collectionType && (selfFields[field?index+1].primitiveType || !selfFields[field?index+1].optional) >

        	</#if>
		<#else>
		    ${field.name}.Encode(buffer);
		</#if>
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
			    			${field.name}.Add(buffer.Read${field.valueType?cap_first}());
						<#else>
			    			var ${field.name}Value = new ${field.classValueType}();
			  				${field.name}Value.Decode(buffer);
			    			${field.name}.Add(${field.name}Value);
						</#if>
		    			}
               	 	<#elseif field.type=="map">
                    	buffer.ReadInt();
                    	var ${field.name}Size = buffer.ReadInt();
		    			for (var i = 0; i < ${field.name}Size; i++) 
						{
						<#if field.builtinValueType>
			    			${field.name}.Add(buffer.Read${field.keyType?cap_first}(), buffer.Read${field.valueType?cap_first}());
						<#else>
			    			var ${field.name}Key = buffer.Read${field.keyType?cap_first}();
			    			var ${field.name}Value = new ${field.classValueType}();
							${field.name}Value.Decode(buffer);
			    			${field.name}.Add(${field.name}Key, ${field.name}Value);
						</#if>
						}
                	<#elseif field.type=="float"||field.type=="double">
                    	${field.name} = buffer.Read${field.type?cap_first}(<#if field.scale gt 0>${field.scale}</#if>);
                	<#elseif field.builtinType>
                    	${field.name} = buffer.Read${field.type?cap_first}();
                	<#elseif field.enumType>
                    	${field.name} = (${field.type})buffer.ReadInt();
                	<#elseif field.optional>
                    	buffer.ReadInt();
                    	if (${field.name} == null)
						{
		            		${field.name} = new ${field.classType}();
		        		}
		        		${field.name}.Decode(buffer);
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
		<#if field?index gt 0>

        </#if>
		    var ${field.name}Size = buffer.ReadInt();
		    for (var i = 0; i < ${field.name}Size; i++) 
			{
		<#if field.builtinValueType>
			    ${field.name}.Add(buffer.Read${field.valueType?cap_first}());
		<#else>
			    var ${field.name}Value = new ${field.classValueType}();
			  	${field.name}Value.Decode(buffer);
			    ${field.name}.Add(${field.name}Value);
		</#if>
		    }
        <#if field?has_next && !selfFields[field?index+1].collectionType && (selfFields[field?index+1].primitiveType || !selfFields[field?index+1].optional) >

        </#if>
	<#elseif field.type=="map">
		<#if field?index gt 0>

        </#if>
		    var ${field.name}Size = buffer.ReadInt();
		    for (var i = 0; i < ${field.name}Size; i++) 
			{
		<#if field.builtinValueType>
			    ${field.name}.Add(buffer.Read${field.keyType?cap_first}(), buffer.Read${field.valueType?cap_first}());
		<#else>
			    var ${field.name}Key = buffer.Read${field.keyType?cap_first}();
			    var ${field.name}Value = new ${field.classValueType}();
				${field.name}Value.Decode(buffer);
			    ${field.name}.Add(${field.name}Key, ${field.name}Value);
		</#if>
		    }
        <#if field?has_next && !selfFields[field?index+1].collectionType && (selfFields[field?index+1].primitiveType || !selfFields[field?index+1].optional) >

        </#if>
	<#elseif field.type=="float"||field.type=="double">
			${field.name} = buffer.Read${field.type?cap_first}(<#if field.scale gt 0>${field.scale}</#if>);
	<#elseif field.builtinType>
		    ${field.name} = buffer.Read${field.type?cap_first}();
	<#elseif field.enumType>
		    ${field.name} = (${field.type})buffer.ReadInt();
	<#elseif field.optional>
		<#if field?index gt 0>

        </#if>
		    if (buffer.ReadBool()) {
		        if (${field.name} == null)
				{
		            ${field.name} = new ${field.classType}();
		        }
		        ${field.name}.Decode(buffer);
            }
        <#if field?has_next && !selfFields[field?index+1].collectionType && (selfFields[field?index+1].primitiveType || !selfFields[field?index+1].optional) >

        </#if>
	<#else>
		    ${field.name}.Decode(buffer);
	</#if>
</#list>
</#if>
		}

		public override string ToString()
		{
			return "${name}{" +
			<#list fields as field>
				   "<#rt>
				<#if field?index gt 0>
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