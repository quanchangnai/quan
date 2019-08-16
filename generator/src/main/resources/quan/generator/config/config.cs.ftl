using System;
using System.Collections.Generic;
using ConfigCS;
<#list imports as import>
using ${import};
</#list>

namespace ${fullPackageName}
{
    /// <summary>
<#if comment !="">
	/// ${comment}<br/>
</#if>
	/// Created by 自动生成
	/// </summary>
    public class ${name} : <#if definitionType ==2>Bean<#elseif definitionType ==6>Config</#if>
    {

    }
}