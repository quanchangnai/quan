<#if packageName??>
package ${packageName};

 </#if>
import quan.rpc.Promise;
import quan.rpc.Worker;

import javax.annotation.processing.Generated;

/**
<#list comment?split('\n','r') as c>
 *${c}
</#list>
 * @see ${name}
 */
@Generated("quan.generator.rpc.RpcGenerator")
public final class ${name}Proxy${typeParametersStr}{

    /**
     * 目标服务器ID
     */
    public final int serverId;

    /**
     * 目标服务ID
     */
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
    <#list method.comment?split('\n','r') as c>
     *${c}
    </#list>
     * @see ${name}#${method.name}
     */
    public ${method.typeParametersStr}Promise<${method.returnType}> ${method.name}(<#rt>
    <#list method.parameters?keys as paramName>
        ${method.parameters[paramName]} ${paramName}<#if paramName?has_next>, </#if><#t>
    </#list>
    <#lt>) {
        return Worker.current().sendRequest(serverId, serviceId, ${method?index+1}<#if method.parameters?keys?size gt 0> , ${ method.parameters?keys?join(' , ')}</#if>);
    }

</#list>

}
