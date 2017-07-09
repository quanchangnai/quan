<#if packageName !=".">package ${packageName};

</#if>
/**
<#if comment !="">
 * ${comment}
 * Created by {@link quan.protocol.generator.JavaGenerator}
<#else>
 * Created by {@link quan.protocol.generator.JavaGenerator}
</#if>
 */
public enum ${name} {

<#list fields as field>
    <#if field_has_next>
    ${field.name}(${field.value}),<#if field.comment !="">//${field.comment}</#if>
    <#else>
    ${field.name}(${field.value});<#if field.comment !="">//${field.comment}</#if>
    </#if>
</#list>

    private int value;

    ${name}(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ${name} valueOf(int value) {
        switch (value) {
        <#list fields  as field>
            case ${field.value}:
                return ${field.name};
        </#list>
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        return "RoleType{" +
                "name='" + name() + '\'' +
                ",value=" + value +
                '}';
    }
}
