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
     * 构造一个${name}Proxy
     * @param serverId 目标服务器ID
     * @param serviceId 目标服务ID
     */
    public ${name}Proxy(int serverId, Object serviceId) {
        this.serverId = serverId;
        this.serviceId = serviceId;
    }

    /**
     * 构造一个目标服务器为本地服务器的${name}Proxy
     * @param serviceId 目标服务ID
     */
    public ${name}Proxy(Object serviceId) {
        this.serverId = 0;
        this.serviceId = serviceId;
    }

<#list methods as method>
    /**
    <#list method.comments as comment>
     *${comment}
    </#list>
     * @see ${name}#${method.signature}
     */
    public final ${method.typeParametersStr}Promise<${method.optimizedReturnType}> ${method.name}(<#rt>
    <#list method.optimizedParameters?keys as paramName>
        ${method.optimizedParameters[paramName]} ${paramName}<#if paramName?has_next>, </#if><#t>
    </#list>
    <#lt>) {
        String methodSignature = "${name}.${method.signature}";
        return Worker.current().sendRequest(serverId, serviceId, methodSignature, ${method?index+1}<#if method.optimizedParameters?keys?size gt 0>, ${ method.optimizedParameters?keys?join(', ')}</#if>);
    }

</#list>
}
