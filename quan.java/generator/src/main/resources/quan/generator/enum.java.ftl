package ${fullPackageName};

/**
<#if comment !="">
 * ${comment}<br/>
</#if>
 * 自动生成
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

}
