package ${packageName};

import quan.rpc.Promise;
import quan.rpc.Worker;

import javax.annotation.processing.Generated;

@Generated("quan.generator.rpc.RpcGenerator")
public final class ${simpleName}Proxy {

    private final int targetServerId;

    private final Object serviceId;

    public ${simpleName}Proxy(int targetServerId, Object serviceId) {
        this.targetServerId = targetServerId;
        this.serviceId = serviceId;
    }

    public int getTargetServerId() {
        return targetServerId;
    }

    public Object getServiceId() {
        return serviceId;
    }

<#list methods as method>
    public Promise<${method.returnType}> ${method.name}(<#rt>
    <#list method.parameters?keys as paramName>
        ${method.parameters[paramName]} ${paramName}<#if paramName?has_next>, </#if><#t>
    </#list>
    <#lt>) {
        return Worker.current().sendRequest(targetServerId, serviceId, ${method?index+1}<#if method.parameters?keys?size gt 0> , ${ method.parameters?keys?join(' , ')}</#if>);
    }

</#list>

}
