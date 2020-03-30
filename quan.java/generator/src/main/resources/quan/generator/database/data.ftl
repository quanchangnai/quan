package ${getFullPackageName("java")};

import java.util.*;
import com.alibaba.fastjson.*;
import org.pcollections.*;
import quan.database.*;
<#list imports as import>
import ${import};
</#list>

/**
<#if comment !="">
 * ${comment}<br/>
</#if>
 * 自动生成
 */
public class ${name} extends <#if definitionType ==2>Entity<#elseif definitionType ==5>Data<${keyType}></#if> {

<#list fields as field>
    <#if field.comment !="">
    //${field.comment}
    </#if>
    <#if field.type == "set" || field.type == "list">
    private ${field.classType}<${field.classValueType}> ${field.name} = new ${field.classType}<>(_getRoot());

    <#elseif field.type == "map">
    private ${field.classType}<${field.classKeyType}, ${field.classValueType}> ${field.name} = new ${field.classType}<>(_getRoot());

    <#elseif field.type = "string">
    private BaseField<${field.classType}> ${field.name} = new BaseField<>("");

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
    private EntityField<${field.classType}> ${field.name} = new EntityField<>();

    </#if>
</#list>
<#if definitionType ==5>

    <#if persistent>
    private static Table<${keyType}, ${name}> _table;

    public ${name}(${keyType} ${keyName}) {
        super(_table);
        this.${keyName}.setLogValue(${keyName}, _getRoot());
    }

    public ${name}(Table<${keyType}, ${name}> table, ${keyType} ${keyName}) {
        super(table);
        this.${keyName}.setLogValue(${keyName}, _getRoot());
    }

    <#else>
    public ${name}() {
        super(null);
    }
        
    public ${name}(${keyType} ${keyName}) {
        super(null);
        this.${keyName}.setLogValue(${keyName}, _getRoot());
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
        this.${field.name}.setLogValue(${field.name}.value(), _getRoot());
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
        this.${field.name}.setLogValue(${field.name}, _getRoot());
        return this;
    }

    </#if>
