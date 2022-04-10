<#if packageName??>
package ${packageName};

 </#if>
<#list imports?keys as importKey>
import ${imports[importKey]};
</#list>

/**
<#list comments as comment>
 *${comment}
</#list>
 * @see ${name}
 */
public class ${name}Proxy${typeParametersStr}{

    public final int serverId;

    public final Object serviceId;

    /**
     * @param serverId 目标服务器ID
     * @param serviceId 目标服务ID
     */
    public ${name}Proxy(int serverId, Object serviceId) {
        this.serverId = serverId;
        this.serviceId = serviceId;
    }

<#list methods as method>
    /**
    <#list method.comments as comment>
     *${comment}
    </#list>
     * @see ${name}#${method.name}
     */
    public final ${method.typeParametersStr}Promise<${method.optimizedReturnType}> ${method.name}(<#rt>
    <#list method.optimizedParameters?keys as paramName>
        ${method.optimizedParameters[paramName]} ${paramName}<#if paramName?has_next>, </#if><#t>
    </#list>
    <#lt>) {
        return Worker.current().sendRequest(serverId, serviceId, ${method?index+1}<#if method.optimizedParameters?keys?size gt 0>, ${ method.optimizedParameters?keys?join(', ')}</#if>);
    }

</#list>
}
