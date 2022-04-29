<#if packageName??>
package ${packageName};

 </#if>
<#list imports?keys as importKey>
import ${imports[importKey]};
</#list>

/**
 * @see ${name}
 */
public final class ${name}Caller implements Caller {

    public static final ${name}Caller instance = new ${name}Caller();

    private ${name}Caller() {
    }

    @Override
    public Object call(Service service, int methodId, Object... params) throws Exception {
        ${name} ${name?uncap_first} = (${name}) service;
        switch (methodId) {
        <#list methods as method>
            case ${method?index+1}:
            <#if method.returnVoid>
                ${name?uncap_first}.${method.name}(<#rt>
            <#else>
                return ${name?uncap_first}.${method.name}(<#rt>
            </#if>
            <#list method.optimizedParameters?keys as paramName>
                <#assign paramType=method.optimizedParameters[paramName]/>
                <#if method.isNeedCastArray(paramName)>
                cast(params[${paramName?index}], ${paramType?substring(0,paramType?length-2)}.class)<#t>
                <#else>
                (${paramType}) params[${paramName?index}]<#t>
                </#if>
                <#if paramName?has_next>, </#if><#t>
            </#list>
            <#lt>);
            <#if method.returnVoid>
                return null;
            </#if>
        </#list>
            default:
                throw new IllegalArgumentException(String.format("${name}不存在方法:%d", methodId));
        }
    }

}