</#list>

    @Override
    protected void _setChildrenLogRoot(Data root) {
<#list fields as field>
    <#if field.collectionType>
        _setLogRoot(${field.name}, root);
    <#elseif !field.builtinType && !field.enumType>
        <#if field_index gt 0 && fields[field_index-1].collectionType>

        </#if>
        ${field.type} $${field.name} = this.${field.name}.getValue();
        if ($${field.name} != null) {
            _setLogRoot($${field.name}, root);
        }

    </#if>
</#list>
    }

    @Override
    public JSONObject encode() {
        JSONObject json = new JSONObject();

<#list fields as field>
    <#if field.ignore>
        <#continue>
    </#if>
    <#if field.type == "list" || field.type == "set">
        <#if field_index gt 0 >

        </#if>
        JSONArray $${field.name} = new JSONArray();
        <#if !field.builtinValueType>
        for (${field.classValueType} $${field.name}$Value : this.${field.name}) {
            $${field.name}.add($${field.name}$Value.encode());
        }
        <#else>
        $${field.name}.addAll(this.${field.name});
        </#if>
        json.put("${field.name}", $${field.name});
        <#if field_has_next && (fields[field_index+1].enumType || fields[field_index+1].primitiveType) >

        </#if>
    <#elseif field.type == "map">
        <#if field_index gt 0 >

        </#if>
        JSONObject $${field.name} = new JSONObject();
        for (${field.classKeyType} $${field.name}$Key : this.${field.name}.keySet()) {
        <#if !field.builtinValueType>
            $${field.name}.put(String.valueOf($${field.name}$Key), this.${field.name}.get($${field.name}$Key).encode());
        <#else>
            $${field.name}.put(String.valueOf($${field.name}$Key), this.${field.name}.get($${field.name}$Key));
        </#if>
        }
        json.put("${field.name}", $${field.name});
        <#if field_has_next && (fields[field_index+1].enumType || fields[field_index+1].primitiveType) >

        </#if>
    <#elseif field.builtinType || field.enumType>
        json.put("${field.name}", this.${field.name}.getValue());
    <#else>
        <#if field_index gt 0 >

        </#if>
        ${field.type} $${field.name} = this.${field.name}.getValue();
        if ($${field.name} != null) {
            json.put("${field.name}", $${field.name}.encode());
        }
        <#if field_has_next && (fields[field_index+1].enumType || fields[field_index+1].primitiveType) >

        </#if>
    </#if>
</#list>

        return json;
    }

    @Override
    public void decode(JSONObject json) {
<#list fields as field>
    <#if field.ignore>
        <#continue>
    </#if>
    <#if field.type == "list">
        <#if field_index gt 0 >

        </#if>
        JSONArray $${field.name}$1 = json.getJSONArray("${field.name}");
        if ($${field.name}$1 != null) {
            PVector<${field.classValueType}> $${field.name}$2 = Empty.vector();
            for (int i = 0; i < $${field.name}$1.size(); i++) {
        <#if !field.builtinValueType>
                ${field.classValueType} $${field.name}$Value = new ${field.classValueType}();
                $${field.name}$Value.decode($${field.name}$1.getJSONObject(i));
                $${field.name}$2 = $${field.name}$2.plus($${field.name}$Value);
        <#else>
                $${field.name}$2 = $${field.name}$2.plus($${field.name}$1.get${field.classValueType}(i));
        </#if>
            }
            this.${field.name}.setValue($${field.name}$2);
        }
        <#if field_has_next && (fields[field_index+1].enumType || fields[field_index+1].primitiveType && fields[field_index+1].type!="string" )>

        </#if>
    <#elseif field.type == "set">
        <#if field_index gt 0 >

        </#if>
        JSONArray $${field.name}$1 = json.getJSONArray("${field.name}");
        if ($${field.name}$1 != null) {
            PSet<${field.classValueType}> $${field.name}$2 = Empty.set();
            for (int i = 0; i < $${field.name}$1.size(); i++) {
        <#if !field.builtinValueType>
                ${field.classValueType} $${field.name}$Value = new ${field.classValueType}();
                $${field.name}$Value.decode($${field.name}$1.getJSONObject(i));
                $${field.name}$2 = $${field.name}$2.plus($${field.name}$Value);
        <#else>
                $${field.name}$2 = $${field.name}$2.plus($${field.name}$1.get${field.classValueType}(i));
        </#if>
            }
            this.${field.name}.setValue($${field.name}$2);
        }
        <#if field_has_next && (fields[field_index+1].enumType || fields[field_index+1].primitiveType && fields[field_index+1].type!="string" )>

        </#if>
    <#elseif field.type == "map">
        <#if field_index gt 0 >

        </#if>
        JSONObject $${field.name}$1 = json.getJSONObject("${field.name}");
        if ($${field.name}$1 != null) {
            PMap<${field.classKeyType}, ${field.classValueType}> $${field.name}$2 = Empty.map();
            for (String $${field.name}$Key : $${field.name}$1.keySet()) {
        <#if !field.builtinValueType>
                ${field.classValueType} $${field.name}$Value = new ${field.classValueType}();
                $${field.name}$Value.decode($${field.name}$1.getJSONObject($${field.name}$Key));
                $${field.name}$2 = $${field.name}$2.plus(${field.classKeyType}.valueOf($${field.name}$Key), $${field.name}$Value);
        <#else>
                $${field.name}$2 = $${field.name}$2.plus(${field.classKeyType}.valueOf($${field.name}$Key), $${field.name}$1.get${field.classValueType}($${field.name}$Key));
        </#if>
            }
            this.${field.name}.setValue($${field.name}$2);
        }
        <#if field_has_next && (fields[field_index+1].enumType || fields[field_index+1].primitiveType && fields[field_index+1].type!="string" )>

        </#if>
    <#elseif field.type=="int" || field.enumType>
        this.${field.name}.setValue(json.getIntValue("${field.name}"));
    <#elseif field.type=="string">
        this.${field.name}.setValue(json.getOrDefault("${field.name}", "").toString());
    <#elseif field.builtinType>
        this.${field.name}.setValue(json.get${field.classType}Value("${field.name}"));
    <#else>
        <#if field_index gt 0 >

        </#if>
        JSONObject $${field.name} = json.getJSONObject("${field.name}");
        if ($${field.name} != null) {
            ${field.classType} $${field.name}$Value = this.${field.name}.getValue();
            if ($${field.name}$Value == null) {
                $${field.name}$Value = new ${field.classType}();
                this.${field.name}.setValue($${field.name}$Value);
            }
            $${field.name}$Value.decode($${field.name});
        }
        <#if field_has_next && (fields[field_index+1].enumType || fields[field_index+1].primitiveType && fields[field_index+1].type!="string" )>

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
    public synchronized static void setTable(Table<${keyType}, ${name}> table) {
        if (_table != null && _table.isWorkable()) {
            throw new IllegalStateException("数据已设置缓存表");
        }
        Objects.requireNonNull(table, "参数[table]不能为空");
        table.checkWorkable();
        _table = table;
    }

    private synchronized static void checkTable() {
        if (_table != null && _table.isWorkable()) {
            return;
        }

        Database database = Database.getDefault();
        if (database == null) {
            throw new IllegalStateException("没有默认数据库");
        }

        if (_table == null) {
            _table = new Table<>("${name}", ${name}::new);
        }
        database.registerTable(_table);
    }

    public static ${name} get(${keyType} ${keyName}) {
        checkTable();
        return _table.get(${keyName});
    }

    public static void delete(${keyType} ${keyName}) {
        checkTable();
        _table.delete(${keyName});
    }

    public static void insert(${name} data) {
        checkTable();
        _table.insert(data);
    }

    public static ${name} getOrInsert(${keyType} ${keyName}) {
        checkTable();
        return _table.getOrInsert(${keyName});
    }

    public static void save(${keyType} ${keyName}) {
        checkTable();
        _table.save(${keyName});
    }

</#if>
}