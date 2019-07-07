<#if packageName !=".">package ${packageName};

</#if>
<#list imports as import>
import ${import};
<#if !import_has_next>

</#if>
</#list>
/**
<#if comment !="">
 * ${comment}
</#if>
 * Created by 自动生成
 */
public class ${name} extends <#if definitionType ==2>Bean<#elseif definitionType ==5>Data<${keyType}></#if> {
<#if definitionType ==5>

    private static Cache<${keyType}, ${name}> cache;

    public ${name}(${keyType} ${keyName}) {
        super(cache);
        this.${keyName}.setLogValue(${keyName}, getRoot());
    }

    @Override
    public ${keyType} getKey() {
        return get${keyName?cap_first}();
    }

    public synchronized static void setCache(Cache<${keyType}, ${name}> cache) {
        cache.checkClosed();
        if (${name}.cache != null) {
            throw new IllegalStateException("数据已设置缓存");
        }
        ${name}.cache = cache;
    }


    private synchronized static void checkCache() {
        if (cache != null && !cache.isClosed()) {
            return;
        }

        Database database = Database.getDefault();
        if (database == null) {
            throw new IllegalStateException("没有默认数据库");
        }

        if (cache == null) {
            cache = new Cache<>("${name}", ${name}::new);
            database.registerCache(cache);
        } else if (cache.isClosed()) {
            database.registerCache(cache);
        }

    }

    public static ${name} get(${keyType} ${keyName}) {
        checkCache();
        return cache.get(${keyName});
    }

    public static void delete(${keyType} ${keyName}) {
        checkCache();
        cache.delete(${keyName});
    }

    public static void insert(${name} data) {
        checkCache();
        cache.insert(data);
    }

    public static ${name} getOrInsert(${keyType} ${keyName}) {
        checkCache();
        return cache.getOrInsert(${keyName});
    }

</#if>

<#list fields as field>
    <#if field.type == "set" || field.type == "list">
    private ${field.classType}<${field.classValueType}> ${field.name} = new ${field.classType}<>(getRoot());<#if field.comment !="">//${field.comment}</#if>

    <#elseif field.type == "map">
    private ${field.classType}<${field.classKeyType}, ${field.classValueType}> ${field.name} = new ${field.classType}<>(getRoot());<#if field.comment !="">//${field.comment}</#if>

    <#elseif field.type = "string">
    private BaseField<${field.classType}> ${field.name} = new BaseField<>("");<#if field.comment !="">//${field.comment}</#if>

    <#elseif field.type = "byte">
    private BaseField<${field.classType}> ${field.name} = new BaseField<>((byte) 0);<#if field.comment !="">//${field.comment}</#if>

    <#elseif field.type = "short">
    private BaseField<${field.classType}> ${field.name} = new BaseField<>((short) 0);<#if field.comment !="">//${field.comment}</#if>

    <#elseif field.type = "int">
    private BaseField<${field.classType}> ${field.name} = new BaseField<>(0);<#if field.comment !="">//${field.comment}</#if>

    <#elseif field.type = "long">
    private BaseField<${field.classType}> ${field.name} = new BaseField<>(0L);<#if field.comment !="">//${field.comment}</#if>

    <#elseif field.type = "float">
    private BaseField<${field.classType}> ${field.name} = new BaseField<>(0F);<#if field.comment !="">//${field.comment}</#if>

    <#elseif field.type = "double">
    private BaseField<${field.classType}> ${field.name} = new BaseField<>(0D);<#if field.comment !="">//${field.comment}</#if>

    <#elseif field.type = "bool">
    private BaseField<${field.classType}> ${field.name} = new BaseField<>(false);<#if field.comment !="">//${field.comment}</#if>

    <#else>
    private BeanField<${field.classType}> ${field.name} = new BeanField<>();<#if field.comment !="">//${field.comment}</#if>

    </#if>
</#list>

<#list fields as field>
    <#if field.type == "list" || field.type == "set">
    public ${field.basicType}<${field.classValueType}> get${field.name?cap_first}() {
        return ${field.name};
    }

    <#elseif field.type == "map">
    public ${field.basicType}<${field.classKeyType}, ${field.classValueType}> get${field.name?cap_first}() {
        return ${field.name};
    }

    <#elseif definitionType ==5 && field.name == keyName>
    public ${field.basicType} get${field.name?cap_first}() {
        return ${field.name}.getValue();
    }

    <#else>
    public ${field.basicType} get${field.name?cap_first}() {
        return ${field.name}.getValue();
    }

    public void set${field.name?cap_first}(${field.basicType} ${field.name}) {
        this.${field.name}.setLogValue(${field.name}, getRoot());
    }

    </#if>
