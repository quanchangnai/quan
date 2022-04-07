<#if packageName??>
package ${packageName};

 </#if>
import quan.rpc.Caller;
import quan.rpc.Service;

import javax.annotation.processing.Generated;

@Generated("quan.generator.rpc.RpcGenerator")
public final class ${name}Caller implements Caller {

    public static final ${name}Caller instance = new ${name}Caller();

    private ${name}Caller() {
    }

    @Override
    public Object call(Service service, int methodId, Object... params) {
        ${name} ${name?uncap_first} = (${name}) service;
        switch (methodId) {
        <#list methods as method>
            case ${method?index+1}:
            <#if method.returnVoid>
                ${name?uncap_first}.${method.name}(<#rt>
            <#else>
                return ${name?uncap_first}.${method.name}(<#rt>
            </#if>
            <#list method.parameters?keys as paramName>
                (${method.eraseParameterType(method.parameters[paramName])}) params[${paramName?index}]<#if paramName?has_next>, </#if><#t>
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
