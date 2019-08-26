namespace ${fullPackageName}
{
    /// <summary>
    <#if comment !="">
    /// ${comment}<br/>
    </#if>
    /// 自动生成
    /// </summary>
    public enum ${name} {

    <#list fields as field>
        <#if field.comment !="">
        /// <summary>
        /// ${field.comment}
        /// </summary>
        </#if>
        <#if field_has_next>
        ${field.name?cap_first} = ${field.value},
        <#else>
        ${field.name?cap_first} = ${field.value}
        </#if>

    </#list>
    }
}
