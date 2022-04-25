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
public class ${name}Proxy${typeParametersStr} extends Proxy{

    /**
     * 服务类名
     */
    private static final String SERVICE_NAME = "${fullName}";

    private static final String[] signatures = new String[${methods?size}];

<#if !serviceId??>
    public ${name}Proxy(int serverId, Object serviceId) {
        super(serverId, serviceId);
    }

    public ${name}Proxy(Object serviceId) {
        super(serviceId);
    }

<#else>
    public ${name}Proxy(int serverId) {
        super(serverId, "${serviceId}");
    }

    public ${name}Proxy() {
        super("${serviceId}");
    }

    public static final ${name}Proxy instance = new ${name}Proxy();

</#if>
    @Override
    protected String _getServiceName$() {
        return SERVICE_NAME;
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
        if (signatures[${method?index}] == null) {
            signatures[${method?index}] = SERVICE_NAME + ".${method.signature}";
        }
        return _sendRequest$(signatures[${method?index}], ${method.securityModifier}, ${method?index+1}<#if method.optimizedParameters?keys?size gt 0>, ${ method.optimizedParameters?keys?join(', ')}</#if>);
    }

</#list>
}
