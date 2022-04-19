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
 *<#if !customPath> @see<#elseif comments?size gt 0> <br/></#if> ${name}
 */
public class ${name}Proxy${typeParametersStr}{

    /**
     * 服务类名
     */
    private static final String SERVICE_NAME = "${fullName}";

    /**
     * 目标服务器ID
     */
    private int serverId = -1;

    /**
     * 目标服务ID
     */
    private final Object serviceId;

<#if !serviceId??>
    public ${name}Proxy(int serverId, Object serviceId) {
        if (serverId < 0) {
            throw new IllegalArgumentException("服务器ID不能小于0");
        }
        this.serverId = serverId;
        this.serviceId = serviceId;
    }

    public ${name}Proxy(Object serviceId) {
        this.serviceId = serviceId;
    }

<#else>
    public ${name}Proxy(int serverId) {
        if (serverId < 0) {
            throw new IllegalArgumentException("服务器ID不能小于0");
        }
        this.serverId = serverId;
        this.serviceId = "${serviceId}";
    }

    public ${name}Proxy() {
        this.serviceId = "${serviceId}";
    }

    public static final ${name}Proxy instance = new ${name}Proxy();

</#if>
    private int getServerId() {
        if (serverId < 0) {
            serverId = Worker.current().resolveTargetServerId(SERVICE_NAME);
        }
        return serverId;
    }

<#list methods as method>
    /**
    <#list method.comments as comment>
     *${comment}
    </#list>
     *<#if !customPath> @see<#elseif  method.comments?size gt 0> <br/></#if> ${name}#${method.signature}
     */
    public final ${method.typeParametersStr}Promise<${method.optimizedReturnType}> ${method.name}(<#rt>
    <#list method.optimizedParameters?keys as paramName>
        ${method.optimizedParameters[paramName]} ${paramName}<#if paramName?has_next>, </#if><#t>
    </#list>
    <#lt>) {
        String callee = SERVICE_NAME + ".${method.signature}";
        return Worker.current().sendRequest(getServerId(), serviceId, callee, ${method?index+1}<#if method.optimizedParameters?keys?size gt 0>, ${ method.optimizedParameters?keys?join(', ')}</#if>);
    }

</#list>
}
