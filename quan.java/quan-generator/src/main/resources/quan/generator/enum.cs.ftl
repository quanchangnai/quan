namespace ${getFullPackageName("cs")}
{
    /// <summary>
    <#if comment !="">
    /// ${comment}<br/>
    </#if>
    /// 代码自动生成，请勿手动修改
    /// </summary>
    public enum ${name} {

    <#list fields as field>
        <#if field.comment !="">
        /// <summary>
        /// ${field.comment}
        /// </summary>
        </#if>
        <#if field_has_next>
        ${field.name} = ${field.enumValue},
        <#else>
        ${field.name} = ${field.enumValue}
        </#if>

    </#list>
    }
}
