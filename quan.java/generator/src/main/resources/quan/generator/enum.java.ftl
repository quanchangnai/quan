package ${getFullPackageName("java")};

/**
<#if comment !="">
 * ${comment}<br/>
</#if>
 * 代码自动生成，请勿手动修改
 */
public enum ${name} {

<#list fields as field>
    <#if field.comment !="">
    /**
     * ${field.comment}
     */
    </#if>
    <#if field_has_next>
    ${field.name}(${field.value}),
    <#else>
    ${field.name}(${field.value});
    </#if>

</#list>

    private final int value;

    ${name}(int value) {
        this.value = value;
    }

    public int value() {
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

}
