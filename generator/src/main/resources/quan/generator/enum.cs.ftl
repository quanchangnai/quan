namespace ${packageName}
{
    /// <summary>
    <#if comment !="">
    /// ${comment}<br/>
    </#if>
    /// Created by 自动生成
    /// </summary>
    public enum ${name} {

    <#list fields as field>
        <#if field.comment !="">//${field.comment}</#if>
        <#if field_has_next>
        ${field.name}=${field.value},
        <#else>
        ${field.name}=${field.value}
        </#if>
    </#list>


    }
}
