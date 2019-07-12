using System;
using System.Collections.Generic;
<#list imports as import>
    using ${import};
    <#if !import_has_next>

    </#if>
</#list>
namespace ${packageName}
{

    public class ${name} : <#if definitionType ==2>Bean<#elseif definitionType ==3>Message</#if>
    {
<#list fields as field>
    <#if field.type == "set" || field.type == "list">
	public ${field.classType}<${field.classValueType}> ${field.name} { get; } = new ${field.classType}<${field.classValueType}>();

    <#elseif field.type == "map">
	public ${field.classType}<${field.classKeyType}, ${field.classValueType}> ${field.name} { get; } = new ${field.classType}<${field.classKeyType}, ${field.classValueType}>();

    <#elseif field.type == "string">
	private string _${field.name} = "";

	public string ${field.name}
	{
	    get => ${field.name};
	    set => ${field.name} = value ?? throw new NullReferenceException();
	}

    <#elseif field.type == "bytes">
	private ${field.basicType} _${field.name} = new byte[0];
        
	public byte[] ${field.name}
	{
            get => ${field.name};
            set => ${field.name} = value ?? throw new NullReferenceException();
	}

    <#elseif field.builtInType || field.enumType>
	public ${field.basicType} ${field.name}{ get; set; };

    <#elseif !field.optional>
	private ${field.basicType} _${field.name} = new ${field.type}();

	public ${field.basicType} ${field.name}
	{
	    get => _${field.name};
	    set => _${field.name} = value ?? throw new NullReferenceException();
	}
    <#else>
	private ${field.basicType} ${field.name};
    </#if>
</#list>
        

	public ${name}()<#if definitionType ==3>: base(${id});</#if>
	{
	}

	public override ${name} create()
	{
          return new ${name}();
	}


	public override void encode(Buffer buffer)
	{
	    base.encode(buffer);        
	}

	public override void decode(Buffer buffer)
	{
	    base.decode(buffer);        
	}
        
    }
}