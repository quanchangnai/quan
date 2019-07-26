<#if packageName !=".">package ${packageName};

</#if>
<#list imports as import>
import ${import};
<#if !import_has_next>

</#if>
</#list>
/**
<#if comment !="">
 * ${comment}<br/>
</#if>
 * Created by 自动生成
 */
public class ${name} extends <#if definitionType ==2>Bean<#elseif definitionType ==5>Data<${keyType}></#if> {

<#list fields as field>
    <#if field.comment !="">
    //${field.comment}
    </#if>
    <#if field.type == "set" || field.type == "list">
    private ${field.classType}<${field.classValueType}> ${field.name} = new ${field.classType}<>(getRoot());

    <#elseif field.type == "map">
    private ${field.classType}<${field.classKeyType}, ${field.classValueType}> ${field.name} = new ${field.classType}<>(getRoot());

    <#elseif field.type = "string">
    private BaseField<${field.classType}> ${field.name} = new BaseField<>("");

    <#elseif field.type = "byte">
    private BaseField<${field.classType}> ${field.name} = new BaseField<>((byte) 0);

    <#elseif field.type = "short">
    private BaseField<${field.classType}> ${field.name} = new BaseField<>((short) 0);

    <#elseif field.type = "int" || field.enumType>
    private BaseField<Integer> ${field.name} = new BaseField<>(0);

    <#elseif field.type = "long">
    private BaseField<${field.classType}> ${field.name} = new BaseField<>(0L);

    <#elseif field.type = "float">
    private BaseField<${field.classType}> ${field.name} = new BaseField<>(0F);

    <#elseif field.type = "double">
    private BaseField<${field.classType}> ${field.name} = new BaseField<>(0D);

    <#elseif field.type = "bool">
    private BaseField<${field.classType}> ${field.name} = new BaseField<>(false);

    <#else>
    private BeanField<${field.classType}> ${field.name} = new BeanField<>();

    </#if>
</#list>
<#if definitionType ==5>

    <#if persistent>
    private static Cache<${keyType}, ${name}> _cache;

    public ${name}(${keyType} ${keyName}) {
	    super(_cache);
        this.${keyName}.setLogValue(${keyName}, getRoot());
    }

    <#else>
    public ${name}() {
        super(null);
    }
        
    public ${name}(${keyType} ${keyName}) {
        super(null);
        this.${keyName}.setLogValue(${keyName}, getRoot());
    }

    </#if>
    @Override
    public ${keyType} getKey() {
        return get${keyName?cap_first}();
    }
</#if>

<#list fields as field>
    <#if field.comment !="">
    /**
     * ${field.comment}
     */
    </#if>
    <#if field.type == "list" || field.type == "set">
    public ${field.basicType}<${field.classValueType}> get${field.name?cap_first}() {
        return ${field.name};
    }

    <#elseif field.type == "map">
    public ${field.basicType}<${field.classKeyType}, ${field.classValueType}> get${field.name?cap_first}() {
        return ${field.name};
    }

    <#elseif definitionType ==5 && persistent && field.name == keyName>
    public ${field.basicType} get${field.name?cap_first}() {
        return ${field.name}.getValue();
    }

    <#elseif field.enumType>
    public ${field.type} get${field.name?cap_first}() {
        return ${field.type}.valueOf(${field.name}.getValue());
    }

    <#if field.comment !="">
    /**
     * ${field.comment}
     */
    </#if>
    public ${name} set${field.name?cap_first}(${field.basicType} ${field.name}) {
        this.${field.name}.setLogValue(${field.name}.getValue(), getRoot());
	    return this;
    }

    <#else>
    public ${field.basicType} get${field.name?cap_first}() {
        return ${field.name}.getValue();
    }

    <#if field.comment !="">
    /**
     * ${field.comment}
     */
    </#if>
    public ${name} set${field.name?cap_first}(${field.basicType} ${field.name}) {
        this.${field.name}.setLogValue(${field.name}, getRoot());
        return this;
    }

    </#if>