</#list>

    @Override
    public void setChildrenLogRoot(Data root) {
<#list fields as field>
    <#if field.collectionType>
        ${field.name}.setLogRoot(root);
    <#elseif !field.builtInType>
        <#if field_index gt 0 && fields[field_index-1].collectionType>

        </#if>
        ${field.type} _${field.name} = this.${field.name}.getValue();
        if (_${field.name} != null) {
            _${field.name}.setLogRoot(root);
        }

    </#if>
</#list>
    }

    @Override
    public JSONObject encode() {
        JSONObject object = new JSONObject();

<#list fields as field>
    <#if field.type == "list" || field.type == "set">
        <#if field_index gt 0 && fields[field_index-1].builtInType && !fields[field_index-1].collectionType>

        </#if>
        JSONArray _${field.name} = new JSONArray();
        for (${field.classValueType} _${field.name}_value : ${field.name}) {
        <#if !field.valueBuiltInType>
            _${field.name}.add(_${field.name}_value.encode());
        <#else>
            _${field.name}.add(_${field.name}_value);
        </#if>
        }
        object.put("${field.name}", _${field.name});

    <#elseif field.type == "map">
        <#if field_index gt 0 && fields[field_index-1].builtInType && !fields[field_index-1].collectionType>

        </#if>
        JSONObject _${field.name} = new JSONObject();
        for (${field.classKeyType} _${field.name}_key : ${field.name}.keySet()) {
        <#if !field.valueBuiltInType>
            _${field.name}.put(String.valueOf(_${field.name}_key), ${field.name}.get(_${field.name}_key).encode());
        <#else>
            _${field.name}.put(String.valueOf(_${field.name}_key), ${field.name}.get(_${field.name}_key));
        </#if>
        }
        object.put("${field.name}", _${field.name});

    <#elseif field.builtInType>
        object.put("${field.name}", ${field.name}.getValue());
    <#else>
        <#if field_index gt 0 && fields[field_index-1].builtInType && !fields[field_index-1].collectionType>

        </#if>
        ${field.type} _${field.name} = ${field.name}.getValue();
        if (_${field.name} != null) {
            object.put("${field.name}", _${field.name}.encode());
        }

    </#if>
</#list>
        return object;
    }

    @Override
    public void decode(JSONObject object) {
<#list fields as field>
    <#if field.type == "list">
        <#if field_index gt 0 && fields[field_index-1].builtInType && !fields[field_index-1].collectionType && fields[field_index-1].type!="string">

        </#if>
        JSONArray _${field.name}_1 = object.getJSONArray("${field.name}");
        if (_${field.name}_1 != null) {
            List<${field.classValueType}> _${field.name}_2 = new ArrayList<>();
            for (int i = 0; i < _${field.name}_1.size(); i++) {
        <#if !field.valueBuiltInType>
                ${field.classValueType} _${field.name}_value = new ${field.classValueType}();
                _${field.name}_value.decode(_${field.name}_1.getJSONObject(i));
                _${field.name}_2.add(_${field.name}_value);
        <#else>
                _${field.name}_2.add(_${field.name}_1.get${field.classValueType}(i));
        </#if>
            }
            PVector<${field.classValueType}> _${field.name}_3 = Empty.vector();
            ${field.name}.setValue(_${field.name}_3.plusAll(_${field.name}_2));
        }

    <#elseif field.type == "set">
        <#if field_index gt 0 && fields[field_index-1].builtInType && !fields[field_index-1].collectionType && fields[field_index-1].type!="string">

        </#if>
        JSONArray _${field.name}_1 = object.getJSONArray("${field.name}");
        if (_${field.name}_1 != null) {
            Set<${field.classValueType}> _${field.name}_2 = new HashSet<>();
            for (int i = 0; i < _${field.name}_1.size(); i++) {
        <#if !field.valueBuiltInType>
                ${field.classValueType} _${field.name}_value = new ${field.classValueType}();
                _${field.name}_value.decode(_${field.name}_1.getJSONObject(i));
                _${field.name}_2.add(_${field.name}_value);
        <#else>
                _${field.name}_2.add(_${field.name}_1.get${field.classValueType}(i));
        </#if>
            }
            PSet<${field.classValueType}> _${field.name}_3 = Empty.set();
            ${field.name}.setValue(_${field.name}_3.plusAll(_${field.name}_2));
        }

    <#elseif field.type == "map">
        <#if field_index gt 0 && fields[field_index-1].builtInType && !fields[field_index-1].collectionType && fields[field_index-1].type!="string">

        </#if>
        JSONObject _${field.name}_1 = object.getJSONObject("${field.name}");
        if (_${field.name}_1 != null) {
            Map<${field.classKeyType}, ${field.classValueType}> _${field.name}_2 = new HashMap<>();
            for (String _${field.name}_1_key : _${field.name}_1.keySet()) {
        <#if !field.valueBuiltInType>
                ${field.classValueType} _${field.name}_value = new ${field.classValueType}();
                _${field.name}_value.decode(_${field.name}_1.getJSONObject(_${field.name}_1_key));
                _${field.name}_2.put(${field.classKeyType}.valueOf(_${field.name}_1_key), _${field.name}_value);
        <#else>
                _${field.name}_2.put(${field.classKeyType}.valueOf(_${field.name}_1_key), _${field.name}_1.get${field.classValueType}(_${field.name}_1_key));
        </#if>
            }
            PMap<${field.classKeyType}, ${field.classValueType}> _${field.name}_3 = Empty.map();
            ${field.name}.setValue(_${field.name}_3.plusAll(_${field.name}_2));
        }

    <#elseif field.type=="int">
        ${field.name}.setValue(object.getIntValue("${field.name}"));
    <#elseif field.type=="string">
        <#if field_index gt 0 && fields[field_index-1].builtInType && !fields[field_index-1].collectionType && fields[field_index-1].type!="string">

        </#if>
        String _${field.name} = object.getString("${field.name}");
        if (_${field.name} == null) {
            _${field.name} = "";
        }
        ${field.name}.setValue(_${field.name});

    <#elseif field.builtInType>
        ${field.name}.setValue(object.get${field.classType}Value("${field.name}"));
    <#else>
        <#if field_index gt 0 && fields[field_index-1].builtInType && !fields[field_index-1].collectionType && fields[field_index-1].type!="string">

        </#if>
        JSONObject _${field.name} = object.getJSONObject("${field.name}");
        if (_${field.name} != null) {
            ${field.classType} _${field.name}_value = ${field.name}.getValue();
            if (_${field.name}_value == null) {
                _${field.name}_value = new ${field.classType}();
                ${field.name}.setValue(_${field.name}_value);
            }
            _${field.name}_value.decode(_${field.name});
        }

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
            <#else>
                <#lt>${field.name}=" + ${field.name} +
            </#if>
        </#list>
                '}';

    }

}
