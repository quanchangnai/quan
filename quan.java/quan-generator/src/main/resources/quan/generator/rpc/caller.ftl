package ${packageName};

import quan.rpc.Caller;
import quan.rpc.Service;

import javax.annotation.processing.Generated;

@Generated("quan.generator.rpc.RpcGenerator")
public final class ${simpleName}Caller implements Caller {

    public static final ${simpleName}Caller instance = new ${simpleName}Caller();

    private ${simpleName}Caller() {
    }

    public Object call(Service service, int methodId, Object... params) {
        ${simpleName} ${simpleName?uncap_first} = (${simpleName}) service;
        switch (methodId) {
        <#list methods as method>
            case ${method?index+1}:
            <#if !method.returnVoid>
                return ${simpleName?uncap_first}.${method.name}(<#rt>
            <#else>
                ${simpleName?uncap_first}.${method.name}(<#rt>
            </#if>
            <#list method.parameters?keys as paramName>
                (${method.parameters[paramName]}) params[${paramName?index}]<#if paramName?has_next>, </#if><#t>
            </#list>
            <#lt>);
            <#if method.returnVoid>
                return null;
            </#if>
        </#list>
            default:
                return null;
        }
    }

}