</#list>

    @Override
    public void setChildrenLogRoot(Data root) {
<#list fields as field>
    <#if field.collectionType>
        ${field.name}.setLogRoot(root);
    <#elseif !field.builtInType && !field.enumType>
        <#if field_index gt 0 && fields[field_index-1].collectionType>

        </#if>
        ${field.type} $${field.name} = this.${field.name}.getValue();
        if ($${field.name} != null) {
            $${field.name}.setLogRoot(root);
        }

    </#if>
</#list>
    }

    @Override
    public JSONObject encode() {
        JSONObject object = new JSONObject();

<#list fields as field>
    <#if field.type == "list" || field.type == "set">
        <#if field_index gt 0 >

        </#if>
        JSONArray $${field.name} = new JSONArray();
        for (${field.classValueType} $${field.name}$Value : ${field.name}) {
        <#if !field.valueBuiltInType>
            $${field.name}.add($${field.name}$Value.encode());
        <#else>
            $${field.name}.add($${field.name}$Value);
        </#if>
        }
        object.put("${field.name}", $${field.name});
        <#if field_has_next && (fields[field_index+1].enumType || fields[field_index+1].primitiveType) >

        </#if>
    <#elseif field.type == "map">
        <#if field_index gt 0 >

        </#if>
        JSONObject $${field.name} = new JSONObject();
        for (${field.classKeyType} $${field.name}$Key : ${field.name}.keySet()) {
        <#if !field.valueBuiltInType>
            $${field.name}.put(String.valueOf($${field.name}$Key), ${field.name}.get($${field.name}$Key).encode());
        <#else>
            $${field.name}.put(String.valueOf($${field.name}$Key), ${field.name}.get($${field.name}$Key));
        </#if>
        }
        object.put("${field.name}", $${field.name});
        <#if field_has_next && (fields[field_index+1].enumType || fields[field_index+1].primitiveType) >

        </#if>
    <#elseif field.builtInType || field.enumType>
        object.put("${field.name}", ${field.name}.getValue());
    <#else>
        <#if field_index gt 0 >

        </#if>
        ${field.type} $${field.name} = ${field.name}.getValue();
        if ($${field.name} != null) {
            object.put("${field.name}", $${field.name}.encode());
        }
        <#if field_has_next && (fields[field_index+1].enumType || fields[field_index+1].primitiveType) >

        </#if>
    </#if>
</#list>

        return object;
    }

    @Override
    public void decode(JSONObject object) {
<#list fields as field>
    <#if field.type == "list">
        <#if field_index gt 0 >

        </#if>
        JSONArray $${field.name}$1 = object.getJSONArray("${field.name}");
        if ($${field.name}$1 != null) {
            PVector<${field.classValueType}> $${field.name}$2 = Empty.vector();
            for (int i = 0; i < $${field.name}$1.size(); i++) {
        <#if !field.valueBuiltInType>
                ${field.classValueType} $${field.name}$Value = new ${field.classValueType}();
                $${field.name}$Value.decode($${field.name}$1.getJSONObject(i));
                $${field.name}$2 = $${field.name}$2.plus($${field.name}$Value);
        <#else>
                $${field.name}$2 = $${field.name}$2.plus($${field.name}$1.get${field.classValueType}(i));
        </#if>
            }
            ${field.name}.setValue($${field.name}$2);
        }
        <#if field_has_next && (fields[field_index+1].enumType || fields[field_index+1].primitiveType && fields[field_index+1].type!="string")>

        </#if>
    <#elseif field.type == "set">
        <#if field_index gt 0 >

        </#if>
        JSONArray $${field.name}$1 = object.getJSONArray("${field.name}");
        if ($${field.name}$1 != null) {
            PSet<${field.classValueType}> $${field.name}$2 = Empty.set();
            for (int i = 0; i < $${field.name}$1.size(); i++) {
        <#if !field.valueBuiltInType>
                ${field.classValueType} $${field.name}$Value = new ${field.classValueType}();
                $${field.name}$Value.decode($${field.name}$1.getJSONObject(i));
                $${field.name}$2 = $${field.name}$2.plus($${field.name}$Value);
        <#else>
                $${field.name}$2 = $${field.name}$2.plus($${field.name}$1.get${field.classValueType}(i));
        </#if>
            }
            ${field.name}.setValue($${field.name}$2);
        }
        <#if field_has_next && (fields[field_index+1].enumType || fields[field_index+1].primitiveType && fields[field_index+1].type!="string")>

        </#if>
    <#elseif field.type == "map">
        <#if field_index gt 0 >

        </#if>
        JSONObject $${field.name}$1 = object.getJSONObject("${field.name}");
        if ($${field.name}$1 != null) {
            PMap<${field.classKeyType}, ${field.classValueType}> $${field.name}$2 = Empty.map();
            for (String $${field.name}$1_Key : $${field.name}$1.keySet()) {
        <#if !field.valueBuiltInType>
                ${field.classValueType} $${field.name}$Value = new ${field.classValueType}();
                $${field.name}$Value.decode($${field.name}$1.getJSONObject($${field.name}$1_Key));
                $${field.name}$2 = $${field.name}$2.plus(${field.classKeyType}.valueOf($${field.name}$1_Key), $${field.name}$Value);
        <#else>
                $${field.name}$2 = $${field.name}$2.plus(${field.classKeyType}.valueOf($${field.name}$1_Key), $${field.name}$1.get${field.classValueType}($${field.name}$1_Key));
        </#if>
            }
            ${field.name}.setValue($${field.name}$2);
        }
        <#if field_has_next && (fields[field_index+1].enumType || fields[field_index+1].primitiveType && fields[field_index+1].type!="string")>

        </#if>
    <#elseif field.type=="int" || field.enumType>
        ${field.name}.setValue(object.getIntValue("${field.name}"));
    <#elseif field.type=="string">
        <#if field_index gt 0 >

        </#if>
        String $${field.name} = object.getString("${field.name}");
        if ($${field.name} == null) {
            $${field.name} = "";
        }
        ${field.name}.setValue($${field.name});
        <#if field_has_next && (fields[field_index+1].enumType || fields[field_index+1].primitiveType && fields[field_index+1].type!="string")>

        </#if>
    <#elseif field.builtInType>
        ${field.name}.setValue(object.get${field.classType}Value("${field.name}"));
    <#else>
        <#if field_index gt 0 >

        </#if>
        JSONObject $${field.name} = object.getJSONObject("${field.name}");
        if ($${field.name} != null) {
            ${field.classType} $${field.name}$Value = ${field.name}.getValue();
            if ($${field.name}$Value == null) {
                $${field.name}$Value = new ${field.classType}();
                ${field.name}.setValue($${field.name}$Value);
            }
            $${field.name}$Value.decode($${field.name});
        }
        <#if field_has_next && (fields[field_index+1].enumType || fields[field_index+1].primitiveType && fields[field_index+1].type!="string")>

        </#if>
    </#if>
</#list>
    }

    @Override
    public String toString() {
        return "${name}{" +
        <#list fields as field>
                "<#rt>
            <#if field_index gt 0>
                <#lt>,<#rt>
            </#if>
            <#if field.type == "string">
                <#lt>${field.name}='" + ${field.name} + '\'' +
            <#elseif field.enumType>
                <#lt>${field.name}=" + ${field.type}.valueOf(${field.name}.getValue()) +
            <#else>
                <#lt>${field.name}=" + ${field.name} +
            </#if>
        </#list>
                '}';

    }

<#if definitionType ==5 && persistent>
    public synchronized static void setCache(Cache<${keyType}, ${name}> cache) {
        cache.checkWorkable();
        if (_cache != null && _cache.isWorkable()) {
            throw new IllegalStateException("数据已设置缓存");
        }
        _cache = cache;
    }

    private synchronized static void checkCache() {
        if (_cache != null && _cache.isWorkable()) {
            return;
        }

        Database database = Database.getDefault();
        if (database == null) {
            throw new IllegalStateException("没有默认数据库");
        }

        if (_cache == null) {
            _cache = new Cache<>("${name}", ${name}::new);
            database.registerCache(_cache);
        } else if (!_cache.isWorkable()) {
            database.registerCache(_cache);
        }
    }

    public static ${name} get(${keyType} ${keyName}) {
        checkCache();
        return _cache.get(${keyName});
    }

    public static void delete(${keyType} ${keyName}) {
        checkCache();
        _cache.delete(${keyName});
    }

    public static void insert(${name} data) {
        checkCache();
        _cache.insert(data);
    }

    public static ${name} getOrInsert(${keyType} ${keyName}) {
        checkCache();
        return _cache.getOrInsert(${keyName});
    }

</#if>
}
